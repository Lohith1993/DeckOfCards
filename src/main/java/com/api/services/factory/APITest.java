package com.api.services.factory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.api.exceptions.FrameworkException;
import com.api.pojo.TestData;
import com.api.utils.APIUtil;
import com.api.utils.Reporter;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Class for initializing request parameter, Callig the REST Services and for
 * validating the Response of the service calls..
 * 
 * @author
 * @verion 1.0
 * 
 */
public class APITest {

	private static Logger logger = LoggerFactory.getLogger(APITest.class);

	/**
	 * Call the Rest API service and get the response.
	 * 
	 * @param testdata
	 * @return
	 * @throws Exception
	 */
	public Response invokeService(TestData testdata) throws Exception {

		logger.info("Request Body: " + testdata.getRequestBody());
		logger.info("Request Content Type = " + testdata.getContentType());
		logger.info("Request URL: " + testdata.getBaseURL() + testdata.getRequestURL());

		Reporter.logMessage(Status.INFO, "<b>End Point URL: <br> </b>" + testdata.getBaseURL() + testdata.getRequestURL());
		Reporter.logMessage(Status.INFO, "<b>Method Type: <br> </b>" + testdata.getRequestMethod());
		
		Response response = null;

		RestAssured.baseURI = testdata.getBaseURL().trim();

		if (Constants.RELAX_HTTPS_VALIDATION) {
			RestAssured.useRelaxedHTTPSValidation();
		}

		RestAssured.urlEncodingEnabled = Constants.URL_ENCODING_ENABLED;

		if (isNotEmpty(testdata.getAuthUsername()) && isNotEmpty(testdata.getAuthPassword())) {
			doAuthentication(testdata);
		}

		final RequestSpecification reqSpecification = RestAssured.given();

		if (isNotEmpty(testdata.getContentType())) {
			reqSpecification.contentType(testdata.getContentType());
		}

		Map<String, String> headers = createMap(testdata.getHeaders());
		headers.putAll(testdata.getReqHeaders());
		reqSpecification.headers(headers);

		reqSpecification.queryParams(createMap(testdata.getUrlParameters()));

		reqSpecification.pathParams(createMap(testdata.getRequestParameters()));

		if (isNotEmpty(testdata.getRequestBody())) {
			prepareRequestPayload(testdata, reqSpecification);
		}

		if (Constants.GET.equalsIgnoreCase(testdata.getRequestMethod())) {
			response = reqSpecification.get(testdata.getRequestURL()).andReturn().then().extract().response();
		} else if (Constants.DELETE.equalsIgnoreCase(testdata.getRequestMethod())) {
			response = reqSpecification.delete(testdata.getRequestURL()).andReturn().then().extract().response();
		} else if (Constants.POST.equalsIgnoreCase(testdata.getRequestMethod())) {
			response = reqSpecification.post(testdata.getRequestURL()).andReturn().then().extract().response();
		} else if (Constants.PUT.equalsIgnoreCase(testdata.getRequestMethod())) {
			response = reqSpecification.put(testdata.getRequestURL()).andReturn().then().extract().response();
		}

		if (response != null) {
			logger.info("Response Body" + response.asString());
			logger.info("Response Content Type = " + response.getContentType());
			logPayload("Response", response.getContentType().split(Constants.SEMI_COLON)[0].trim(),
					response.asString());
		}
		return response;
	}

	/**
	 * Reads payload from given file and updates the dynamic content.
	 * 
	 * @param testdata
	 * @param reqSpecification
	 */
	public String prepareRequestPayload(TestData testdata, final RequestSpecification reqSpecification) {
		String requestPayload = APIUtil.readPayloadData(testdata.getRequestBody());
		requestPayload = updatePayload(testdata, requestPayload);
		reqSpecification.body(requestPayload);

		logPayload("Request", testdata.getContentType(), requestPayload);
		return requestPayload;
	}

	/**
	 * Logs payload into the report.
	 * 
	 * @param text
	 * @param contentType
	 * @param requestPayload
	 */
	private void logPayload(String text, String contentType, String requestPayload) {
		requestPayload = formatPayload(requestPayload);
		Reporter.logMessage(Status.INFO, text + " Payload" + "<textarea>" + requestPayload + "</textarea>");
	}

	/**
	 * Reads authentication data and sends to the application for validation.
	 */
	private void doAuthentication(TestData testdata) {
		try {
			PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
			authScheme.setUserName(testdata.getAuthUsername());
			authScheme.setPassword(testdata.getAuthPassword());
			RestAssured.authentication = authScheme;
		} catch (Exception e) {
			throw new FrameworkException("Error Occured while doing Authentication", e);
		}
	}

	/**
	 * Prepares map object based on provided data. Ex: key1=value1&key2=value2
	 * 
	 * @param expResponseString
	 * @return
	 */
	private Map<String, String> createMap(String expResponseString) {
		Map<String, String> maptest = new HashMap<String, String>();
		if (isNotEmpty(expResponseString)) {
			final String[] texct = expResponseString.split(Constants.AND);
			for (int j = 0; j < texct.length; j++) {
				final String[] text1 = texct[j].split(Constants.EQUAL);
				maptest.put(text1[0], text1[1]);
			}
		}
		return maptest;
	}

