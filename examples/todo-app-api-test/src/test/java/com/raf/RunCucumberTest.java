package com.raf;

import io.cucumber.testng.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.Predicate;

// See : https://github.com/cucumber/cucumber-jvm/pull/1863
@CucumberOptions(
        strict = true,
        plugin = {"pretty", "json:target/results/json/result.json", "timeline:target/results/timeline/", "html:target/results/html/"},
        features = "src/test/resources/features",
        tags = {"not @wip"},
        glue = {"com.raf"},
        monochrome = false)

@Log4j2
public class RunCucumberTest {

    private static final String UI_CONFIG = "config/uiconfig.ini";
    private static final String ENV = System.getProperty("env", "qa").toLowerCase();

    private static final Predicate<Pickle> isSerial = p -> p.getTags().contains("@serial") || p.getTags().contains("@Serial");

    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    // For parallel run
    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios - Parallel", dataProvider = "parallelScenarios")
    public void runParallelScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) throws Throwable {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    // For serial run
    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios - Serial", dataProvider = "serialScenarios")
    public void runSerialScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) throws Throwable {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    @DataProvider(parallel = true)
    public Object[][] parallelScenarios() {
        if (testNGCucumberRunner == null) {
            return new Object[0][0];
        }
        return filter(testNGCucumberRunner.provideScenarios(), isSerial.negate());
    }

    @DataProvider
    public Object[][] serialScenarios() {
        if (testNGCucumberRunner == null) {
            return new Object[0][0];
        }

        return filter(testNGCucumberRunner.provideScenarios(), isSerial);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (testNGCucumberRunner == null) {
            return;
        }
        testNGCucumberRunner.finish();
    }

    private Object[][] filter(Object[][] scenarios, Predicate<Pickle> accept) {
        return Arrays.stream(scenarios).filter(objects -> {
            PickleWrapper candidate = (PickleWrapper) objects[0];
            return accept.test(candidate.getPickle());
        }).toArray(Object[][]::new);
    }
}