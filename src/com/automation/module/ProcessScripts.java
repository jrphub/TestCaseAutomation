package com.automation.module;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.WaitForPageToLoad;

import com.automation.beans.Scenarios;
import com.automation.beans.SnapshotTaker;
import com.automation.beans.Subscenarios;

/**
 * @author jrp
 * 
 */

/*
 * This class shouldn't have any catch block, All the exception will be handled
 * at subcenario and scenario level
 */
public class ProcessScripts {
	static Logger log = Logger.getLogger(ProcessScripts.class);
	WaitForPageToLoad wait = new WaitForPageToLoad();

	/**
	 * Creating actual script to run for a subscenario by substituting variable
	 * with values in syntax
	 * 
	 * @param mappedValues
	 *            - having variable and mapped value (metadata)
	 * @param scripts
	 *            - syntax where variables will be replaced with mapped values
	 * 
	 * @return
	 */
	private String getActualScript(final Map<Object, Object> mappedValues,
			String scripts) {
		String[] primarySplit = null;
		String[] secondarySplit = null;
		String value = null;
		Object val = null;
		primarySplit = scripts.split("\\[");

		for (int i = 1; i < primarySplit.length; i++) {
			secondarySplit = primarySplit[i].split("\\]");
			val = mappedValues.get(secondarySplit[0]);
			value = (String) val;
			if (value != null) {
				scripts = scripts.replace("[" + secondarySplit[0] + "]", value);
			}
		}

		return scripts;
	}

	/**
	 * Runs each part of the script for a subscenario
	 * 
	 * @param runnableScript
	 * @param myD
	 * @param scenarios
	 * @throws Exception
	 */
	private void keywordDrivenProcess(final String runnableScript,
			final WebDriver myD, final Scenarios scenarios) throws Exception {
		final String[] eachEvent = runnableScript.split("\\|");
		switch (eachEvent[0].toLowerCase()) {
		// Make all case names in lowercase
		case "open":
			scenarios.setFunctionalError("Open");
			kzOpen(scenarios, eachEvent[1].toLowerCase(), myD);
			break;

		case "type":
			scenarios.setFunctionalError("Type");
			kzType(myD, eachEvent[1], eachEvent[2]);
			break;

		case "click":
			scenarios.setFunctionalError("Click");
			kzClick(myD, eachEvent[1]);
			break;

		case "wait":
			scenarios.setFunctionalError("Wait");
			kzwait(eachEvent[1]);
			break;

		case "assertmsg":
			scenarios.setFunctionalError("Assert Message");
			final String check = kzAssertMsg(myD, eachEvent[1]);
			log.info("Expected value: " + eachEvent[1] + " is " + check);
			break;

		case "sleep":
			scenarios.setFunctionalError("Sleep");
			kzSleep(eachEvent[1], scenarios);
			break;

		case "orderid":
			scenarios.setFunctionalError("orderid");
			System.out.println("order id is " + eachEvent[1]);
			break;

		default:
			scenarios.setFunctionalError("Anonymous error");
			throw new Exception();
		}
	}

	private String kzAssertMsg(final WebDriver myD, final String assertExpected) {
		final String check = myD.getTitle().equalsIgnoreCase(assertExpected) ? "Pass"
				: "Fail";
		return check;
	}

	private void kzClick(final WebDriver myD, final String actionString) {
		final String[] firstElements = actionString.split("=");
		if (firstElements[0].equalsIgnoreCase("name")) {
			myD.findElement(By.name(firstElements[1])).click();
		} else if (firstElements[0].equalsIgnoreCase("id")) {
			myD.findElement(By.id(firstElements[1])).click();
		} else if (firstElements[0].equalsIgnoreCase("xpath")) {
			myD.findElement(By.xpath(firstElements[1])).click();
		} else if (firstElements[0].equalsIgnoreCase("linkText")) {
			myD.findElement(By.linkText(firstElements[1])).click();
		}
	}

	private void kzOpen(final Scenarios scenarios, final String url,
			final WebDriver myD) {
		myD.navigate().to(url);

	}

	private void kzSleep(final String timeToWait, final Scenarios scenarios)
			throws NumberFormatException, InterruptedException {
		Thread.sleep(Long.parseLong(timeToWait));

	}

	private void kzType(final WebDriver myD, final String firstElement,
			final String secondElement) {
		final String[] firstElements = firstElement.split("=");
		if (firstElements[0].equalsIgnoreCase("name")) {
			myD.findElement(By.name(firstElements[1])).sendKeys(secondElement);
		} else if (firstElements[0].equalsIgnoreCase("id")) {
			myD.findElement(By.id(firstElements[1])).sendKeys(secondElement);
		} else if (firstElements[0].equalsIgnoreCase("xpath")) {
			myD.findElement(By.xpath(firstElements[1])).sendKeys(secondElement);
		} else if (firstElements[0].equalsIgnoreCase("linkText")) {
			myD.findElement(By.linkText(firstElements[1])).sendKeys(
					secondElement);
		}

	}

	private void kzwait(final String timeToWait) throws NumberFormatException,
			InterruptedException {
		wait.setTimeToWait(Integer.parseInt(timeToWait));
	}

	/**
	 * Creating script to run for a subscenario
	 * 
	 * @param mappedValues
	 * @param subScnBeanObj
	 * @param myD
	 * @param sf
	 * @param scenarios
	 * @throws Exception
	 */
	public void scriptExec(final Map<Object, Object> mappedValues,
			final Subscenarios subScnBeanObj, final WebDriver myD,
			final SnapshotTaker sf, final Scenarios scenarios) throws Exception {

		// For "Order Place scenario , generate order id
		if (scenarios.getScn_name().equals("Order Place")) {

			scenarios.setOrder_id("manual_order_id_105");

			// To convert rule to actual script, we need to populate hashmap
			mappedValues.put("Order ID", scenarios.getOrder_id());
		}

		final String runnableScript = getActualScript(mappedValues,
				subScnBeanObj.getScripts());

		log.info("Actual Script is: " + runnableScript);

		final String[] seg_runnable_script = runnableScript.split(";");

		/* Running the script by seggregating it */
		for (final String each_seg_script : seg_runnable_script) {
			if (!each_seg_script.equals("")) {
				keywordDrivenProcess(each_seg_script, myD, scenarios);
			}
		}

	}

}
