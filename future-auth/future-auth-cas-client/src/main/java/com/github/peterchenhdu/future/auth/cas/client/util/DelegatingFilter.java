/*
 * Copyright (c) 2011-2025 PiChen
 */

package com.github.peterchenhdu.future.auth.cas.client.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * A Delegating Filter looks up a parameter in the request object and matches
 * (either exact or using Regular Expressions) the value. If there is a match,
 * the associated filter is executed. Otherwise, the normal chain is executed.
 *
 * @author Scott Battaglia
 * @version $Revision: 11729 $ $Date: 2006-09-26 14:22:30 -0400 (Tue, 26 Sep 2006) $
 * @since 3.0
 */
public final class DelegatingFilter implements Filter {

    /**
     * Instance of Commons Logging.
     */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The request parameter to look for in the Request object.
     */
    private final String requestParameterName;

    /**
     * The map of filters to delegate to and the criteria (as key).
     */
    private final Map<String, Filter> delegators;

    /**
     * The default filter to use if there is no match.
     */
    private final Filter defaultFilter;

    /**
     * Whether the key in the delegators map is an exact match or a regular
     * expression.
     */
    private final boolean exactMatch;

    public DelegatingFilter(final String requestParameterName, final Map<String, Filter> delegators, final boolean exactMatch) {
        this(requestParameterName, delegators, exactMatch, null);
    }

    public DelegatingFilter(final String requestParameterName, final Map<String, Filter> delegators, final boolean exactMatch, final Filter defaultFilter) {
        CommonUtils.assertNotNull(requestParameterName, "requestParameterName cannot be null.");
        CommonUtils.assertTrue(!delegators.isEmpty(), "delegators cannot be empty.");

        this.requestParameterName = requestParameterName;
        this.delegators = delegators;
        this.defaultFilter = defaultFilter;
        this.exactMatch = exactMatch;
    }

    public void destroy() {
        // nothing to do here
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {

        final String parameter = CommonUtils.safeGetParameter((HttpServletRequest) request, this.requestParameterName);

        if (CommonUtils.isNotEmpty(parameter)) {
            for (final String key : this.delegators.keySet()) {
                if ((parameter.equals(key) && this.exactMatch) || (parameter.matches(key) && !this.exactMatch)) {
                    final Filter filter = this.delegators.get(key);
                    if (log.isDebugEnabled()) {
                        log.debug("Match found for parameter ["
                                + this.requestParameterName + "] with value ["
                                + parameter + "]. Delegating to filter ["
                                + filter.getClass().getName() + "]");
                    }
                    filter.doFilter(request, response, filterChain);
                    return;
                }
            }
        }

        log.debug("No match found for parameter [" + this.requestParameterName + "] with value [" + parameter + "]");

        if (this.defaultFilter != null) {
            this.defaultFilter.doFilter(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        // nothing to do here.
    }
}
