package com.example.jaxws.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.example.jaxws.service.Exception_Exception;
import com.example.jaxws.service.FileNotFound_Exception;

public class FileTransferClient extends Thread {

	private static Log log = LogFactory.getLog(FileTransferClient.class);

	private static String mode = "upload";

	private static int numThreads = 1;

	private static int numIterations = 1;

	private static int interval = 10;

	/**
	 * File name to upload existing on the local file system.<br/>
	 */
	private static String uploadFileName = "UploadData.txt";

	/**
	 * File name to download existing on the remote server.<br/>
	 * (without path)
	 */
	private static String downloadFileName = "DownloadData.txt";

	/**
	 * Local directory name that stores the downloaded file.
	 */
	private static String downloadDir = "DownloadedFiles";

	public FileTransferClient() {
	}

	public static void loadConfig() {
		Properties conf = null;
		try {
			conf = new Properties();
			InputStream is = new FileInputStream(new File("conf/client.properties"));
			conf.load(is);
			is.close();

			String key = null;
			String param = null;
			int tmpInt = 0;

			try {

				key = "client.mode";
				param = conf.getProperty(key);
				if (param.equals("upload") || param.equals("download")) {
					mode = param;
				} else {
					log.error(String.format("Invalid mode. [%s=%s]", key, mode));
					System.exit(1);
				}

				key = "client.threads";
				param = conf.getProperty(key);
				tmpInt = Integer.valueOf(param).intValue();
				if (tmpInt <= 0) {
					log.warn(String.format("Invalid number [%s=%s], It was set to default.", key, param));
				} else {
					numThreads = tmpInt;
				}

				key = "client.iterations";
				param = conf.getProperty(key);
				tmpInt = Integer.valueOf(param).intValue();
				if (tmpInt <= 0) {
					log.warn(String.format("Invalid number [%s=%s], It was set to default.", key, param));
				} else {
					numIterations = tmpInt;
				}

				key = "client.interval";
				param = conf.getProperty(key);
				tmpInt = Integer.valueOf(param).intValue();
				if (tmpInt <= 0) {
					log.warn(String.format("Invalid number [%s=%s], It was set to default.", key, param));
				} else {
					interval = tmpInt;
				}

				if (mode.equals("upload")) {
					// File to upload.
					key = "upload.filename";
					uploadFileName = conf.getProperty(key);
					File f = new File(uploadFileName);
					if (!f.exists()) {
						log.error(String.format("File not found [%s=%s].", key, uploadFileName));
						System.exit(1);
					}
					if (!f.isFile()) {
						log.error(String.format("Invalid file name [%s=%s].", key, uploadFileName));
						System.exit(1);
					}
				} else if (mode.equals("download")){
					// File to download
					key = "download.filename";
					downloadFileName = conf.getProperty(key);

					key = "download.dirName";
					downloadDir = conf.getProperty(key);
					File dir = new File(downloadDir);
					if (!dir.exists()) {
						dir.mkdirs();
					} else if (!dir.isDirectory()) {
						log.error(String.format("%s is already exists. But it is not a directory. [%s=%s]", downloadDir, key, downloadDir));
						System.exit(1);
					}
				}

			} catch (NumberFormatException e) {
				log.error(String.format("Illegal parameter. [%s=%s]", key, param), e);
				System.exit(1);
			}

		} catch (IOException e) {
			log.warn("conf/client.properties was not found. All parameters set to default.", e);
		}

	}

	public static void main(String args[]) {

		loadConfig();

		log.info(String.format("Number of threads    : %d\n", numThreads));
		log.info(String.format("Number of iterations : %d\n", numIterations));
		log.info(String.format("Interval time (ms)   : %d\n", interval));

		FileTransferClient[] threads = new FileTransferClient[numThreads];
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new FileTransferClient();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	public void run() {
		if (numIterations <= 1) {
			try {
				doIteration(1);
			} catch (Exception_Exception e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			} catch (FileNotFound_Exception e) {
				log.error(e);
			}
		} else {
			for (int i = 1; i <= numIterations; i++) {
				try {
					doIteration(i);
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					log.error(e);
					break;
				} catch (Exception_Exception e) {
					log.error(e);
					break;
				} catch (IOException e) {
					log.error(e);
					break;
				} catch (FileNotFound_Exception e) {
					log.error(e);
					break;
				}
			}
		}
	}

	private void doIteration(int i) throws Exception_Exception, IOException, FileNotFound_Exception {
		
		if (mode.equals("upload")) {
			String uploadRemoteFileName = makeFileName(i);
			new Uploader(uploadFileName, uploadRemoteFileName).upload();
		} else if (mode.equals("download")) {
			String downloadLocalFileName = makeFileName(i);
			Path p = Paths.get(downloadDir, downloadLocalFileName);
			new Downloader(downloadFileName, p.toString()).download();
		}
		
	}

	public String makeFileName(int iterationNumber) {
		String name = String.format("%03d-%03d.%s", this.getId(), iterationNumber, uploadFileName);
		return name;
	}
}
