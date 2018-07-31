package com.adzuki.worker.web.tag;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * 增加默认的日期格式支持 ： yyyy-MM-dd yyyy-MM-dd HH:mm:ss
 * @author yechao1
 *
 */
public class CustomDateEditor extends PropertyEditorSupport {

	public static String FORMART_DATETIME = "yyyy-MM-dd HH:mm:ss";

	public static String FORMART_DATE = "yyyy-MM-dd";
	
	public static  Pattern REGPATTERN_DATETIME = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}");
	
	public static  Pattern REGPATTERN_DATE = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");

	private final DateFormat dateFormat;

	private final boolean allowEmpty;

	private final int exactDateLength;

	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = -1;
	}

	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = exactDateLength;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			setValue(null);
		} else if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
			throw new IllegalArgumentException(
					"Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
		} else {
			try {
				if(dateFormat == null){
					if(REGPATTERN_DATE.matcher(text).matches()){
						SimpleDateFormat sdf = new SimpleDateFormat(FORMART_DATE);
						sdf.setLenient(false);
						setValue(sdf.parse(text));
					}else if(REGPATTERN_DATETIME.matcher(text).matches()){
						SimpleDateFormat sdf = new SimpleDateFormat(FORMART_DATETIME);
						sdf.setLenient(false);
						setValue(sdf.parse(text));
					}
				}else{
					setValue(this.dateFormat.parse(text));
				}
			} catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
			}
		}
	}

	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? this.dateFormat.format(value) : "");
	}

}
