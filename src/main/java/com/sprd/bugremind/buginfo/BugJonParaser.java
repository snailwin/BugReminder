package com.sprd.bugremind.buginfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sprd.bugremind.bean.BugFilterInfo;
import com.sprd.bugremind.bugdata.BugDataBase;

/**
 * 登录URL解析JSON数据获取
 * @author Zhengxiao.Su
 *
 */
public class BugJonParaser {
	private static String fixed = "http://bugzilla.spreadtrum.com/bugzilla/rest/bug?token=9779-xnQpkEnxva&f1=reporter&o1=anywords&v1=Huanming.Han%20Jieying.Li%20Zhengxiao.SU,Linlin.Li,Guoquan.Qin,Jia.Tian,Jianying.Shi,Jiaqi.Chen,Lisa.Zhang,Min.Xu,Qi.Hao,Ran.Lu,Shuo.Liu,Xiuguang.Ma,Xuefei.Yang,Yanli.Zhang,Ying.Meng,Zhuan.Yao,Haidong.Wang,Hualiang.Zhang,Jinliang.Wang,Qiuyu.Jia,Runnan.Jiao,Tingting.Su,Xiangbo.Song,Xin.Ma,Yutao.Xu,Zhihua.Zhang,Andy.Wang,Candy.Liu,Fan.Ding,Fenghua.Chen,Huanming.Han,Hui.Qian,Jia.Tian,Jieying.Li,Jinliang.Wang,Lianjie.Sun,Linlin.Li,Lisa.Zhang,Melo.Yang,Min.Xu,Moon.Li,Qian.Sun,Qikai.Xie,Qiuyu.Jia,Suya.Shang,Tingting.Su,Tong.Li,Wenchao.Li,Xiangbo.Song,Xiao.Feng,Xuedong.Wu,Ying.Meng,Yue.Chen,Chen.Yu,Dandan.Zhang,Fangfang.Li,Fenghua.Chen,Lisa.Zhang,Rongxin.Liu,Suya.Shang,Tingting.Su,Tong.Li,Tong.Qi,Wenchao.Li,Xiaobo.Li,Ya.Wang,Cy.Wang,Guoquan.Qin,Haidong.Wang,Hongbin.Liu,Hualiang.Zhang,Janna.Jiang,Jianying.Shi,Jiaqi.Chen,Lin.Niu,Luna.Li,Qi.Hao,Qikai.Xie,Ran.Lu,Runnan.Jiao,Sheng.Li,Shuo.Liu,Tangh.Lin,Tongfeng.Liu,Xin.Ma,Xiuguang.Ma,Xuefei.Yang,Yanli.Zhang,Yedda.Zhang,Yutao.Xu,Zhihua.Zhang,Zhuan.Yao,Zihui.Yuan,Zuxin.HuaAndy.Wang,Candy.Liu,Dandan.Zhang,Fan.Ding,Lianjie.Sun,Lin.Niu,Luna.Li,Melo.Yang,Moon.Li,Qian.Sun,Suya.Shang,Xiao.Feng,Xuedong.Wu,Yedda.Zhang,Ying.Meng,Chen.Yu,Cy.Wang,Fenghua.Chen,Hongbin.Liu,Rongxin.Liu,Sheng.Li,Tong.Li,Tong.Qi,Tongfeng.Liu,Xiaobo.Li,Yue.Chen,Zuxin.Hua,Tingting.Su,Tong.Li,Wenchao.Li,Ying.Meng,Zhiyong.Ding,Xinhong.Zhang,Jiajia.Li,Chong.Wang,Haijie.Lin,Hongyan.Cheng,Jingwen.Chen,Mengchen.Hu,Elaine.Zhang,Crytal.Wang,Bin.Ding&f2=bug_status&o2=substring&v2=Fixed&v3=2015-1-1&resolution=---&list_id=10781644&query_format=advanced&f3=creation_ts&o3=greaterthan&o4=greaterthan&v4=50";
	private static final Log log = LogFactory.getLog(BugJonParaser.class);
	private static String HtmlBody = "";
    private static ArrayList<String> list = new ArrayList<String>();
	
