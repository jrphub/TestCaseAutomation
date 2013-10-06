package com.automation.module;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.automation.beans.Scenarios;
import com.automation.beans.SnapshotTaker;
import com.automation.utility.Constants;

/**
 * @author jrp
 * 
 */
public class ProcessTestCases {
	static Logger log = Logger.getLogger(ProcessTestCases.class);
	Sheet sheet;
	Workbook wb;
	private int max_col_count;
	private ArrayList<String> metadata = null;
	SnapshotTaker sf;
	FileOutputStream fileOut;
	String scnName;
	private Map<String, int[]> scnMap = new HashMap<String, int[]>();

	public ProcessTestCases(final Workbook wb, final SnapshotTaker sf,
			final Map<String, int[]> scnMap) {
		this.wb = wb;
		this.sf = sf;
		this.scnMap = scnMap;
	}

	/**
	 * Runs each test scenario and starts execution by populating variable's
	 * value
	 */

	public void runTestCases() {
		log.info("In ProcessTestCases: START");
		// log.info("By Thread" + Thread.currentThread().getId());
		metadata = new ArrayList<String>();

		// order_id_future_use variable
		String order_id_future_use = null;
		final ProcessScenario proScnObj = new ProcessScenario();
		final Sheet sheet = wb.getSheetAt(0);

		/* Starts procesing each row of sheet 0 */
		for (final Row row : sheet) {
			final Map<Object, Object> mappedValues = new HashMap<Object, Object>();
			final Scenarios scenarios = new Scenarios();
			try {
				fileOut = new FileOutputStream(Constants.EXCEL_LINK);
				if (row.getRowNum() == 0) {
					max_col_count = row.getLastCellNum();
					for (int i = 4; i < max_col_count; i++) {
						metadata.add(row.getCell(i).toString());
					}
				} else

				{
					scenarios.setScn_name(row.getCell(0).toString());
					scnName = scenarios.getScn_name();
					log.info("Populating Scenario '" + scnName
							+ "' bean object");
					scenarios
							.setScn_sheet(row.getCell(1).getNumericCellValue());
					log.info("creating HashMap for " + scnName);
					/*
					 * Populating variable and it's value of scenarios
					 */
					for (int j = 4; j < max_col_count; j++) {
						switch (row
								.getCell(
										j,
										org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK)
								.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							mappedValues.put(metadata.get(j - 4), row
									.getCell(j).getRichStringCellValue()
									.toString());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							row.getCell(
									j,
									org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK)
									.setCellType(Cell.CELL_TYPE_STRING);
							mappedValues.put(metadata.get(j - 4), row
									.getCell(j).getRichStringCellValue()
									.getString());
							break;
						case Cell.CELL_TYPE_BLANK:
							mappedValues.put(metadata.get(j - 4), null);
							break;
						}

					}

					log.info("Hash Map object is created for '" + scnName + "'");

					// To reuse order_id_future_use variable
					if ((String) mappedValues.get("Order ID") != null) {
						scenarios.setOrder_id((String) mappedValues
								.get("Order ID"));

					} else if (order_id_future_use != null) {

						scenarios.setOrder_id(order_id_future_use);
						// To convert rule to actual script, we need to
						// populate
						// hashmap
						mappedValues.put("Order ID", order_id_future_use);

					} else {
						// do nothing, let it be like this, may be in
						// future ,
						// we will generate order id
						// if we are not generating any order id and we
						// are told
						// to use order id , then it should through
						// error.
					}

					final Sheet sheetNext = wb.getSheetAt((int) scenarios
							.getScn_sheet());
					log.info("Moving to next sheet "
							+ (int) scenarios.getScn_sheet()
							+ " processing the scenario");

					/* Starting Execution of Scenario and scenario is in loop */
					proScnObj.scenarioExec(sheetNext, mappedValues, scenarios,
							sf, scnMap);

					/*
					 * Storing the generated id of order place for future use
					 */
					if (scenarios.getOrder_id() != null) {
						order_id_future_use = scenarios.getOrder_id();
					}

				}

			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (row.getRowNum() != 0) {
						log.info("Writing into the file started");
						Cell exec_status_cell = row.getCell(2);
						Cell failed_sub_scn_name = row.getCell(3);
						exec_status_cell = row.createCell(2);
						failed_sub_scn_name = row.createCell(3);

						if (scenarios.getResultStatus().equals("Fail")) {
							log.info("Writing the failed scenario");
							exec_status_cell.setCellValue("Fail");
							failed_sub_scn_name.setCellValue(scenarios
									.getFailedSubScnName());
						} else {
							log.info("Writing the passed scenario");
							exec_status_cell.setCellValue("Pass");
						}

						wb.write(fileOut);
						mappedValues.clear();
						log.info("Hash Map object is cleared for '"
								+ scenarios.getScn_name() + "'");
						fileOut.close();
					}

				} catch (final IOException e) {
					e.printStackTrace();
				}
				/*
				 * log.info("IO Stream closed for thread " +
				 * Thread.currentThread().getId());
				 */
			}
		}

	}
}
