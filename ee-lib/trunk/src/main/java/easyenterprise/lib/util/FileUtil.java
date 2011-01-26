package easyenterprise.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

	public static void copy(InputStream is, OutputStream os) throws IOException {
		byte data[] = new byte[2048];
		try {
				int count;
				while ((count = is.read(data)) != -1) {
					os.write(data, 0, count);
				}
				os.flush();
		} finally {
			is.close();
			os.close();
		}
	}
	
	public static File createTempDir(String prefix, String suffix) throws IOException {
		File tempFile = File.createTempFile(prefix, suffix);
		tempFile.delete();
		tempFile.mkdir();
		tempFile.deleteOnExit();
		return tempFile;
	}
}
