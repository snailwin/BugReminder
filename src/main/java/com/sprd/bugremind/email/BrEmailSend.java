package com.sprd.bugremind.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BrEmailSend {
	private static final Log log = LogFactory.getLog(BrEmailSend.class);
	
	/**
	 * 发送文本邮件
	 * @param UserName
	 * @param PassWord
	 * @param FromAddress
	 * @param ToAddress
	 * @param Subject
	 * @param Content
	 */
	public static void sendEmail(String UserName, String PassWord, String FromAddress, String ToAddress, String CC_Address, String Subject,
			String Content) {
		if(!isEmail(ToAddress)){
			return;
		}
		MailSenderInfo mailInfo = new MailSenderInfo();
		
		//邮件内容
		mailInfo.setMailServerHost("mail.spreadtrum.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName(UserName);
		mailInfo.setPassword(PassWord);// 您的邮箱密码
		mailInfo.setFromAddress(FromAddress);
		mailInfo.setToAddress(ToAddress);
		mailInfo.setCcAddress(CC_Address);
		mailInfo.setSubject(Subject);
		mailInfo.setContent(Content);
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
//		sms.sendTextMail(mailInfo);// 发送txt格式 
		sms.sendHtmlMail(mailInfo);
		log.info("BugReminder正在发送邮件...");
	}			
	
	/**
	    * 
	    * @param email
	    * @return
	    */
		public static boolean isEmail(String email) {
			if (null == email || "".equals(email))
				return false;
			String[] emails = email.split(";");
			Pattern p = Pattern
					.compile("\\w+([-+.]\\w+)*");// 匹配
			for (int i = 0; i < emails.length; i++) {
				Matcher m = p.matcher(emails[i]);
				if (!m.matches())
					return false;
			}
			return true;
		}
}
