package core.businessobject.pv.search;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import Siteview.Convert;
import Siteview.SiteviewException;

public class SecurityPWD {
	
	private static  SecretKey currentKey;
	
	static{
		try {
			currentKey = createKey();
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
	
	
	private byte[] encrypt(byte[] byteS) throws SiteviewException{
        try {
        	Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,currentKey);
            return cipher.doFinal(byteS);
        } catch (Exception e) {
        	throw new SiteviewException(e.getMessage(),e);
        }
	}
	
	private byte[] decrypt(byte[] byteD) throws Exception{
        try {
        	Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,currentKey);
            return cipher.doFinal(byteD);
        } catch (Exception e) {
        	throw e;
        }
    }
	
	private static SecretKey createKey() throws SiteviewException{
		try {
			KeyGenerator generator = KeyGenerator.getInstance("AES");
	        generator.init(new SecureRandom(new byte[]{23,15,-8,92,-39,-20,19,77}));
	        return generator.generateKey();
		} catch (Exception e) {
			throw new SiteviewException(e.getMessage(),e);
		}
	}
	
	public String encrypt2String(String str) throws SiteviewException{
		try {
			return Convert.ToBase64String(encrypt(str.getBytes()));
		} catch (Exception e) {
			throw new SiteviewException(e.getMessage(),e);
		}
	}
	
	public String decrypt2String(String str) throws SiteviewException{
		try {
			return new String(decrypt(Convert.FromBase64String(str)));
		} catch (Exception e) {
			throw new SiteviewException(e.getMessage(),e);
		}
	}
	
}
