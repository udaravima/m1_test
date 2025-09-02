package com.sdp.m1.Steps.Generated;

import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.epam.healenium.SelfHealingDriverWait;
import com.epam.healenium.SelfHealingDriver;
import com.sdp.m1.Utils.TestUtils;
import com.sdp.m1.Utils.TestConfigs;

public class GeneratedServiceProviderRegistrationSteps {

    private final SelfHealingDriver driver;
    private final SelfHealingDriverWait wait;

    public GeneratedServiceProviderRegistrationSteps(SelfHealingDriver driver, SelfHealingDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @When("I fill the 'Service Provider ID' with a valid value")
    public void i_fill_the_service_provider_id_with_a_valid_value() {
        driver.findElement(By.cssSelector("#spId")).sendKeys("some_value");
    }

    @When("I fill the 'Company name' with a valid value")
    public void i_fill_the_company_name_with_a_valid_value() {
        driver.findElement(By.cssSelector("#companyName")).sendKeys("some_value");
    }

    @When("I fill the 'Address' with a valid value")
    public void i_fill_the_address_with_a_valid_value() {
        driver.findElement(By.cssSelector("#address")).sendKeys("some_value");
    }

    @When("I fill the 'SP users' with a valid value")
    public void i_fill_the_sp_users_with_a_valid_value() {
        driver.findElement(By.cssSelector("#userNames")).sendKeys("some_value");
    }

    @When("I fill the 'Resources' with a valid value")
    public void i_fill_the_resources_with_a_valid_value() {
        driver.findElement(By.cssSelector("#allowedNcses")).sendKeys("some_value");
    }

}