	/**
	 * Validated Response code and response payload.
	 * 
	 * @param response
	 * @param testData
	 * @throws Exception
	 */
	public void validateResponse(Response response, TestData testData) throws Exception {

		String expectedResponseCode = testData.getExpectedResponseCode();

		if (response != null && (expectedResponseCode.equals(Integer.toString(response.getStatusCode())))) {
			Reporter.logMessage(Status.INFO, "Received successfull response Code: " + response.getStatusCode());
			validateServiceResponse(response, testData);

		} else if (Constants.ERROR_RESP_CODES_LIST.contains(Integer.toString(response.getStatusCode()))) {

			logger.info("Exp response: " + expectedResponseCode + " Act response: "
					+ (Integer.toString(response.getStatusCode())));
			Reporter.logMessage(Status.INFO, "Received failed response Code: " + response.getStatusCode());

			Reporter.logMessage(Status.INFO, "Received Error response from the server" + "<pre>" + "<b>"
					+ "Response Body::: " + "</b><br>" + formatPayload(response.body().asString()) + "</pre>");

			throw new FrameworkException("<b>" + "Received Error response from the server:::" + "</b>"
					+ response.getStatusLine().toString());

		} else {
			logger.error("No Response");
			throw new FrameworkException("<b>" + "Received Empty response from the server:::" + "</b>"
					+ response.getStatusLine().toString());
		}
	}

	/**
	 * Validating service response.
	 * 
	 * @param response
	 * @param testData
	 * @throws Exception
	 */
	private void validateServiceResponse(Response response, TestData testData) throws Exception {

		if (validateContentType(Constants.JSON_CONTENT_TYPE, response.getContentType())) {
			validateJsonResponse(response, testData);

		} else if (validateContentType(Constants.XML_CONTENT_TYPE, response.getContentType().trim())) {
			validateXmlResponse(response.body().asString(), testData);

		} else if (validateContentType(Constants.TEXT_CONTENT_TYPE, response.getContentType().trim())
				|| validateContentType(Constants.TEXT_HTML_CONTENT_TYPE, response.getContentType().trim())
				|| validateContentType(Constants.EML_CONTENT_TYPE, response.getContentType().trim())) {
			if (isJSONContent(response.body().asString())) {
				validateJsonResponse(response, testData);
			} else {
				validateXmlResponse(response.body().asString(), testData);
			}
		} else {
			throw new FrameworkException("Content Type not matched to validate response payload please check!!! \n"
					+ " Expected Content Type: " + response.getContentType());
		}
	}

