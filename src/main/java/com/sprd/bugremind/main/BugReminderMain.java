package com.sprd.bugremind.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sprd.bugremind.util.GetConfigXML;

public class BugReminderMain {
	private static final Log log = LogFactory.getLog(BugReminderMain.class);
	private static String StartByTimer = GetConfigXML.getXmlValue("BRConfig.xml", "StartByTimer", "value")[0];	
	
	/**
	 * BugReminder主方法
	 * @param args
	 */
	public static void main (String[] args) {
		if (StartByTimer.equals("true")) {
			RemindTimeManager bugmanager = new RemindTimeManager();
			BugRemindTask timetask = new BugRemindTask() ;
			bugmanager.TimeManager(timetask);
		}   else if (StartByTimer.equals("false")) {
			log.info("BugReminder will execute at once!");
			BugRemindTask bugtask = new BugRemindTask();
			bugtask.run();
		}
		
	}	
}
	