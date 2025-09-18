
package com.example.runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "src/test/resources/features/login.feature"
    },
    glue = {"com.example.steps"},
    plugin = {
        "pretty",
        "json:target/JSONReport/report.json"
        // If you want Extent/Timeline/Rerun, add dependencies and then uncomment:
        // ,"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        // ,"timeline:test-output-thread/"
        // ,"rerun:test-output-excel/rerun.txt"
    }
)
public class MyTestRunner {
}
