package com.example.jaxws.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.example.jaxws.service.Exception_Exception;
import com.example.jaxws.service.FileTransferer;
import com.example.jaxws.service.FileTransfererImplService;
import com.sun.xml.ws.developer.JAXWSProperties;

public class Uploader {

	private static Log log = LogFactory.getLog(Uploader.class);

	File localFile;

	String remoteFileName;

	public Uploader(String localFile, String remoteFile) {
		this.localFile = new File(localFile);
		this.remoteFileName = remoteFile;
	}

	public void upload() throws Exception_Exception, IOException {

		log.debug(String.format("%s : Start.", remoteFileName));
		
		int fileLength = (int) localFile.length();
		byte data[] = new byte[fileLength];
		FileInputStream fis;
		fis = new FileInputStream(localFile);
		fis.read(data);
		fis.close();

		FileTransferer fileTransferer = new FileTransfererImplService().getFileTransfererImplPort(new MTOMFeature());
		Map<String, Object> ctxt = ((BindingProvider) fileTransferer).getRequestContext();
		ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
		fileTransferer.upload(data, remoteFileName);
		
		log.debug(String.format("%s : End.", remoteFileName));
	}
}
