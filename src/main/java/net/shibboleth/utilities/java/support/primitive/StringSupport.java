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

package net.shibboleth.utilities.java.support.primitive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

/** String utility methods. */
public final class StringSupport {

    /** Constructor. */
    private StringSupport() {
    }

    /**
     * Converts a List of strings into a single string, with values separated by a specified delimiter.
     * 
     * @param values list of strings
     * @param delimiter the delimiter used between values
     * 
     * @return delimited string of values
     */
    @Nonnull public static String
            listToStringValue(@Nonnull final List<String> values, @Nonnull final String delimiter) {
        Constraint.isNotNull(values, "List of values can not be null");
        Constraint.isNotNull(delimiter, "String delimiter may not be null");

        final StringBuilder stringValue = new StringBuilder();
        final Iterator<String> valueItr = values.iterator();
        while (valueItr.hasNext()) {
            stringValue.append(valueItr.next());
            if (valueItr.hasNext()) {
                stringValue.append(delimiter);
            }
        }

        return stringValue.toString();
    }

    /**
     * Converts a delimited string into a list.  We cannot user an ungarnished tokenizer since it doesn't add a empty
     * String if end of the input String was the delimiter.  Hence we have to explicitly check.
     * 
     * @param string the string to be split into a list
     * @param delimiter the delimiter between values. This string may contain multiple delimiter characters, as allowed
     *            by {@link StringTokenizer}
     * 
     * @return the list of values or an empty list if the given string is empty
     */
    @Nonnull public static List<String> stringToList(@Nonnull final String string, @Nonnull final String delimiter) {
        Constraint.isNotNull(string, "String data can not be null");
        Constraint.isNotNull(delimiter, "String delimiter may not be null");

        final ArrayList<String> values = new ArrayList<String>();

        final String trimmedString = trimOrNull(string);
        if (trimmedString != null) {
            final StringTokenizer tokens = new StringTokenizer(trimmedString, delimiter);
            while (tokens.hasMoreTokens()) {
                values.add(tokens.nextToken());
            }
            if (string.endsWith(delimiter)) {
                values.add("");
            }
        }

        return values;
    }

    /**
     * Safely trims a string.
     * 
     * @param s the string to trim, may be null
     * 
     * @return the trimmed string or null if the given string was null
     */
    @Nullable public static String trim(@Nullable final String s) {
        if (s == null) {
            return null;
        }

        return s.trim();
    }

    /**
     * Safely trims a string and, if empty, converts it to null.
     * 
     * @param s the string to trim, may be null
     * 
     * @return the trimmed string or null if the given string was null or the trimmed string was empty
     */
    @Nullable public static String trimOrNull(@Nullable final String s) {
        final String temp = trim(s);
        if (temp == null || temp.length() == 0) {
            return null;
        }

        return temp;
    }
}