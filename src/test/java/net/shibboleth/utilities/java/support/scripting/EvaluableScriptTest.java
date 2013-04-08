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
import java.io.FileWriter;
import java.io.IOException;

import javax.script.ScriptException;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/** Tests for {@link EvaluableScript}.*/
public class EvaluableScriptTest {

    
    private static final String SCRIPT_LANGUAGE = "JavaScript";

    /** A simple script to set a constant value. */
    private static final String TEST_SIMPLE_SCRIPT = "importPackage(Packages.net.shibboleth.idp.attribute);\n"
            + "foo = res = new Attribute(\"bar\");\n foo.addValue(\"value\");\n";

    
    @Test public void testEvaluableScript() throws ScriptException, IOException {
       
        new EvaluableScript(SCRIPT_LANGUAGE, TEST_SIMPLE_SCRIPT);
        
        try {
            new EvaluableScript(" ", TEST_SIMPLE_SCRIPT);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // OK
        }
        
        try {
            new EvaluableScript(SCRIPT_LANGUAGE, " ");
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // OK
        }

        try {
            new EvaluableScript(null, TEST_SIMPLE_SCRIPT);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // OK
        }
        
        try {
            new EvaluableScript(SCRIPT_LANGUAGE, (String) null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // OK
        }

        File f = File.createTempFile("shibTest", "js");

        FileWriter s = new FileWriter(f);
        s.write(TEST_SIMPLE_SCRIPT, 0, TEST_SIMPLE_SCRIPT.length());
        s.close();

        Assert.assertEquals((new EvaluableScript(SCRIPT_LANGUAGE, f)).getScriptLanguage(), SCRIPT_LANGUAGE);

        try {
            new EvaluableScript(null, f);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // OK
        }
        
        try {
            new EvaluableScript(SCRIPT_LANGUAGE, (File) null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // OK
        }

    }
}