	public static void main (String [] args) {
		BugJonParaser bugjsonparaser = new BugJonParaser();
		bugjsonparaser.logInGetJson(fixed);
	} 
	/**
     * 登录网址获取数据到本地
	 * @return 
     */
	public String logInGetJson(String SearchURL) { 
		BugFilterInfo buginfo = new BugFilterInfo();
		StringBuilder htmlbody = new StringBuilder();	
		String featurename = "----";
		//数据库
		BugDataBase bugdatabase = new BugDataBase();
		Connection connect = null;
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			//Login Bugzilla by API
			URL url = new URL(SearchURL);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.63 Safari/534.3  Gecko/20100101 Firefox/37.0");
			
			connection.setConnectTimeout(60*1000);
			connection.setReadTimeout(60*1000);
 			InputStream instream = connection.getInputStream();
 			BufferedReader bufread = new BufferedReader(new InputStreamReader(instream, "UTF-8"));		
 			String line1;
 			StringBuilder sbuild = new StringBuilder();
 			while ((line1 = bufread.readLine()) != null) {
 				sbuild.append(line1);
 			}
 			JSONObject jsonObject = new JSONObject(sbuild.toString());
 			JSONArray jsonMainArray = jsonObject.getJSONArray("bugs");
			for (int i = 0; i < jsonMainArray.length(); i++) {  // **line 2**
	    		 JSONObject childJSONObject = jsonMainArray.getJSONObject(i);
	    	
    		 
	    	//解析bug JSON 信息至bean
	    	buginfo.setId(childJSONObject.getInt("id")); //bugid
	    	buginfo.setStatus(childJSONObject.getString("status"));//bugstatus
	    	
	    	/*计算LastChangeTime与当前时间差*/
	    	Calendar calendar = Calendar.getInstance();
	        SimpleDateFormat simpledateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	String LastChangeTime = childJSONObject.getString("last_change_time").subSequence(0, 10) + " " + childJSONObject.getString("last_change_time").subSequence(11, 19); 
	    	String CurrentDateTime =  simpledateform.format(calendar.getTime()).toString();
	    	buginfo.setLast_change_time(LastChangeTime);
	    	String Elapsedday = ElapsedDate(LastChangeTime, CurrentDateTime);
	    	//添加Elapsed_day
	    	buginfo.setElapsedday(Elapsedday);//elapsed day
	    	
	    	buginfo.setCreator(childJSONObject.getString("creator").substring(0, childJSONObject.getString("creator").length()-15));//reporter
	    	buginfo.setProduct(childJSONObject.getString("product"));//product
	    	buginfo.setPriority(childJSONObject.getString("priority"));//priority
	    	buginfo.setSummary(childJSONObject.getString("summary"));//	summary
	    	/*需求：Component对应FO name*/
	    	buginfo.setComponent(childJSONObject.getString("component"));//component
	        
	    	//数据库连接	 
	        connect = bugdatabase.getConnection();
	        statement = connect.createStatement();
	        String query = "select * from bugzilla.folist where component = '"+ childJSONObject.getString("component") +"'";
	        resultSet = statement.executeQuery(query); 
            while (resultSet.next()) {           	
            	featurename = resultSet.getString("featureowner");    	
            }
            //添加FO_Name
            buginfo.setFeatureowner(featurename);//  featureowner
            if(featurename.equals("----")) {
    	        String summary = childJSONObject.getString("summary");
    	        String summaryquery = "select * from bugzilla.summarykey";
    	        resultSet = statement.executeQuery(summaryquery); 
    	        Map<String, String> summarymap =new HashMap<String, String>();
    	        
                while (resultSet.next()) { 
                	summarymap.put(resultSet.getString("keyword"), resultSet.getString("featureowner"));
                }
                
              //遍历summarymap Map
                Set<String> set = summarymap.keySet();
                for(Iterator<String> iter = set.iterator(); iter.hasNext();){
                	String key = iter.next();
                	String value = summarymap.get(key);
                	if (summary.contains(key)){               		 
                		buginfo.setFeatureowner(value);//  featureowner
                    }
                }
            }
            //close MySQL
            resultSet.close();
            statement.close();
            connect.close();
	    	buginfo.setSeverity(childJSONObject.getString("severity"));//severity
	    	buginfo.setProbability(childJSONObject.getString("cf_probability").substring(0, childJSONObject.getString("cf_probability").length()-11));//probability
   	
	    	//邮件内容存入数据库表emailcontent
	    	String InsertComment = "insert into  bugzilla.emailcontent values (default, ?, ?, ?, ? , ?, ?,?,?,?,?,?)";
//	    	String Insertlabels = "SELECT bugid, bugstatus, elapsed_day, creator, product, severity, probability, component , probability, featureowner, summary from bugzilla.emailcontent";
	    	connect = bugdatabase.getConnection();
	    	//当前时间
	    	Date date= new Date();
	    	String currentTime = simpledateform.format(date);
	    	
	    	preparedStatement = connect.prepareStatement(InsertComment);
 		    preparedStatement.setLong(1, buginfo.getId());
 		    preparedStatement.setString(2, buginfo.getStatus());
 		    preparedStatement.setString(3, buginfo.getElapsedday());
 		    preparedStatement.setString(4, buginfo.getCreator());
 		    preparedStatement.setString(5, buginfo.getProduct());
 		    preparedStatement.setString(6, buginfo.getSeverity());
		    preparedStatement.setString(7, buginfo.getProbability());
 		    preparedStatement.setString(8, buginfo.getComponent());
 		    preparedStatement.setString(9, buginfo.getFeatureowner());  		      		   
 		    preparedStatement.setString(10, buginfo.getSummary());
 		    preparedStatement.setString(11, currentTime);
 		    preparedStatement.executeUpdate();
 		    preparedStatement.close();
            connect.close();
	    	
	    	//HTML 本行数据背景颜色
	    	String bg = (i % 2 == 0) ? "trbgeven" : "trbgodd";
            //HTML5 body
	    	htmlbody.append("<tr class='"+ bg +"'>\n");
	    	String bugid = String.format("%-8d", buginfo.getId());
	    	htmlbody.append("<td><a href='http://bugzilla.spreadtrum.com/bugzilla/show_bug.cgi?id=" + bugid + "'>"+ bugid + "</a></td>\n");
	    	htmlbody.append("<td>" + buginfo.getStatus()      + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getElapsedday()  + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getCreator()     + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getProduct()     + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getSeverity()    + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getProbability() + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getComponent()   + "</td>\n");
	    	htmlbody.append("<td>" + buginfo.getFeatureowner()+ "</td>\n");	    	
	    	}
			bufread.close();	
			instream.close();

		} catch (IOException | JSONException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HtmlBody = htmlbody.toString()+"</tr>";
		return HtmlBody;	
  }
	
