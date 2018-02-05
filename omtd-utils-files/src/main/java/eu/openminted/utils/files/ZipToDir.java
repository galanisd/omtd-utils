package eu.openminted.utils.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.utils.IOUtils;

/**
 * @author galanisd
 *
 */
public class ZipToDir {

	public static void unpackToWorkDir(File archiveFile, File toDir) throws IOException {
		java.util.zip.ZipFile zipFile = null;
		try {
			zipFile = new java.util.zip.ZipFile(archiveFile);
//            Enumeration<ZipArchiveEntry> zipEntries = zipFile.getEntriesInPhysicalOrder();
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				String entryName = entry.getName();
				File outFile = new File(toDir, entryName);
				if (!outFile.getParentFile().exists()) {
					if (!outFile.getParentFile().mkdirs()) {
						throw new IOException(
								"Failed to create parent directory: " + outFile.getParentFile().getCanonicalPath());
					}
				}
				if (entry.isDirectory()) {
					if (!outFile.mkdir()) {
						throw new IOException("Failed to create directory: " + outFile.getCanonicalPath());
					}
				} else {
					InputStream zipStream = zipFile.getInputStream(entry);
					OutputStream outFileStream = new FileOutputStream(outFile);

					try {
						IOUtils.copy(zipStream, outFileStream);
					} finally {
						IOUtils.closeQuietly(zipStream);
						IOUtils.closeQuietly(outFileStream);
					}
				}
			}
		} finally {
//            ZipFile.closeQuietly(zipFile);
			zipFile.close();
		}
	}
}
