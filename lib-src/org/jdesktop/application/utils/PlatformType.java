/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.utils;

/**
 *
 * @author Illya Yalovyy
 */
public enum PlatformType {
    DEFAULT("Default", ""),
    SOLARIS("Solaris", "sol", "solaris"),
    FREE_BSD("FreeBSD", "bsd", "FreeBSD"),
    LINUX("Linux", "lin", "linux"),
    OS_X("Mac OS X", "osx", "mac os x"),
    WINDOWS("Windows", "win", "windows");

    private final String name;
    private final String resourceSuffix;
    private final String[] patterns;

    private PlatformType(String name, String resourcePrefix, String... patterns) {
        this.name = name;
        this.resourceSuffix = resourcePrefix;
        this.patterns = patterns;
    }

    public String getName() {
        return name;
    }

    public String[] getPatterns() {
        return patterns.clone();
    }

    public String getResourceSuffix() {
        return resourceSuffix;
    }

    @Override
    public String toString() {
        return name;
    }
}
