package com.indigo.esb.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * ���� ���� ��ƿ $Id: ZipUtils.java,v 1.2 2012/02/14 00:21:00 seo0859 Exp $
 * 
 */
public class ZipUtils {
	/**
	 * ����
	 * 
	 * @param inFile
	 *            ������ ����
	 * @param zipFile
	 *            ���� ��� ����
	 * @throws Exception
	 */
	public static void compress(File inFile, File zipFile) throws Exception {
		//compress(inFile, FilenameUtils.getPath(inFile.getPath()), zipFile);
		compress(inFile, FilenameUtils.getFullPath(inFile.getPath()), zipFile);
	}

	/**
	 * ����
	 * 
	 * @param inFile
	 *            ������ ����
	 * @param inPath
	 *            ������ ����Ʈ�� �⺻ ���
	 * @param zipFile
	 *            ���� ��� ����
	 * @throws Exception
	 */
	public static void compress(File inFile, String inPath, File zipFile) throws Exception {
		compress(new File[] { inFile }, inPath, zipFile);
	}

	/**
	 * ����
	 * 
	 * @param inFiles
	 *            ������ ���� ����Ʈ
	 * @param inPath
	 *            ������ ����Ʈ�� �⺻ ���
	 * @param zipFile
	 *            ���� ��� ����
	 * @throws Exception
	 */
	public static void compress(File[] inFiles, String inPath, File zipFile) throws Exception {
		OutputStream output = null;
		ArchiveOutputStream zipOutput = null;
		try {
			if (zipFile == null)
				throw new FileNotFoundException("ZipFile cannot be configured properly : " + zipFile);
			FileUtils.forceMkdir(zipFile.getParentFile());
			output = new FileOutputStream(zipFile);
			zipOutput = new ArchiveStreamFactory().createArchiveOutputStream("zip", output);
			for (int i = 0; i < inFiles.length; i++) {
				zipEntry(inFiles[i], inPath, zipOutput);
			}
		} finally {
			if (zipOutput != null) {
				try {
					zipOutput.close();
				} catch (Exception ignored) {
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	/**
	 * 
	 * @param inFile
	 * @param inPath
	 * @param zipOutput
	 * @throws Exception
	 */
	private static void zipEntry(File inFile, String inPath, ArchiveOutputStream zipOutput) throws Exception {
		FileInputStream fis = null;
		try {
			if (inFile.isDirectory()) {
				File[] fileArray = inFile.listFiles();
				for (int i = 0; i < fileArray.length; i++) {
					zipEntry(fileArray[i], inPath, zipOutput);
				}
			} else {
				String inFilePath = FilenameUtils.separatorsToUnix(inFile.getPath());
				String zipEntryName = inFilePath.substring(
						inPath.endsWith("/") ? inPath.length() + 1 : inPath.length(), inFilePath.length());

				ZipArchiveEntry zentry = new ZipArchiveEntry(zipEntryName);
				zentry.setTime(inFile.lastModified());
				zipOutput.putArchiveEntry(zentry);
				fis = new FileInputStream(inFile);
				IOUtils.copy(fis, zipOutput);
				zipOutput.closeArchiveEntry();
			}
		} finally {
			fis.close();
		}
	}

	/**
	 * ���� Ǯ��
	 * 
	 * @param outDir
	 *            ���� ������ ������ ������ ���
	 * @param zipFile
	 *            ZIP ������ �����ϴ� ���
	 * @return ���� ������ ������ ����� List<String>
	 * @throws Exception
	 */
	public static List<File> decompress(File outDir, File zipFile) throws Exception {
		InputStream inputStream = null;
		ArchiveInputStream zipInputStream = null;
		List<File> fileList = null;
		try {
			inputStream = new FileInputStream(zipFile);
			zipInputStream = new ArchiveStreamFactory().createArchiveInputStream("zip", inputStream);
			ZipArchiveEntry entry = null;
			fileList = new ArrayList<File>();
			while ((entry = (ZipArchiveEntry) zipInputStream.getNextEntry()) != null) {
				File outFile = new File(outDir, entry.getName());
				if (entry.isDirectory()) {
					FileUtils.forceMkdir(outFile);
				} else {
					FileUtils.forceMkdir(outFile.getParentFile());
					OutputStream outputStream = new FileOutputStream(outFile);
					try {
						IOUtils.copy(zipInputStream, outputStream);
					} finally {
						outputStream.close();
					}
				}
				fileList.add(outFile);
			}
			return fileList;
		} finally {
			if (zipInputStream != null) {
				try {
					zipInputStream.close();
				} catch (Exception ignored) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception ignored) {
				}
			}
		}
	}
}
