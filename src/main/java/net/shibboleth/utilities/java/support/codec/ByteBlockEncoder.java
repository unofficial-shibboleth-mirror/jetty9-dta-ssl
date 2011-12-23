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

package net.shibboleth.utilities.java.support.codec;

/**
 * An {@link Encoder} that operates on a set number of bytes at a time.
 * 
 * Many instances of this type of encoder will operate on <code>byte[]</code> inputs larger than the size given by its
 * {@link #getBlockSize()}. In such cases, the input is usually chunked in to blocks of the appropriate size with the
 * final chunk being padded as necessary.
 * 
 * @param <Output> type of data that is output from the encoding function
 */
public interface ByteBlockEncoder<Output> extends Encoder<byte[], Output> {

    /**
     * Gets the number of bytes upon within the unit of information upon which this encoder acts.
     * 
     * @return number of bytes upon within the unit of information upon which this encoder acts
     */
    public int getBlockSize();

}