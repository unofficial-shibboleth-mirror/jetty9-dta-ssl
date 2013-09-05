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
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;
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

    /** Constructor. */
    public DataSealer() {
        keystoreType = "JCEKS";
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
            
            if (random == null) {
                random = new SecureRandom();
            }

            cipherKey = loadKey(cipherKeyAlias);

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
     * Sets the pseudorandom generator.
     * 
     * @param r the pseudorandom generator to set
     */
    public void setRandom(@Nonnull final SecureRandom r) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        random = Constraint.isNotNull(r, "SecureRandom cannot be null");
    }

    /**
     * Sets the keystore type.
     * 
     * @param type the keystore type to set
     */
    public void setKeystoreType(@Nonnull @NotEmpty final String type) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keystoreType = Constraint.isNotNull(StringSupport.trimOrNull(type), "Keystore type cannot be null or empty");
    }

    /**
     * Sets the keystore path.
     * 
     * @param path the keystore path to set
     */
    public void setKeystorePath(@Nonnull @NotEmpty final String path) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keystorePath = Constraint.isNotNull(StringSupport.trimOrNull(path), "Keystore path cannot be null or empty");
    }

    /**
     * Sets the keystore password.
     * 
     * @param password the keystore password to set
     */
    public void setKeystorePassword(@Nonnull @NotEmpty final String password) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keystorePassword = Constraint.isNotNull(password, "Keystore password cannot be null");
    }

    /**
     * Sets the default encryption key alias.
     * 
     * @param alias the encryption key alias to set
     */
    public void setCipherKeyAlias(@Nonnull @NotEmpty final String alias) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        cipherKeyAlias = Constraint.isNotNull(StringSupport.trimOrNull(alias),
                "Cipher key alias cannot be null or empty");
    }

    /**
     * Sets the encryption key password.
     * 
     * @param password the encryption key password to set
     */
    public void setCipherKeyPassword(@Nonnull @NotEmpty final String password) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        cipherKeyPassword = Constraint.isNotNull(password, "Cipher key password cannot be null");
    }

    /**
     * Decrypts and verifies an encrypted bundle created with {@link #wrap(String, long)}.
     * 
     * <p>If the data indicates that a non-default key was used, an attempt to load this key
     * from the keystore will be made.</p>
     * 
     * @param wrapped the encoded blob
     * @return the decrypted data, if it's unexpired
     * @throws DataSealerException if the data cannot be unwrapped and verified
     */
    @Nonnull public String unwrap(@Nonnull @NotEmpty final String wrapped) throws DataSealerException {

        try {
            final byte[] in = Base64Support.decode(wrapped);

            final ByteArrayInputStream inputByteStream = new ByteArrayInputStream(in);
            final DataInputStream inputDataStream = new DataInputStream(inputByteStream);
            
            // Extract alias of key, and load if necessary.
            SecretKey keyUsed;
            String keyAlias = inputDataStream.readUTF();
            log.trace("Data was encrypted by key '{}'", keyAlias);
            if (keyAlias.equals(cipherKeyAlias)) {
                keyUsed = cipherKey;
            } else {
                keyUsed = loadKey(keyAlias);
                log.trace("Loaded older key '{}' from keystore", keyAlias);
            }
            
            final GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            
            // Load the IV.
            final int ivSize = cipher.getUnderlyingCipher().getBlockSize();
            final byte[] iv = new byte[ivSize];
            inputDataStream.readFully(iv);

            final AEADParameters aeadParams =
                    new AEADParameters(new KeyParameter(keyUsed.getEncoded()), 128, iv, cipherKeyAlias.getBytes());
            cipher.init(false, aeadParams);

            // Data can't be any bigger the original minus IV.
            final byte[] data = new byte[in.length - ivSize];
            final int dataSize = inputDataStream.read(data);
            
            final byte[] plaintext = new byte[cipher.getOutputSize(dataSize)];
            final int outputLen = cipher.processBytes(data, 0, dataSize, plaintext, 0);
            cipher.doFinal(data, outputLen);
            
            // Decrypt the rest of the data and pass it into the subroutine for processing.
            return extractAndCheckDecryptedData(plaintext);

        } catch (IllegalStateException | InvalidCipherTextException| IOException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Exception unwrapping data", e);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Exception loading legacy key", e);
        }
    }

    /**
     * Extract the GZIP'd data and test for expiration before returning it.
     * 
     * @param decryptedBytes the data we are looking at
     * 
     * @return the decoded data if it is valid and unexpired
     * @throws DataSealerException if the data cannot be unwrapped and verified
     */
    @Nonnull private String extractAndCheckDecryptedData(@Nonnull @NotEmpty byte[] decryptedBytes)
            throws DataSealerException {
        
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(decryptedBytes);
            GZIPInputStream compressedData = new GZIPInputStream(byteStream);
            DataInputStream dataInputStream = new DataInputStream(compressedData);

            final long decodedExpirationTime = dataInputStream.readLong();
            final String decodedData = dataInputStream.readUTF();

            if (System.currentTimeMillis() > decodedExpirationTime) {
                log.info("Unwrapped data has expired");
                throw new DataExpiredException("Unwrapped data has expired");
            }

            log.debug("Unwrapped data verified");
            return decodedData;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Caught IOException unwrapping data", e);
        }
    }

    /**
     * Encodes data into an AEAD-encrypted blob, gzip(exp|data)
     * 
     * <ul>
     * <li>exp = expiration time of the data; 8 bytes; Big-endian</li>
     * <li>data = the data; a UTF-8-encoded string</li>
     * </ul>
     * 
     * <p>As part of encryption, the key alias is supplied as additional authenticated data
     * to the cipher. Afterwards, the encrypted data is prepended by the IV and then again by the alias
     * (in length-prefixed UTF-8 format), which identifies the key used. Finally the result is base64-encoded.</p>
     * 
     * @param data the data to wrap
     * @param exp expiration time
     * @return the encoded blob
     * @throws DataSealerException if the wrapping operation fails
     */
    @Nonnull public String wrap(@Nonnull @NotEmpty final String data, long exp) throws DataSealerException {

        if (data == null || data.length() == 0) {
            throw new IllegalArgumentException("Data must be supplied for the wrapping operation");
        }

        try {

            final GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            final byte[] iv = new byte[cipher.getUnderlyingCipher().getBlockSize()];
            random.nextBytes(iv);
            
            final AEADParameters aeadParams =
                    new AEADParameters(new KeyParameter(cipherKey.getEncoded()), 128, iv, cipherKeyAlias.getBytes());
            cipher.init(true, aeadParams);

            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            final GZIPOutputStream compressedStream = new GZIPOutputStream(byteStream);
            final DataOutputStream dataStream = new DataOutputStream(compressedStream);

            dataStream.writeLong(exp);
            dataStream.writeUTF(data);

            dataStream.flush();
            compressedStream.flush();
            compressedStream.finish();
            byteStream.flush();

            final byte[] plaintext = byteStream.toByteArray();
            final byte[] encryptedData = new byte[cipher.getOutputSize(plaintext.length)];
            int outputLen = cipher.processBytes(plaintext, 0, plaintext.length, encryptedData, 0);
            cipher.doFinal(encryptedData, outputLen);

            final ByteArrayOutputStream finalByteStream = new ByteArrayOutputStream();
            final DataOutputStream finalDataStream = new DataOutputStream(finalByteStream);
            finalDataStream.writeUTF(cipherKeyAlias);
            finalDataStream.write(iv);
            finalDataStream.write(encryptedData);
            finalDataStream.flush();
            finalByteStream.flush();
            
            return Base64Support.encode(finalByteStream.toByteArray(), false);

        } catch (IOException | IllegalStateException | InvalidCipherTextException e) {
            log.error(e.getMessage());
            throw new DataSealerException("Exception wrapping data", e);
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
            final GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            final byte[] iv = new byte[cipher.getUnderlyingCipher().getBlockSize()];
            random.nextBytes(iv);
            final AEADParameters aeadParams = new AEADParameters(
                    new KeyParameter(cipherKey.getEncoded()), 128, iv, "aad".getBytes(StandardCharsets.UTF_8));
            cipher.init(true, aeadParams);
            byte[] plaintext = "test".getBytes(StandardCharsets.UTF_8);
            final byte[] encryptedData = new byte[cipher.getOutputSize(plaintext.length)];
            int outputLen = cipher.processBytes(plaintext, 0, plaintext.length, encryptedData, 0);
            cipher.doFinal(encryptedData, outputLen);

            cipher.init(false, aeadParams);
            plaintext = new byte[cipher.getOutputSize(encryptedData.length)];
            outputLen = cipher.processBytes(encryptedData, 0, encryptedData.length, plaintext, 0);
            cipher.doFinal(plaintext, outputLen);
            decrypted = Strings.fromUTF8ByteArray(plaintext);
            
        } catch (IllegalStateException | InvalidCipherTextException e) {
            log.error("Round trip encryption/decryption test unsuccessful", e);
            throw new DataSealerException("Round trip encryption/decryption test unsuccessful", e);
        }

        if (decrypted == null || !"test".equals(decrypted)) {
            log.error("Round trip encryption/decryption test unsuccessful. Decrypted text did not match");
            throw new DataSealerException("Round trip encryption/decryption test unsuccessful");
        }
    }

    /**
     * Load a particular key from the keystore designated by the bean's properties.
     * 
     * @param alias alias of the key to load
     * 
     * @return  the loaded key
     * @throws GeneralSecurityException if the load fails due to a security-related issue
     * @throws IOException if the load process fails
     */
    @Nonnull private SecretKey loadKey(@Nonnull @NotEmpty final String alias)
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

        Key loadedKey = ks.getKey(alias, cipherKeyPassword.toCharArray());
        if (loadedKey == null) {
            log.error("Key '{}' not found", alias);
            throw new KeyException("Key was not found in keystore");
        } else if (!(loadedKey instanceof SecretKey)) {
            log.error("Key '{}' is not a symmetric key", alias);
            throw new KeyException("Key was of incorrect type");
        }
        return (SecretKey) loadedKey;
    }
    
}