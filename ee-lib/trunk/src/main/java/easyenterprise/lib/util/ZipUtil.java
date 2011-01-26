package easyenterprise.lib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	public static void unzip(InputStream is, File destination) throws IOException {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		byte data[] = new byte[2048];
		try {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				int count;
				// write the files to the disk
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(entry.getName()));
				while ((count = zis.read(data)) != -1) {
					os.write(data, 0, count);
				}
				os.flush();
				os.close();
			}
		} finally {
			zis.close();
		}
	}
}
