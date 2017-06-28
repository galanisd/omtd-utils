package eu.openminted.utils.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;


import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

/**
 * @author galanisd
 *
 */
public class ZipToDir {

	public static void unpackToWorkDir(File archiveFile, File toDir) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(archiveFile);
			Enumeration<ZipArchiveEntry> zipEntries = zipFile.getEntriesInPhysicalOrder();
			while (zipEntries.hasMoreElements()) {
				ZipArchiveEntry entry = zipEntries.nextElement();
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
					InputStream zipStream = null;
					OutputStream outFileStream = null;
					zipStream = zipFile.getInputStream(entry);
					outFileStream = new FileOutputStream(outFile);
					try {
						IOUtils.copy(zipStream, outFileStream);
					} finally {
						IOUtils.closeQuietly(zipStream);
						IOUtils.closeQuietly(outFileStream);
					}
				}
			}
		} finally {
			ZipFile.closeQuietly(zipFile);
		}
	}
}
