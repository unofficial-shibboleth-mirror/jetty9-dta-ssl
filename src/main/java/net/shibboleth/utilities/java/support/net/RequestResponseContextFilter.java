package net.shibboleth.utilities.java.support.net;

import java.io.IOException;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of an HTTP servlet {@link Filter} which stores the current {@link HttpServletRequest} 
 * and {@link HttpServletResponse} being serviced on thread-local storage via the use
 * of holder class {@link HttpServletRequestResponseContext}.
 */
public class RequestResponseContextFilter implements Filter {
	
	/** {@inheritDoc} */
	public void init(FilterConfig filterConfig) throws ServletException { }

	/** {@inheritDoc} */
	public void destroy() { }

	/** {@inheritDoc} */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("Request is not an instance of HttpServletRequest");
		}
		
		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("Response is not an instance of HttpServletResponse");
		}
		
		try {
			HttpServletRequestResponseContext.loadCurrent((HttpServletRequest) request, (HttpServletResponse) response);
			chain.doFilter(request, response);
		} finally {
			HttpServletRequestResponseContext.clearCurrent();
		}

	}

}
