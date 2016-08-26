package com.sprd.bugremind.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sprd.bugremind.buginfo.BugJonParaser;


public class HtmlGenerator {
	private static final Log log = LogFactory.getLog(HtmlGenerator.class);
	private static String BaseURL = "http://bugzilla.spreadtrum.com/bugzilla/rest/bug?";
	//用户登录token信息 读取BRConfig.XML
	private static String UserToken = GetConfigXML.getXmlValue("BRConfig.xml", "UserToken", "value")[0];
	/* 测试Reporter名单 读取BRConfig.XML */
	private static String Reporters = GetConfigXML.getXmlValue("BRConfig.xml", "Reporters", "value")[0];
	//URL后缀
	private static String URLSuffix= "&resolution=---&list_id=10781644&query_format=advanced";
	private static String EmailSubject_Delay = GetConfigXML.getXmlValue("BRConfig.xml", "EmailSubject_Delay", "value")[0];	
	private static String EmailSubject_New = GetConfigXML.getXmlValue("BRConfig.xml", "EmailSubject_New", "value")[0];	
	private static String needInfoMes= "";
	private static String wontFixMes= "";
	private static String assigneeMes= "";
	private static String fixedMes= "";
	private static String longdissovledMes= "";
	private static String highpriorityUrlMes = "";

