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

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The intercepter binding annotation for {@code AuthorizeInterceptor}
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Authorize {
    @Nonbinding
    String action() default "";

    @Nonbinding
    String resource() default "user_mgmt";

    @Nonbinding
    String role() default "SECURITY_ADMIN";
}
