package com.input.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TimeUtils {

	/**
	 * 获取当前日期、时间
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrentTime(String pattern) {
		SimpleDateFormat date = new SimpleDateFormat(pattern);
		Date current = new Date();
		return date.format(current);
	}
	/**
	 * 以当前时间加随机数作为文件名
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrentName() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate)+new Random().nextInt(99);
		return str;
	}
}
