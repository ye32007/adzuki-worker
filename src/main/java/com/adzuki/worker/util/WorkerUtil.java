package com.adzuki.worker.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Description: Worker工具类
 * @author yangkuan@jd.coom
 *
 */
public class WorkerUtil {

	
	
	/**
	 * Description: 将任务信息转换成对应的Map集合
	 * @param str
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, String> convertToMap(String str) throws Exception{
		String[] keyValues = str.split("\\|");
		Map<String, String> map = new HashMap<String, String>();
		for(String keyValue : keyValues){
			String[] keyAndValue = keyValue.split("=");
			String value = keyAndValue.length == 1 ? "":keyAndValue[1];
			value = "null".equals(value) ? "":value;
			map.put(keyAndValue[0], value);
		}
		return map;
	}
	
	/**
	 * Description: 将任务信息转换成JSON格式
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String convertToJSON(String str) throws Exception{
		return null;
	}
	
	/**
	 * Description: 将任务信息转换成XML格式
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String convertToXML(String str) throws Exception{
		return null;
	}
	
	/**
	 * Description: 获取sys-notify.properties服务属性
	 * @param prefix
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, String> getServiceProperties(String prefix) throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		//地址
		String address = "address";
		//token
		String token = "token";
		//app
		String app = "app";
		
		ResourceBundle rb = ResourceBundle.getBundle("sys-notify");
		map.put(address, rb.getString(prefix+"."+address));
		map.put(token, rb.getString(prefix+"."+token));
		map.put(app, rb.getString(prefix+"."+app));
		
		return map;
	}
	
	/**
	 * Description: 根据key获取sys-config.properties文件中的值
	 * @param key
	 * @return String
	 * @throws Exception
	 */
	public static String getSysProperty(String key) throws Exception{
		ResourceBundle rb = ResourceBundle.getBundle("sys-config");
		return rb.getString(key);
	}
	
	/**
	 * Description: 根据key获取sys-plat.properties文件中的值
	 * @param key
	 * @return String
	 * @throws Exception
	 */
	public static String getSysPlatProperty(String key) throws Exception{
		ResourceBundle rb = ResourceBundle.getBundle("sys-plat");
		return rb.getString(key);
	}

	/**
	 * 计算批处理执行次数
	 * @param listSize
	 * @param batchSize
	 * @return
	 */
	public static int computeBatchTimes(int listSize,int batchSize){
		int times = 0;
		if(listSize%batchSize>0){
			times = listSize/batchSize + 1;
		}else{
			times = listSize/batchSize;
		}
		return times;
	}
	
	/**
	 * 计算批处开始
	 * @param listSize
	 * @param batchSize
	 * @return
	 */
	public static int computeBatchFromIndex(int listSize,int batchSize,int i){
		int from = i*batchSize;
		if(from>listSize){
			from = listSize;
		}
		return from;
	}
	
	/**
	 * 计算批处理结束
	 * @param listSize
	 * @param batchSize
	 * @return
	 */
	public static int computeBatchToIndex(int listSize,int batchSize,int i){
		int to = (i+1)*batchSize;
		if(to>listSize){
			to = listSize;
		}
		return to;
	}
    public static void main(String[] args){
        System.out.println(computeBatchTimes(19,10));
        System.out.println(computeBatchFromIndex(29,10,1));

        System.out.println(computeBatchToIndex(29,10,1));
    }
}
