// Proposed new file: src/test/java/com/sdp/m1/Generator/TestGenerator.java

package com.sdp.m1.Generator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdp.m1.Extractors.WebPageExtractorJSON;
import com.sdp.m1.Generator.Model.CorrelatedField;
import com.sdp.m1.Generator.Model.Srs;
import com.sdp.m1.Generator.Model.UiComponent;
import com.sdp.m1.Utils.TestConfigs;
import com.sdp.m1.Utils.TestUtils;
import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.Map;
import java.util.List;

public class TestGenerator {
    private static final Logger logger = Logger.getLogger(TestGenerator.class.getName());
    private static final Boolean LOGIN_PAGE = true;

    public static void main(String[] args) throws Exception {
        // 1. Configuration
        String srsPath = "src/test/java/com/sdp/m1/Generator/srs_registration_form.json"; // Path to your detailed SRS JSON
        String targetUrl = TestConfigs.getBaseUrl() + "/provisioning/registerServiceProvider.html";
        String featureOutputPath = "src/test/resources/Features/Generated_SP_Registration.feature";

        logger.info("Starting test generation process...");

        // 2. Initialize Browser and Extractor
        SelfHealingDriver driver = TestUtils.getDriver(TestConfigs.getBrowser());
        SelfHealingDriverWait wait = TestUtils.getWaitDriver(driver);

        // Perform login to access the target page
        // (This would use your existing m1LoginPage and login steps logic)
        if (!LOGIN_PAGE) {
            // login(driver, wait);
        }

        driver.get(targetUrl);
        WebPageExtractorJSON extractor = new WebPageExtractorJSON(driver, wait);

        // 3. Load and Parse Inputs
        String srsJsonContent = new String(Files.readAllBytes(Paths.get(srsPath)));
        String pageComponentsJson = extractor.extractComponentsAsJsonString();

        // Parse JSON into our POJO models for type-safe access
        Gson gson = new Gson();
        Srs srsData = gson.fromJson(srsJsonContent, Srs.class);
        List<UiComponent> componentData = gson.fromJson(pageComponentsJson, new TypeToken<List<UiComponent>>(){}.getType());

        // 4. Correlate SRS with UI Components (The "AI" part)
        List<CorrelatedField> correlatedData = correlate(srsData, componentData);
        logger.info(String.format("Correlation complete. Found %d mappable fields.", correlatedData.size()));
        correlatedData.forEach(field -> logger.info(String.format("Mapped SRS field '%s' to UI element '%s' via %s", field.srsRequirement.label, field.uiField.selector, field.matchType)));

        // 5. Generate Test Artifacts
        // String featureFileContent = generateFeatureFile(correlatedData);
        // writeFile(featureOutputPath, featureFileContent);
        // logger.info("Generated feature file: " + featureOutputPath);

        // String pageObjectContent = generatePageObject(correlatedData);
        // ... write file ...

        // String stepDefsContent = generateStepDefinitions(correlatedData);
        // ... write file ...

        driver.quit();
        logger.info("Test generation finished successfully.");
    }

    /**
     * Maps requirements from the SRS to actual UI components found on the page.
     *
     * @param srsData The parsed SRS data.
     * @param uiComponents The list of UI components extracted from the page.
     * @return A list of successfully correlated fields.
     */
    private static List<CorrelatedField> correlate(Srs srsData, List<UiComponent> uiComponents) {
        List<CorrelatedField> correlatedFields = new ArrayList<>();
        List<UiComponent.Field> allUiFields = new ArrayList<>();
        uiComponents.forEach(component -> allUiFields.addAll(component.fields));

        for (Srs.Requirement req : srsData.requirements) {
            boolean matched = false;

            // Pass 1: Match by ID (highest confidence)
            for (UiComponent.Field uiField : allUiFields) {
                // The extractor puts the ID in the selector as '#id'
                if (uiField.selector.equals("#" + req.id)) {
                    addCorrelation(correlatedFields, req, uiField, CorrelatedField.MatchType.ID);
                    matched = true;
                    break;
                }
            }
            if (matched) continue;

            // Pass 2: Match by 'name' attribute
            for (UiComponent.Field uiField : allUiFields) {
                if (req.id.equals(uiField.name)) {
                    addCorrelation(correlatedFields, req, uiField, CorrelatedField.MatchType.NAME);
                    matched = true;
                    break;
                }
            }
            if (matched) continue;

            // Pass 3: Match by label text (medium confidence)
            for (UiComponent.Field uiField : allUiFields) {
                if (req.label.equalsIgnoreCase(uiField.label)) {
                    addCorrelation(correlatedFields, req, uiField, CorrelatedField.MatchType.LABEL);
                    matched = true;
                    break;
                }
            }
        }
        return correlatedFields;
    }

    private static void addCorrelation(List<CorrelatedField> list, Srs.Requirement req, UiComponent.Field uiField, CorrelatedField.MatchType type) {
        CorrelatedField correlatedField = new CorrelatedField();
        correlatedField.srsRequirement = req;
        correlatedField.uiField = uiField;
        correlatedField.matchType = type;
        list.add(correlatedField);
    }

    // private static String generateFeatureFile(Map<String, Object> data) { ... }
    // private static void writeFile(String path, String content) { ... }
}
