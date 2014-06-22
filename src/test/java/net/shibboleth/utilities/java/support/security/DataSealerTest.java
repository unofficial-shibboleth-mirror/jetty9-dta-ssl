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

import java.security.SecureRandom;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.TestResourceConverter;

import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for {@link DataSealer}.
 */
public class DataSealerTest {

    private Resource keystoreResource;

    final private String THE_DATA = "THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA"
            + "THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA"
            + "THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA"
            + "THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA THIS IS SOME TEST DATA";
    final private long THE_DELAY = 500;

    @BeforeClass public void initializeKeystoreResource() {
        final ClassPathResource resource =
                new ClassPathResource("/data/net/shibboleth/utilities/java/support/security/SealerKeyStore.jks");
        Assert.assertTrue(resource.exists());
        keystoreResource = TestResourceConverter.of(resource);
    }

    @Test public void defaults() {
        DataSealer sealer = new DataSealer();
        Assert.assertEquals(sealer.getKeystoreType(), "JCEKS");
    }

    @Test public void setterGetters() {
        final SecureRandom random = new SecureRandom();
        final String CIPHER_KEY_ALIIAS = "CipherAlias";
        final String CIPHER_KEY_PASSWORD = "Cpassword";
        final String KEYSTORE_PASSWORD = "Kpassword";
        final String KEYSTORE_TYPE = "KType";

        DataSealer sealer = new DataSealer();

        sealer.setKeystoreType(KEYSTORE_TYPE);
        sealer.setKeystoreResource(keystoreResource);
        sealer.setKeystorePassword(KEYSTORE_PASSWORD);

        sealer.setCipherKeyAlias(CIPHER_KEY_ALIIAS);
        sealer.setCipherKeyPassword(CIPHER_KEY_PASSWORD);

        sealer.setRandom(random);

        Assert.assertEquals(sealer.getKeystoreType(), KEYSTORE_TYPE);
        Assert.assertEquals(sealer.getKeystoreResource(), keystoreResource);
        Assert.assertEquals(sealer.getKeystorePassword(), KEYSTORE_PASSWORD);

        Assert.assertEquals(sealer.getCipherKeyAlias(), CIPHER_KEY_ALIIAS);
        Assert.assertEquals(sealer.getCipherKeyPassword(), CIPHER_KEY_PASSWORD);

        Assert.assertEquals(sealer.getRandom(), random);

    }

    private DataSealer createDataSealer() throws DataSealerException, ComponentInitializationException {
        DataSealer sealer = new DataSealer();
        sealer.setCipherKeyAlias("secret");
        sealer.setCipherKeyPassword("kpassword");

        sealer.setKeystorePassword("password");
        sealer.setKeystoreResource(keystoreResource);

        sealer.initialize();

        return sealer;
    }

    private DataSealer createDataSealer2() throws DataSealerException, ComponentInitializationException {
        DataSealer sealer = new DataSealer();
        sealer.setCipherKeyAlias("secret2");
        sealer.setCipherKeyPassword("kpassword");

        sealer.setKeystorePassword("password");
        sealer.setKeystoreResource(keystoreResource);

        sealer.initialize();

        return sealer;
    }
    
    @Test public void encodeDecode() throws DataSealerException, ComponentInitializationException {
        final DataSealer sealer = createDataSealer();

        String encoded = sealer.wrap(THE_DATA, System.currentTimeMillis() + 50000);
        Assert.assertEquals(sealer.unwrap(encoded), THE_DATA);
    }

    @Test public void encodeDecodeSecondKey() throws DataSealerException, ComponentInitializationException {
        final DataSealer sealer = createDataSealer();
        final DataSealer sealer2 = createDataSealer2();

        String encoded = sealer.wrap(THE_DATA, System.currentTimeMillis() + 50000);
        Assert.assertEquals(sealer.unwrap(encoded), THE_DATA);
        Assert.assertEquals(sealer2.unwrap(encoded), THE_DATA);
    }
    
    @Test public void timeOut() throws DataSealerException, InterruptedException, ComponentInitializationException {
        final DataSealer sealer = createDataSealer();

        String encoded = sealer.wrap(THE_DATA, System.currentTimeMillis() + THE_DELAY);
        Thread.sleep(THE_DELAY + 1);
        try {
            sealer.unwrap(encoded);
            Assert.fail("Should have timed out");
        } catch (DataExpiredException ex) {
            // OK
        }
    }

    @Test public void badValues() throws DataSealerException, ComponentInitializationException {
        DataSealer sealer = new DataSealer();

        try {
            sealer.initialize();
            Assert.fail("no keys");
        } catch (ComponentInitializationException e) {

        }
        sealer.setCipherKeyAlias("secret");
        sealer.setCipherKeyPassword("kpassword");

        sealer.setKeystorePassword("password");
        sealer.setKeystoreResource(keystoreResource);

        sealer.initialize();

        try {
            sealer.unwrap("");
            Assert.fail("no keys");
        } catch (DataSealerException e) {
            // OK
        }

        try {
            sealer.unwrap("RandomGarbage");
            Assert.fail("no keys");
        } catch (DataSealerException e) {
            // OK
        }

        final String wrapped = sealer.wrap(THE_DATA, 3600 * 1000);

        final String corrupted = wrapped.substring(0, 25) + "A" + wrapped.substring(27);

        try {
            sealer.unwrap(corrupted);
            Assert.fail("no keys");
        } catch (DataSealerException e) {
            // OK
        }

        try {
            sealer.wrap(null, 10);
            Assert.fail("no keys");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

}