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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Applies a MAC to time-limited information and encrypts with a symmetric key.
 */
public class DataSealer extends AbstractInitializableComponent {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(DataSealer.class);

    /** Default key used for encryption. */
    @NonnullAfterInit private SecretKey cipherKey;

    /** Default key used for MAC. */
    @NonnullAfterInit private SecretKey macKey;

    /** Source of secure random data. */
    @NonnullAfterInit private SecureRandom random;

    /** Type of keystore to use for access to keys. */
    @NonnullAfterInit private String keystoreType;

    /** Path to keystore. */
    @NonnullAfterInit private String keystorePath;

    /** Password for keystore. */
    @NonnullAfterInit private String keystorePassword;

    /** Keystore alias for the default encryption key. */
    @NonnullAfterInit private String cipherKeyAlias;

    /** Password for encryption key(s). */
    @NonnullAfterInit private String cipherKeyPassword;

    /** Encryption algorithm to use. */
    @Nonnull @NotEmpty private String cipherAlgorithm;

    /** Keystore alias for the MAC key. */
    @NonnullAfterInit private String macKeyAlias;

    /** Password for MAC key. */
    @NonnullAfterInit private String macKeyPassword;

    /** MAC algorithm to use. */
    @Nonnull @NotEmpty private String macAlgorithm;

    /** Constructor. */
    public DataSealer() {
        keystoreType = "JCEKS";
        cipherAlgorithm = "AES/CBC/PKCS5Padding";
        macAlgorithm = "HmacSHA256";
    }
    
    /** {@inheritDoc} */
    public void doInitialize() throws ComponentInitializationException {
        try {
            try {
                Constraint.isNotNull(keystoreType, "Keystore type cannot be null");
                Constraint.isNotNull(keystorePath, "Keystore path cannot be null");
                Constraint.isNotNull(keystorePassword, "Keystore password cannot be null");
                Constraint.isNotNull(cipherKeyAlias, "Cipher key alias cannot be null");
                Constraint.isNotNull(cipherKeyPassword, "Cipher key password cannot be null");
            } catch (ConstraintViolationException e) {
                throw new ComponentInitializationException(e);
            }
            
            if (macKeyAlias == null) {
                macKeyAlias = cipherKeyAlias;
            }
            
            if (macKeyPassword == null) {
                macKeyPassword = cipherKeyPassword;
            }

            if (random == null) {
                random = new SecureRandom();
            }

            loadKeys();

            // Before we finish initialization, make sure that things are working.
            testEncryption();

        } catch (GeneralSecurityException|IOException e) {
            log.error(e.getMessage());
            throw new ComponentInitializationException("Exception loading the keystore", e);
        } catch (DataSealerException e) {
            log.error(e.getMessage());
            throw new ComponentInitializationException("Exception testing the encryption settings used", e);
        }
    }

    /**
     * Returns the encryption key.
     * 
     * @return the encryption key
     */
    @NonnullAfterInit public SecretKey getCipherKey() {
        return cipherKey;
    }

    /**
     * Returns the MAC key, if different from the encryption key.
     * 
     * @return the MAC key
     */
    @NonnullAfterInit public SecretKey getMacKey() {
        return macKey;
    }

    /**
     * Returns the pseudorandom generator.
     * 
     * @return the pseudorandom generator
     */
    @NonnullAfterInit public SecureRandom getRandom() {
        return random;
    }

    /**
     * Returns the keystore type.
     * 
     * @return the keystore type.
     */
    @NonnullAfterInit public String getKeystoreType() {
        return keystoreType;
    }

    /**
     * Returns the keystore path.
     * 
     * @return the keystore path
     */
    @NonnullAfterInit public String getKeystorePath() {
        return keystorePath;
    }

    /**
     * Returns the keystore password.
     * 
     * @return the keystore password
     */
    @NonnullAfterInit public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     * Returns the encryption key alias.
     * 
     * @return the encryption key alias
     */
    @NonnullAfterInit public String getCipherKeyAlias() {
        return cipherKeyAlias;
    }

    /**
     * Returns the encryption key password.
     * 
     * @return the encryption key password
     */
    @NonnullAfterInit public String getCipherKeyPassword() {
        return cipherKeyPassword;
    }

