package cn.ffcs.wisdom.city.simico.kit.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static interface CopyListener {
		public abstract void onCopy(int readLen, long copyLen);
	}

	public IOUtils() {
	}

	public static void closeQuietly(InputStream is) {
		try {
			if (is != null)
				is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeQuietly(RandomAccessFile raf) {
		try {
			if (raf != null)
				raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeQuietly(OutputStream os) {
		try {
			if (os != null)
				os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeQuietly(Reader reader) {
		try {
			if (reader != null)
				reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeQuietly(Writer writer) {
		try {
			if (writer != null)
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将输入流数据拷贝到输出流中
	 * @param is
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream is, OutputStream os) throws IOException {
		byte buf[] = new byte[DEFAULT_BUFFER_SIZE];
		long l = 0L;
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
			l += len;
		}
		return l;
	}

	/**
	 * 将输入流数据拷贝到输出流中
	 * @param is
	 * @param os
	 * @param listener
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream is, OutputStream os,
			CopyListener listener) throws IOException {
		byte buf[] = new byte[DEFAULT_BUFFER_SIZE];
		long copyLen = 0;
		int readLen = -1;
		while ((readLen = is.read(buf)) != -1) {
			os.write(buf, 0, readLen);
			copyLen += readLen;
			if (listener != null)
				listener.onCopy(readLen, copyLen);
		}
		return copyLen;
	}

	public static long copy(Reader reader, Writer writer) throws IOException {
		char buf[] = new char[DEFAULT_BUFFER_SIZE];
		long l = 0L;
		int readLen = -1;
		while ((readLen = reader.read(buf)) != -1) {
			writer.write(buf, 0, readLen);
			l += readLen;
		}
		return l;
	}

	public static void copy(InputStream is, Writer writer) throws IOException {
		copy(new InputStreamReader(is), writer);
	}

	public static void copy(InputStream is, Writer writer, String format)
			throws IOException {
		if (format == null)
			copy(is, writer);
		else
			copy(new InputStreamReader(is, format), writer);
	}

	public static void copy(Reader reader, OutputStream os)
			throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		copy(reader, osw);
		os.flush();
	}

	public static List<String> readLines(InputStream is) throws IOException {
		return readLines(new InputStreamReader(is));
	}

	public static List<String> readLines(InputStream is, String s)
			throws IOException {
		List<String> list;
		if (s == null)
			list = readLines(is);
		else
			list = readLines(new InputStreamReader(is, s));
		return list;
	}

	public static List<String> readLines(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		List<String> lines = new ArrayList<String>();
		String tmp = null;
		while ((tmp = br.readLine()) != null) {
			lines.add(tmp);
		}
		return lines;
	}

	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy(is, bos);
		return bos.toByteArray();
	}

	public static String toString(InputStream is, String format)
			throws IOException {
		StringWriter sw = new StringWriter();
		copy(is, sw, format);
		return sw.toString();
	}

	public static void write(String s, OutputStream os)
			throws IOException {
		if (s != null)
			os.write(s.getBytes());
	}

	public static void write(String content, OutputStream os, String charsetName)
			throws IOException {
		if (content != null) {
			if (charsetName == null)
				write(content, os);
			else
				os.write(content.getBytes(charsetName));
		}
	}
}
