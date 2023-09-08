/*
 * Copyright 2022 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.core.execution.evaluators;

import com.netflix.conductor.core.events.ScriptEvaluator;
import com.netflix.conductor.core.exception.TerminateWorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

@Component(JavascriptEvaluator.NAME)
public class JavascriptEvaluator implements Evaluator {

    public static final String NAME = "javascript";
    private static final Logger LOGGER = LoggerFactory.getLogger(JavascriptEvaluator.class);

    @Override
    public Object evaluate(String expression, Object input) {
        LOGGER.debug("Javascript evaluator -- expression: {}", expression);
        try {
            // Evaluate the expression by using the Javascript evaluation engine.
            Object result = ScriptEvaluator.eval(expression, input);
            LOGGER.debug("Javascript evaluator -- result: {}", result);
            return result;
        } catch (ScriptException e) {
            LOGGER.error("Error while evaluating script: {}", expression, e);
            throw new TerminateWorkflowException(e.getMessage());
        }
    }
}