	/**
	 * 计算时间差
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String ElapsedDate(String startTime, String endTime) {
		Calendar mStartCalendar = Calendar.getInstance();
		mStartCalendar.setTime(StringFormatDate(startTime));
		Calendar mEndCalendat = Calendar.getInstance();
		mEndCalendat.setTime(StringFormatDate(endTime));
		return getElapsedTime(mStartCalendar, mEndCalendat);
	}
	/**
	 * String格式时间转换成Date格式
	 * @param date
	 * @return
	 */
	public static Date StringFormatDate(String date) {
		Date times = null;
		try {
		times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
		} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return times;
		}
	/**
	 * 换算时间差
	 * @param mStartCalendar
	 * @param mEndCalendat
	 * @return
	 */
    private static String getElapsedTime(Calendar mStartCalendar,
			Calendar mEndCalendat) {
		long excutTime = mEndCalendat.getTimeInMillis()
				- mStartCalendar.getTimeInMillis();
		int day = (int) (excutTime / (1000 * 60 * 60) / 24);
		int hour = (int) (excutTime / (1000 * 60 * 60));
		int min = (int) ((excutTime % (1000 * 60 * 60)) / (1000 * 60));
		int second = (int) (((excutTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
		
//		String result = (String) (hour > 0 ? hour + "小时" : "");		
//		result += (min < 9 ? min + "分" : min + "分");
//		result += (second < 9 ? "0" + second + "秒" : second + "秒");
		String result = (String) (day > 0 ? day + "天" : hour + "小时");
		return result;
	}
}