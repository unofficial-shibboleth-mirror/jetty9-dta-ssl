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

package net.shibboleth.utilities.java.support.logic;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This builds upon the Java <code>assert</code> functionality by offering a compare-and-assign functionality. Because
 * it uses the Java <code>assert</code> functionality the JVM is still informed that the constraint check has been made
 * and could, if it was so inclined, track this to avoid multiple overlapping checks over the lifetime of the checked
 * object.
 */
public final class Assert {

    /** Constructor. */
    private Assert() {
    }

    /**
     * Checks that the given collection is empty. If the collection is not empty an {@link AssertionError} is thrown.
     * 
     * @param <T> type of items in the collection
     * @param collection collection check
     * 
     * @return the checked input
     */
    @Nonnull public static <T> Collection<T> isEmpty(@Nonnull final Collection<T> collection) {
        assert collection != null && collection.isEmpty();
        return collection;
    }

    /**
     * Checks that the given collection is empty. If the collection is not empty an {@link AssertionError} is thrown.
     * 
     * @param <T> type of items in the collection
     * @param collection collection check
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    @Nonnull public static <T> Collection<T> isEmpty(@Nonnull final Collection<T> collection,
            @Nonnull final String message) {
        assert collection != null && collection.isEmpty();
        return collection;
    }

    /**
     * Checks that the given boolean is false. If not an {@link AssertionError} is thrown.
     * 
     * @param b boolean to check
     * 
     * @return the checked boolean
     */
    public static boolean isFalse(final boolean b) {
        assert !b;
        return b;
    }

    /**
     * Checks that the given boolean is false. If not an {@link AssertionError} is thrown.
     * 
     * @param b boolean to check
     * @param message message used in {@link AssertionError}
     * 
     * @return the checked boolean
     */
    public static boolean isFalse(final boolean b, @Nonnull final String message) {
        assert !b : message;
        return b;
    }

    /**
     * Checks that the given number is greater than a given threshold. If the number is not greater than the threshold
     * an {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * 
     * @return the checked input
     */
    public static long isGreaterThan(final long threshold, @Nonnull final long number) {
        assert number > threshold;
        return number;
    }

    /**
     * Checks that the given number is greater than a given threshold. If the number is not greater than the threshold
     * an {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    public static long isGreaterThan(final long threshold, final long number, @Nonnull final String message) {
        assert number > threshold : message;
        return number;
    }

    /**
     * Checks that the given number is greater than, or equal to, a given threshold. If the number is not greater than,
     * or equal to, the threshold an {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * 
     * @return the checked input
     */
    public static long isGreaterThanOrEqual(final long threshold, final long number) {
        assert number >= threshold;
        return number;
    }

    /**
     * Checks that the given number is greater than, or equal to, a given threshold. If the number is not greater than,
     * or equal to, the threshold an {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    public static long isGreaterThanOrEqual(final long threshold, final long number, @Nonnull final String message) {
        assert number >= threshold : message;
        return number;
    }

    /**
     * Checks that the given number is less than a given threshold. If the number is not less than the threshold an
     * {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * 
     * @return the checked input
     */
    public static long isLessThan(final long threshold, final long number) {
        assert number < threshold;
        return number;
    }

    /**
     * Checks that the given number is less than a given threshold. If the number is not less than the threshold an
     * {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    public static long isLessThan(final long threshold, final long number, @Nonnull final String message) {
        assert number < threshold : message;
        return number;
    }

    /**
     * Checks that the given number is less than, or equal to, a given threshold. If the number is not less than, or
     * equal to, the threshold an {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * 
     * @return the checked input
     */
    public static long isLessThanOrEqual(final long threshold, final long number) {
        assert number <= threshold;
        return number;
    }

    /**
     * Checks that the given number is less than, or equal to, a given threshold. If the number is not less than, or
     * equal to, the threshold an {@link AssertionError} is thrown.
     * 
     * @param threshold the threshold
     * @param number the number to be checked
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    public static long isLessThanOrEqual(final long threshold, final long number, @Nonnull final String message) {
        assert number <= threshold : message;
        return number;
    }

    /**
     * Checks that the given collection is not empty. If the collection is empty an {@link AssertionError} is thrown.
     * 
     * @param <T> type of items in the collection
     * @param collection collection check
     * 
     * @return the checked input
     */
    @Nonnull public static <T> Collection<T> isNotEmpty(@Nonnull final Collection<T> collection) {
        assert collection != null && !collection.isEmpty();
        return collection;
    }

