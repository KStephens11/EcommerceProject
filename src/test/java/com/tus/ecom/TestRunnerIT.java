package com.tus.ecom;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/features/cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.tus.ecom")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-report")
public class TestRunnerIT {
}