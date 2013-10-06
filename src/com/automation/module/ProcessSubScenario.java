package com.automation.module;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.WebDriver;

import com.automation.beans.Scenarios;
import com.automation.beans.SnapshotTaker;
import com.automation.beans.Subscenarios;

public class ProcessSubScenario {
	static Logger log = Logger.getLogger(ProcessSubScenario.class);
	private Row row;

	/**
	 * Runs all subscenario of a scenario.
	 * 
	 * @param sheetNext
	 *            - the sheet no. of scenario
	 * @param mappedValues
	 *            - having variable and mapped value (metadata)
	 * @param scenarios
	 *            - object having all details of a scenario
	 * @param startRowNum
	 *            - First subscenario index for a scenario
	 * @param endRowNum
	 *            - last subscenario index for a scenario
	 * @param myD
	 *            - web driver object
	 * @param sf
	 *            - Snapshot taker object
	 * @throws Exception
	 */
	public void subScenarioExec(final Sheet sheetNext,
			final Map<Object, Object> mappedValues, final Scenarios scenarios,
			final int startRowNum, final int endRowNum, final WebDriver myD,
			final SnapshotTaker sf) throws Exception {
		final String scnName = scenarios.getScn_name();
		String scripts = null;

		/* Running all subscenario of a scenario in a loop */
		for (int i = startRowNum; i <= endRowNum; i++) {
			final Subscenarios subScnBeanObj = new Subscenarios();
			row = sheetNext.getRow(i);

			subScnBeanObj.setSub_scn_name(row.getCell(3).getStringCellValue());
			log.info(scnName + " - Procesing subcenario '"
					+ subScnBeanObj.getSub_scn_name() + "' STARTED");
			try {
				final Cell c = row.getCell(4,
						org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK);
				scripts = c.getStringCellValue();
				if (!scripts.equals("")) {
					subScnBeanObj.setScripts(scripts);
					log.info("Script Rule is:  " + subScnBeanObj.getScripts());

					final ProcessScripts proScrObj = new ProcessScripts();

					proScrObj.scriptExec(mappedValues, subScnBeanObj, myD, sf,
							scenarios);

					log.info(scnName + " - Procesing of subcenario '"
							+ subScnBeanObj.getSub_scn_name() + "' is END");

				} else {
					log.info(scnName + " - Procesing of subcenario '"
							+ subScnBeanObj.getSub_scn_name()
							+ "' is completed and it has no script");
				}
			} catch (final Exception e) {
				scenarios.setFailedSubScnName(subScnBeanObj.getSub_scn_name());
				log.error(scnName + " - Execution of '"
						+ subScnBeanObj.getSub_scn_name()
						+ "' is FAILED due to \n" + e.getMessage());
				throw new Exception(e);
			}
		}
	}

}
