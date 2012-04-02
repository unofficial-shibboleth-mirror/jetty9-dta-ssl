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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import net.shibboleth.utilities.java.support.logic.Constraint;

/** A stopwatch with millisecond precision. */
@NotThreadSafe
public class Stopwatch {

    /** Time the stopwatch was started. */
    private Long startTime;

    /** Time the stopwatch was stopped. */
    private Long stopTime;

    /** Time that elapsed while the stopwatch was running. */
    private long elapsedTime;

    /** Constructor. */
    public Stopwatch() {

    }

    /**
     * Constructor. Using this constructor creates an already started stopwatch
     * 
     * @param startTimeInMillis the time, in milliseconds since the epoch, when the stop watch started
     */
    public Stopwatch(final long startTimeInMillis) {
        startTime = startTimeInMillis;
    }

    /**
     * Time, in milliseconds, that elapsed while the stopwatch was running.
     * 
     * @return time that elapsed while the stopwatch was running, 0 if the stopwatch has not yet been stopped
     */
    public long elapsedTime() {
        return elapsedTime;
    }

    /**
     * Time, in the given time unit, that elapsed while the stopwatch was running. Fractions of the given time unit are
     * discarded.
     * 
     * @param unit time unit in which the result is returned
     * 
     * @return time that elapsed while the stopwatch was running, 0 if the stopwatch has not yet been stopped
     */
    public long elapsedTime(@Nonnull final TimeUnit unit) {
        Constraint.isNotNull(unit, "Provided TimeUnit can not be null");
        return unit.convert(elapsedTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Checks if the stopwatch is currently running.
     * 
     * @return true if the stopwatch is running, false if not
     */
    public boolean isRunning() {
        return startTime != null && stopTime == null;
    }

    /** Reset the stopwatch such that it is not running and no time has elapsed. */
    public void reset() {
        startTime = null;
        stopTime = null;
        elapsedTime = 0;
    }

    /** Starts the stopwatch if it hasn't been started since being constructed or {@link #reset()} was called. */
    public void start() {
        // we take the current time before checking if the watch is running just to avoid
        // an latency that might show up in that call
        long currentTime = System.currentTimeMillis();
        if (!isRunning()) {
            startTime = currentTime;
        }
    }

    /** Stops the stopwatch if it hasn't been stopped since being constructed or {@link #reset()} was called. */
    public void stop() {
        // we take the current time before checking if the watch is running just to avoid
        // an latency that might show up in that call
        long currentTime = System.currentTimeMillis();
        if (isRunning()) {
            stopTime = currentTime;
            elapsedTime = stopTime - startTime;
        }
    }
}