	private boolean validateContentType(String expContentType, String actContentType) {
		if (actContentType.contains(Constants.SEMI_COLON)) {
			actContentType = actContentType.split(Constants.SEMI_COLON)[0].trim();
		}
		if (expContentType.equalsIgnoreCase(actContentType.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * Validates JSON Service Response by provided keys.
	 * 
	 * @param response
	 * @param testData
	 * @return
	 */
	private void validateJsonResponse(Response response, TestData testData) {
		if (isNotEmpty(testData.getExpectedResponse()) || !testData.getExpectedResponseKeys().isEmpty()) {
			// JsonPath jsonPath = new JsonPath(response);
			Map<String, String> maptest = createMap(testData.getExpectedResponse());
			maptest.putAll(testData.getExpectedResponseKeys());
			boolean isRespValid = true;
			for (String key : maptest.keySet()) {
				String actualRespValue = "";
				if (isNotEmpty(testData.getResponseRootPath())) {
					actualRespValue = convertToString(response.path(testData.getResponseRootPath() + "." + key));
				} else {
					actualRespValue = convertToString(response.path(key));
				}
				String expectedRespValue = (String) maptest.get(key);
				if (!softValidateField(expectedRespValue, actualRespValue, "Validating Response for Key : " + key)) {
					isRespValid = false;
				}
			}
			if (!isRespValid) {
				throw new FrameworkException("Response Validation failed");
			}
		}
	}

	private String convertToString(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return String.valueOf(value);
			} else if (value instanceof Boolean) {
				return String.valueOf(value);
			} else if (value instanceof List) {
				return Joiner.on('&').join((Iterable<?>) value);
			}
		}
		return (String) value;
	}

	/**
	 * Validates XML Service Response by provided keys.
	 * 
	 * @param response
	 * @param testData
	 * @return
	 * @throws Exception
	 */
	private void validateXmlResponse(String resStr, TestData testData) throws Exception {
		if (isNotEmpty(testData.getExpectedResponse()) || !testData.getExpectedResponseKeys().isEmpty()) {
			Map<String, String> maptest = createMap(testData.getExpectedResponse());
			maptest.putAll(testData.getExpectedResponseKeys());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(resStr)));
			Element rootElement = document.getDocumentElement();
			boolean isRespValid = true;
			for (Map.Entry<String, String> entry : maptest.entrySet()) {
				String key = entry.getKey();
				String expectedRespValue = entry.getValue();
				String actualRespValue = getXmlValue(key, rootElement);
				if (!softValidateField(expectedRespValue, actualRespValue, "Validating Response for Key: " + key)) {
					isRespValid = false;
				}
			}
			if (!isRespValid) {
				throw new FrameworkException("Response Validation failed");
			}
		}
	}

	/**
	 * Retrieves value for the given key from Response payload.
	 * 
	 * @param resStr
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getValueFromResponse(String resStr, String key) throws Exception {
		if (isNotEmpty(key)) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(resStr)));
			Element rootElement = document.getDocumentElement();
			return getXmlValue(key, rootElement);
		} else {
			throw new FrameworkException("Key " + key + " not found in the response");
		}
	}

	/**
	 * Fetches given tag values
	 * 
	 * @param tagName
	 * @param element
	 * @return
	 */
	private String getXmlValue(String tagName, Element element) {
		String value = "";
		NodeList list = element.getElementsByTagName(tagName);
		for (int i = 0; i < list.getLength(); i++) {
			NodeList subList = list.item(i).getChildNodes();
			if (subList != null && subList.getLength() > 0) {
				value += subList.item(0).getNodeValue() + "&";
			}
		}
		int count = CharMatcher.is('&').countIn(value);
		if (count == 1) {
			value = value.replace("&", "");
		}
		return value;
	}

	/**
	 * Updates payload by the provided values for the keys.
	 * 
	 * @param token
	 * @param tokenKey
	 * @param requestBody
	 * @return
	 * @throws Exception
	 */
	public String updatePayload(TestData testdata, String requestBody) {
		String updatedPayload = requestBody;
		try {
			if (isNotEmpty(testdata.getDynamicKeysToUpdate()) || !testdata.getDynamicKeys().isEmpty()) {
				Map<String, String> dynamicKeys = createMap(testdata.getDynamicKeysToUpdate());
				dynamicKeys.putAll(testdata.getDynamicKeys());

				for (Map.Entry<String, String> entry : dynamicKeys.entrySet()) {
					updatedPayload = updatedPayload.replace("{{" + entry.getKey() + "}}", entry.getValue());
				}
			}
		} catch (Exception ex) {
			logger.info("Exception occured while updating the request payload" + ex.getMessage());
		}
		return updatedPayload;
	}

	/**
	 * Soft Validates the actual value displayed in a application against the
	 * expected value. Compares both the values
	 * 
	 * @param expectedValue
	 * @param actualValueInScreen
	 * @param erroMessageToLog
	 */
	public boolean softValidateField(String expectedValue, String actualValueInScreen, String messageToLog) {
		boolean isEqual = false;
		if (isNotEmpty(expectedValue)) {
			// if actual is null set it to empty to avoid NullPointer
			if (!isNotEmpty(actualValueInScreen)) {
				actualValueInScreen = "";
			}
			if (actualValueInScreen.contains(expectedValue)) {
				isEqual = true;
				Reporter.logMessage(Status.INFO,
						messageToLog + "<b><font color=" + "green" + "><br> Expected value : <br>" + expectedValue
								+ ".<br> Actual value : <br>" + actualValueInScreen + ".</font><b><br>");
			} else {
				Reporter.logMessage(Status.INFO,
						messageToLog + "<b><font color=" + "red" + "> <br> Expected value : <br>" + expectedValue
								+ ". <br> Actual Value: <br>" + actualValueInScreen + ".</font><b><br>");
			}
		} else {
			throw new FrameworkException("Expected value is empty - cannot be verified. Kindly revisit Test Data");
		}
		return isEqual;
	}

	/**
	 * Checks whether given value empty or not.
	 * 
	 * @param value
	 * @return
	 */
	public boolean isNotEmpty(String value) {
		if (StringUtils.isNotBlank(value)) {
			return true;
		}
		return false;
	}

	/**
	 * Formats the payload.
	 * 
	 * @param payload
	 * @return
	 */
	public String formatPayload(String payload) {
		return formatXML(payload);
	}

	/**
	 * Formats xml payload
	 * 
	 * @param input
	 * @return
	 */
	private String formatXML(String input) {
		try {
			Document doc = DocumentHelper.parseText(input);
			StringWriter sw = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setIndent(true);
			format.setIndentSize(3);
			XMLWriter xw = new XMLWriter(sw, format);
			xw.write(doc);
			return sw.toString();
		} catch (Exception e) {
			input = formatJson(input);
			return input;
		}
	}

	/**
	 * Formats json payload.
	 * 
	 * @param json
	 * @return
	 */
	private String formatJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object jsonObject = mapper.readValue(json, Object.class);
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			logger.info(json);
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return json;
	}

	/**
	 * Verifies whether json or xml
	 * 
	 * @param payload
	 * @return
	 */
	private boolean isJSONContent(String payload) {
		try {
			new JSONObject(payload);
		} catch (JSONException ex) {
			try {
				new JSONArray(payload);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
}
