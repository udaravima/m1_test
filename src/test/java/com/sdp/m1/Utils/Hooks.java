package com.sdp.m1.Utils;

import com.sdp.m1.Runner.TestConfigs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.logging.Logger;

/**
 * This class contains global setup and teardown logic for all scenarios.
 * It uses Cucumber's hook annotations to manage the browser lifecycle
 * and handle test failures centrally.
 */
public class Hooks {

    private static final Logger logger = Logger.getLogger(Hooks.class.getName());

    @Before
    public void setUp() {
        // The driver will be created lazily on its first use in a step.
        logger.info("Starting new scenario.");
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            if (TestConfigs.isScreenshotOnFailure()) {
                logger.warning(String.format("Scenario '%s' FAILED. Taking screenshot.", scenario.getName()));
                TestUtils.takeScreenshot(scenario.getName() + "_failure");
            }
        } else {
            if (TestConfigs.isScreenshotOnSuccess()) {
                logger.info(String.format("Scenario '%s' PASSED. Taking screenshot.", scenario.getName()));
                TestUtils.takeScreenshot(scenario.getName() + "_success");
            }
        }

        // Always close the driver at the end of the scenario
        TestUtils.removeDriver();
    }
}