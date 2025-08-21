package com.sdp.m1.Steps;

import com.sdp.m1.Pages.m1DashboardPage;
import com.sdp.m1.Pages.m1LoginPage;
import com.epam.healenium.SelfHealingDriver;
import io.cucumber.java.en.*;
import org.junit.Assert;

public class DashboardSteps {
	private SelfHealingDriver driver;
	private m1DashboardPage dashboardPage;

	public DashboardSteps() {
		// Assume driver is initialized and available from LoginSteps or a shared context
		this.driver = StepContext.getDriver();
		this.dashboardPage = new m1DashboardPage(driver);
	}

	@Given("I am on the dashboard page")
	public void i_am_on_the_dashboard_page() {
		dashboardPage.waitForDashboardLoad();
	}

	@Then("the dashboard should be visible")
	public void the_dashboard_should_be_visible() {
		dashboardPage.verifyDashboardVisible();
	}

	@Then("the page title should be {string}")
	public void the_page_title_should_be(String title) {
		dashboardPage.verifyPageTitle(title);
	}

	@When("I click the {string} menu")
	public void i_click_the_menu(String menuName) {
		dashboardPage.clickMenu(menuName);
	}

	@Then("the profile page should be displayed")
	public void the_profile_page_should_be_displayed() {
		dashboardPage.verifyProfilePage();
	}

	@Then("the settings page should be displayed")
	public void the_settings_page_should_be_displayed() {
		dashboardPage.verifySettingsPage();
	}

	@Then("all dashboard widgets should be visible")
	public void all_dashboard_widgets_should_be_visible() {
		dashboardPage.verifyAllWidgetsVisible();
	}

	@Then("each widget should have data")
	public void each_widget_should_have_data() {
		dashboardPage.verifyWidgetsHaveData();
	}

	@When("I click the logout button")
	public void i_click_the_logout_button() {
		dashboardPage.clickLogout();
	}

	@Then("I should be redirected to the login page")
	public void i_should_be_redirected_to_the_login_page() {
		dashboardPage.verifyRedirectToLogin();
	}
}

// Utility class to share driver between steps (replace with your actual context management)
class StepContext {
	private static SelfHealingDriver driver;
	public static void setDriver(SelfHealingDriver d) { driver = d; }
	public static SelfHealingDriver getDriver() { return driver; }
}
