package com.sdp.m1.Steps;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;
import java.util.Map;
// import java.time.Duration;

import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingDriverWait;

import com.sdp.m1.Extractors.WebPageExtractorJSON;
import com.sdp.m1.Pages.ServiceProviderRegistrationPage;
import com.sdp.m1.Utils.TestConfigs;
import com.sdp.m1.Utils.TestUtils;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ServiceProviderRegistrationSteps {
    private static final Logger logger = Logger.getLogger(ServiceProviderRegistrationSteps.class.getName());
    private final ServiceProviderRegistrationPage registrationPage;
    private final WebPageExtractorJSON webPageExtractor;
    private Map<String, String> testData;

    public ServiceProviderRegistrationSteps() {
        SelfHealingDriver driver = TestUtils.getDriver(TestConfigs.getBrowser());
        SelfHealingDriverWait wait = TestUtils.getWaitDriver(driver);
        this.registrationPage = new ServiceProviderRegistrationPage(driver, wait);
        this.webPageExtractor = new WebPageExtractorJSON(driver, wait);
    }

    @Then("I navigate to the service provider registration page")
    public void i_navigate_to_registration_page() {
        try {
            // The driver is already initialized and logged in from the Background steps.
            // We just need to navigate.
            registrationPage.open();
            logger.info("Navigated to the service provider registration page.");
        } catch (Exception e) {
            logger.severe(String.format("Error navigating to registration page: %s", e.getMessage()));
        }
    }

    @Then("the registration form should be visible")
    public void registration_form_should_be_visible() {
        assertTrue(registrationPage.isFormVisible());
    }

    @When("I enter {string} in the Service Provider ID field")
    public void enter_service_provider_id(String spId) {
        if (spId.equals("random")) {
            spId = TestUtils.generateRandomNumber(8);
        }
        registrationPage.enterServiceProviderId(spId);
    }

    @When("I leave the Service Provider ID field empty")
    public void leave_service_provider_id_empty() {
        registrationPage.enterServiceProviderId("");
    }

    @When("I enter {string} in the Company Name field")
    public void enter_company_name(String companyName) {
        registrationPage.enterCompanyName(companyName);
    }

    @When("I leave the Company Name field empty")
    public void leave_company_name_empty() {
        registrationPage.enterCompanyName("");
    }

    @When("I enter {string} in the Address field")
    public void enter_address(String address) {
        registrationPage.enterAddress(address);
    }

    @When("I enter a string longer than 255 characters in the Address field")
    public void enter_long_address() {
        registrationPage.enterAddress("A".repeat(256));
    }

    @When("I enter {string} in the Description field")
    public void enter_description(String description) {
        registrationPage.enterDescription(description);
    }

    @When("I enter a string longer than 255 characters in the Description field")
    public void enter_long_description() {
        registrationPage.enterDescription("A".repeat(256));
    }

    @When("I enter {string} in the White Listed Users field")
    public void enter_white_listed_users(String users) {
        registrationPage.enterWhiteListedUsers(users);
    }

    @When("I enter {string} in the Black Listed Users field")
    public void enter_black_listed_users(String users) {
        registrationPage.enterBlackListedUsers(users);
    }

    @When("I enter {string} in both White Listed and Black Listed Users fields")
    public void enter_white_and_black_listed_users(String users) {
        registrationPage.enterWhiteListedUsers(users);
        registrationPage.enterBlackListedUsers(users);
    }

    @When("I enter {string} in the Dedicated Alias field")
    public void enter_dedicated_alias(String alias) {
        registrationPage.enterDedicatedAlias(alias);
    }

    @When("I select {string} in the SP users field")
    public void select_sp_users(String user) {
        registrationPage.selectSPUser(user);
    }

    @When("I leave the SP users field empty")
    public void leave_sp_users_empty() {
        registrationPage.clearSPUsers();
    }

    @When("I select {string} in the Marketing Users field")
    public void select_marketing_users(String user) {
        registrationPage.selectMarketingUser(user);
    }

    @When("I leave the Marketing Users field empty")
    public void leave_marketing_users_empty() {
        registrationPage.clearMarketingUsers();
    }

    @When("I select {string} in the Resources field")
    public void select_resources(String resource) {
        registrationPage.selectResource(resource);
    }

    @When("I leave the Resources field empty")
    public void leave_resources_empty() {
        registrationPage.clearResources();
    }

    @When("I fill other required fields with valid data")
    public void fill_other_required_fields() {
        this.testData = registrationPage.fillRequiredFieldsWithValidData();
    }

    @When("I submit the registration form")
    public void submit_registration_form() {
        registrationPage.submitForm();
    }

    @Then("I should see a confirmation dialog")
    public void should_see_confirmation_dialog() {
        assertTrue(registrationPage.isConfirmationDialogVisible());
    }

    @And("I should see the confirmation with the correct data")
    public void i_should_see_the_confirmation_with_the_correct_data() {
        registrationPage.validateConfirmationData(this.testData);
    }

    @Then("I should see error {string}")
    public void should_see_error_message(String errorMsg) {
        assertTrue(registrationPage.isErrorMessageVisible(errorMsg));
    }

    @And("I extract the page components")
    public void i_extract_the_page_components() throws Exception {
        logger.info("Starting page component extraction...");
        String currentUrl = registrationPage.getCurrentUrl();
        String fileName = webPageExtractor.getFileName(currentUrl);
        webPageExtractor.runExtractor(fileName);
        logger.info("Page component extraction finished.");
    }

    @When("I confirm the registration")
    public void confirm_registration() {
        // Code to confirm the registration
        registrationPage.confirmRegistration();
    }

    @Then("I should see a success message")
    public void should_see_success_message() {
        assertTrue(registrationPage.isSuccessMessageVisible());
    }

    public static void main(String[] args) {
        // Main method for testing or running the application
    }

    @Given("a service provider with ID {string} already exists")
    public void a_service_provider_with_id_already_exists(String serviceProviderId) {
        // Code to check if the service provider exists
        // assertTrue(registrationPage.doesServiceProviderExist(serviceProviderId));
    }
}
