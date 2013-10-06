package com.automation.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.automation.beans.SnapshotTaker;
import com.automation.utility.Constants;

/**
 * @author jrp
 * 
 */
public class ProcessMain {
	static Logger log = Logger.getLogger(ProcessMain.class);

	private static Map<String, int[]> scnMap = new HashMap<String, int[]>();
	private static ProcessTestCases processTestCasesObj;

	public static void main(final String[] args) {
		PropertyConfigurator.configure(Constants.LOG4J_PROPERTY_LINK);
		log.info("Application started ...........");

		int count = 0;

		/* Creating snapshot folder */
		final SnapshotTaker sf = new SnapshotTaker();
		final File file = new File(Constants.SCREENSHOT_LINK);
		if (file.isDirectory()) {
			final String[] files = file.list();
			count = files.length;
			sf.setCount(count);
		}

		try {
			final Workbook wb = WorkbookFactory.create(new FileInputStream(
					Constants.EXCEL_LINK));
			log.info("Processing the sheet 0");
			final Sheet sheet = wb.getSheetAt(0);

			final String folder_name = Constants.SCREENSHOT_LINK + "/run_"
					+ sf.getCount();
			new File(folder_name).mkdir();
			log.info("Populating scenario map for all sheet : STARTED");
			scnMap = populateScenarioMap(wb, sheet);

			/*
			 * Runnable R0; R0 = new ProcessTestCases(wb, sf, scnMap); Thread
			 * t0, t1; t0 = new Thread(R0); t1 = new Thread(R0);
			 * 
			 * t0.start(); t1.start();
			 */
			processTestCasesObj = new ProcessTestCases(wb, sf, scnMap);
			processTestCasesObj.runTestCases();
			log.info("Application is finished successfully .... ");
		} catch (InvalidFormatException | IOException e) {
			log.info("Application finished with exception " + e.getMessage());
		} catch (final Exception e) {
			log.info("Application finished with exception " + e.getMessage());
		} finally {
			scnMap.clear();
		}

	}

	/**
	 * Populates Map of Scenario name and corresponding start and end row for
	 * all active sheet
	 * 
	 * @param wb
	 * @param sheet
	 * @return
	 */
	private static Map<String, int[]> populateScenarioMap(final Workbook wb,
			final Sheet sheet) {

		final Set<Integer> sheetSet = new HashSet<Integer>();
		final Map<String, int[]> scnMaps = new HashMap<String, int[]>();
		String prevScn = new String();
		String curScn = new String("default");
		int startCur = 0;
		final int[] scnStaEndCurr = new int[2];

		scnMaps.clear();

		/* Creating a set having unique sheets for processing of scenarios */
		for (final Row row : sheet) {
			final Cell c = row.getCell(1,
					org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK);
			if (row.getRowNum() == 0) {
				continue;
			}
			if (!String.valueOf(c.getNumericCellValue()).isEmpty()) {
				sheetSet.add((int) c.getNumericCellValue());
			}
			log.info("Sheet no. are populated in set " + sheetSet);
		}

		/*
		 * Creating a Map<String, int[]> populating scenario name and its starnd
		 * ending row
		 */
		for (final int sheetNo : sheetSet) {
			final Sheet sheetNext = wb.getSheetAt(sheetNo);
			for (final Row eachRow : sheetNext) {
				// A temporary array to store the value
				final int[] scnStaEndPrev = new int[2];
				if (eachRow.getRowNum() == 0) {
					continue;
				}
				final Cell ce = eachRow.getCell(2,
						org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK);

				if (!ce.getStringCellValue().isEmpty()) {

					prevScn = curScn;
					scnStaEndPrev[0] = scnStaEndCurr[0];
					curScn = ce.getStringCellValue();

					startCur = eachRow.getRowNum();
					scnStaEndCurr[0] = startCur;
					scnMaps.put(curScn, scnStaEndCurr);

					scnStaEndPrev[1] = startCur - 1;
					scnMaps.put(prevScn, scnStaEndPrev);
				}

				if (eachRow.getRowNum() == sheetNext.getPhysicalNumberOfRows() - 1) {
					scnStaEndCurr[1] = eachRow.getRowNum();
					scnMaps.put(curScn, scnStaEndCurr);
				}

			}
		}
		log.info("Map for Scenario names are populated " + scnMaps);

		for (final Map.Entry<String, int[]> scnDetails : scnMaps.entrySet()) {
			if (scnDetails != null) {
				final String scnName = scnDetails.getKey().toString();

				log.info("Scenario : " + scnName + ":: start row index :"
						+ scnDetails.getValue()[0] + " and end row index :"
						+ scnDetails.getValue()[1]);
			}
		}
		return scnMaps;
	}
}
