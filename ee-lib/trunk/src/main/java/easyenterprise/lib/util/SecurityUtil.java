package easyenterprise.lib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
	
	public static final String computeSHA1(byte[] data) {
		return computeSHA1(data, 0, data.length);
	}
	
	public static final String computeSHA1(byte[] data, int offset, int length) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(data, offset, length);
			byte[] sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
  public static String convertToHex(byte[] data) { 
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < data.length; i++) { 
        int halfbyte = (data[i] >>> 4) & 0x0F;
        int two_halfs = 0;
        do { 
            if ((0 <= halfbyte) && (halfbyte <= 9)) 
                buf.append((char) ('0' + halfbyte));
            else 
                buf.append((char) ('a' + (halfbyte - 10)));
            halfbyte = data[i] & 0x0F;
        } while(two_halfs++ < 1);
    } 
    return buf.toString();
} 

}
