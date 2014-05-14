/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application.session;

import java.awt.*;

/**
 * Defines the {@code sessionState} property.  The value of this
 * property is the GUI state that should be preserved across
 * sessions for the specified component.  The type of sessionState
 * values just one those supported by
 * {@link java.beans.XMLEncoder XMLEncoder} and
 * {@link java.beans.XMLDecoder XMLDecoder}, for example beans
 * (null constructor, read/write properties), primitives, and
 * Collections.
 *
 * @see #putProperty
 * @see #getProperty(Class)
 * @see #getProperty(Component)
 */
public interface PropertySupport {

    /**
     * Return the value of the {@code sessionState} property, typically
     * a Java bean or a Collection the defines the {@code Component} state
     * that should be preserved across Application sessions.  This
     * value will be stored with {@link java.beans.XMLEncoder XMLEncoder},
     * loaded with {@link java.beans.XMLDecoder XMLDecoder}, and
     * passed to {@code setSessionState} to restore the Component's
     * state.
     *
     * @param c the Component.
     * @return the {@code sessionState} object for Component {@code c}.
     * @see #setSessionState
     */
    Object getSessionState(Component c);

    /**
     * Restore Component {@code c's} {@code sessionState} from the specified
     * object.
     *
     * @param c the Component.
     * @param state the value of the {@code sessionState} property.
     * @see #getSessionState
     */
    void setSessionState(Component c, Object state);
}
