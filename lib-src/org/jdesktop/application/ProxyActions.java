/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/
package org.jdesktop.application;

import java.lang.annotation.*;

/**
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProxyActions {

    String[] value() default {};
}
