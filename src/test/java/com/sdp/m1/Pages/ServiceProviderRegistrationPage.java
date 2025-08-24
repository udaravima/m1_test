package com.sdp.m1.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingDriverWait;

import java.util.logging.Logger;

import com.sdp.m1.Utils.TestConfigs;

import org.openqa.selenium.support.ui.ExpectedConditions;

public class ServiceProviderRegistrationPage {
    private static final Logger logger = Logger.getLogger(ServiceProviderRegistrationPage.class.getName());
    private final SelfHealingDriver driver;
    private final SelfHealingDriverWait wait;

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
    private final By successMsg = By.id("msg");
    private final By errorMsg = By.id("status");

    public ServiceProviderRegistrationPage(SelfHealingDriver driver, SelfHealingDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void open() {
        String registrationUrl = TestConfigs.getBaseUrl() + "/provisioning/registerServiceProvider.html";
        logger.info(String.format("Cookies before navigation: %s", driver.manage().getCookies().toString()));
        driver.navigate().to(String.format("%s/provisioning", TestConfigs.getBaseUrl()));
        driver.navigate().to(registrationUrl);
        driver.navigate().to(registrationUrl); // Debugging
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("serviceProviderImpl")));
        logger.info("Navigated to Service Provider Registration page.");
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

    public void fillRequiredFieldsWithValidData() {
        logger.info("Filling required fields with valid data...");
        enterCompanyName("Valid Company");
        enterAddress("123 Main Street");
        enterDescription("A valid description");
        enterWhiteListedUsers("12345678,123456789012345");
        selectSPUser("acrsp1");
        selectMarketingUser("ThiostMktg");
        selectResource("SMS");
        logger.info("Finished filling required fields.");
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
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg));
            return el.isDisplayed() && el.getText().contains(expectedMsg);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isConfirmationDialogVisible() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void confirmRegistration() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            logger.info("Confirmed the registration alert.");
        } catch (Exception e) {
            logger.warning("No alert present to confirm.");
        }
    }
}