	/*
	 * 筛选条件REST连接
	 */
	//所有项目新提的1、2级必现Bug每日提醒（提bug时间小于等于1天）
	private static String highpriorityUrl = BaseURL + UserToken
			//测试人员
			+ "&f1=reporter"+"&o1=anywords"+"&v1="+ Reporters
			//bug severity：1-Critical,2-Major
			+ "&f2=bug_severity"+"&o2=anywordssubstr"+"&v2=1-Critical%2C2-Major"
			//bug概率
			+ "&f3=cf_probability"+"&o3=substring"+"&v3=1-Always"
			//Days since bug changed 小于等于1天
			+ "&f4=days_elapsed"+"&o4=lessthaneq"+"&v4=1" 
			+ URLSuffix;
	//Needinfo超1天未处理
	private static String needInfoUrl = BaseURL + UserToken
			//测试人员
			+ "&f1=reporter"+"&o1=anywords"+"&v1="+ Reporters
			//bug status： needInfo
			+ "&f2=bug_status"+"&o2=substring"+"&v2=Need_Info"
			//Days since bug changed 超过1天
			+ "&f3=days_elapsed"+"&o3=greaterthan"+"&v3=1" 
			+ URLSuffix;
	//Won't fix未处理		
	private static String wontfixedUrl = BaseURL + UserToken
			//测试人员
			+ "&f1=reporter"+"&o1=anywords"+"&v1="+ Reporters
			//bug status： WONTFIX
			+ "&f2=bug_status"+"&o2=substring"+"&v2=WONTFIX"
			//Days since bug changed 超过0天
			+ "&f3=days_elapsed"+"&o3=greaterthan"+"&v3=0" 
			+ URLSuffix;
	//Assignee到FO/Tester未及时转出的Bug		
	private static String assigneeUrl = BaseURL + UserToken
			//assigned_tos测试人员
			+ "&f1=assigned_to"+"&o1=anywords"+"&v1="+ Reporters
			//bug status： NEW,Assigned
			+ "&f2=bug_status"+"&o2=anywordssubstr"+"&v2=NEW%2CAssigned"
			//Days since bug changed 超过0天
			+ "&f3=days_elapsed"+"&o3=greaterthan"+"&v3=0" 
			+ URLSuffix;
	//2015-1-1之后提交的，Fix超三周未处理		
	private static String fixed = BaseURL + UserToken
			//测试人员
			+ "&f1=reporter"+"&o1=anywords"+"&v1="+ Reporters
			//bug status： Fixed
			+ "&f2=bug_status"+"&o2=substring"+"&v2=Fixed"
			//2015-1-1以来提交的bug
			+ "&f3=creation_ts"+"&o3=greaterthan"+"&v3=2015-1-1"
			//Days since bug changed 超过3周
			+ "&f4=days_elapsed"+"&o4=greaterthan"+"&v4=22" 
			+ URLSuffix;
	//今年2月1号之后提交的一二级（new或assign）问题，且超三周未处理，需要测试提醒研发更新bug进度
	private static String longdissovledUrl = BaseURL + UserToken
			//测试人员
			+ "&f1=reporter"+"&o1=anywords"+"&v1="+ Reporters
			//bug status： NEW,Assigned
			+ "&f2=bug_status"+"&o2=anywordssubstr"+"&v2=NEW%2CAssigned"
			//2015-1-1以来提交的bug
			+ "&f3=creation_ts"+"&o3=greaterthan"+"&v3=2015-2-1"
			//Days since bug changed 超过3周
			+ "&f4=days_elapsed"+"&o4=greaterthan"+"&v4=22" 
			//bug等级
			+ "&f5=bug_severity"+"&o5=anywordssubstr"+"&v5=1-Critical%2C2-Major" 
			+ URLSuffix;
	public static void main (String [] args) {
		HtmlGenerator htmlGenerator =new HtmlGenerator();
		String WaitingTask = htmlGenerator.renderResultHtml(htmlGenerator.getWaitingTaskBodies());
		String NewTask = htmlGenerator.renderResultHtml(htmlGenerator.getNewTaskBodies());
		SaveToFile(NewTask, EmailSubject_New);
		SaveToFile(WaitingTask, EmailSubject_Delay);
		System.out.println(SaveToFile(WaitingTask, EmailSubject_Delay));
		System.out.println(SaveToFile(NewTask, EmailSubject_New));
	}
	/**
	 * 待处理bug数据
	 * @return
	 */
	public String  getWaitingTaskBodies () {
		BugJonParaser bugJsonParaser = new BugJonParaser();	
		StringBuilder WaitingTaskHtml = new StringBuilder();
		//获取筛选信息
		needInfoMes = bugJsonParaser.logInGetJson(needInfoUrl);
		log.info("Login bugzilla, paraser needInfoUrl success...\n");
		wontFixMes = bugJsonParaser.logInGetJson(wontfixedUrl);
		log.info("Login bugzilla, paraser wontfixedUrl success...\n");
		assigneeMes = bugJsonParaser.logInGetJson(assigneeUrl);
		log.info("Login bugzilla, paraser assigneeUrl success...\n");
		fixedMes = bugJsonParaser.logInGetJson(fixed); 
		log.info("Login bugzilla, paraser fixed success...\n");
		longdissovledMes = bugJsonParaser.logInGetJson(longdissovledUrl);					
		log.info("Login bugzilla, paraser longdissovledUrl success...\n");
		
		/*HTML bodies 组合到一起*/
		//needInfoMes
		if (!needInfoMes.equals("</tr>")) {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'> Needinfo超1天未处理</p></font></h2>\n");
			WaitingTaskHtml.append("<table cellspacing='0' cellpadding='0' border='1' class='collapse'><tbody ><tr class='trbgeven'>"
					+ "<th><b class='bterm'>BugID</b></th>"
					+ "<th><b class='bterm'>Bugstatus</b></th>"
					+ "<th><b class='bterm'>Elapseday</b></th>"
					+ "<th><b class='bterm'>Reportor</b></th>"
					+ "<th><b class='bterm'>Product</b></th>"
					+ "<th><b class='bterm'>Severity</b></th>"
					+ "<th><b class='bterm'>Probability</b></th>"
					+ "<th><b class='bterm'>Component</b></th>"
					+ "<th><b class='bterm'>FeatureOwner</b></th>"+ "</tr>\n");
			WaitingTaskHtml.append(needInfoMes);
			WaitingTaskHtml.append("</tbody></table>\n");
		}   else {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'> 截止到邮件发送时，没有符合Needinfo超1天未处理条件的CR!</p></font></h2>\n");
		}		
		//wontFixMes
		if (!wontFixMes.equals("</tr>")) {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>Won't fix未处理</p></font></h2>\n");
			WaitingTaskHtml.append("<table cellspacing='0' cellpadding='0' border='1' class='collapse'><tbody ><tr class='trbgeven'>"
					+ "<th><b class='bterm'>BugID</b></th>"
					+ "<th><b class='bterm'>Bugstatus</b></th>"
					+ "<th><b class='bterm'>Elapseday</b></th>"
					+ "<th><b class='bterm'>Reportor</b></th>"
					+ "<th><b class='bterm'>Product</b></th>"
					+ "<th><b class='bterm'>Severity</b></th>"
					+ "<th><b class='bterm'>Probability</b></th>"
					+ "<th><b class='bterm'>Component</b></th>"
					+ "<th><b class='bterm'>FeatureOwner</b></th>"+ "</tr>\n");
			WaitingTaskHtml.append(wontFixMes);
			WaitingTaskHtml.append("</tbody></table>\n");
		}   else {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>截止到邮件发送时，没有符合wontfix条件的CR!</p></font></h2>\n");
		}		
		//assigneeMes
		if (!assigneeMes.equals("</tr>")) {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>Assignee到FO/Tester未及时转出的Bug</p></font></h2>\n");
			WaitingTaskHtml.append("<table cellspacing='0' cellpadding='0' border='1' class='collapse'><tbody ><tr class='trbgeven'>"
					+ "<th><b class='bterm'>BugID</b></th>"
					+ "<th><b class='bterm'>Bugstatus</b></th>"
					+ "<th><b class='bterm'>Elapseday</b></th>"
					+ "<th><b class='bterm'>Reportor</b></th>"
					+ "<th><b class='bterm'>Product</b></th>"
					+ "<th><b class='bterm'>Severity</b></th>"
					+ "<th><b class='bterm'>Probability</b></th>"
					+ "<th><b class='bterm'>Component</b></th>"
					+ "<th><b class='bterm'>FeatureOwner</b></th>"+ "</tr>\n");
			WaitingTaskHtml.append(assigneeMes);
			WaitingTaskHtml.append("</tbody></table>\n");
		}   else {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>截止到邮件发送时，没有符合assignee到Tester条件的CR!</p></font></h2>\n");
		}
		
		//fixedMes
		if (!fixedMes.equals("</tr>")) {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>2015-1-1之后提交的，Fix超三周未处理</p></font></h2>\n");
			WaitingTaskHtml.append("<table cellspacing='0' cellpadding='0' border='1' class='collapse'><tbody ><tr class='trbgeven'>"
					+ "<th><b class='bterm'>BugID</b></th>"
					+ "<th><b class='bterm'>Bugstatus</b></th>"
					+ "<th><b class='bterm'>Elapseday</b></th>"
					+ "<th><b class='bterm'>Reportor</b></th>"
					+ "<th><b class='bterm'>Product</b></th>"
					+ "<th><b class='bterm'>Severity</b></th>"
					+ "<th><b class='bterm'>Probability</b></th>"
					+ "<th><b class='bterm'>Component</b></th>"
					+ "<th><b class='bterm'>FeatureOwner</b></th>"+ "</tr>\n");
			WaitingTaskHtml.append(fixedMes);
			WaitingTaskHtml.append("</tbody></table>\n");
		}   else {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>截止到邮件发送时，没有符合2015-1-1之后提交的,fixed超过3周未处理条件的CR!</p></font></h2>\n");
		}
		
		//longdissovledMes
		if (!longdissovledMes.equals("</tr>")) {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>今年2月1号之后提交的一二级（new或assign）问题，且超三周未处理，需要测试提醒研发更新bug进度</p></font></h2>\n");
			WaitingTaskHtml.append("<table cellspacing='0' cellpadding='0' border='1' class='collapse'><tbody ><tr class='trbgeven'>"
					+ "<th><b class='bterm'>BugID</b></th>"
					+ "<th><b class='bterm'>Bugstatus</b></th>"
					+ "<th><b class='bterm'>Elapseday</b></th>"
					+ "<th><b class='bterm'>Reportor</b></th>"
					+ "<th><b class='bterm'>Product</b></th>"
					+ "<th><b class='bterm'>Severity</b></th>"
					+ "<th><b class='bterm'>Probability</b></th>"
					+ "<th><b class='bterm'>Component</b></th>"
					+ "<th><b class='bterm'>FeatureOwner</b></th>"+ "</tr>\n");
			WaitingTaskHtml.append(longdissovledMes);
			WaitingTaskHtml.append("</tbody></table>\n");
		}   else {
			WaitingTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>截止到邮件发送时,没有符合今年2月1号之后提交的一二级（new或assign）问题，且超三周未处理，需要测试提醒研发更新bug进度</p></font></h2>\n");
		}		
		return WaitingTaskHtml.toString();
		
	}
	/**
	 * 新提1、2级必现bug数据
	 * @return
	 */
	public String  getNewTaskBodies () {
		BugJonParaser bugJsonParaser = new BugJonParaser();	
		StringBuilder NewTaskHtml = new StringBuilder();
		//获取筛选信息
	    highpriorityUrlMes = bugJsonParaser.logInGetJson(highpriorityUrl);
	    log.info("Login bugzilla, paraser highpriorityUrl success...\n");
	    //highpriorityUrlMes
	  	if (!highpriorityUrlMes.equals("</tr>")) {
	  		NewTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>所有项目新提交1、2级必现Bug每日提醒</p></font></h2>\n");
	  		NewTaskHtml.append("<table cellspacing='0' cellpadding='0' border='1' class='collapse'><tbody ><tr class='trbgeven'>"
	  				+ "<th><b class='bterm'>BugID</b></th>"
	  				+ "<th><b class='bterm'>Bugstatus</b></th>"
	  				+ "<th><b class='bterm'>Elapseday</b></th>"
	  				+ "<th><b class='bterm'>Reportor</b></th>"
	  				+ "<th><b class='bterm'>Product</b></th>"
	  				+ "<th><b class='bterm'>Severity</b></th>"
	  				+ "<th><b class='bterm'>Probability</b></th>"
	  				+ "<th><b class='bterm'>Component</b></th>"
	  				+ "<th><b class='bterm'>FeatureOwner</b></th>"+ "</tr>\n");
	  		NewTaskHtml.append(highpriorityUrlMes);
	  		NewTaskHtml.append("</tbody></table>\n");
	  	}   else {
	  		NewTaskHtml.append("<h2><font face='微软雅黑'><p cellspacing='0' cellpadding='0' border='1' class='bterm'>截止到邮件发送时，未检索出新提交1、2级必现Bug!</p></font></h2>\n");
	  	}
		return NewTaskHtml.toString();		
	}
	