    /**
     * Returns the encryption algorithm.
     * 
     * @return the encryption algorithm
     */
    @Nonnull @NotEmpty public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    /**
     * Returns the MAC key alias.
     * 
     * @return the MAC key alias
     */
    @NonnullAfterInit public String getMacKeyAlias() {
        return macKeyAlias;
    }

    /**
     * Returns the MAC key password.
     * 
     * @return the MAC key password
     */
    @NonnullAfterInit public String getMacKeyPassword() {
        return macKeyPassword;
    }

    /**
     * Returns the MAC algorithm.
     * 
     * @return the MAC algorithm
     */
    @Nonnull @NotEmpty public String getMacAlgorithm() {
        return macAlgorithm;
    }

    /**
     * Sets the pseudorandom generator.
     * 
     * @param r the pseudorandom generator to set
     */
    public void setRandom(@Nonnull final SecureRandom r) {
        random = Constraint.isNotNull(r, "SecureRandom cannot be null");
    }

    /**
     * Sets the keystore type.
     * 
     * @param type the keystore type to set
     */
    public void setKeystoreType(@Nonnull @NotEmpty final String type) {
        keystoreType = Constraint.isNotNull(StringSupport.trimOrNull(type), "Keystore type cannot be null or empty");
    }

    /**
     * Sets the keystore path.
     * 
     * @param path the keystore path to set
     */
    public void setKeystorePath(@Nonnull @NotEmpty final String path) {
        keystorePath = Constraint.isNotNull(StringSupport.trimOrNull(path), "Keystore path cannot be null or empty");
    }

    /**
     * Sets the keystore password.
     * 
     * @param password the keystore password to set
     */
    public void setKeystorePassword(@Nonnull @NotEmpty final String password) {
        keystorePassword = Constraint.isNotNull(password, "Keystore password cannot be null");
    }

    /**
     * Sets the default encryption key alias.
     * 
     * @param alias the encryption key alias to set
     */
    public void setCipherKeyAlias(@Nonnull @NotEmpty final String alias) {
        cipherKeyAlias = Constraint.isNotNull(StringSupport.trimOrNull(alias),
                "Cipher key alias cannot be null or empty");
    }

    /**
     * Sets the encryption key password.
     * 
     * @param password the encryption key password to set
     */
    public void setCipherKeyPassword(@Nonnull @NotEmpty final String password) {
        cipherKeyPassword = Constraint.isNotNull(password, "Cipher key password cannot be null");
    }

    /**
     * Sets the encryption algorithm.
     * 
     * @param alg the encryption algorithm to set
     */
    public void setCipherAlgorithm(@Nonnull @NotEmpty final String alg) {
        cipherAlgorithm = Constraint.isNotNull(StringSupport.trimOrNull(alg),
                "Cipher algorithm cannot be null or empty");
    }

    /**
     * Sets the MAC key alias.
     * 
     * @param alias the MAC key alias to set
     */
    public void setMacKeyAlias(@Nonnull @NotEmpty final String alias) {
        macKeyAlias = Constraint.isNotNull(StringSupport.trimOrNull(alias), "MAC key alias cannot be null or empty");
    }

    /**
     * Sets the MAC key password.
     * 
     * @param password the the MAC key password to set
     */
    public void setMacKeyPassword(@Nonnull @NotEmpty final String password) {
        macKeyPassword = Constraint.isNotNull(password, "MAC key password cannot be null");
    }

    /**
     * Sets the MAC key algorithm.
     * 
     * @param alg the MAC algorithm to set
     */
    public void setMacAlgorithm(@Nonnull @NotEmpty final String alg) {
        macAlgorithm = Constraint.isNotNull(StringSupport.trimOrNull(alg), "MAC algorithm cannot be null or empty");
    }

