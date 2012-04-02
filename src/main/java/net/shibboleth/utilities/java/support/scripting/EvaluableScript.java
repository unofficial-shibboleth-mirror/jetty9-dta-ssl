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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import com.google.common.base.Optional;
import com.google.common.io.Files;

/** This is a helper class that takes care of reading in, optionally compiling, and evaluating a script. */
public class EvaluableScript {

    /** The scripting language. */
    private final String scriptLanguage;

    /** The script to execute. */
    private final String script;

    /** The script engine to execute the script. */
    private ScriptEngine scriptEngine;

    /** The compiled form of the script, if the script engine supports compiling. */
    private CompiledScript compiledScript;

    /**
     * Constructor.
     * 
     * @param engineName the JSR-223 scripting engine name
     * @param scriptSource the script source
     * 
     * @throws ScriptException thrown if the scripting engine supports compilation and the script does not compile
     */
    public EvaluableScript(@Nonnull @NotEmpty String engineName, @Nonnull @NotEmpty String scriptSource)
            throws ScriptException {
        scriptLanguage =
                Constraint.isNull(StringSupport.trimOrNull(engineName), "Scripting language can not be null or empty");
        script = Constraint.isNull(StringSupport.trimOrNull(scriptSource), "Script source can not be null or empty");

        initialize();
    }

    /**
     * Constructor.
     * 
     * @param engineName the JSR-223 scripting engine name
     * @param scriptSource the script source
     * 
     * @throws ScriptException thrown if the script source file can not be read or the scripting engine supports
     *             compilation and the script does not compile
     */
    public EvaluableScript(@Nonnull @NotEmpty String engineName, @Nonnull File scriptSource) throws ScriptException {
        scriptLanguage =
                Constraint.isNull(StringSupport.trimOrNull(engineName), "Scripting language can not be null or empty");

        Constraint.isNotNull(scriptSource, "Script source file can not be null");

        if (!scriptSource.exists()) {
            throw new ScriptException("Script source file " + scriptSource.getAbsolutePath() + " does not exist");
        }

        if (!scriptSource.canRead()) {
            throw new ScriptException("Script source file " + scriptSource.getAbsolutePath()
                    + " exists but is not readable");
        }

        try {
            script =
                    Constraint.isNull(StringSupport.trimOrNull(Files.toString(scriptSource, Charset.defaultCharset())),
                            "Scritp source can not be empty");
        } catch (IOException e) {
            throw new ScriptException("Unable to read data from source file " + scriptSource.getAbsolutePath());
        }

        initialize();
    }

    /**
     * Gets the script source.
     * 
     * @return the script source
     */
    @Nonnull public String getScript() {
        return script;
    }

    /**
     * Evaluates this script against the given bindings.
     * 
     * @param scriptBindings the script bindings
     * 
     * @return the result of the script or {@link Optional#absent()} if the script did not return a result
     * 
     * @throws ScriptException thrown if there was a problem evaluating the script
     */
    @Nonnull public Optional<Object> eval(Bindings scriptBindings) throws ScriptException {
        if (compiledScript != null) {
            return Optional.fromNullable(compiledScript.eval(scriptBindings));
        } else {
            return Optional.fromNullable(scriptEngine.eval(script, scriptBindings));
        }
    }

    /**
     * Evaluates this script against the given context.
     * 
     * @param scriptContext the script context
     * 
     * @return the result of the script or {@link Optional#absent()} if the script did not return a result
     * 
     * @throws ScriptException thrown if there was a problem evaluating the script
     */
    @Nonnull public Optional<Object> eval(ScriptContext scriptContext) throws ScriptException {
        if (compiledScript != null) {
            return Optional.fromNullable(compiledScript.eval(scriptContext));
        } else {
            return Optional.fromNullable(scriptEngine.eval(script, scriptContext));
        }
    }

    /**
     * Initializes the scripting engine and compiles the script, if possible.
     * 
     * @throws ScriptException thrown if the scripting engine supports compilation and the script does not compile
     */
    private void initialize() throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        scriptEngine = engineManager.getEngineByName(scriptLanguage);
        Constraint.isNotNull(scriptEngine, "No scripting engine associated with scripting language " + scriptLanguage);

        if (scriptEngine instanceof Compilable) {
            compiledScript = ((Compilable) scriptEngine).compile(script);
        } else {
            compiledScript = null;
        }
    }
}