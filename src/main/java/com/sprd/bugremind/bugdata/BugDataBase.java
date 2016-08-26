package com.sprd.bugremind.bugdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sprd.bugremind.bean.BugFilterInfo;
import com.sprd.bugremind.buginfo.BugJonParaser;

public class BugDataBase {
	private static final Log log = LogFactory.getLog(BugDataBase.class); 
	private static String BugJSonInfo = "";
    private static ArrayList<String> list = new ArrayList<String>();
	private static BugFilterInfo buginfo = new BugFilterInfo();
    
	//数据库
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	/**
	 * JDBC连接MySQL数据库
	 * Database：bugzilla， table：buginfos
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * 
	 */
	 public Connection getConnection() throws ClassNotFoundException, SQLException {
		 
		 //加载MySQL的JDBC驱动
		 Class.forName("com.mysql.jdbc.Driver");
		 //获取URL链接，用户名，密码
		 String url = "jdbc:mysql://10.5.41.10:3306/bugzilla";
		 String username = "root";
		 String password = "admin";
		 //创建数据库连接
		 Connection connection = DriverManager.getConnection(url, username, password);	 
		 
		return connection;
	 }

}
