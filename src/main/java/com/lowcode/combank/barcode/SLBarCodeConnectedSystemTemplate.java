package com.lowcode.combank.barcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.simplified.sdk.connectiontesting.SimpleTestableConnectedSystemTemplate;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.connectiontesting.TestConnectionResult;
import com.dynamsoft.dbr.BarcodeReader;

@TemplateId(name = "SLBarCodeConnectedSystemTemplate")
public class SLBarCodeConnectedSystemTemplate extends SimpleTestableConnectedSystemTemplate {

	public static final String LICENSE_KEY = "LicenseKey";
	
	private static Logger logger = LoggerFactory.getLogger(SLBarCodeConnectedSystemTemplate.class);

	@Override
	protected SimpleConfiguration getConfiguration(SimpleConfiguration simpleConfiguration,
			ExecutionContext executionContext) {

		return simpleConfiguration.setProperties(textProperty(LICENSE_KEY).label("License Key").isExpressionable(false)
				.isRequired(true).instructionText("License Key").build());

	}

	@Override
	protected TestConnectionResult testConnection(SimpleConfiguration simpleConfiguration, ExecutionContext arg1) {
		String key = simpleConfiguration.getValue(LICENSE_KEY);
		try {
			logger.info( "Before license  key initializing");
			BarcodeReader.initLicense(key);
			logger.info( "License  successfully initialized");
			BarcodeReader dbr = BarcodeReader.getInstance();
			if (dbr == null) {
				logger.error("Fail to get BarCodeReader ");
				return TestConnectionResult.error("Fail to get BarCodeReader");
			}
			dbr.recycle();
		} catch (Exception e) {
			logger.info( "BarCodeReader Failed " , e);
			return TestConnectionResult.error("Fail to get BarCodeReader");
		}
		return TestConnectionResult.success();
	}

}
