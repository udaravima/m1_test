package com.sdp.m1.Runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/Features", glue = {
                "com.sdp.m1.Steps" }, tags = "@Registration", // Default tags to run from IDE. Can be
                                                                        // overridden by Maven.
                monochrome = true, plugin = { //
                                "pretty",
                                "html:target/HtmlReports/login.html",
                                "json:target/JsonReports/login.json",
                                "junit:target/JunitReports/login.xml"
                })
public class TestRunner {
        // This class should be empty, it's just a runner
}
