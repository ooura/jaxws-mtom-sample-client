package com.example.jaxws.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.example.jaxws.service.Exception_Exception;
import com.example.jaxws.service.FileNotFound_Exception;
import com.example.jaxws.service.FileTransferer;
import com.example.jaxws.service.FileTransfererImplService;
import com.sun.xml.ws.developer.JAXWSProperties;

public class Downloader {

	private static Log log = LogFactory.getLog(Downloader.class);
	
	private String remoteFileName;

	private String localFileName;

	public Downloader(String remoteFileName, String localFileName) {
		this.remoteFileName = remoteFileName;
		this.localFileName = localFileName;
	}

	public void download() throws Exception_Exception, FileNotFound_Exception, FileNotFoundException {

		FileTransferer fileTransferer = new FileTransfererImplService().getFileTransfererImplPort(new MTOMFeature());
		Map<String, Object> ctxt = ((BindingProvider) fileTransferer).getRequestContext();
		ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
		byte[] data = fileTransferer.download(remoteFileName);

		FileOutputStream out = new FileOutputStream(localFileName);
		try {
			out.write(data);
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}
