package com.sprd.bugremind.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sprd.bugremind.util.GetConfigXML;

public class RemindTimeManager {
	private static final Log log = LogFactory.getLog(RemindTimeManager.class);
	//时间间隔
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    		//24 * 60 * 60 * 1000;  
    public void TimeManager(TimerTask task) {  
        Calendar calendar = Calendar.getInstance();
        //yyyy-MM-dd HH:mm:ss
        String startTime = GetConfigXML.getXmlValue("BRConfig.xml", "StartExecuteTime", "value")[0];
        Date startdate = StringFormatDate(startTime);
        
        calendar.setTime(startdate); //Start Reminder time  
        Date date=calendar.getTime(); //第一次执行定时任务的时间  
        //如果第一次执行定时任务的时间 小于当前的时间  
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。  
        if (date.before(new Date())) {  
        	SimpleDateFormat simpledateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        	
        	date = this.addDay(date, 1);
        	String startdateadd = simpledateform.format(date);
            log.info("BugReminder will execute at " + startdateadd + ", please wait in patient!");
        } else {
            log.info("BugReminder will execute at " + startTime+ ", please wait in patient!");
        }
        
        Timer timer = new Timer(); 
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。  
        timer.schedule(task, date, PERIOD_DAY);
    }  
    // 增加或减少天数  
    public Date addDay(Date date, int num) {  
        Calendar startDT = Calendar.getInstance();  
        startDT.setTime(date);  
        startDT.add(Calendar.DAY_OF_MONTH, num);  
        return startDT.getTime();  
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
}