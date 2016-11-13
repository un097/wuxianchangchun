package cn.ffcs.wisdom.city.simico.kit.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static final char EXTENSION_SEPARATOR = 46;
	private static final long SIZE_GB = 0x40000000;
	private static final long SIZE_KB = 1024;
	private static final long SIZE_MB = 0x100000;
	private static final char UNIX_SEPARATOR = 47;
	private static final char WINDOWS_SEPARATOR = 92;

	public FileUtils() {
	}

	/**
	 * 清空文件夹内容
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void cleanDirectory(File file) throws IOException {
		if (!file.exists())
			throw new IllegalArgumentException(file + " does not exist");
		if (!file.isDirectory())
			throw new IllegalArgumentException(file + " is not a directory");
		File[] contentFiles = file.listFiles();
		if (contentFiles == null)
			throw new IOException("Failed to list contents of " + file);
		int i = 0;
		while (i < contentFiles.length) {
			forceDelete(contentFiles[i]);
			i++;
		}
	}

	/**
	 * 拷贝文件夹
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination)
			throws IOException {
		copyDirectory(source, destination, true);
	}

	/**
	 * 拷贝文件夹
	 * 
	 * @param source
	 * @param destination
	 * @param flag
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination, boolean flag)
			throws IOException {
		copyDirectory(source, destination, null, flag);
	}

	/**
	 * 拷贝文件夹
	 * 
	 * @param source
	 * @param destination
	 * @param filefilter
	 * @param flag
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination,
			FileFilter filefilter, boolean flag) throws IOException {
		if (source == null)
			throw new NullPointerException("Source must not be null");
		if (destination == null)
			throw new NullPointerException("Destination must not be null");
		if (!source.exists())
			throw new FileNotFoundException("Source '" + source
					+ "' does not exist");
		if (!source.isDirectory())
			throw new IOException("Source '" + source
					+ "' exists but is not a directory");
		if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
			throw new IOException("Source '" + source + "' and destination '"
					+ destination + "' are the same");
		List<String> pathList = null;
		if (destination.getCanonicalPath()
				.startsWith(source.getCanonicalPath())) {
			File[] sourceFiles;
			if (filefilter == null) {
				sourceFiles = source.listFiles();
			} else {
				sourceFiles = source.listFiles(filefilter);
			}
			if (sourceFiles != null && sourceFiles.length > 0) {
				pathList = new ArrayList<String>(sourceFiles.length);
				for (int i = 0; i < sourceFiles.length; i++) {
					pathList.add(new File(destination, sourceFiles[i].getName())
							.getCanonicalPath());
				}
			}
		}
		doCopyDirectory(source, destination, filefilter, flag, pathList);
	}

	/**
	 * 拷贝文件
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination)
			throws IOException {
		copyFile(source, destination, true);
	}

	public static void copyFile(File source, File destination, boolean flag)
			throws IOException {
		if (source == null)
			throw new NullPointerException("Source must not be null");
		if (destination == null)
			throw new NullPointerException("Destination must not be null");
		if (!source.exists())
			throw new FileNotFoundException("Source '" + source
					+ "' does not exist");
		if (source.isDirectory())
			throw new IOException("Source '" + source
					+ "' exists but is a directory");
		if (!source.getCanonicalPath().equals(destination.getCanonicalPath())) {
			if (destination.getParentFile() != null
					&& !destination.getParentFile().exists()
					&& !destination.getParentFile().mkdirs())
				throw new IOException("Destination '" + destination
						+ "' directory cannot be created");
			if (destination.exists() && !destination.canWrite())
				throw new IOException("Destination '" + destination
						+ "' exists but is read-only");
			doCopyFile(source, destination, flag);
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void deleteDirectory(File file) throws IOException {
		if (file.exists()) {
			cleanDirectory(file);
			if (!file.delete())
				throw new IOException("Unable to delete directory " + file
						+ ".");
		}
	}

	/**
	 * 删除文件 文件夹
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteQuietly(File file) {
		if (file != null) {
			try {
				if (file.isDirectory()) {
					cleanDirectory(file);
					return true;
				} else
					return file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 拷贝文件夹
	 * 
	 * @param source
	 * @param destination
	 * @param filefilter
	 * @param flag
	 * @param list
	 * @throws IOException
	 */
	private static void doCopyDirectory(File source, File destination,
			FileFilter filefilter, boolean flag, List<String> list)
			throws IOException {
		if (destination.exists()) {
			if (!destination.isDirectory())
				throw new IOException("Destination '" + destination
						+ "' exists but is not a directory");
		} else {
			if (!destination.mkdirs())
				throw new IOException("Destination '" + destination
						+ "' directory cannot be created");
			if (flag) {
				destination.setLastModified(source.lastModified());
			}
		}
		if (!destination.canWrite()) {
			throw new IOException("Destination '" + destination
					+ "' cannot be written to");
		}
		File[] files;
		if (filefilter == null)
			files = source.listFiles();
		else
			files = source.listFiles(filefilter);
		if (files == null) {
			throw new IOException("Failed to list contents of " + source);
		}
		int i = 0;
		while (i < files.length) {
			File desFile = new File(destination, files[i].getName());
			if (list == null || !list.contains(files[i].getCanonicalPath()))
				if (files[i].isDirectory())
					doCopyDirectory(files[i], desFile, filefilter, flag, list);
				else
					doCopyFile(files[i], desFile, flag);
			i++;
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param source
	 * @param destination
	 * @param flag
	 * @throws IOException
	 */
	private static void doCopyFile(File source, File destination, boolean flag)
			throws IOException {
		if (destination.exists() && destination.isDirectory())
			throw new IOException("Destination '" + destination
					+ "' exists but is a directory");
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(destination);
		IOUtils.copy(fis, fos);
		IOUtils.closeQuietly(fos);
		IOUtils.closeQuietly(fis);
		if (source.length() != destination.length())
			throw new IOException("Failed to copy full contents from '"
					+ source + "' to '" + destination + "'");
		if (flag)
			destination.setLastModified(source.lastModified());
	}

	/**
	 * 文件大小并格式化返回
	 * 
	 * @param fileSize
	 * @return
	 */
	public static String fileSize(long fileSize) {
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		String sizeStr;
		if (fileSize <= 0) {
			sizeStr = "0 MB";
		} else if (fileSize <= SIZE_KB) {
			sizeStr = fileSize + " Bytes";
		} else if (fileSize <= SIZE_MB) {
			sizeStr = format.format(fileSize / 1024d) + " KB";
		} else if (fileSize <= SIZE_GB) {
			sizeStr = format.format(fileSize / 1048576d) + " MB";
		} else {
			sizeStr = format.format(fileSize / 1073741824d) + " GB";
		}
		return sizeStr;
	}

	public static String fileSize2(long fileSize) {
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		String sizeStr;
		if (fileSize <= 0) {
			sizeStr = "0 MB";
		} else if (fileSize <= SIZE_GB) {
			sizeStr = format.format(fileSize / 1048576d) + " M";
		} else {
			sizeStr = format.format(fileSize / 1073741824d) + " G";
		}
		return sizeStr;
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (file.exists()) {
				if (!file.delete())
					throw new IOException("Unable to delete file: " + file);
			} else {
				throw new FileNotFoundException("File does not exist: " + file);
			}
		}
	}

	/**
	 * 获取文件的扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		String extension = null;
		if (fileName != null) {
			int idx = indexOfExtension(fileName);
			if (idx == -1)
				extension = "";
			else
				extension = fileName.substring(idx + 1);
		}
		return extension;
	}

	/**
	 * 从下载地址中获取文件名称
	 * 
	 * @param url
	 * @return
	 */
	public static String getName(String url) {
		String name = null;
		if (url != null)
			name = url.substring(1 + indexOfLastSeparator(url));
		return name;
	}

	/**
	 * 获取扩展名的位置
	 * 
	 * @param extension
	 * @return
	 */
	public static int indexOfExtension(String extension) {
		int location = -1;
		if (extension != null) {
			int idx = extension.lastIndexOf('.');
			if (indexOfLastSeparator(extension) > idx)
				location = -1;
			else
				location = idx;
		}
		return location;
	}

	/**
	 * 最后一个文件分隔符的位置
	 * 
	 * @param str
	 * @return
	 */
	public static int indexOfLastSeparator(String str) {
		int idx = -1;
		if (str != null)
			idx = Math.max(str.lastIndexOf(UNIX_SEPARATOR),
					str.lastIndexOf(WINDOWS_SEPARATOR));
		return idx;
	}

	/**
	 * 移动文件
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void moveFile(File source, File destination)
			throws IOException {
		if (source == null)
			throw new NullPointerException("Source must not be null");
		if (destination == null)
			throw new NullPointerException("Destination must not be null");
		if (!source.exists())
			throw new FileNotFoundException("Source '" + source
					+ "' does not exist");
		if (source.isDirectory())
			throw new IOException("Source '" + source + "' is a directory");
		if (destination.exists())
			throw new IOException("Destination '" + destination
					+ "' already exists");
		if (destination.isDirectory())
			throw new IOException("Destination '" + destination
					+ "' is a directory");
		if (!source.renameTo(destination)) {
			copyFile(source, destination);
			if (!source.delete()) {
				deleteQuietly(destination);
				throw new IOException("Failed to delete original file '"
						+ source + "' after copy to '" + destination + ".");
			}
		}
	}

	/**
	 * 打开一个文件输入流
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory())
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			if (!file.canRead())
				throw new IOException("File '" + file + "' cannot be read");
			else
				return new FileInputStream(file);
		} else {
			throw new FileNotFoundException("File '" + file
					+ "' does not exist");
		}
	}

	public static FileOutputStream openOutputStream(File file)
			throws IOException {
		if (file.exists()) {
			if (file.isDirectory())
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			if (!file.canWrite())
				throw new IOException("File '" + file
						+ "' cannot be written to");
		} else {
			File parent = file.getParentFile();
			if (parent != null && !parent.exists() && !parent.mkdirs())
				throw new IOException("File '" + file
						+ "' could not be created");
		}
		return new FileOutputStream(file);
	}

	/**
	 * 将文件转成字节数组
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileToByteArray(File file) throws IOException {
		FileInputStream fis = openInputStream(file);
		byte[] bytes = IOUtils.toByteArray(fis);
		IOUtils.closeQuietly(fis);
		return bytes;
	}

	/**
	 * 将文件转成字符内容,从文件读内容
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFileToString(File file) throws IOException {
		return readFileToString(file, null);
	}

	/**
	 * 从文件中读取内容
	 * 
	 * @param file
	 * @param format
	 * @return
	 * @throws IOException
	 */
	public static String readFileToString(File file, String format) {
		String content = null;
		FileInputStream fos = null;
		try {
			fos = openInputStream(file);
			content = IOUtils.toString(fos, format);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fos);
		}
		return content;
	}

	/**
	 * 读文件并将每行用集合方式返回
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<String> readLines(File file) throws IOException {
		return readLines(file, null);
	}

	/**
	 * 读文件并将每行用集合方式返回
	 * 
	 * @param file
	 * @param enc
	 *            编码
	 * @return
	 * @throws IOException
	 */
	public static List<String> readLines(File file, String enc)
			throws IOException {
		FileInputStream fis = openInputStream(file);
		List<String> list = IOUtils.readLines(fis, enc);
		IOUtils.closeQuietly(fis);
		return list;
	}

	/**
	 * 将字节数据写入文件
	 * 
	 * @param file
	 * @param datas
	 * @throws IOException
	 */
	public static void writeByteArrayToFile(File file, byte[] datas)
			throws IOException {
		FileOutputStream fos = openOutputStream(file);
		fos.write(datas);
		IOUtils.closeQuietly(fos);
	}

	public static void saveFile(File file, InputStream is) throws IOException {
		FileOutputStream fout = null;
		try {
			fout = openOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) != -1) {
				fout.write(buf, 0, len);
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 将字符串写入文件
	 * 
	 * @param file
	 * @param content
	 * @param charsetName
	 */
	public static void writeStringToFile(File file, String content,
			String charsetName) {
		FileOutputStream fos = null;
		try {
			fos = openOutputStream(file);
			IOUtils.write(content, fos, charsetName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	public static void renameFiles(File dir) {
		if (dir == null || dir.isDirectory() || !dir.exists()) {
			return;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			String newName = file.getName().replace(".jpg", ".j")
					.replace(".png", ".p");
			file.renameTo(new File(file.getPath(), newName));
		}
	}

	public static boolean isExist(String fileName) {
		return new File(fileName).exists();
	}
}
