/*
 * Copyright (c) 2011-2025 PiChen
 */

package com.github.peterchenhdu.future.auth.cas.client.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of {@link ProxyGrantingTicketStorage} that is backed by a
 * HashMap that keeps a ProxyGrantingTicket for a specified amount of time.
 * <p>
 * {@link ProxyGrantingTicketStorage#cleanUp()} must be called on a regular basis to
 * keep the HashMap from growing indefinitely.
 *
 * @author Scott Battaglia
 * @author Brad Cupit (brad [at] lsu {dot} edu)
 * @version $Revision: 11729 $ $Date: 2007-09-26 14:22:30 -0400 (Tue, 26 Sep 2007) $
 * @since 3.0
 */
public final class ProxyGrantingTicketStorageImpl implements ProxyGrantingTicketStorage {
	
	private final Log log = LogFactory.getLog(getClass());

    /**
     * Default timeout in milliseconds.
     */
    private static final long DEFAULT_TIMEOUT = 60000;

    /**
     * Map that stores the PGTIOU to PGT mappings.
     */
    private final ConcurrentMap<String,ProxyGrantingTicketHolder> cache = new ConcurrentHashMap<String,ProxyGrantingTicketHolder>();

    /**
     * time, in milliseconds, before a {@link ProxyGrantingTicketHolder}
     * is considered expired and ready for removal.
     * 
     * @see ProxyGrantingTicketStorageImpl#DEFAULT_TIMEOUT
     */
	private long timeout;

    /**
     * Constructor set the timeout to the default value.
     */
    public ProxyGrantingTicketStorageImpl() {
        this(DEFAULT_TIMEOUT);
    }

    /**
     * Sets the amount of time to hold on to a ProxyGrantingTicket if its never
     * been retrieved.
     *
     * @param timeout the time to hold on to the ProxyGrantingTicket
     */
    public ProxyGrantingTicketStorageImpl(final long timeout) {
    	this.timeout = timeout;
    }

    /**
     * NOTE: you can only retrieve a ProxyGrantingTicket once with this method.
     * Its removed after retrieval.
     */
    public String retrieve(final String proxyGrantingTicketIou) {
        final ProxyGrantingTicketHolder holder = this.cache.get(proxyGrantingTicketIou);

        if (holder == null) {
        	log.info("No Proxy Ticket found for [" + proxyGrantingTicketIou + "].");
            return null;
        }

        this.cache.remove(proxyGrantingTicketIou);

        if (log.isDebugEnabled()) {
        	log.debug("Returned ProxyGrantingTicket of [" + holder.getProxyGrantingTicket() + "]");
        }
        return holder.getProxyGrantingTicket();
    }

    public void save(final String proxyGrantingTicketIou, final String proxyGrantingTicket) {
        final ProxyGrantingTicketHolder holder = new ProxyGrantingTicketHolder(proxyGrantingTicket);

        if (log.isDebugEnabled()) {
        	log.debug("Saving ProxyGrantingTicketIOU and ProxyGrantingTicket combo: [" + proxyGrantingTicketIou + ", " + proxyGrantingTicket + "]");
        }
        this.cache.put(proxyGrantingTicketIou, holder);
    }

    /**
     * Cleans up old, expired proxy tickets. This method must be
     * called regularly via an external thread or timer.
     */
    public void cleanUp() {
        for (final Map.Entry<String,ProxyGrantingTicketHolder> holder : this.cache.entrySet()) {
            if (holder.getValue().isExpired(this.timeout)) {
                this.cache.remove(holder.getKey());
            }
        }
    }
    
    private static final class ProxyGrantingTicketHolder {

        private final String proxyGrantingTicket;

        private final long timeInserted;

        protected ProxyGrantingTicketHolder(final String proxyGrantingTicket) {
            this.proxyGrantingTicket = proxyGrantingTicket;
            this.timeInserted = System.currentTimeMillis();
        }

        public String getProxyGrantingTicket() {
            return this.proxyGrantingTicket;
        }

        final boolean isExpired(final long timeout) {
            return System.currentTimeMillis() - this.timeInserted > timeout;
        }
    }
}
