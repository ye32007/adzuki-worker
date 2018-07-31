package com.adzuki.worker.web.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 防御XSS攻击过滤器
 */
public class XSSFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(XSSFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("xss security filter initialize successful...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		XSSHttpServletRequestWrapper xssHttpServletRequestWrapper = new XSSHttpServletRequestWrapper(
				httpServletRequest);
		chain.doFilter(xssHttpServletRequestWrapper, response);
	}

	@Override
	public void destroy() {
		logger.info("xss security filter destroy successful...");
	}

}
