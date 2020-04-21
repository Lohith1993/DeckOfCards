package com.deckofcards.tests;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.api.exceptions.FrameworkException;
import com.api.pojo.TestData;
import com.api.services.factory.APITest;
import com.api.utils.Reporter;
import com.aventstack.extentreports.Status;

import io.restassured.response.Response;

public class DeckOfCardTest extends BaseTest {

	APITest apiTest;

	@BeforeMethod
	public void addLoggers(Method method) {
		apiTest = new APITest();
		Reporter.createExtentTest(method.getName());
	}

	@Test(description = "Verify Creation of new deck of cards")
	public void tc01_createNewDeckOfCards() {
		try {
			TestData testData = new TestData();
			testData.setBaseURL("http://deckofcardsapi.com/");
			testData.setRequestURL("/api/deck/new/");
			testData.setRequestMethod("GET");
			testData.setContentType("application/json");

			Response response = apiTest.invokeService(testData);
			String deckId = getValueFromJsonResponse(response, "deck_id");
			setdependentTestData("DeckId", deckId);
			testData.setExpectedResponseCode("200");

			testData.addExpectedResponseKeys("success", "true");

			apiTest.validateResponse(response, testData);

		} catch (Exception e) {
			Reporter.logMessage(Status.FAIL, "Failed to create new deck of cards </br>" + e);
			throw new FrameworkException("Failed to create new deck of cards", e);
		}
	}
	
	@Test(dependsOnMethods = "tc01_createNewDeckOfCards", description = "Verify draw card from of deck of cards")
	public void tc02_drawCardFromDeck() {
		try {
			String generatedDeckId = (String) getdependentTestData("DeckId");
			TestData testData = new TestData();
			testData.setBaseURL("http://deckofcardsapi.com/");
			testData.setRequestURL("api/deck/{deck_id}/draw/");
			testData.setRequestMethod("GET");
			testData.setContentType("application/json");
			testData.setRequestParameters("deck_id=" + generatedDeckId);

			Response response = apiTest.invokeService(testData);
			String deckId = getValueFromJsonResponse(response, "deck_id");
			Reporter.logMessage(Status.INFO, "DeckId : " + deckId);
			testData.setExpectedResponseCode("200");

			testData.addExpectedResponseKeys("success", "true");
			testData.addExpectedResponseKeys("deck_id", generatedDeckId);

			apiTest.validateResponse(response, testData);

		} catch (Exception e) {
			Reporter.logMessage(Status.FAIL, "Failed to create new deck of cards </br>" + e);
			throw new FrameworkException("Failed to create new deck of cards", e);
		}
	}
	
	@Test(description = "Verify Creation of jokers")
	public void tc03_addJokers() {
		try {
			TestData testData = new TestData();
			testData.setBaseURL("http://deckofcardsapi.com/");
			testData.setRequestURL("/api/deck/new");
			testData.setRequestMethod("GET");
			testData.setContentType("application/json");
            testData.setUrlParameters("jokers_enabled=true");
			
			Response response = apiTest.invokeService(testData);
			String deckId = getValueFromJsonResponse(response, "deck_id");
			setdependentTestData("DeckId", deckId);
			testData.setExpectedResponseCode("200");

			testData.addExpectedResponseKeys("success", "true");

			apiTest.validateResponse(response, testData);

		} catch (Exception e) {
			Reporter.logMessage(Status.FAIL, "Failed to add jokers </br>" + e);
			throw new FrameworkException("Failed to add jokers", e);
		}
	}
	
	@Test(dependsOnMethods = "tc03_addJokers", description = "Verify added jokers")
	public void tc04_verifyJokers() {
		try {
			String generatedDeckId = (String) getdependentTestData("DeckId");
			TestData testData = new TestData();
			testData.setBaseURL("http://deckofcardsapi.com/");
			testData.setRequestURL("api/deck/{deck_id}/draw/");
			testData.setRequestMethod("GET");
			testData.setContentType("application/json");
			testData.setRequestParameters("deck_id=" + generatedDeckId);

			Response response = apiTest.invokeService(testData);
			String deckId = getValueFromJsonResponse(response, "deck_id");
			Reporter.logMessage(Status.INFO, "DeckId : " + deckId);
			testData.setExpectedResponseCode("200");

			
			testData.addExpectedResponseKeys("success", "true");
			testData.addExpectedResponseKeys("deck_id", generatedDeckId);
			testData.addExpectedResponseKeys("cards.value", "2");
			testData.addExpectedResponseKeys("cards.suit", "SPADES");
			apiTest.validateResponse(response, testData);

		} catch (Exception e) {
			Reporter.logMessage(Status.FAIL, "Failed to create new deck of cards </br>" + e);
			throw new FrameworkException("Failed to create new deck of cards", e);
		}
	}
}