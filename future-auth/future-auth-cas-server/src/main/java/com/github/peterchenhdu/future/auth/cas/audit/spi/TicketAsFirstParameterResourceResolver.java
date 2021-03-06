/*
 * Copyright (c) 2011-2025 PiChen
 */
package com.github.peterchenhdu.future.auth.cas.audit.spi;

import com.github.inspektr.audit.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;
import com.github.peterchenhdu.future.auth.cas.util.AopUtils;

/**
 * Implementation of the ResourceResolver that can determine the Ticket Id from the first parameter of the method call.
 *
 * @author Scott Battaglia
 * @version $Revision: 1.1 $ $Date: 2005/08/19 18:27:17 $
 * @since 3.1.2
 */
public final class TicketAsFirstParameterResourceResolver implements AuditResourceResolver {

    public String[] resolveFrom(final JoinPoint joinPoint, final Exception exception) {
        return new String[]{AopUtils.unWrapJoinPoint(joinPoint).getArgs()[0].toString()};
    }

    public String[] resolveFrom(final JoinPoint joinPoint, final Object object) {
        return new String[]{AopUtils.unWrapJoinPoint(joinPoint).getArgs()[0].toString()};
    }
}