    /**
     * Checks that the given collection is not empty. If the collection is empty an {@link AssertionError} is thrown.
     * 
     * @param <T> type of items in the collection
     * @param collection collection check
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    @Nonnull public static <T> Collection<T> isNotEmpty(@Nonnull final Collection<T> collection,
            @Nonnull final String message) {
        assert collection != null && !collection.isEmpty() : message;
        return collection;
    }

    /**
     * Checks that the given object is not null. If the object is null an {@link AssertionError} is thrown.
     * 
     * @param <T> object type
     * @param obj object to check
     * 
     * @return the checked input
     */
    @Nonnull public static <T> T isNotNull(@Nullable final T obj) {
        assert obj != null;
        return obj;
    }

    /**
     * Checks that the given object is not null. If the object is null an {@link AssertionError} is thrown.
     * 
     * @param <T> object type
     * @param obj object to check
     * @param message message used in {@link AssertionError}
     * 
     * @return the checked input
     */
    @Nonnull public static <T> T isNotNull(@Nullable final T obj, @Nonnull final String message) {
        assert obj != null : message;
        return obj;
    }

    /**
     * Checks that the given object is null. If the object is not null an {@link AssertionError} is thrown.
     * 
     * @param <T> object type
     * @param obj object to check
     * 
     * @return the checked input
     */
    @Nullable public static <T> T isNull(@Nullable final T obj) {
        assert obj == null;
        return obj;

    }

    /**
     * Checks that the given object is null. If the object is not null an {@link AssertionError} is thrown.
     * 
     * @param <T> object type
     * @param obj object to check
     * @param message message used in {@link AssertionError}
     * 
     * @return the checked input
     */
    @Nullable public static <T> T isNull(@Nullable final T obj, @Nonnull final String message) {
        assert obj != null : message;
        return obj;

    }

    /**
     * Checks that the given boolean is true. If not an {@link AssertionError} is thrown.
     * 
     * @param b boolean to check
     * 
     * @return the checked boolean
     */
    public static boolean isTrue(final boolean b) {
        assert b;
        return b;
    }

    /**
     * Checks that the given boolean is true. If not an {@link AssertionError} is thrown.
     * 
     * @param b boolean to check
     * @param message message used in {@link AssertionError}
     * 
     * @return the checked boolean
     */
    public static boolean isTrue(final boolean b, @Nonnull final String message) {
        assert b : message;
        return b;
    }

    /**
     * Checks that the array does not contain any null elements.
     * 
     * @param <T> type of elements in the array
     * @param array array to check
     * 
     * @return the given array
     */
    @Nonnull public static <T> T[] noNullItems(@Nonnull final T[] array) {
        assert array != null;

        for (T element : array) {
            assert element != null;
        }

        return array;
    }

    /**
     * Checks that the array does not contain any null elements.
     * 
     * @param <T> type of elements in the array
     * @param array array to check
     * @param message message used in the {@link AssertionError}
     * 
     * @return the given array
     */
    @Nonnull public static <T> T[] noNullItems(@Nonnull final T[] array, @Nonnull String message) {
        assert array != null;

        for (T element : array) {
            assert element != null : message;
        }

        return array;
    }

    /**
     * Checks that the given number is in the exclusive range. If the number is not in the range an
     * {@link AssertionError} is thrown.
     * 
     * @param lowerTheshold lower bound of the range
     * @param upperThreshold upper bound of the range
     * @param number number to check
     * 
     * @return the checked input
     */
    public static long numberInRangeExclusive(final long lowerTheshold, final long upperThreshold, final long number) {
        assert number > lowerTheshold && number < upperThreshold;
        return number;
    }

    /**
     * Checks that the given number is in the exclusive range. If the number is not in the range an
     * {@link AssertionError} is thrown.
     * 
     * @param lowerTheshold lower bound of the range
     * @param upperThreshold upper bound of the range
     * @param number number to check
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    public static long numberInRangeExclusive(final long lowerTheshold, final long upperThreshold, final long number,
            @Nonnull final String message) {
        assert number > lowerTheshold && number < upperThreshold : message;
        return number;
    }

    /**
     * Checks that the given number is in the inclusive range. If the number is not in the range an
     * {@link AssertionError} is thrown.
     * 
     * @param lowerTheshold lower bound of the range
     * @param upperThreshold upper bound of the range
     * @param number number to check
     * 
     * @return the checked input
     */
    public static long numberInRangeInclusive(final long lowerTheshold, final long upperThreshold, final long number) {
        assert number >= lowerTheshold && number <= upperThreshold;
        return number;
    }

    /**
     * Checks that the given number is in the inclusive range. If the number is not in the range an
     * {@link AssertionError} is thrown.
     * 
     * @param lowerTheshold lower bound of the range
     * @param upperThreshold upper bound of the range
     * @param number number to check
     * @param message message used in the {@link AssertionError}
     * 
     * @return the checked input
     */
    public static long numberInRangeInclusive(final long lowerTheshold, final long upperThreshold, final long number,
            @Nonnull final String message) {
        assert number >= lowerTheshold && number <= upperThreshold : message;
        return number;
    }
}