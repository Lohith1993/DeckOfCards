package com.deckofcards.tests;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.api.exceptions.FrameworkException;
import com.api.utils.Reporter;

import io.restassured.response.Response;

public class BaseTest {

	public static Map<String, Object> dependentTestData = Collections.synchronizedMap(new LinkedHashMap<>());

	@BeforeSuite
	public void initialize() {
		Reporter.startReport();
	}

	@AfterSuite
	public void tearDown() {
		Reporter.flushTests();
	}

	/**
	 * Retrieves value from the json response
	 * 
	 * @param response
	 * @param path
	 * @return
	 */
	public String getValueFromJsonResponse(Response response, String path) {
		return response.jsonPath().getString(path);
	}

	/**
	 * Stores Dependent TestData.
	 * 
	 * @param testName
	 * @param object
	 */
	public void setdependentTestData(String testName, Object object) {
		dependentTestData.put(testName, object);
	}

	/**
	 * Returns dependent TestData.
	 * 
	 * @param testName
	 * @return
	 */
	public Object getdependentTestData(String key) {
		Object testData = null;
		testData = dependentTestData.get(key);
		if (testData != null) {
			return testData;
		} else {
			throw new FrameworkException(
					"Looks like the dependent test case is not executed.Please check the dependent test case: " + key);
		}
	}
}
