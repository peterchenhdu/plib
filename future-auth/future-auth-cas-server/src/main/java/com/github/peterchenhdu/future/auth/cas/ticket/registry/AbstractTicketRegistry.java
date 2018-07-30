/*
 * Copyright (c) 2011-2025 PiChen
 */
package com.github.peterchenhdu.future.auth.cas.ticket.registry;

import com.github.peterchenhdu.future.auth.cas.monitor.TicketRegistryState;
import com.github.peterchenhdu.future.auth.cas.ticket.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author Scott Battaglia
 * @since 3.0.4
 * <p>
 * This is a published and supported CAS Server 3 API.
 * </p>
 */
public abstract class AbstractTicketRegistry implements TicketRegistry, TicketRegistryState {

    /** The Commons Logging log instance. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @throws IllegalArgumentException if class is null.
     * @throws ClassCastException if class does not match requested ticket
     * class.
     */
    public final Ticket getTicket(final String ticketId,
        final Class<? extends Ticket> clazz) {
        Assert.notNull(clazz, "clazz cannot be null");

        final Ticket ticket = this.getTicket(ticketId);

        if (ticket == null) {
            return null;
        }

        if (!clazz.isAssignableFrom(ticket.getClass())) {
            throw new ClassCastException("Ticket [" + ticket.getId()
                + " is of type " + ticket.getClass()
                + " when we were expecting " + clazz);
        }

        return ticket;
    }
    
    public int sessionCount() {
      log.debug("sessionCount() operation is not implemented by the ticket registry instance {}. Returning unknown as {}", 
                this.getClass().getName(), Integer.MIN_VALUE);
      return Integer.MIN_VALUE;
    }

    public int serviceTicketCount() {
      log.debug("serviceTicketCount() operation is not implemented by the ticket registry instance {}. Returning unknown as {}", 
                this.getClass().getName(), Integer.MIN_VALUE);
      return Integer.MIN_VALUE;
    }
}