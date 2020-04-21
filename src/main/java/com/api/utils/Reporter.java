package com.api.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Reporter {
	// Helps to generate the logs in test report.
	static ExtentReports extent = new ExtentReports();;
	static ExtentTest test;
	static ExtentTest node ;

	public static void startReport() {
		// initialize the HtmlReporter
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(
				System.getProperty("user.dir") + "/report/testReport_" + System.currentTimeMillis() + ".html");
		// initialize ExtentReports and attach the HtmlReporter
		extent.attachReporter(htmlReporter);
		// To add system or environment info by using the setSystemInfo method.
		extent.setSystemInfo("OS", System.getProperty("os.name"));

		// configuration items to change the look and feel
		// add content, manage tests etc
		htmlReporter.config().setDocumentTitle("API Report");
		htmlReporter.config().setReportName("API Test Report");
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
	}

	public static ExtentTest createExtentTest(String testCaseName) {
		test = extent.createTest(testCaseName, "PASSED test case");
		return test;
	}

	public static void flushTests() {
		extent.flush();
	}

	public static void logMessage(Status status, String desc) {
		try {
			test.log(status, desc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
