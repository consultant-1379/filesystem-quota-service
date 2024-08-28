/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.filesystemquota.rest.interceptor;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.*;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.lang.reflect.Method;

/**
 * The intercepter class for checking the access control for user.
 */
@Authorize
@Interceptor
public class AuthorizeInterceptor {

    public static final String ACCESS_DENIED = "Access Denied : %s role required";

    @Inject
    private EAccessControl accessControl;

    @Inject
    private Logger logger;

    /**
     * Checks if the user has access to the application.
     */
    @AroundInvoke
    public Object intercept(final InvocationContext ic) throws Exception { // NOPMD
        boolean decision = false;
        
        final Method calledMethod = ic.getMethod();
        final Authorize authAnnotation = calledMethod.getAnnotation(Authorize.class);
        final String action = authAnnotation.action();
        final String role = authAnnotation.role();
        final String resource = authAnnotation.resource();
        logger.info ("Action: {} -- role: {} --- resource: {}", action, role, resource);
        try {
            decision = accessControl.isAuthorized(new ESecurityResource(resource), new ESecurityAction(action),
                    new EPredefinedRole[] {});
        } catch (final SecurityViolationException e) {
            logger.info(e.getMessage());
        }
        if (!decision) {
            return Response.status(Status.UNAUTHORIZED).entity(String.format(ACCESS_DENIED, role)).build();
        }
        return ic.proceed();
    }
}
