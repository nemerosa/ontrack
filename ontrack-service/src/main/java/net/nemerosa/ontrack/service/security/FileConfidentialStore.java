package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.common.Utils;
import net.nemerosa.ontrack.model.support.EnvService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * Storing the keys as files in a directory.
 */
@Component
public class FileConfidentialStore implements ConfidentialStore {

    private static final String ENCODING = "UTF-8";

    private final SecureRandom sr = new SecureRandom();

    /**
     * Directory that stores individual keys.
     */
    private final File rootDir;

    /**
     * The master key.
     * <p>
     * The sole purpose of the master key is to encrypt individual keys on the disk.
     * Because leaking this master key compromises all the individual keys, we must not let
     * this master key used for any other purpose, hence the protected access.
     */
    private final SecretKey masterKey;

    @Autowired
    public FileConfidentialStore(EnvService envService) throws IOException, InterruptedException {
        this(envService.getWorkingDir("security", "secrets"));
    }

    @Override
    public String getId() {
        return "file";
    }

    public FileConfidentialStore(File rootDir) throws IOException, InterruptedException {
        this.rootDir = rootDir;

        File masterSecret = new File(rootDir, "master.key");
        if (!masterSecret.exists()) {
            // we are only going to use small number of bits (since export control limits AES key length)
            // but let's generate a long enough key anyway
            FileUtils.write(masterSecret, Utils.toHexString(randomBytes(128)), ENCODING);
        }
        this.masterKey = SecurityUtils.toAes128Key(FileUtils.readFileToString(masterSecret, ENCODING));
    }

    /**
     * Persists the payload of {@link ConfidentialKey} to the disk.
     */
    @Override
    public void store(ConfidentialKey key, byte[] payload) throws IOException {
        CipherOutputStream cos = null;
        FileOutputStream fos = null;
        try {
            Cipher sym = Cipher.getInstance("AES");
            sym.init(Cipher.ENCRYPT_MODE, masterKey);
            cos = new CipherOutputStream(fos = new FileOutputStream(getFileFor(key)), sym);
            cos.write(payload);
            cos.write(MAGIC);
        } catch (GeneralSecurityException e) {
            throw new IOException("Failed to persist the key: " + key.getId(), e);
        } finally {
            IOUtils.closeQuietly(cos);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * Reverse operation of {@link #store(ConfidentialKey, byte[])}
     *
     * @return null the data has not been previously persisted.
     */
    @Override
    public byte[] load(ConfidentialKey key) throws IOException {
        CipherInputStream cis = null;
        FileInputStream fis = null;
        try {
            File f = getFileFor(key);
            if (!f.exists()) return null;

            Cipher sym = Cipher.getInstance("AES");
            sym.init(Cipher.DECRYPT_MODE, masterKey);
            cis = new CipherInputStream(fis = new FileInputStream(f), sym);
            byte[] bytes = IOUtils.toByteArray(cis);
            return verifyMagic(bytes);
        } catch (GeneralSecurityException e) {
            throw new IOException("Failed to persist the key: " + key.getId(), e);
        } finally {
            IOUtils.closeQuietly(cis);
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * Verifies that the given byte[] has the MAGIC trailer, to verify the integrity of the decryption process.
     */
    private byte[] verifyMagic(byte[] payload) {
        int payloadLen = payload.length - MAGIC.length;
        if (payloadLen < 0) return null;    // obviously broken

        for (int i = 0; i < MAGIC.length; i++) {
            if (payload[payloadLen + i] != MAGIC[i])
                return null;    // broken
        }
        byte[] truncated = new byte[payloadLen];
        System.arraycopy(payload, 0, truncated, 0, truncated.length);
        return truncated;
    }

    private File getFileFor(ConfidentialKey key) {
        return new File(rootDir, key.getId());
    }

    public byte[] randomBytes(int size) {
        byte[] random = new byte[size];
        sr.nextBytes(random);
        return random;
    }

    private static final byte[] MAGIC = "::::MAGIC::::".getBytes();
}
