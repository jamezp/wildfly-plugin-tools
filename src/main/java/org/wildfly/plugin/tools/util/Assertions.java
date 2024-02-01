/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wildfly.plugin.tools.util;

import java.util.Collection;

import org.wildfly.common.Assert;

/**
 * A utility for assertions.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Assertions {

    /**
     * Checks if the parameter is {@code null} or empty and throws an {@link IllegalArgumentException} if it is.
     *
     * @param name  the name of the parameter
     * @param value the value to check
     *
     * @return the parameter value
     *
     * @throws IllegalArgumentException if the object representing the parameter is {@code null}
     */
    public static String requiresNotNullOrNotEmptyParameter(final String name, final String value)
            throws IllegalArgumentException {
        Assert.checkNotNullParam(name, value);
        Assert.checkNotEmptyParam(name, value);
        return value;
    }

    /**
     * Checks if the parameter is {@code null} or empty and throws an {@link IllegalArgumentException} if it is.
     *
     * @param name  the name of the parameter
     * @param value the value to check
     *
     * @return the parameter value
     *
     * @throws IllegalArgumentException if the object representing the parameter is {@code null}
     */
    public static <E, T extends Collection<E>> T requiresNotNullOrNotEmptyParameter(final String name, final T value)
            throws IllegalArgumentException {
        Assert.checkNotNullParam(name, value);
        Assert.checkNotEmptyParam(name, value);
        return value;
    }
}
