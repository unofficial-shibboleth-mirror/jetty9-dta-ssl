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

package net.shibboleth.utilities.java.support.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import net.shibboleth.utilities.java.support.annotation.constraint.GreaterThan;
import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;
import net.shibboleth.utilities.java.support.logic.Assert;

import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Generates a random number of bytes via a {@link Random} source and encodes them into a string using a
 * {@link ByteBlockEncoder}.
 */
public class RandomIdentifierGenerationStrategy implements IdentifierGenerationStrategy {

    /** Random number generator. */
    private final Random random;

    /** Number of random bytes in the identifier. */
    private final int sizeOfIdentifier;

    /** Encoder used to convert the random bytes in to a string. */
    private final BinaryEncoder encoder;

    /**
     * Constructor. Initializes the random number source to a new {@link SecureRandom}, size of identifier is set to 16
     * bytes, and the encoder is set to a {@link HexEncoder}.
     */
    public RandomIdentifierGenerationStrategy() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            sizeOfIdentifier = 16;
            encoder = new Hex();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1PRNG is required to be supported by the JVM but is not", e);
        }
    }

    /**
     * Constructor. Initializes the random number source to a new {@link SecureRandom} and the encoder is set to a
     * {@link HexEncoder}.
     * 
     * @param identifierSize number of random bytes in identifier
     */
    public RandomIdentifierGenerationStrategy(@GreaterThan(0) final int identifierSize) {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            sizeOfIdentifier =
                    (int) Assert.isGreaterThan(0, identifierSize,
                            "Number of bytes in the identifier must be greater than 0");
            encoder = new Hex();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1PRNG is required to be supported by the JVM but is not", e);
        }
    }

    /**
     * Constructor.
     * 
     * @param source source of random bytes
     * @param identifierSize number of random bytes in the identifier
     * @param identifierEncoder encoder used to convert random bytes to string identifier
     */
    public RandomIdentifierGenerationStrategy(@NotNull final Random source, @GreaterThan(0) final int identifierSize,
            @NotNull final BinaryEncoder identifierEncoder) {
        random = Assert.isNotNull(source, "Random number source can not be null");
        sizeOfIdentifier =
                (int) Assert.isGreaterThan(0, identifierSize,
                        "Number of bytes in the identifier must be greater than 0");
        encoder = Assert.isNotNull(identifierEncoder, "Identifier is encoder can not be null");
    }

    /** {@inheritDoc} */
    public String generateIdentifier() {
        byte[] buf = new byte[sizeOfIdentifier];
        random.nextBytes(buf);
        try {
            return StringUtils.newStringUsAscii(encoder.encode(buf));
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }
}