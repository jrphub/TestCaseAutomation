package com.automation.module;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.automation.beans.Scenarios;
import com.automation.beans.SnapshotTaker;
import com.automation.utility.Constants;

/**
 * @author jrp
 * 
 */
public class ProcessScenario {
	static Logger log = Logger.getLogger(ProcessScenario.class);
	private int startRowNum = 0;
	private int endRowNum = 0;
	private String scenarioName = "";

	/**
	 * Running each scenario in loop
	 * 
	 * @param sheetNext
	 *            - the sheet no. of scenario
	 * @param mappedValues
	 *            - having variable and mapped value (metadata)
	 * @param scenarios
	 *            - object having all details of a scenario
	 * @param sf
	 *            - snapshot object
	 * @param scnMap
	 *            - Map having scenario name, start row and end row of all
	 *            scenarios
	 * @throws Exception
	 */
	public void scenarioExec(final Sheet sheetNext,
			final Map<Object, Object> mappedValues, final Scenarios scenarios,
			final SnapshotTaker sf, final Map<String, int[]> scnMap)
			throws Exception {
		scenarioName = scenarios.getScn_name();

		WebDriver myD = new HtmlUnitDriver();
		myD = new FirefoxDriver();
		// myD = new InternetExplorerDriver();

		startRowNum = scnMap.get(scenarioName)[0];
		endRowNum = scnMap.get(scenarioName)[1];
		log.info(scenarioName + " : Start Row num:" + startRowNum
				+ " and End row Num:" + endRowNum);
		final ProcessSubScenario proSubScnObj = new ProcessSubScenario();
		try {
			/* Processing Subscenarios of a scenario */
			proSubScnObj.subScenarioExec(sheetNext, mappedValues, scenarios,
					startRowNum, endRowNum, myD, sf);
			scenarios.setResultStatus("Pass");
			log.info("Processing of scenario: '" + scenarioName
					+ "' is completed");
		} catch (final Exception e) {
			scenarios.setResultStatus("Fail");
			final String file_name_time = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance()
					.getTime());
			// file_name_time will be printed as 20121029_235240
			final File scrFile = ((TakesScreenshot) myD)
					.getScreenshotAs(OutputType.FILE);
			final String scn_folder_name = Constants.SCREENSHOT_LINK + "/run_"
					+ sf.getCount() + "/scn_" + scenarioName;
			new File(scn_folder_name).mkdir();
			FileUtils.copyFile(scrFile, new File(scn_folder_name + "/error_"
					+ scenarios.getFunctionalError() + "_" + file_name_time
					+ ".png"));
			log.info("Screenshot is created");
			log.info("Processing of scenario: '" + scenarioName
					+ "' is completed with error");
			throw new Exception(e);

		} finally {
			myD.quit();
		}

	}
}