	/**
	 * 生成HTML
	 * @param HtmlBody
	 * @return
	 */
	public String renderResultHtml(String HtmlBody){
		Date date= new Date();    	
		SimpleDateFormat simpledateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpledateform.format(date);
		//HTML String 
		StringBuilder html = new StringBuilder();
		String traversalmodel=null;
		@SuppressWarnings("unused")
		String font=null;
		
		Locale locale = Locale.getDefault();  
		String language=locale.getLanguage();
		if(language.equals("zh")){
			font="微软雅黑";
		}else{
			font="Microsoft YaHei";
		}
		html.append("<!doctype html>\n");
		html.append("<html lang='zh'>\n");

		html.append("<head>\n");
		html.append("<meta charset='utf-8'>\n");
		html.append("<title>BugReminder_Test_TJ_CR状态_" + currentTime.substring(0, currentTime.length()-9) +"</title>\n");
		html.append("<style media='screen' type='text/css'> body{margin:0;}h1{background:#4F94CD;margin:0;font-size:28px;color:#FFFFFF;padding:10px 30px;}." +
				"description{background:#E4EFF1;padding:5px 30px;margin:0;font-size:12px;color:#B03060;text-align:right;}#content{max-width:1680px;margin:0 auto;padding:1px 1px;color:#B03060;font-family:Times New Roman;font-size:12px;background-color:#FFF5DA;}" +
				"#summary{float:left;width:40%;margin-right:10%;}#summary .total{color:#FF8F00;font-size:20px;padding-bottom:5px;font-family:microsoft yahei;font-weight:bold;}.tablearea h2{font-family:microsoft yahei;color:#000000;}" +
				"#device.tablearea h2{padding: 5px 20px;font-size:16px}.tablearea .seperator{color:#CA7575;padding:0 10px;}.tablearea h2 span{color:#4F94CD;}" +
				"#summary .trbgeven{background-color:#FFF5DA;}#summary .trbgodd{background:#fff;}" +
				"#summary table{}#device{float:left;width:50%;}.div{ height:auto!important;height:100px;min-height:100px;}" +
				"#exception{clear:both;}.collapse{font-size:12px;border-collapse:collapse;margin-bottom:20px;width:100%;}." +
				"collapse .collapse,.cntTblNoBorder{border-collapse:collapse;border-bottom:0px;}.collapse th,.cntTblNoBorder th{font-family:microsoft yahei;text-align:left;padding:5px 10px 5px 5px;background:#4F94CD !important;font-size:1.12em;color:#FFFFFF;font-weight:normal;}" +
				"#detail{clear:both;}.collapse{font-size:12px;border-collapse:collapse;margin-bottom:20px;width:100%;}.collapse.collapse,.cntTblNoBorder{border-collapse:collapse;border-bottom:0px;}.collapse th,.cntTblNoBorder " +
				"th{font-family:Times New Roman;text-align:center;padding:3px 5px 5px 3px;background:#FFDEAD !important;font-size:1.2em;color:#000000;font-weight:normal;}.collapse " +
				"td{padding:10px 5px;margin:0px 0px 0px 0px;}tr.fail{background:#FF7373;color:#fff}.pass td.green{color:green}.trbgeven{background-color:#FFF5DA;padding:0;vertical-align:center;}.trbgodd{background-color:#ffffff;padding:0;vertical-align:center;}." +
				"collapse td.name{width:25%;font-weight:bold;font-family:Times New Roman;border-right:1px solid #fff;}</style>");
		html.append("</head>\n\n");

		html.append("<h1><font color='#ffffff' face='微软雅黑'>Bug Reminder Email</font></h1>\n");
//		html.append("<h3 class='description'>Generated on " + split[1] + "</h3>\n");

		html.append("<body><div id='content'>\n");
		
		//BugID  Bugstatus Elapseday Reportor  Product   Severity   Probability     Component    FeatureOwner 
		html.append("<div id='tableoverflow'>\n");

		html.append(HtmlBody);	
	
		html.append("</div></div></body>\n\n");

		html.append("</html>");
		
		return html.toString();
		
	}
	/**
	 * 
	 * @param string
	 * @return 
	 */
	public static  String SaveToFile(String string, String emailsubject) {
		Date date= new Date();    	
		SimpleDateFormat simpledateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpledateform.format(date);
		String filename ="results\\" + emailsubject +currentTime.substring(0, currentTime.length()-9) +".html";
		
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BufferedWriter writer=null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return filename;
	}
	/**
     * 获取项目的相对路径下文件的绝对路径
     * 
     * @param parentDir
     *            目标文件的父目录,例如说,工程的目录下,有lib与bin和conf目录,那么程序运行于lib or
     *            bin,那么需要的配置文件却是conf里面,则需要找到该配置文件的绝对路径
     * @param fileName
     *            文件名
     * @return 一个绝对路径
     */
    public static String getPath(String parentDir, String fileName) {
        String path = null;
        String userdir = System.getProperty("user.dir");
        String userdirName = new File(userdir).getName();
        if (userdirName.equalsIgnoreCase("lib")
                || userdirName.equalsIgnoreCase("bin")) {
            File newf = new File(userdir);
            File newp = new File(newf.getParent());
            if (fileName.trim().equals("")) {
                path = newp.getPath() + File.separator + parentDir;
            } else {
                path = newp.getPath() + File.separator + parentDir
                        + File.separator + fileName;
            }
        } else {
            if (fileName.trim().equals("")) {
                path = userdir + File.separator + parentDir;
            } else {
                path = userdir + File.separator + parentDir + File.separator
                        + fileName;
            }
        }
 
        return path;
    }
}