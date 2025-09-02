package com.sdp.m1.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingDriverWait;

import java.util.logging.Logger;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import com.sdp.m1.Utils.TestConfigs;
import com.sdp.m1.Utils.TestUtils;
import com.sdp.m1.Extractors.WebPageExtractorJSON;

import org.openqa.selenium.support.ui.ExpectedConditions;

public class ServiceProviderRegistrationPage {
    private static final Logger logger = Logger.getLogger(ServiceProviderRegistrationPage.class.getName());
    private final SelfHealingDriver driver;
    private final SelfHealingDriverWait wait;
    private final WebPageExtractorJSON extractor;

    // Locators
    private final By form = By.id("serviceProviderImpl");
    private final By spId = By.id("spId");
    private final By companyName = By.id("companyName");
    private final By address = By.id("address");
    private final By description = By.id("description");
    private final By whiteListedUsers = By.id("whiteListedUsers");
    private final By blackListedUsers = By.id("blackListedUsers");
    private final By dedicatedAliases = By.id("dedicatedAliases");
    private final By spUsers = By.id("userNames");
    private final By marketingUsers = By.id("allowedMarketingUsers");
    private final By resources = By.id("allowedNcses");
    private final By submitButton = By.xpath("//*[@id='serviceProviderImpl']//input[@type='submit']");
    private final By confirmButton = By.id("submitApplication");
    private final By successMsg = By.cssSelector("#serviceProviderImpl > div > div.success-msg");
    private final By errorMsg = By.id("status");

    // Locators for confirmation page values without IDs
    private final By spUsersConfirm = By
            .xpath("//label[text()='SP users']/following-sibling::label[@class='confirm-text']");
    private final By marketingUsersConfirm = By
            .xpath("//label[text()='Marketing Users']/following-sibling::label[@class='confirm-text']");
    private final By resourcesConfirm = By
            .xpath("//label[text()='Resources']/following-sibling::label[@class='ncs_confirm-text']");

    public ServiceProviderRegistrationPage(SelfHealingDriver driver, SelfHealingDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.extractor = new WebPageExtractorJSON(driver, wait);
    }

