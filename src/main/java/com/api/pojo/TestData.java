package com.api.pojo;

import java.util.HashMap;
import java.util.Map;

public class TestData {

	private String baseURL;

	private String requestURL;

	private String headers;

	private String requestParameters;

	private String requestMethod;

	private String contentType;

	private String requestBody;

	private String dynamicKeysToUpdate;

	private String urlParameters;

	private String responseRootPath;

	private String expectedResponseCode;

	private String expectedResponse;

	private String authUsername;

	private String authPassword;
	
	private boolean encoderConfig;
	
	private String encodingType;

	Map<String, String> reqHeaders = new HashMap<String, String>();
	Map<String, String> dynamicKeys = new HashMap<String, String>();
	Map<String, String> expectedResponseMap = new HashMap<String, String>();

	Map<String, String> responseValidationMap;

	/**
	 * Stores Request headers keys and values.
	 * 
	 * @param key
	 * @param value
	 */
	public TestData addReqHeaders(String key, String value) {
		reqHeaders.put(key, value);
		return this;
	}

	public Map<String, String> getReqHeaders() {
		return reqHeaders;
	}

	/**
	 * Stores dynamic values in payload for given keys.
	 * 
	 * @param key
	 * @param value
	 */
	public TestData addDynamicKeys(String key, String value) {
		dynamicKeys.put(key, value);
		return this;
	}

	public Map<String, String> getDynamicKeys() {
		return dynamicKeys;
	}

	/**
	 * Stores expected response keys and values.
	 * 
	 * @param key
	 * @param value
	 */
	public TestData addExpectedResponseKeys(String key, String value) {
		expectedResponseMap.put(key, value);
		return this;
	}

	public Map<String, String> getExpectedResponseKeys() {
		return expectedResponseMap;
	}

	/**
	 * Stores Response validation details.
	 * 
	 * @return
	 */
	public Map<String, String> getRespValidationMap() {
		if (responseValidationMap == null) {
			responseValidationMap = new HashMap<String, String>();
		}
		return responseValidationMap;
	}

	public String getDynamicKeysToUpdate() {
		return dynamicKeysToUpdate;
	}

	public void setDynamicKeysToUpdate(String dynamicKeysToUpdate) {
		this.dynamicKeysToUpdate = dynamicKeysToUpdate;
	}

	public String getExpectedResponse() {
		return expectedResponse;
	}

	public void setExpectedResponse(String expectedResponse) {
		this.expectedResponse = expectedResponse;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getExpectedResponseCode() {
		return expectedResponseCode;
	}

	public void setExpectedResponseCode(String expectedResponseCode) {
		this.expectedResponseCode = expectedResponseCode;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getUrlParameters() {
		return urlParameters;
	}

	public void setUrlParameters(String urlParameters) {
		this.urlParameters = urlParameters;
	}

	public String getResponseRootPath() {
		return responseRootPath;
	}

	public void setResponseRootPath(String responseRootPath) {
		this.responseRootPath = responseRootPath;
	}

	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getAuthUsername() {
		return authUsername;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public boolean isEncoderConfig() {
		return encoderConfig;
	}

	public void setEncoderConfig(boolean encoderConfig) {
		this.encoderConfig = encoderConfig;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

}
