package com.adzuki.worker.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 */
public class DateUtil {
	
	final static  Logger logger = LoggerFactory.getLogger(DateUtil.class);
	

	public static String FORMATE_DAY = "yyyy-MM-dd";
	public static String FORMATE_TIME = "yyyy-MM-dd HH:mm:ss";

	/**
	 *
	 *
	 * @param dateStr
	 * @param fromFormat
	 *            原始的时间格式
	 * @param toFormat
	 *            转换后的时间格式
	 * @return
	 */
	public static String formatDate(String dateStr, String fromFormat,
			String toFormat) {

		DateFormat fromDateFormat = new SimpleDateFormat(fromFormat);
		Date date = null;
		try {
			date = fromDateFormat.parse(dateStr);
		} catch (Exception e) {
			throw new RuntimeException("时间转换异常:dateStr=" + dateStr
					+ ", fromFormat=" + fromFormat);
		}

		DateFormat toDateFormat = new SimpleDateFormat(toFormat);

		return toDateFormat.format(date);
	}

	private static Map<String, ThreadLocal<SimpleDateFormat>> dataFormatMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	/**
	 * 使用ThreadLocal以空间换时间解决SimpleDateFormat线程安全问题 第一次调用get将返回null
	 * 故需要初始化一个SimpleDateFormat，并set到threadLocal中
	 * 
	 * @return
	 */
	public static SimpleDateFormat getDateFormat(final String pattern) {
		ThreadLocal<SimpleDateFormat> threadLocal = dataFormatMap.get(pattern);
		if (threadLocal == null) {
			threadLocal = new ThreadLocal<SimpleDateFormat>() {
				@Override
				protected SimpleDateFormat initialValue() {
					return new SimpleDateFormat(pattern);
				}
			};
			dataFormatMap.put(pattern, threadLocal);
		}
		return threadLocal.get();
	}

	public static Date addDays(Date date, int amount) {
		return addDay(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date addMins(Date date, int amount) {
		return addDay(date, Calendar.MINUTE, amount);
	}

	public static Date addDay(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}
	
	
	public static Date getDateByInteralBefore(int day) throws ParseException{
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE ,-day );
		
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATE_DAY);
		Date date1 = date;
		try{
			String dateStr = sdf.format(date);
			date1 = sdf.parse(dateStr);
		}catch(ParseException e){
			logger.error("时间转换错误：{}" , e);
		}
		return date1;
	
	}
	
	public static Date getDateByInteralAfter(int day){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE ,day );
		Date date = cal.getTime();
		return date;
	}
	
	public static Date getIntegerDate(Date date){
		
		Date inteDate = date;
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATE_DAY);
		try{
			String dateStr = sdf.format(date);
			inteDate = sdf.parse(dateStr);
		}catch(ParseException e){
			logger.error("时间转换错误：{}" , e);
		}
		return inteDate;
	}
	
	public static Date getLastTimeOfDay(Date date){
		
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.set(Calendar.HOUR_OF_DAY, 23);
		cl.set(Calendar.MINUTE, 59);
		cl.set(Calendar.SECOND, 59);
		date = cl.getTime();
		return date;
	}
	
	public static Date convertStringToStartDate(String time , String format){
		 
		Date date = null ;

		if (!StringUtils.isNotBlank(time)) {
			return date;
		}
		try{
			if(!StringUtils.isNotBlank(format)){
				format = DateUtil.FORMATE_DAY;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(time);

		}catch(Exception e){
            logger.error("时间格式转换失败 time:{},format:{}",  time , format);
            logger.error("时间格式转换失败异常",e);
		}
		return date;
	}
	
	public static Date convertStringToEndDate(String time , String format){
		 
		Date date = null ;

		if (!StringUtils.isNotBlank(time)) {
			return date;
		}
		try{
			if(!StringUtils.isNotBlank(format)){
				format = DateUtil.FORMATE_DAY;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(time);
			Calendar cl = Calendar.getInstance();
			cl.setTime(date);
			cl.set(Calendar.HOUR, 23);
			cl.set(Calendar.MINUTE, 59);
			cl.set(Calendar.SECOND, 59);
			date = cl.getTime();
		}catch(Exception e){
            logger.error("时间格式转换失败 time:{},format:{}",  time , format);
            logger.error("时间格式转换失败异常",e);
		}
		return date;
	}
	
	public static String convertDateToString(Date date , String format){
		 
		if(date == null){
			return "";
		}
		if(!StringUtils.isNotBlank(format)){
			format = DateUtil.FORMATE_DAY;
		}
		
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			String time = sdf.format(date);
			return time;

		}catch(Exception e){
            logger.error("时间格式转换失败 date:{},format:{}",  date , format);
            logger.error("时间格式转换失败异常",e);
		}
		return "";
	}
}