    public void open() {
        String registrationUrl = TestConfigs.getBaseUrl() + "/registerServiceProvider.html";
        logger.info(String.format("Cookies before navigation: %s", driver.manage().getCookies().toString()));
        driver.navigate().to(String.format("%s/provisioning", TestConfigs.getBaseUrl()));
        driver.navigate().to(registrationUrl);
        driver.navigate().to(registrationUrl); // Debugging
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("serviceProviderImpl")));
        logger.info("Navigated to Service Provider Registration page.");
        String filename;
        try {
            filename = extractor.getFileName(registrationUrl);
        } catch (Exception e) {
            logger.severe(String.format("Error occurred while getting file name: %s", e.getMessage()));
            return;
        }
        extractor.runExtractor(filename);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isFormVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(form));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void enterServiceProviderId(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(spId));
        el.clear();
        el.sendKeys(value);
    }

    public void enterCompanyName(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(companyName));
        el.clear();
        el.sendKeys(value);
    }

    public void enterAddress(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(address));
        el.clear();
        el.sendKeys(value);
    }

    public void enterDescription(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(description));
        el.clear();
        el.sendKeys(value);
    }

    public void enterWhiteListedUsers(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(whiteListedUsers));
        el.clear();
        el.sendKeys(value);
    }

    public void enterBlackListedUsers(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(blackListedUsers));
        el.clear();
        el.sendKeys(value);
    }

    public void enterDedicatedAlias(String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(dedicatedAliases));
        el.clear();
        el.sendKeys(value);
    }

    public void selectSPUser(String user) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(spUsers));
        Select select = new Select(selectElement);
        select.selectByVisibleText(user);
    }

    public void clearSPUsers() {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(spUsers));
        Select select = new Select(selectElement);
        select.deselectAll();
    }

    public void selectMarketingUser(String user) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(marketingUsers));
        Select select = new Select(selectElement);
        select.selectByVisibleText(user);
    }

    public void clearMarketingUsers() {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(marketingUsers));
        Select select = new Select(selectElement);
        select.deselectAll();
    }

    public void selectResource(String resource) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(resources));
        Select select = new Select(selectElement);
        select.selectByVisibleText(resource);
    }

    public void clearResources() {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(resources));
        Select select = new Select(selectElement);
        select.deselectAll();
    }

    public String getServiceProviderId() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(spId)).getAttribute("value");
    }

    public String getCompanyName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(companyName)).getAttribute("value");
    }

    public String getAddress() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(address)).getAttribute("value");
    }

    public String getDescription() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(description)).getAttribute("value");
    }

    public String getWhiteListedUsers() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(whiteListedUsers)).getAttribute("value");
    }

    public String getBlackListedUsers() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(blackListedUsers)).getAttribute("value");
    }

    public String getDedicatedAlias() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(dedicatedAliases)).getAttribute("value");
    }

    public static class FormDataBuilder {
        private Map<String, String> data = new LinkedHashMap<>();

        public FormDataBuilder() {
            // Initialize with default valid data
            data.put("Service Provider ID", TestUtils.generateRandomNumber(8));
            data.put("Company name", TestUtils.generateRandomString(10));
            data.put("Address", "123 Main Street");
            data.put("Description", "A valid description");
            data.put("White Listed Users", "12345678,123456789012345");
            data.put("Black Listed Users", "");
            data.put("Dedicated Alias", "");
            data.put("SP users", "acrsp1");
            data.put("Marketing Users", "ThiostMktg");
            data.put("Resources", "SMS");
        }

        public FormDataBuilder withServiceProviderId(String spId) {
            data.put("Service Provider ID", spId);
            return this;
        }

        public FormDataBuilder withoutServiceProviderId() {
            data.remove("Service Provider ID");
            return this;
        }

        public FormDataBuilder withCompanyName(String companyName) {
            data.put("Company name", companyName);
            return this;
        }

        public FormDataBuilder withoutCompanyName() {
            data.remove("Company name");
            return this;
        }

        // Add with and without methods for other fields here

        public Map<String, String> build() {
            return data;
        }
    }

    public void fillForm(Map<String, String> data) {
        data.forEach((fieldName, value) -> {
            switch (fieldName) {
                case "Service Provider ID":
                    enterServiceProviderId(value);
                    break;
                case "Company name":
                    enterCompanyName(value);
                    break;
                case "Address":
                    enterAddress(value);
                    break;
                case "Description":
                    enterDescription(value);
                    break;
                case "White Listed Users":
                    enterWhiteListedUsers(value);
                    break;
                case "Black Listed Users":
                    enterBlackListedUsers(value);
                    break;
                case "Dedicated Alias":
                    enterDedicatedAlias(value);
                    break;
                case "SP users":
                    selectSPUser(value);
                    break;
                case "Marketing Users":
                    selectMarketingUser(value);
                    break;
                case "Resources":
                    selectResource(value);
                    break;
            }
        });
    }

    @Deprecated
    public Map<String, String> fillRequiredFieldsWithValidData() {
        Map<String, String> data = new FormDataBuilder().build();
        fillForm(data);
        return data;
    }

    public static FormDataBuilder getNewWithValidData() {
        return new FormDataBuilder();
    }

    public void submitForm() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        button.click();
        logger.info("Registration form submitted.");
    }

    public boolean isSuccessMessageVisible() {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg));
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorMessageVisible(String expectedMsg) {
        try {
            By errorLocator = By.xpath("//div[@class='error']/span[contains(text(), '" + expectedMsg + "')] ");
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isConfirmationDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getConfirmationText(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el.getText();
        } catch (Exception e) {
            logger.warning(String.format("Could not find confirmation text for locator: %s. Error: %s", locator,
                    e.getMessage()));
            return null;
        }
    }

    public String getConfirmationValue(String fieldName) {
        logger.info(String.format("Getting confirmation value for field: %s", fieldName));
        return switch (fieldName.toLowerCase().replaceAll("\s+", "")) {
            case "serviceproviderid" -> getConfirmationText(spId);
            case "companyname" -> getConfirmationText(companyName);
            case "address" -> getConfirmationText(address);
            case "description" -> getConfirmationText(description);
            case "whitelistedusers" -> getConfirmationText(whiteListedUsers);
            case "blacklistedusers" -> getConfirmationText(blackListedUsers);
            case "dedicatedalias" -> getConfirmationText(dedicatedAliases);
            case "spusers" -> getConfirmationText(spUsersConfirm);
            case "marketingusers" -> getConfirmationText(marketingUsersConfirm);
            case "resources" -> getConfirmationText(resourcesConfirm);
            default -> {
                logger.warning(String.format("Unknown field name for confirmation: %s", fieldName));
                yield null;
            }
        };
    }

    public void validateConfirmationData(Map<String, String> expectedData) {
        logger.info("Validating data on confirmation page...");
        expectedData.forEach((fieldName, expectedValue) -> {
            String actualValue = getConfirmationValue(fieldName);

            if (actualValue == null) {
                actualValue = ""; // Treat null as empty string for comparison
            }

            if (!expectedValue.equals(actualValue)) {
                String errMsg = String.format("Validation failed for field '%s'. Expected: '%s', but got: '%s'",
                        fieldName, expectedValue, actualValue);
                logger.severe(errMsg);
                throw new AssertionError(errMsg);
            }
            logger.info(String.format("Field '%s' validated successfully. Value: '%s'", fieldName, actualValue));
        });
        logger.info("All fields on confirmation page validated successfully.");
    }

    public void confirmRegistration() {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(confirmButton));
            button.click();
            logger.info("Clicked the 'Confirm' button on the registration confirmation page.");
        } catch (Exception e) {
            logger.severe(String.format("Could not find or click the 'Confirm' button: %s", e.getMessage()));
            throw new RuntimeException("Failed to confirm registration", e);
        }
    }
}