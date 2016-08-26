package com.sprd.bugremind.email;


import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address; 
import javax.mail.BodyPart; 
import javax.mail.Message; 
import javax.mail.MessagingException; 
import javax.mail.Multipart; 
import javax.mail.SendFailedException;
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.smtp.SMTPAddressFailedException;

	/** 
	* 简单邮件（不带附件的邮件）发送器 
	*/ 
	public class SimpleMailSender  { 
		private static final Log log = LogFactory.getLog(SimpleMailSender.class);
	/** 
	  * 以文本格式发送邮件 
	  * @param mailInfo 待发送的邮件的信息 
	  */ 
		public boolean sendTextMail(MailSenderInfo mailInfo) { 
		  // 判断是否需要身份认证 
		  MyAuthenticator authenticator = null; 
		  Properties pro = mailInfo.getProperties();
		  if (mailInfo.isValidate()) { 
		  // 如果需要身份认证，则创建一个密码验证器 
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword()); 
		  }
		  // 根据邮件会话属性和密码验证器构造一个发送邮件的session 
		  Session sendMailSession = Session.getDefaultInstance(pro,authenticator); 
		  try { 
		  // 根据session创建一个邮件消息 
		  Message mailMessage = new MimeMessage(sendMailSession); 
		  // 创建邮件发送者地址 
		  Address from = new InternetAddress(mailInfo.getFromAddress()); 
		  // 设置邮件消息的发送者 
		  mailMessage.setFrom(from); 
		  // 创建邮件的接收者地址，并设置到邮件消息中 
		  List<Address> recipentList = new ArrayList<Address>();
		  for(String recipient : mailInfo.getToAddress().split(";")){
			  Address to = null;
			  if(recipient.contains("spreadtrum.com"))
				  to = new InternetAddress(recipient); 
			  else
				  to = new InternetAddress(recipient + "@spreadtrum.com");
			  recipentList.add(to);
			  // Message.RecipientType.TO属性表示接收者的类型为TO 
		  }
		  Address[] list = new Address[recipentList.size()];
		  recipentList.toArray(list);
		  mailMessage.addRecipients(Message.RecipientType.TO,list);
		  
		  // 设置邮件消息的主题 
		  mailMessage.setSubject(mailInfo.getSubject()); 
		  // 设置邮件消息发送的时间 
		  mailMessage.setSentDate(new Date()); 
		  // 设置邮件消息的主要内容 
		  String mailContent = mailInfo.getContent(); 
		  mailMessage.setText(mailContent); 
		  // 发送邮件 
		  Transport.send(mailMessage);
		  return true; 
		  } catch (MessagingException ex) {
			  ex.printStackTrace(); 
		  } 
		  return false; 
		} 
		
		/** 
		  * 以HTML格式发送邮件 
		  * @param mailInfo 待发送的邮件信息 
		  */ 
		public boolean sendHtmlMail(MailSenderInfo mailInfo) {
		  // 判断是否需要身份认证 
		  MyAuthenticator authenticator = null;
		  Properties pro = mailInfo.getProperties();
		  //如果需要身份认证，则创建一个密码验证器  
		  if (mailInfo.isValidate()) { 
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		  } 
		  // 根据邮件会话属性和密码验证器构造一个发送邮件的session 
		  Session sendMailSession = Session.getDefaultInstance(pro,authenticator); 
		  try { 
		  // 根据session创建一个邮件消息 
		  Message mailMessage = new MimeMessage(sendMailSession); 
		  // 创建邮件发送者地址 
		  Address from = new InternetAddress(mailInfo.getFromAddress()); 
		  // 设置邮件消息的发送者 
		  mailMessage.setFrom(from); 
		  // 创建邮件的接收者地址，并设置到邮件消息中 
		  List<Address> recipentList = new ArrayList<Address>();
		  for(String recipient : mailInfo.getToAddress().split(";")){
			  Address to = null;
			  if(recipient.contains("spreadtrum.com"))
				  to = new InternetAddress(recipient); 
			  else
				  to = new InternetAddress(recipient + "@spreadtrum.com");
			  recipentList.add(to);
			  // Message.RecipientType.TO属性表示接收者的类型为TO 
		  }
		  Address[] list = new Address[recipentList.size()];
		  recipentList.toArray(list);
		  mailMessage.addRecipients(Message.RecipientType.TO,list);
		// 创建邮件的抄送者地址，并设置到邮件消息中 
		  List<Address> recipent_ccList = new ArrayList<Address>();
		  for(String recipient_cc : mailInfo.getCcAddress().split(";")){
			  Address cc = null;
			  if(recipient_cc.contains("spreadtrum.com"))
				  cc = new InternetAddress(recipient_cc); 
			  else
				  cc = new InternetAddress(recipient_cc + "@spreadtrum.com");
			  recipent_ccList.add(cc);
			  // Message.RecipientType.CC属性表示接收者的类型为CC 
		  }
		  Address[] cclist = new Address[recipent_ccList.size()];
		  recipent_ccList.toArray(cclist);
		  try{
			  mailMessage.addRecipients(Message.RecipientType.CC,cclist);
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  // 设置邮件消息的主题 
		  mailMessage.setSubject(mailInfo.getSubject()); 
		  // 设置邮件消息发送的时间 
		  mailMessage.setSentDate(new Date()); 
		  // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象 
		  
		  Multipart multipart = new MimeMultipart();
		  MimeBodyPart html = new MimeBodyPart();
		  
		  html.setContent(mailInfo.getContent(), "text/html; charset=utf-8"); 
		  multipart.addBodyPart(html);
//		  String fileName = "E:/resources/as/results/SP7730A/0206TestRunner/2015.02.12_13.38.45/testResult.xml";
		  if(mailInfo.getAttachFileNames() != null){
			  for(String fileName : mailInfo.getAttachFileNames()){
					if (fileName != null)
						addAttachment(multipart, fileName);
			  }
		  }
		  // 将MiniMultipart对象设置为邮件内容 
		  mailMessage.setContent(multipart); 
		  // 发送邮件 
		  try {
			  Transport.send(mailMessage); 
		  } catch(SendFailedException se) {
	             Address[] unsend = se.getValidUnsentAddresses(); 
	             if(null!=unsend)  
	             {    
	                 log.debug(" ==valid Addresses"); 
	                 String validAddress = "";  
	                 for(int i=0;i<unsend.length;i++){  
	                     validAddress += unsend[i] + ";";  
	                     log.debug((i+1)+": " + unsend[i]);  
	                 }  
	                 validAddress = validAddress.substring(0,validAddress.length()-1);
	                 System.out.println("All Invalid: "+validAddress);
	             }
	   		  }  finally {
	   			log.debug("SendEmail Over!");
		         }  
		  
		  return true; 
		  } catch (MessagingException ex) { 
			  ex.printStackTrace(); 
		  } 
		  return false; 
		}
		
		// 添加附件
		private static void addAttachment(Multipart multipart, String fileName) throws MessagingException
		{
		    DataSource source = new FileDataSource(fileName);
		    BodyPart messageBodyPart = new MimeBodyPart();        
		    messageBodyPart.setDataHandler(new DataHandler(source));
	        
		    int pos = fileName.lastIndexOf("/");
	        String attFileName = null;
	        if (pos == -1)
	        	attFileName = fileName;
	        attFileName = fileName.substring(pos + 1, fileName.length());
		    
			messageBodyPart.setFileName(attFileName);
		    multipart.addBodyPart(messageBodyPart);
		}
		
} 
	



