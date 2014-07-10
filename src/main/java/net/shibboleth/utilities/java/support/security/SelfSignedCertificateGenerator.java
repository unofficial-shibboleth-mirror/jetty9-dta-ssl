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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.X509NameEntryConverter;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * A helper class to generate self-signed keypairs.
 */
public class SelfSignedCertificateGenerator {

    /** Command line argument container. */
    private static final CommandLineArgs COMMAND_LINE_ARGS = new CommandLineArgs();
    
    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SelfSignedCertificateGenerator.class);
    
    /** Type of key to generated. */
    @Nonnull @NotEmpty private String keyType;

    /** Size of the generated key. */
    private int keySize;

    /** Number of years before the self-signed certificate expires. */
    private int certificateLifetime;

    /** Hostname that will appear as the certifcate's DN common name component. */
    private String hostname;

    /** Optional DNS subject alt names. */
    private String[] dnsSubjectAltNames;

    /** Optional DNS subject alt names. */
    private String[] uriSubjectAltNames;

    /** File to which the public key will be written. */
    private File privateKeyFile;

    /** File to which the certificate will be written. */
    private File certificateFile;

    /** Type of keystore to create. */
    private String keystoreType;
    
    /** File to which the keystore will be written. */
    private File keystoreFile;

    /** Password for the generated keystore. */
    private String keystorePassword;

    /** Constructor. */
    public SelfSignedCertificateGenerator() {
        keyType = "RSA";
        keySize = 2048;
        certificateLifetime = 20;
        keystoreType = "PKCS12";
    }
    
    /**
     * Set the type of key that will be generated. Defaults to RSA.
     * 
     * @param type type of key that will be generated
     */
    public void setKeyType(String type) {
        keyType = type;
    }

    /**
     * Set the size of the generated key. Defaults to 2048
     * 
     * @param size size of the generated key
     */
    public void setKeySize(int size) {
        keySize = size;
    }

    /**
     * Set the number of years for which the certificate will be valid.
     * 
     * @param lifetime number of years for which the certificate will be valid
     */
    public void setCertificateLifetime(int lifetime) {
        certificateLifetime = lifetime;
    }

    /**
     * Set the hostname that will appear in the certificate's DN.
     * 
     * @param name hostname that will appear in the certificate's DN
     */
    public void setHostName(String name) {
        hostname = name;
    }

    /**
     * Set the file to which the private key will be written.
     * 
     * @param file file to which the private key will be written
     */
    public void setPrivateKeyFile(File file) {
        privateKeyFile = file;
    }

    /**
     * Set the file to which the certificate will be written.
     * 
     * @param file file to which the certificate will be written
     */
    public void setCertificateFile(File file) {
        certificateFile = file;
    }

    /**
     * Set the type of keystore to create.
     * 
     * @param type keystore type
     */
    public void setKeystoreType(String type) {
        keystoreType = type;
    }

    /**
     * Set the file to which the keystore will be written.
     * 
     * @param file file to which the keystore will be written
     */
    public void setKeystoreFile(File file) {
        keystoreFile = file;
    }

    /**
     * Set the password for the generated keystore.
     * 
     * @param password password for the generated keystore
     */
    public void setKeystorePassword(String password) {
        keystorePassword = password;
    }

    /**
     * Set the optional DNS subject alt names.
     * 
     * @param altNames space delimited set of subject alt names.
     */
    public void setDnsSubjectAltNames(String altNames) {
        dnsSubjectAltNames = altNames.split(" ");
    }

    /**
     * Set the optional URI subject alt names.
     * 
     * @param altNames space delimited set of subject alt names.
     */
    public void setURISubjectAltNames(String altNames) {
        uriSubjectAltNames = altNames.split(" ");
    }
    
    /**
     * The main routine.
     * 
     * @throws Exception if an error occurs
     */
    public void generate() throws Exception {
        validate();
        
        // Check all the files to prevent overwrite.
        
        if (privateKeyFile != null) {
            if (!privateKeyFile.createNewFile()) {
                throw new IOException("Private key file exists: " + privateKeyFile.getAbsolutePath());
            }
        }

        if (certificateFile != null) {
            if (!certificateFile.createNewFile()) {
                throw new IOException("Certificate file exists: " + certificateFile.getAbsolutePath());
            }
        }
        
        if (keystoreFile != null) {
            if (!keystoreFile.createNewFile()) {
                throw new IOException("KeyStore file exists: " + keystoreFile.getAbsolutePath());
            }
        }
        
        // Generate the material.
        final KeyPair keypair = generateKeyPair();
        final X509Certificate certificate = generateCertificate(keypair);

        
        // Write the requested files.
        
        if (privateKeyFile != null) {
            final PEMWriter keyOut = new PEMWriter(new FileWriter(privateKeyFile));
            keyOut.writeObject(keypair.getPrivate());
            keyOut.flush();
            keyOut.close();
        }

        if (certificateFile != null) {
            final PEMWriter certOut = new PEMWriter(new FileWriter(certificateFile));
            certOut.writeObject(certificate);
            certOut.flush();
            certOut.close();
        }

        if (keystoreFile != null) {
            final KeyStore store = KeyStore.getInstance(keystoreType);
            store.load(null, null);
            store.setKeyEntry(hostname, keypair.getPrivate(), keystorePassword.toCharArray(),
                    new X509Certificate[] {certificate});

            final FileOutputStream keystoreOut = new FileOutputStream(keystoreFile);
            store.store(keystoreOut, keystorePassword.toCharArray());
            keystoreOut.flush();
            keystoreOut.close();
        }
    }

    /** Validates the settings. */
    protected void validate() {
        if (keySize > 2048) {
            log.warn("Key size is greater than 2048, this may cause problems with some JVMs");
        }

        if (hostname == null || hostname.length() == 0) {
            throw new IllegalArgumentException("The hostname attribute is required and may not contain an empty value");
        }

        if (keystoreFile != null && (keystorePassword == null || keystorePassword.length() == 0)) {
            throw new IllegalArgumentException("Keystore password may not be null if a keystore file is given");
        }
    }

    /**
     * Generates the key pair for the certificate.
     * 
     * @return key pair for the certificate
     * @throws NoSuchAlgorithmException if there is a problem generating the keys
     */
    protected KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance(keyType);
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            log.error("The {} key type is not supported by this JVM", keyType);
            throw e;
        }
    }

    /**
     * Generates the self-signed certificate.
     * 
     * @param keypair keypair associated with the certificate
     * 
     * @return self-signed certificate
     * @throws Exception if an error occurs
     */
    protected X509Certificate generateCertificate(KeyPair keypair) throws Exception {
        X509V3CertificateGenerator certifcateGenerator = new X509V3CertificateGenerator();
        certifcateGenerator.setPublicKey(keypair.getPublic());

        StringBuffer dnBuffer = new StringBuffer("CN=").append(hostname);

        X509Name dn = new X509Name(false, dnBuffer.toString(), new RdnConverter());
        certifcateGenerator.setIssuerDN(dn);
        certifcateGenerator.setSubjectDN(dn);

        GregorianCalendar date = new GregorianCalendar();
        certifcateGenerator.setNotBefore(date.getTime());

        date.set(GregorianCalendar.YEAR, date.get(GregorianCalendar.YEAR) + certificateLifetime);
        certifcateGenerator.setNotAfter(date.getTime());

        certifcateGenerator.setSerialNumber(new BigInteger(160, new SecureRandom()));

        certifcateGenerator.setSignatureAlgorithm("SHA1withRSA");

        certifcateGenerator.addExtension(X509Extensions.SubjectAlternativeName, false,
                GeneralNames.getInstance(new DERSequence(buildSubjectAltNames())));

        certifcateGenerator.addExtension(X509Extensions.SubjectKeyIdentifier, false,
                new SubjectKeyIdentifierStructure(keypair.getPublic()));

        return certifcateGenerator.generate(keypair.getPrivate());
    }

    /**
     * Builds the subject alt names for the certificate.
     * 
     * @return subject alt names for the certificate
     */
    protected ASN1Encodable[] buildSubjectAltNames() {
        ArrayList<ASN1Encodable> subjectAltNames = new ArrayList<ASN1Encodable>();

        subjectAltNames.add(new GeneralName(GeneralName.dNSName, hostname));

        if (dnsSubjectAltNames != null) {
            for (String subjectAltName : dnsSubjectAltNames) {
                subjectAltNames.add(new GeneralName(GeneralName.dNSName, subjectAltName));
            }
        }

        if (uriSubjectAltNames != null) {
            for (String subjectAltName : uriSubjectAltNames) {
                subjectAltNames.add(new GeneralName(GeneralName.uniformResourceIdentifier, subjectAltName));
            }
        }

        return subjectAltNames.toArray(new ASN1Encodable[0]);
    }
    
    /**
     * Command line entry point.
     * 
     * @param args  command line arguments
     * 
     * @throws Exception 
     */
    public static void main(@Nonnull final String[] args) throws Exception {
        
        // Parse command line.
        JCommander jc = new JCommander(COMMAND_LINE_ARGS, args);
        if (COMMAND_LINE_ARGS.help) {
            jc.setProgramName("SelfSignedCertificateGenerator");
            jc.usage();
            return;
        }
    
        final SelfSignedCertificateGenerator generator = new SelfSignedCertificateGenerator();
    
        generator.setKeyType(COMMAND_LINE_ARGS.keyType);
        generator.setKeySize(COMMAND_LINE_ARGS.keySize);
        generator.setCertificateLifetime(COMMAND_LINE_ARGS.certificateLifetime);
        generator.setHostName(COMMAND_LINE_ARGS.hostname);
        generator.setKeystoreType(COMMAND_LINE_ARGS.keystoreType);
        if (COMMAND_LINE_ARGS.privateKeyFile != null) {
            generator.setPrivateKeyFile(new File(COMMAND_LINE_ARGS.privateKeyFile));
        }
        if (COMMAND_LINE_ARGS.certificateFile != null) {
            generator.setCertificateFile(new File(COMMAND_LINE_ARGS.certificateFile));
        }
        if (COMMAND_LINE_ARGS.keystoreFile != null) {
            generator.setKeystoreFile(new File(COMMAND_LINE_ARGS.keystoreFile));
        }
        generator.setKeystorePassword(COMMAND_LINE_ARGS.keystorePassword);
        
        generator.generate();
    }

    /** Callback that renders a string as either a DER printable string or DER UTF-8 string. */
    private class RdnConverter extends X509NameEntryConverter {

        /** {@inheritDoc} */
        @Override
        public ASN1Primitive getConvertedValue(ASN1ObjectIdentifier oid, String value) {
            if (canBePrintable(value)) {
                return new DERPrintableString(value);
            } else {
                return new DERUTF8String(value);
            }
        }
    }

    /** Manages command line parsing for application. */
    private static class CommandLineArgs {

        /** Display command usage. */
        public static final String HELP = "--help";

        /** Key algorithm. */
        public static final String KEY_TYPE = "--type";
        
        /** Key size. */
        public static final String KEY_SIZE = "--size";

        /** Certificate lifetime. */
        public static final String CERT_LIFETIME = "--lifetime";

        /** Hostname for cert subject. */
        public static final String HOSTNAME = "--hostname";

        /** Path to private key file to create. */
        public static final String KEY_FILE = "--keyfile";

        /** Path to certificate file to create. */
        public static final String CERT_FILE = "--certfile";

        /** Type of keystore to create. */
        public static final String STORE_TYPE = "--storetype";
        
        /** Path to keystore to create.  */
        public static final String STORE_FILE = "--storefile";

        /** Keystore password.  */
        public static final String STORE_PASS = "--storepass";
        
        /** Display command usage. */
        @Parameter(names = HELP, description = "Display program usage", help = true)
        private boolean help;

        /** Key algorithm. */
        @Parameter(names = KEY_TYPE, description = "Type of key to generate (default: RSA)")
        private String keyType = "RSA";
        
        /** Key size. */
        @Parameter(names = KEY_SIZE, description = "Size of key to generate (default: 2048)")
        private int keySize = 2048;

        /** Certificate lifetime. */
        @Parameter(names = CERT_LIFETIME, description = "Certificate lifetime in years (default: 20)")
        private int certificateLifetime = 20;

        /** Hostname. */
        @Parameter(names = HOSTNAME, description = "Hostname for certificate subject")
        private String hostname;

        /** Private key file. */
        @Parameter(names = KEY_FILE, description = "Path to private key file")
        private String privateKeyFile;

        /** Certificate file. */
        @Parameter(names = CERT_FILE, description = "Path to certificate file")
        private String certificateFile;
        
        /** Keystore type. */
        @Parameter(names = STORE_TYPE, description = "Type of keystore to generate (default: PKCS12)")
        private String keystoreType = "PKCS12";
        
        /** Keystore file. */
        @Parameter(names = STORE_FILE, description = "Path to keystore")
        private String keystoreFile;
        
        /** Keystore password. */
        @Parameter(names = STORE_PASS, description = "Password for keystore")
        private String keystorePassword;
    }
    
}