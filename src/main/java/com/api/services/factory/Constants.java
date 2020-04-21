package com.api.services.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.api.utils.PropertyUtils;

public class Constants {
	public static final String YES = "YES";
	public static final String NO = "NO";
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";
	public static final String PUT = "PUT";
	public static final String AND = "&";
	public static final String NA = "NA";
	public static final String COMMA = ",";
	public static final String SOAP = "SOAP";
	public static final String REST = "REST";
	public static final String EQUAL = "=";
	public static final String ERROR_RESP_CODES = "400,401,405,415,499";
	public static final List<String> ERROR_RESP_CODES_LIST = new ArrayList<String>(
			Arrays.asList(ERROR_RESP_CODES.split(",")));
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String XML_CONTENT_TYPE = "application/xml";
	public static final String TEXT_CONTENT_TYPE = "text/plain";
	public static final String TEXT_HTML_CONTENT_TYPE = "text/html";
	public static final String EML_CONTENT_TYPE = "application/x-eML19AU";
	public static final String JSON = "json";
	public static final String XML = "xml";
	public static final String JSON_PAYLOAD_PATH = "jsonpayloads";
	public static final String XML_PAYLOAD_PATH = "xmlpayloads";
	public static final String USER_DIR=System.getProperty("user.dir");
	public static final String TEST_CONFIG_PROPERTIES_FILENAME = "src/test/resources/testconfig.properties";
	public static PropertyUtils myPropertyUtils = PropertyUtils.getInstance(TEST_CONFIG_PROPERTIES_FILENAME);
	public static final boolean CERTIFICATE_VALIDATION = Boolean.getBoolean(myPropertyUtils.getProperty("certificateValidation"));
	public static final boolean AUTHENTICATION = Boolean.getBoolean(myPropertyUtils.getProperty("authentication"));
	public static final String USER_NAME = myPropertyUtils.getProperty("userName");
	public static final String PASSWORD = myPropertyUtils.getProperty("password");
	public static final String SEMI_COLON = ";";
	public static final boolean RELAX_HTTPS_VALIDATION = Boolean.getBoolean(myPropertyUtils.getProperty("relax.https.validation"));
	public static final boolean URL_ENCODING_ENABLED = Boolean.getBoolean(myPropertyUtils.getProperty("encoding.url.enabled"));

}
