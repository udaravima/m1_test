package com.sdp.m1.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.Assert;
import com.epam.healenium.SelfHealingDriver;
import java.time.Duration;
import java.util.logging.Logger;

public class m1DashboardPage {
    private static final Logger logger = Logger.getLogger(m1DashboardPage.class.getName());
    private SelfHealingDriver driver;
    private WebDriverWait wait;

    // Locators
    private By dashboardMain = By.id("dashboard-main");
    private By profileMenu = By.xpath("//nav//a[text()='Profile']");
    private By settingsMenu = By.xpath("//nav//a[text()='Settings']");
    private By widgets = By.cssSelector(".dashboard-widget");
    private By logoutButton = By.id("logout-button");

    public m1DashboardPage(SelfHealingDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void waitForDashboardLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardMain));
        logger.info("Dashboard loaded");
    }

    public void verifyDashboardVisible() {
        WebElement dashboard = driver.findElement(dashboardMain);
        Assert.assertTrue("Dashboard is not visible", dashboard.isDisplayed());
    }

    public void verifyPageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertEquals("Page title mismatch", expectedTitle, actualTitle);
    }

    public void clickMenu(String menuName) {
        By menuLocator = By.xpath("//nav//a[text()='" + menuName + "']");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuLocator));
        menu.click();
        logger.info("Clicked menu: " + menuName);
    }

    public void verifyProfilePage() {
        wait.until(ExpectedConditions.urlContains("/profile"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/profile"));
    }

    public void verifySettingsPage() {
        wait.until(ExpectedConditions.urlContains("/settings"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/settings"));
    }

    public void verifyAllWidgetsVisible() {
        for (WebElement widget : driver.findElements(widgets)) {
            Assert.assertTrue("Widget not visible", widget.isDisplayed());
        }
    }

    public void verifyWidgetsHaveData() {
        for (WebElement widget : driver.findElements(widgets)) {
            String data = widget.getText();
            Assert.assertFalse("Widget has no data", data == null || data.trim().isEmpty());
        }
    }

    public void clickLogout() {
        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        logoutBtn.click();
        logger.info("Logout button clicked");
    }

    public void verifyRedirectToLogin() {
        wait.until(ExpectedConditions.urlContains("/login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}