    /**
     * Decrypts and verifies an encrypted bundle of MAC'd data, and returns it.
     * 
     * @param wrapped the encoded blob
     * @return the decrypted data, if it's unexpired
     * @throws DataSealerException if the data cannot be unwrapped and verified
     */
    @Nonnull public String unwrap(@Nonnull String wrapped) throws DataSealerException {

        try {
            final byte[] in = Base64Support.decode(wrapped);

            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            final int ivSize = cipher.getBlockSize();
            final byte[] iv = new byte[ivSize];


            if (in.length < ivSize) {
                log.error("Wrapped data is malformed (not enough bytes).");
                throw new DataSealerException("Wrapped data is malformed (not enough bytes).");
            }

            // extract the IV, setup the cipher and extract the encrypted handle
            System.arraycopy(in, 0, iv, 0, ivSize);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, cipherKey, ivSpec);

            final byte[] encryptedHandle = new byte[in.length - iv.length];
            System.arraycopy(in, ivSize, encryptedHandle, 0, in.length - iv.length);

            // decrypt the rest of the data and setup the streams
            final byte[] decryptedBytes = cipher.doFinal(encryptedHandle);
            return extractAndCheckDecryptedData(decryptedBytes);

        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught GeneralSecurityException unwrapping data.", e);
        }
    }

    /**
     * Extract the components from the provided data stream decode them and test them prior to returning the value.
     * 
     * @param decryptedBytes the data we are looking at.
     * @return the decoded data if it is valid and unexpired.
     * @throws DataSealerException if the data cannot be unwrapped and verified
     */
    @Nonnull private String extractAndCheckDecryptedData(@Nonnull byte[] decryptedBytes)
            throws DataSealerException {
        
        try {
            final Mac mac = Mac.getInstance(macAlgorithm);
            mac.init(macKey);
            final int macSize = mac.getMacLength();
            
            ByteArrayInputStream byteStream = new ByteArrayInputStream(decryptedBytes);
            GZIPInputStream compressedData = new GZIPInputStream(byteStream);
            DataInputStream dataInputStream = new DataInputStream(compressedData);

            // extract the components
            final byte[] decodedMac = new byte[macSize];
            final int bytesRead;

            bytesRead = dataInputStream.read(decodedMac);
            if (bytesRead != macSize) {
                log.error("Error parsing unwrapped data, unable to extract HMAC.");
                throw new DataSealerException("Error parsing unwrapped data, unable to extract HMAC.");
            }
            final long decodedExpirationTime = dataInputStream.readLong();
            final String decodedData = dataInputStream.readUTF();

            if (System.currentTimeMillis() > decodedExpirationTime) {
                log.info("Unwrapped data has expired.");
                throw new DataExpiredException("Unwrapped data has expired.");
            }

            final byte[] generatedMac = getMAC(mac, decodedData, decodedExpirationTime);

            if (!Arrays.equals(decodedMac, generatedMac)) {
                log.warn("Unwrapped data failed integrity check.");
                throw new DataSealerException("Unwrapped data failed integrity check.");
            }

            log.debug("Unwrapped data verified.");
            return decodedData;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught IOException unwrapping data.", e);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught GeneralSecurityException unwrapping data.", e);
        }

    }

    /**
     * Encodes data into a cryptographic blob: [IV][HMAC][exp][data] where: [IV] = the Initialization Vector; byte-array
     * [HMAC] = the HMAC; byte array [exp] = expiration time of the data; 8 bytes; Big-endian [data] = the principal; a
     * UTF-8-encoded string The bytes are then GZIP'd. The IV is pre-pended to this byte stream, and the result is
     * Base32-encoded. We don't need to encode the IV or MAC's lengths. They can be obtained from Cipher.getBlockSize()
     * and Mac.getMacLength(), respectively.
     * 
     * @param data the data to wrap
     * @param exp expiration time
     * @return the encoded blob
     * @throws DataSealerException if the wrapping operation fails
     */
    @Nonnull public String wrap(@Nonnull String data, long exp) throws DataSealerException {

        if (data == null) {
            throw new IllegalArgumentException("Data must be supplied for the wrapping operation.");
        }

        try {
            final Mac mac = Mac.getInstance(macAlgorithm);
            mac.init(macKey);

            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            final byte[] iv = new byte[cipher.getBlockSize()];
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, cipherKey, ivSpec);

            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            final GZIPOutputStream compressedStream = new GZIPOutputStream(byteStream);
            final DataOutputStream dataStream = new DataOutputStream(compressedStream);

            dataStream.write(getMAC(mac, data, exp));
            dataStream.writeLong(exp);
            dataStream.writeUTF(data);

            dataStream.flush();
            compressedStream.flush();
            compressedStream.finish();
            byteStream.flush();

            final byte[] encryptedData = cipher.doFinal(byteStream.toByteArray());

            final byte[] handleBytes = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, handleBytes, 0, iv.length);
            System.arraycopy(encryptedData, 0, handleBytes, iv.length, encryptedData.length);

            return Base64Support.encode(handleBytes, false);

        } catch (KeyException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught KeyException wrapping data.", e);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught GeneralSecurityException wrapping data.", e);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught IOException wrapping data.", e);
        }

    }

    /**
     * Run a test over the configured bean properties.
     * 
     * @throws DataSealerException if the test fails
     */
    private void testEncryption() throws DataSealerException {

        String decrypted;
        try {
            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            final byte[] iv = new byte[cipher.getBlockSize()];
            random.nextBytes(iv);
            final IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, cipherKey, ivSpec);
            final byte[] cipherText = cipher.doFinal("test".getBytes());

            cipher.init(Cipher.DECRYPT_MODE, cipherKey, ivSpec);
            decrypted = new String(cipher.doFinal(cipherText));
        } catch (GeneralSecurityException e) {
            log.error("Round trip encryption/decryption test unsuccessful: " + e);
            throw new DataSealerException("Round trip encryption/decryption test unsuccessful.", e);
        }

        if (decrypted == null || !"test".equals(decrypted)) {
            log.error("Round trip encryption/decryption test unsuccessful. Decrypted text did not match.");
            throw new DataSealerException("Round trip encryption/decryption test unsuccessful.");
        }

        final byte[] code;
        try {
            final Mac mac = Mac.getInstance(macAlgorithm);
            mac.init(macKey);
            mac.update("foo".getBytes());
            code = mac.doFinal();
        } catch (GeneralSecurityException e) {
            log.error("Message Authentication test unsuccessful: " + e);
            throw new DataSealerException("Message Authentication test unsuccessful.", e);
        }

        if (code == null) {
            log.error("Message Authentication test unsuccessful.");
            throw new DataSealerException("Message Authentication test unsuccessful.");
        }
    }

    /**
     * Compute a MAC over a string, prefixed by an expiration time.
     * 
     * @param mac MAC object to use
     * @param data data to hash
     * @param exp timestamp to prefix the data with
     * @return the resulting MAC
     */
    @Nonnull protected static byte[] getMAC(@Nonnull Mac mac, @Nonnull String data, long exp) {
        mac.update(getLongBytes(exp));
        mac.update(data.getBytes());
        return mac.doFinal();
    }

    /**
     * Convert a long value into a byte array.
     * 
     * @param longValue value to convert
     * @return a byte array
     */
    @Nonnull protected static byte[] getLongBytes(long longValue) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);

            dataStream.writeLong(longValue);
            dataStream.flush();
            byteStream.flush();

            return byteStream.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Load default keys based on bean properties.
     * 
     * @throws GeneralSecurityException if the load fails due to a security-related issue
     * @throws IOException if the load process fails
     */
    private void loadKeys() throws GeneralSecurityException, IOException {
        
        cipherKey = loadKey(cipherKeyAlias, cipherKeyPassword);

        if (!macKeyAlias.equals(cipherKeyAlias)) {
            macKey = loadKey(macKeyAlias, macKeyPassword);
        } else {
            macKey = cipherKey;
        }
    }
    
    /**
     * Load a particular key from the keystore designated by the bean's properties.
     * 
     * @param alias alias of the key to load
     * @param password password of the key to load
     * 
     * @return  the loaded key
     * @throws GeneralSecurityException if the load fails due to a security-related issue
     * @throws IOException if the load process fails
     */
    private SecretKey loadKey(@Nonnull @NotEmpty final String alias, @Nonnull @NotEmpty final String password)
            throws GeneralSecurityException, IOException {
 
        final KeyStore ks = KeyStore.getInstance(keystoreType);
        FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(keystorePath);
            ks.load(fis, keystorePassword.toCharArray());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        Key loadedKey = ks.getKey(alias, password.toCharArray());
        if (!(loadedKey instanceof SecretKey)) {
            log.error("Key '{}' is not a symmetric key.", alias);
        }
        return (SecretKey) loadedKey;
    }
    
}