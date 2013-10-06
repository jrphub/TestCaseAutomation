package com.automation.beans;

import java.util.Map;

/**
 * @author jrp
 * 
 */
public class Scenarios {
	private String scn_name;
	private double scn_sheet;
	private String scn_exec_status;
	private String scn_subscn_failed;
	private Map<String, String> mappingVars;
	private String resultStatus;
	private String failedSubScnName;
	private String functionalError;
	private String order_id;

	public String getScn_name() {
		return scn_name;
	}

	public void setScn_name(final String scn_name) {
		this.scn_name = scn_name;
	}

	public double getScn_sheet() {
		return scn_sheet;
	}

	public void setScn_sheet(final double d) {
		this.scn_sheet = d;
	}

	public String getScn_exec_status() {
		return scn_exec_status;
	}

	public void setScn_exec_status(final String scn_exec_status) {
		this.scn_exec_status = scn_exec_status;
	}

	public Map<String, String> getMappingVars() {
		return mappingVars;
	}

	public void setMappingVars(final Map<String, String> mappingVars) {
		this.mappingVars = mappingVars;
	}

	public String getScn_subscn_failed() {
		return scn_subscn_failed;
	}

	public void setScn_subscn_failed(final String scn_subscn_failed) {
		this.scn_subscn_failed = scn_subscn_failed;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(final String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getFailedSubScnName() {
		return failedSubScnName;
	}

	public void setFailedSubScnName(final String failedSubScnName) {
		this.failedSubScnName = failedSubScnName;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(final String order_id) {
		this.order_id = order_id;
	}

	public String getFunctionalError() {
		return functionalError;
	}

	public void setFunctionalError(final String functionalError) {
		this.functionalError = functionalError;
	}

}
