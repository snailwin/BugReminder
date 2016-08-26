package com.sprd.bugremind.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import com.sprd.bugremind.email.BrEmailSend;
import com.sprd.bugremind.util.GetConfigXML;
import com.sprd.bugremind.util.HtmlGenerator;

public class BugRemindTask extends TimerTask{
	private static final Log log = LogFactory.getLog(BugRemindTask.class);
	/*
	 * Send Email
	 */
	private static String EmailAccoutName = GetConfigXML.getXmlValue("BRConfig.xml", "EmailAccoutName", "value")[0];
	private static String EmailPassword = GetConfigXML.getXmlValue("BRConfig.xml", "EmailPassword", "value")[0];	
	private static String SendAddress = GetConfigXML.getXmlValue("BRConfig.xml", "SendAddress", "value")[0];
	private static String destAddress_Delay = GetConfigXML.getXmlValue("BRConfig.xml", "destAddress_Delay", "value")[0];
	private static String destAddress_New = GetConfigXML.getXmlValue("BRConfig.xml", "destAddress_New", "value")[0];
	private static String CC_Address_Delay = GetConfigXML.getXmlValue("BRConfig.xml", "CC_Address_Delay", "value")[0];
	private static String CC_Address_New = GetConfigXML.getXmlValue("BRConfig.xml", "CC_Address_New", "value")[0];	
	private static String EmailContent_Delay = null;
	private static String EmailContent_New = null;
	private static String EmailSubject_Delay = GetConfigXML.getXmlValue("BRConfig.xml", "EmailSubject_Delay", "value")[0];	
	private static String EmailSubject_New = GetConfigXML.getXmlValue("BRConfig.xml", "EmailSubject_New", "value")[0];	
	public void run () {
		Date date= new Date();  
		HtmlGenerator htmlgene = new HtmlGenerator();
		SimpleDateFormat simpledateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpledateform.format(date);
		EmailSubject_Delay = EmailSubject_Delay + currentTime.substring(0, currentTime.length()-9);
		EmailSubject_New = EmailSubject_New + currentTime.substring(0, currentTime.length()-9);
		try {					
			/*
			 * 超时待处理CR提醒
			 */
			EmailContent_Delay = htmlgene.renderResultHtml(htmlgene.getWaitingTaskBodies());
			HtmlGenerator.SaveToFile(EmailContent_Delay, EmailSubject_Delay);
			/*
			 * 每日新提CR提醒
			 */
			EmailContent_New = htmlgene.renderResultHtml(htmlgene.getNewTaskBodies());
			HtmlGenerator.SaveToFile(EmailContent_New, EmailSubject_New);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}    
		//发送提醒邮件
		BrEmailSend.sendEmail(EmailAccoutName, EmailPassword, SendAddress, destAddress_Delay, CC_Address_Delay, EmailSubject_Delay, EmailContent_Delay);		
		BrEmailSend.sendEmail(EmailAccoutName, EmailPassword, SendAddress, destAddress_New, CC_Address_New, EmailSubject_New, EmailContent_New);
		log.info("BugRemind邮件发送完毕,请查收!");
	}
	
}

