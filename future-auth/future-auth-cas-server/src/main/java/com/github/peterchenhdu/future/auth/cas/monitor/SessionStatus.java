/*
 * Copyright (c) 2011-2025 PiChen
 */
package com.github.peterchenhdu.future.auth.cas.monitor;

/**
 * Provides status information about the number of SSO sessions established in CAS.
 *
 * @author Marvin S. Addison
 * @since 3.5.0
 */
public class SessionStatus extends Status {
    /**
     * Total number of SSO sessions maintained by CAS.
     */
    private final int sessionCount;

    /**
     * Total number of service tickets in CAS ticket registry.
     */
    private final int serviceTicketCount;

    /**
     * Creates a new status object with the given code.
     *
     * @param code Status code.
     * @param desc Human-readable status description.
     * @see #getCode()
     */
    public SessionStatus(final StatusCode code, final String desc) {
        this(code, desc, 0, 0);
    }


    /**
     * Creates a new status object with the given code.
     *
     * @param code           Status code.
     * @param desc           Human-readable status description.
     * @param sessions       Number of established SSO sessions in ticket registry.
     * @param serviceTickets Number of service tickets in ticket registry.
     * @see #getCode()
     */
    public SessionStatus(final StatusCode code, final String desc, final int sessions, final int serviceTickets) {
        super(code, desc);
        this.sessionCount = sessions;
        this.serviceTicketCount = serviceTickets;
    }


    /**
     * Gets total number of SSO sessions maintained by CAS.
     *
     * @return Total number of SSO sessions.
     */
    public int getSessionCount() {
        return this.sessionCount;
    }


    /**
     * Gets the total number of service tickets in the CAS ticket registry.
     *
     * @return Total number of service tickets.
     */
    public int getServiceTicketCount() {
        return this.serviceTicketCount;
    }
}
