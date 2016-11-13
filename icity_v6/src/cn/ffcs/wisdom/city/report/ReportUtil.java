package cn.ffcs.wisdom.city.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cn.ffcs.wisdom.tools.StreamUtil;

public class ReportUtil {

	public synchronized static void saveTxt(String mail, File mailFile, boolean add) {

		if (mailFile == null) {
			return;
		}

		BufferedReader reader = null;
		FileInputStream in = null;
		BufferedWriter writer = null;
		FileOutputStream out = null;
		InputStreamReader inReader = null;
		OutputStreamWriter outWriter = null;
		StringBuffer sb = new StringBuffer();

		synchronized (sb) {

			try {
				if (!mailFile.exists()) {
					mailFile.createNewFile();
				}
				in = new FileInputStream(mailFile);

				if (add) {
					inReader = new InputStreamReader(in);
					reader = new BufferedReader(inReader);

					String str = "";
					while ((str = reader.readLine()) != null) {
						sb.append(str).append("\r\n");
					}
				}

				out = new FileOutputStream(mailFile);
				outWriter = new OutputStreamWriter(out);
				writer = new BufferedWriter(outWriter);

				sb.append(mail).append("\r\n");
				writer.write(sb.toString());
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				StreamUtil.closeSilently(reader);
				StreamUtil.closeSilently(in);
				StreamUtil.closeSilently(writer);
				StreamUtil.closeSilently(out);
				StreamUtil.closeSilently(inReader);
				StreamUtil.closeSilently(outWriter);
			}
		}
	}
}
