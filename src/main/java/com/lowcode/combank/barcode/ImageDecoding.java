package com.lowcode.combank.barcode;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dynamsoft.dbr.*;

/*
Get Barcode extraction
*/

public class ImageDecoding {
	
	private static Logger logger = LoggerFactory.getLogger(ImageDecoding.class);
		
	public static String[] extract(InputStream stream,String licenseKey) {
		
		if( stream == null)
			logger.info( "Docuemnt  stream is  null ");

		TextResult[] results = null;
		String[] barCodeData = null;
		try {

			BarcodeReader.initLicense(licenseKey);
			BarcodeReader dbr = BarcodeReader.getInstance();
			if (dbr == null) {
				logger.error("Get BarCode Instance Failed" );
				throw new Exception("Get BarCode Instance Failed.");
			}

			results = dbr.decodeFileInMemory(stream, "");

			if (results != null && results.length > 0) {
				for (int i = 0; i < results.length; i++) {
					TextResult result = results[i];
					String s = result.barcodeText;
					barCodeData = s.split("\n");
					break;
				}
			} 

			dbr.recycle();
		} catch (BarcodeReaderException ex) {
			logger.error ( "BarcodeReaderException " , ex);
			
		} catch (IOException ex) {
			logger.error ( "IOException " , ex);
		} catch (Exception ex) {
			logger.error ( "Exception " , ex);
		}

		return barCodeData;
	}
}
