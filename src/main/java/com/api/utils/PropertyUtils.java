package com.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.services.factory.Constants;

/**
 * 
 * @author 
 * @version 1.0
 */
public class PropertyUtils {

	private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

	private final Properties myConfigProperties = new Properties();

	private PropertyUtils(final String aPropFile) throws FileNotFoundException {
		File propFile = new File(Constants.USER_DIR+"/"+aPropFile);
		InputStream systemResource = new FileInputStream(propFile); 
		try {
			myConfigProperties.load(systemResource);
		} catch (FileNotFoundException fex) {
			logger.error("FileNotFoundException {}", fex.getMessage());
		} catch (IOException ioe) {
			logger.error("IOException {}", ioe.getMessage());
		}
	}

	public static PropertyUtils getInstance(final String propFile) {
		try {
			return new PropertyUtils(propFile);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public String getProperty(final String key) {
		return myConfigProperties.getProperty(key);
	}

	public Set<String> getAllPropertyNames() {
		return myConfigProperties.stringPropertyNames();
	}

	public boolean containsKey(final String key) {
		return myConfigProperties.containsKey(key);
	}

}
