package com.adzuki.worker.web.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XSSHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public XSSHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value != null)
			value = cleanXSS(value);
		return value;
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (value != null)
			value = cleanXSS(value);
		return value;
	}

	/**
	 * 转义特殊字符
	 * 
	 * @param value
	 * @return
	 */
	private String cleanXSS(String value) {
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		value = value.replaceAll("'", "&#39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}

}
