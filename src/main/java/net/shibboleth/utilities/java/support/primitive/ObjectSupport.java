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

import com.google.common.base.Objects;

/** Helper methods for working with Objects. */
public final class ObjectSupport {

    /** Constructor. */
    private ObjectSupport() {
    }

    //submitted as RFE 846 for google guava
    /**
     * Performs a safe (null-aware) {@link Object#hashCode()}.
     * 
     * @param o object for which to get the hash code, may be null
     * 
     * @return the hash code for the object of 0 if the given object is null
     */
    public static int hashCode(final Object o) {
        if (o == null) {
            return 0;
        }

        return o.hashCode();
    }

    // submitted as REF 845 for google quava
    /**
     * Null-safe check to determine if the given object is equal to any of a list of objects.
     * 
     * @param o1 object to check if it's equal to any object in a list
     * @param objects list of objects
     * 
     * @return true of the given object is equal to any object in the given list
     */
    public static boolean equalsAny(final Object o1, final Object... objects) {
        if (o1 == null || objects == null) {
            return o1 == objects;
        }

        for (Object object : objects) {
            if (Objects.equal(o1, object)) {
                return true;
            }
        }

        return false;
    }
}