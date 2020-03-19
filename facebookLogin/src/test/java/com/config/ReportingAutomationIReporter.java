package com.config;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.xml.XmlSuite;

public class ReportingAutomationIReporter implements IReporter {

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		// TODO Auto-generated method stub
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> results = suite.getResults();
			Set<String> keys = results.keySet();
			for (String key : keys) {
				ITestContext context = results.get(key).getTestContext();
				System.out.println("Suite Name :-> " + context.getName() + "\nReport output directory :->"
						+ context.getOutputDirectory() + "\nSuite Name :->" + context.getSuite().getName()
						+ "\nStart Date of Execution :->" + context.getStartDate() + "\nEnd Date of Execuion :->"
						+ context.getEndDate());

				IResultMap failedResultMap = context.getFailedTests();
				Collection<ITestNGMethod> failedMethods = failedResultMap.getAllMethods();
				System.out.println("--------FAILED TEST CASE---------");
				for (ITestNGMethod iTestNGMethod : failedMethods) {
					System.out.println("TESTCASE NAME->" + iTestNGMethod.getMethodName()
							+ "\nDescription->" + iTestNGMethod.getDescription()
							+ "\nPriority->" + iTestNGMethod.getPriority()
							+ "\n:Date->" + new Date(iTestNGMethod.getDate()));
				}
			}
		}

		IReporter.super.generateReport(xmlSuites, suites, outputDirectory);
	}

}
