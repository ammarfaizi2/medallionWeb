package helpers;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class Cryptography {
	private static Logger log = Logger.getLogger(Cryptography.class);

	private static final String ENCRYPTION_ALGORITHM = "RC4";
	private static final String HASH_ALGORITHM = "MD5";

	public static byte[] hash(byte[] data) {
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			return digest.digest();
		} catch (Exception e) {
			log.error("Error when hashing", e);
			return null;
		}
	}

	public static byte[] encrypt(byte[] data, String key) {
		return process(Cipher.ENCRYPT_MODE, key, data);
	}

	public static byte[] decrypt(byte[] data, String key) {
		return process(Cipher.DECRYPT_MODE, key, data);
	}

	private static byte[] process(int opmode, String key, byte[] data) {
		try {
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ENCRYPTION_ALGORITHM);
			cipher.init(opmode, keySpec);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("Error when processing encryption", e);
			return null;
		}
	}

}
