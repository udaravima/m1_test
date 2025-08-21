package com.sdp.m1.Pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.epam.healenium.SelfHealingDriver;

import java.util.logging.Logger;

import org.openqa.selenium.support.ui.WebDriverWait;

public class ServiceProviderRegistrationPage {
    private static final Logger logger = Logger.getLogger(ServiceProviderRegistrationPage.class.getName());
    private SelfHealingDriver driver;
    private WebDriverWait wait;

    // Locators
    private By form = By.id("serviceProviderImpl");
    private By spId = By.id("spId");
    private By companyName = By.id("companyName");
    private By address = By.id("address");
    private By description = By.id("description");
    private By whiteListedUsers = By.id("whiteListedUsers");
    private By blackListedUsers = By.id("blackListedUsers");
    private By dedicatedAliases = By.id("dedicatedAliases");
    private By spUsers = By.id("userNames");
    private By marketingUsers = By.id("allowedMarketingUsers");
    private By resources = By.id("allowedNcses");
    private By submitButton = By.xpath("//*[@id='serviceProviderImpl']//input[@type='submit']");
    private By successMsg = By.id("msg");
    private By errorMsg = By.id("status");

    public ServiceProviderRegistrationPage(SelfHealingDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("https://m1-impl.hsenidmobile.com/provisioning/registerServiceProvider.html");
    }

    public boolean isFormVisible() {
        return driver.findElement(form).isDisplayed();
    }

    public void enterServiceProviderId(String value) {
        WebElement el = driver.findElement(spId);
        el.clear();
        el.sendKeys(value);
    }

    public void enterCompanyName(String value) {
        WebElement el = driver.findElement(companyName);
        el.clear();
        el.sendKeys(value);
    }

    public void enterAddress(String value) {
        WebElement el = driver.findElement(address);
        el.clear();
        el.sendKeys(value);
    }

    public void enterDescription(String value) {
        WebElement el = driver.findElement(description);
        el.clear();
        el.sendKeys(value);
    }

    public void enterWhiteListedUsers(String value) {
        WebElement el = driver.findElement(whiteListedUsers);
        el.clear();
        el.sendKeys(value);
    }

    public void enterBlackListedUsers(String value) {
        WebElement el = driver.findElement(blackListedUsers);
        el.clear();
        el.sendKeys(value);
    }

    public void enterDedicatedAlias(String value) {
        WebElement el = driver.findElement(dedicatedAliases);
        el.clear();
        el.sendKeys(value);
    }

    public void selectSPUser(String user) {
        Select select = new Select(driver.findElement(spUsers));
        select.selectByVisibleText(user);
    }

    public void clearSPUsers() {
        Select select = new Select(driver.findElement(spUsers));
        select.deselectAll();
    }

    public void selectMarketingUser(String user) {
        Select select = new Select(driver.findElement(marketingUsers));
        select.selectByVisibleText(user);
    }

    public void clearMarketingUsers() {
        Select select = new Select(driver.findElement(marketingUsers));
        select.deselectAll();
    }

    public void selectResource(String resource) {
        Select select = new Select(driver.findElement(resources));
        select.selectByVisibleText(resource);
    }

    public void clearResources() {
        Select select = new Select(driver.findElement(resources));
        select.deselectAll();
    }

    public void fillRequiredFieldsWithValidData() {
        enterCompanyName("Valid Company");
        enterAddress("123 Main Street");
        enterDescription("A valid description");
        enterWhiteListedUsers("12345678,123456789012345");
        selectSPUser("acrsp1");
        selectMarketingUser("ThiostMktg");
        selectResource("SMS");
    }

    public void submitForm() {
        driver.findElement(submitButton).click();
    }

    public boolean isSuccessMessageVisible() {
        try {
            return driver.findElement(successMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorMessageVisible(String expectedMsg) {
        try {
            WebElement el = driver.findElement(errorMsg);
            return el.isDisplayed() && el.getText().contains(expectedMsg);
        } catch (Exception e) {
            return false;
        }
    }
}
