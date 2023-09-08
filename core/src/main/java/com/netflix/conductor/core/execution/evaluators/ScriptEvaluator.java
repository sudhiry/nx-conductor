package com.netflix.conductor.core.execution.evaluators;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEvaluator {



    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    private ScriptEvaluator() {}

    /**
     * Evaluates the script with the help of input provided but converts the result to a boolean
     * value.
     *
     * @param script Script to be evaluated.
     * @param input Input parameters.
     * @throws ScriptException
     * @return True or False based on the result of the evaluated expression.
     */
    public static Boolean evalBool(String script, Object input) throws ScriptException {
        return toBoolean(eval(script, input));
    }

    /**
     * Evaluates the script with the help of input provided.
     *
     * @param script Script to be evaluated.
     * @param input Input parameters.
     * @throws ScriptException
     * @return Generic object, the result of the evaluated expression.
     */
    public static Object eval(String script, Object input) throws ScriptException {
        Bindings bindings = engine.createBindings();
        bindings.put("$", input);
        return engine.eval(script, bindings);
    }

    /**
     * Converts a generic object into boolean value. Checks if the Object is of type Boolean and
     * returns the value of the Boolean object. Checks if the Object is of type Number and returns
     * True if the value is greater than 0.
     *
     * @param input Generic object that will be inspected to return a boolean value.
     * @return True or False based on the input provided.
     */
    public static Boolean toBoolean(Object input) {
        if (input instanceof Boolean) {
            return ((Boolean) input);
        } else if (input instanceof Number) {
            return ((Number) input).doubleValue() > 0;
        }
        return false;
    }
}
