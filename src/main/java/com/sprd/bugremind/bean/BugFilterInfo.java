package com.sprd.bugremind.bean;

import java.sql.Array;
import java.util.Arrays;
import java.sql.Date;

public class BugFilterInfo {
	//搜索时间(CurrentTime)
	private Date searchtime;
	//The bugs contains some bugs with details.
	private Arrays bugs;
	//The login name of the user to whom the bug is assigned.
	private String assigned_to;
	//The name of the current component of this bug.
	private String component;
	//When the bug was created.
	private Date creation_time;
	//The login name of the person who filed this bug (the reporter).
	private String creator;
	//The day that this bug is due to be completed, in the format YYYY-MM-DD.
	private String deadline;
	//The IDs of bugs that this bug “depends on”.
	private Array depends_on; 
	//The bug ID of the bug that this bug is a duplicate of.	
	private int dupe_of;
	
	//The unique numeric ID of this bug.
	private int id;
	//true if this bug is open, false if it is closed.
	private boolean is_open;
	//The priority of the bug.
	private String priority;
	//The name of the product this bug is in.
	private String product;
	//The current severity of the bug.
	private String severity;
	//The current status of the bug.
	private String status;
	//The priority of the bug
	private String probability;
	//last change time
	private String last_change_time;
	//The summary of this bug.
	private String summary;
    //A URL that demonstrates the problem described in the bug, or is somehow related to the bug repor
	private String url;
	//The value of the “status whiteboard” field on the bug.
	private String whiteboard;
	
	//超期时间，Bug处于XXX状态，自上次修改到本次搜索时间超期，单位：天
	private String elapsed_day;
	//测试FO
	private String featureowner;
	
	public Arrays getBugs() {
		return bugs;
	}
	public void setBugs(Arrays bugs) {
		this.bugs = bugs;
	} 
	public String getAssigned_to() {
		return assigned_to;
	}
	public void setAssigned_to(String assigned_to) {
		this.assigned_to = assigned_to;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public Date getCreation_time() {
		return creation_time;
	}
	public void setCreation_time(Date creation_time) {
		this.creation_time = creation_time;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	public Array getDepends_on() {
		return depends_on;
	}
	public void setDepends_on(Array depends_on) {
		this.depends_on = depends_on;
	}
	public int getDupe_of() {
		return dupe_of;
	}
	public void setDupe_of(int dupe_of) {
		this.dupe_of = dupe_of;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isIs_open() {
		return is_open;
	}
	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getWhiteboard() {
		return whiteboard;
	}
	public void setWhiteboard(String whiteboard) {
		this.whiteboard = whiteboard;
	}
	public String getProbability() {
		return probability;
	}
	public void setProbability(String cf_probability) {
		this.probability = cf_probability;
	}

	public String getFeatureowner() {
		return featureowner;
	}
	public void setFeatureowner(String featureowner) {
		this.featureowner = featureowner;
	}
	public String getElapsedday() {
		return elapsed_day;
	}
	public void setElapsedday(String elapsedday) {
		this.elapsed_day = elapsedday;
	}
	public String getLast_change_time() {
		return last_change_time;
	}
	public void setLast_change_time(String last_change_time) {
		this.last_change_time = last_change_time;
	}
	public Date getSearchtime() {
		return searchtime;
	}
	public void setSearchtime(Date searchtime) {
		this.searchtime = searchtime;
	}
	
}
