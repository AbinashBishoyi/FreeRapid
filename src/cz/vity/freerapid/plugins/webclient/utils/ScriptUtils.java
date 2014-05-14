package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Collection of static utility methods for processing scripts easily.
 *
 * @author ntoskrnl
 * @since 0.85u1
 */
public final class ScriptUtils {

    /**
     * Do not instantiate.
     */
    private ScriptUtils() {
    }

    /**
     * Evaluates a string of JavaScript to an Object.
     *
     * @param script Script to evaluate
     * @return Whatever the script returns, can be null
     * @throws PluginImplementationException If something goes wrong
     */
    public static Object evaluateJavaScript(final String script) throws PluginImplementationException {
        final ScriptEngine engine = getJavaScriptEngine();
        try {
            return engine.eval(script);
        } catch (Exception e) {
            throw new PluginImplementationException("Script execution failed", e);
        }
    }

    /**
     * Evaluates a string of JavaScript to a String by casting the result.
     * Use {@link #evaluateJavaScript(String)}{@link Object#toString() .toString()}
     * if you wish a string representation of an object instead of a cast.
     *
     * @param script Script to evaluate
     * @return Whatever the script returns, can be null
     * @throws PluginImplementationException If something goes wrong
     */
    public static String evaluateJavaScriptToString(final String script) throws PluginImplementationException {
        final Object result = evaluateJavaScript(script);
        if (result == null || result instanceof String) {
            return (String) result;
        } else {
            throw new PluginImplementationException("Wrong script return type: " + result.getClass().getName() + ", expected String");
        }
    }

    /**
     * Evaluates a string of JavaScript to a Number by casting the result.
     * You can further make the return value more specific with the
     * {@link Number#intValue()}, {@link Number#longValue()}, {@link Number#doubleValue()},
     * etc. methods.
     *
     * @param script Script to evaluate
     * @return Whatever the script returns, can be null
     * @throws PluginImplementationException If something goes wrong
     */
    public static Number evaluateJavaScriptToNumber(final String script) throws PluginImplementationException {
        final Object result = evaluateJavaScript(script);
        if (result == null || result instanceof Number) {
            return (Number) result;
        } else {
            throw new PluginImplementationException("Wrong script return type: " + result.getClass().getName() + ", expected Number");
        }
    }

    private static ScriptEngine getJavaScriptEngine() {
        final ScriptEngineManager mgr = new ScriptEngineManager();
        final ScriptEngine engine = mgr.getEngineByName("JavaScript");
        if (engine == null) {
            throw new RuntimeException("JavaScript engine not found");
        }
        return engine;
    }

}
