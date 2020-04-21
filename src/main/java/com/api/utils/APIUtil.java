package com.api.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.services.factory.Constants;


public class APIUtil {

	private static Logger logger = LoggerFactory.getLogger(APIUtil.class);

	/**
	 * Reads payload data from the given file and converts into string.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readPayloadData(String fileName) {
		String requestBody = "";
		try {
			String inputDataFilePath = System.getProperty("user.dir") + "/src/test/resources/";
			String fileExtension = FilenameUtils.getExtension(fileName);
			if (Constants.JSON.equalsIgnoreCase(fileExtension)) {
				inputDataFilePath += Constants.JSON_PAYLOAD_PATH + File.separator + fileName;
			} else if (Constants.XML.equalsIgnoreCase(fileExtension)) {
				inputDataFilePath += Constants.XML_PAYLOAD_PATH + File.separator + fileName;
			} else {
				requestBody = fileName;
			}
			if (StringUtils.isNotBlank(fileExtension)) {
				requestBody = FileUtils.readFileToString(new File(inputDataFilePath),
						StandardCharsets.UTF_8.toString());
			}
		} catch (Exception e) {
			logger.info("Exception occured while reading the file" + e.getMessage());
		}
		return requestBody;
	}
}
