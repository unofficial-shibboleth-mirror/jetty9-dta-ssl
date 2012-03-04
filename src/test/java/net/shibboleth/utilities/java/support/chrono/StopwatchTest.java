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

package net.shibboleth.utilities.java.support.chrono;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link Stopwatch} unit test. */
public class StopwatchTest {

    @Test public void testStopStartReset() throws Exception {
        Stopwatch watch = new Stopwatch();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        watch.start();
        Assert.assertTrue(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);
        
        watch.start();
        Assert.assertTrue(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        Thread.sleep(10);
        watch.stop();
        Assert.assertFalse(watch.isRunning());
        Assert.assertTrue(watch.elapsedTime() > 0);

        watch.reset();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        watch.stop();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        watch.start();
        watch.reset();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);
    }

    @Test public void testStopStartResetWithInitialTime() throws Exception {
        Stopwatch watch = new Stopwatch(1000);
        Assert.assertTrue(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        watch.start();
        Assert.assertTrue(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);
        
        watch.start();
        Assert.assertTrue(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        Thread.sleep(10);
        watch.stop();
        Assert.assertFalse(watch.isRunning());
        // should be about 1010ms less than current system time
        Assert.assertTrue(watch.elapsedTime() < System.currentTimeMillis());

        watch.reset();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        watch.stop();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);

        watch.start();
        watch.reset();
        Assert.assertFalse(watch.isRunning());
        Assert.assertEquals(watch.elapsedTime(), 0);
    }

    @Test public void testElapsedTime() throws Exception {
        Stopwatch watch = new Stopwatch();

        long start = System.currentTimeMillis();
        watch.start();
        Thread.sleep(10);
        watch.stop();
        long stop = System.currentTimeMillis();
        long elapsedTime = stop - start;
        Assert.assertTrue(watch.elapsedTime() > elapsedTime - 3);

        watch.reset();
        watch.start();
        Thread.sleep(1050);
        watch.stop();
        Assert.assertEquals(watch.elapsedTime(TimeUnit.SECONDS), 1);
    }
}