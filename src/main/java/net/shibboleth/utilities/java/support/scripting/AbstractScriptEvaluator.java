/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.utilities.java.support.scripting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import net.shibboleth.utilities.java.support.annotation.ParameterName;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A component that evaluates an {@link EvaluableScript} against a set of inputs
 * and returns the result.
 * 
 * @since 7.4.0
 */
public abstract class AbstractScriptEvaluator {

    /** The default language is Javascript. */
    @Nonnull @NotEmpty public static final String DEFAULT_ENGINE = "JavaScript";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractScriptEvaluator.class);

    /** The script we care about. */
    @Nonnull private final EvaluableScript script;

    /** Debugging info. */
    @Nullable private String logPrefix;

    /** The output type to validate. */
    @Nullable private Class outputType;
    
    /** A custom object to inject into the script. */
    @Nullable private Object customObject;
    
    /** Whether to raise runtime exceptions if a script fails. */
    private boolean hideExceptions;
    
    /** Value to return from script if an error occurs. */
    @Nullable private Object returnOnError;

    /**
     * Constructor.
     * 
     * @param theScript the script we will evaluate.
     */
    public AbstractScriptEvaluator(@Nonnull @ParameterName(name="theScript") final EvaluableScript theScript) {
        script = Constraint.isNotNull(theScript, "Supplied script cannot be null");
    }

    /**
     * Get log prefix for debugging.
     * 
     * @return log prefix
     */
    @Nullable protected String getLogPrefix() {
        return logPrefix;
    }
    
    /**
     * Set log prefix for debugging.
     * 
     * @param prefix log prefix
     */
    public void setLogPrefix(@Nullable final String prefix) {
        logPrefix = prefix;
    }
    
    /**
     * Get the output type to be enforced.
     * 
     * @return output type
     */
    @Nullable protected Class getOutputType() {
        return outputType;
    }
    
    /**
     * Set the output type to be enforced.
     * 
     * @param type output type
     */
    protected void setOutputType(@Nullable final Class type) {
        outputType = type;
    }
    
    /**
     * Return the custom (externally provided) object.
     * 
     * @return the custom object
     */
    @Nullable protected Object getCustomObject() {
        return customObject;
    }

    /**
     * Set the custom (externally provided) object.
     * 
     * @param object the custom object
     */
    public void setCustomObject(@Nullable final Object object) {
        customObject = object;
    }

    /**
     * Get whether to hide exceptions in script execution.
     * 
     * @return whether to hide exceptions in script execution
     */
    protected boolean getHideExceptions() {
        return hideExceptions;
    }
    
    /**
     * Set whether to hide exceptions in script execution (default is false).
     * 
     * @param flag flag to set
     */
    public void setHideExceptions(final boolean flag) {
        hideExceptions = flag;
    }

    /**
     * Get value to return if an error occurs.
     * 
     * @return value to return
     */
    @Nullable protected Object getReturnOnError() {
        return returnOnError;
    }
    
    /**
     * Set value to return if an error occurs.
     * 
     * @param value value to return
     */
    protected void setReturnOnError(@Nullable final Object value) {
        returnOnError = value;
    }

    /**
     * Evaluate the script.
     * 
     * @param input input parameters
     * 
     * @return script result
     */
    protected Object evaluate(@Nullable final Object... input) {
        final SimpleScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setAttribute("custom", getCustomObject(), ScriptContext.ENGINE_SCOPE);
        
        prepareContext(scriptContext, input);

        try {
            final Object result = script.eval(scriptContext);

            if (null != getOutputType() && null != result && !getOutputType().isInstance(result)) {
                log.error("{} Output of type {} was not of type {}", getLogPrefix(), result.getClass(),
                        getOutputType());
                return getReturnOnError();
            }
            
            return result;
            
        } catch (final ScriptException e) {
            if (getHideExceptions()) {
                log.warn("{} Suppressing exception thrown by script", getLogPrefix(), e);
                return getReturnOnError();
            }
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Pre-process the script context before execution.
     * 
     * @param scriptContext the script context
     * @param input the input
     */
    protected abstract void prepareContext(@Nonnull final ScriptContext scriptContext, @Nullable final Object... input);

}