package com.sdp.m1.Generator;

import com.google.gson.Gson;
import com.sdp.m1.Extractors.WebPageExtractorJSON;
import com.sdp.m1.Generator.Model.CorrelatedField;
import com.sdp.m1.Generator.Model.PageData;
import com.sdp.m1.Generator.Model.Srs;
import com.sdp.m1.Generator.Model.UiComponent;
import com.sdp.m1.Pages.m1LoginPage;
import com.sdp.m1.Utils.TestConfigs;
import com.sdp.m1.Utils.TestUtils;
import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;

public class TestGenerator {
    private static final Logger logger = Logger.getLogger(TestGenerator.class.getName());
    private static final Boolean LOGIN_NEEDED = true;

    public static void main(String[] args) throws Exception {
        // 1. Configuration
        String srsPath = "src/test/java/com/sdp/m1/Generator/srs_registration_form.json"; // Path to your detailed SRS
                                                                                          // JSON
        String targetUrl = TestConfigs.getBaseUrl() + "/registerServiceProvider.html"; // TODO
        String featureOutputPath = "src/test/resources/Features/Generated_SP_Registration.feature";
        String stepsOutputPath = "src/test/java/com/sdp/m1/Steps/Generated/GeneratedServiceProviderRegistrationSteps.java";

        logger.info("Starting test generation process...");

        // 2. Initialize Browser and Extractor
        SelfHealingDriver driver = TestUtils.getDriver(TestConfigs.getBrowser());
        SelfHealingDriverWait wait = TestUtils.getWaitDriver(driver);

        if (LOGIN_NEEDED) {
            login(driver, wait);
        }

        driver.get(targetUrl);
        driver.get(targetUrl); // Navigate twice to ensure full load
        WebPageExtractorJSON extractor = new WebPageExtractorJSON(driver, wait);

        // 3. Load and Parse Inputs
        String srsJsonContent = new String(Files.readAllBytes(Paths.get(srsPath)));
        String pageComponentsJson = extractor.extractComponentsAsJsonString();

        Gson gson = new Gson();
        Srs srsData = gson.fromJson(srsJsonContent, Srs.class);
        PageData pageData = gson.fromJson(pageComponentsJson, PageData.class);
        List<UiComponent> componentData = pageData.components;

        // 4. Correlate SRS with UI Components
        List<CorrelatedField> correlatedData = correlate(srsData, componentData);
        logger.info(String.format("Correlation complete. Found %d mappable fields.", correlatedData.size()));
        correlatedData.forEach(field -> logger.info(String.format("Mapped SRS field '%s' to UI element '%s' via %s",
                field.srsRequirement.label, field.uiField.selector, field.matchType)));

        // 5. Generate Test Artifacts
        String featureFileContent = generateFeatureFile(srsData, correlatedData);
        writeFile(featureOutputPath, featureFileContent);
        logger.info("Generated feature file: " + featureOutputPath);

        String stepDefsContent = generateStepDefinitions(correlatedData);
        writeFile(stepsOutputPath, stepDefsContent);
        logger.info("Generated step definitions: " + stepsOutputPath);

        driver.quit();
        logger.info("Test generation finished successfully.");
    }

    private static void login(SelfHealingDriver driver, SelfHealingDriverWait wait) {
        m1LoginPage loginPage = new m1LoginPage(driver, wait);
        driver.get(TestConfigs.getBaseUrl());
        loginPage.waitForPageLoad();
        loginPage.enterUsername(TestConfigs.getUsername());
        loginPage.enterPassword(TestConfigs.getPassword());
        loginPage.clickLoginButton();
        loginPage.verifyDashboard();
    }

    private static List<CorrelatedField> correlate(Srs srsData, List<UiComponent> uiComponents) {
        List<CorrelatedField> correlatedFields = new ArrayList<>();
        List<UiComponent.Field> allUiFields = new ArrayList<>();
        if (uiComponents != null) {
            uiComponents.forEach(component -> {
                if (component.fields != null) {
                    allUiFields.addAll(component.fields);
                }
            });
        }

        logger.info("Starting correlation...");
        logger.info("SRS Requirements: " + srsData.requirements.size());
        logger.info("UI Fields: " + allUiFields.size());

        for (Srs.Requirement req : srsData.requirements) {
            logger.info("Processing SRS requirement: " + req.label + " (id: " + req.id + ")");
            boolean matched = false;

            // Pass 1: Match by ID
            for (UiComponent.Field uiField : allUiFields) {
                logger.info("  Comparing with UI field (selector): " + uiField.selector);
                if (uiField.selector != null && uiField.selector.equals("#" + req.id)) {
                    logger.info("  Matched by ID: " + req.id + " with selector " + uiField.selector);
                    addCorrelation(correlatedFields, req, uiField, CorrelatedField.MatchType.ID);
                    matched = true;
                    break;
                }
            }
            if (matched)
                continue;

            // Pass 2: Match by 'name' attribute
            for (UiComponent.Field uiField : allUiFields) {
                logger.info("  Comparing with UI field (name): " + uiField.name);
                if (uiField.name != null && uiField.name.equals(req.id)) {
                    logger.info("  Matched by name: " + req.id + " with name " + uiField.name);
                    addCorrelation(correlatedFields, req, uiField, CorrelatedField.MatchType.NAME);
                    matched = true;
                    break;
                }
            }
            if (matched)
                continue;

            // Pass 3: Match by label text
            for (UiComponent.Field uiField : allUiFields) {
                logger.info("  Comparing with UI field (label): " + uiField.label);
                if (req.label != null && uiField.label != null && req.label.equalsIgnoreCase(uiField.label)) {
                    logger.info("  Matched by label: " + req.label + " with label " + uiField.label);
                    addCorrelation(correlatedFields, req, uiField, CorrelatedField.MatchType.LABEL);
                    matched = true;
                    break;
                }
            }
        }
        return correlatedFields;
    }

    private static void addCorrelation(List<CorrelatedField> list, Srs.Requirement req, UiComponent.Field uiField,
            CorrelatedField.MatchType type) {
        CorrelatedField correlatedField = new CorrelatedField();
        correlatedField.srsRequirement = req;
        correlatedField.uiField = uiField;
        correlatedField.matchType = type;
        list.add(correlatedField);
    }

    private static String generateFeatureFile(Srs srsData, List<CorrelatedField> correlatedData) {
        StringBuilder featureBuilder = new StringBuilder();
        featureBuilder.append("Feature: ").append(srsData.feature).append("\n\n");
        featureBuilder.append("  ").append(srsData.featureDescription).append("\n\n");
        featureBuilder.append("  Scenario: Fill and submit the service provider registration form\n");

        for (CorrelatedField field : correlatedData) {
            featureBuilder.append("    When I fill the '" + field.srsRequirement.label + "' with a valid value\n");
        }

        featureBuilder.append("    And I click the 'Submit' button\n");
        featureBuilder.append("    Then I should see a success message\n");

        return featureBuilder.toString();
    }

    private static String generateStepDefinitions(List<CorrelatedField> correlatedData) {
        StringBuilder stepsBuilder = new StringBuilder();
        stepsBuilder.append("package com.sdp.m1.Steps.Generated;\n\n");
        stepsBuilder.append("import io.cucumber.java.en.When;\n");
        stepsBuilder.append("import io.cucumber.java.en.By;\n");
        stepsBuilder.append("import io.cucumber.java.en.Given;\n");
        stepsBuilder.append("import io.cucumber.java.en.Then;\n");
        stepsBuilder.append("import io.cucumber.java.en.And;\n");
        stepsBuilder.append("import org.openqa.selenium.NoSuchElementException;\n");
        stepsBuilder.append("import org.openqa.selenium.TimeoutException;\n");
        stepsBuilder.append("import org.openqa.selenium.WebElement;\n");
        stepsBuilder.append("import org.openqa.selenium.support.ui.ExpectedConditions;\n");
        stepsBuilder.append("import com.epam.healenium.SelfHealingDriverWait;\n");
        stepsBuilder.append("import com.epam.healenium.SelfHealingDriver;\n");
        stepsBuilder.append("import com.sdp.m1.Utils.TestUtils;\n\n");
        stepsBuilder.append("import com.sdp.m1.Utils.TestConfigs;\n\n");

        stepsBuilder.append("public class GeneratedServiceProviderRegistrationSteps {\n\n");
        stepsBuilder.append("    private final SelfHealingDriver driver;\n\n");
        stepsBuilder.append("    private final SelfHealingDriverWait wait;\n\n");
        stepsBuilder.append(
                "    public GeneratedServiceProviderRegistrationSteps(SelfHealingDriver driver, SelfHealingDriverWait wait) {\n");
        stepsBuilder.append("        this.driver = driver;\n");
        stepsBuilder.append("        this.wait = wait;\n");
        stepsBuilder.append("    }\n\n");

        for (CorrelatedField field : correlatedData) {
            stepsBuilder.append("    @When(\"I fill the '" + field.srsRequirement.label + "' with a valid value\")\n");
            stepsBuilder.append("    public void i_fill_the_")
                    .append(field.srsRequirement.label.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase())
                    .append("_with_a_valid_value() {\n");
            stepsBuilder.append("        driver.findElement(By.cssSelector(\"" + field.uiField.selector
                    + "\")).sendKeys(\"some_value\");\n");
            stepsBuilder.append("    }\n\n");
        }

        stepsBuilder.append("}    \n");
        return stepsBuilder.toString();
    }

    private static void writeFile(String path, String content) throws IOException {
        java.nio.file.Path p = Paths.get(path);
        java.nio.file.Files.createDirectories(p.getParent());
        Files.write(p, content.getBytes());
    }
}
