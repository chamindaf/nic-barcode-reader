package com.lowcode.combank.barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appian.connectedsystems.simplified.sdk.SimpleIntegrationTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.IntegrationError;
import com.appian.connectedsystems.templateframework.sdk.IntegrationResponse;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appian.connectedsystems.templateframework.sdk.configuration.Document;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyPath;
import com.appian.connectedsystems.templateframework.sdk.configuration.PropertyState;
import com.appian.connectedsystems.templateframework.sdk.configuration.SystemType;
import com.appian.connectedsystems.templateframework.sdk.diagnostics.IntegrationDesignerDiagnostic;
import com.appian.connectedsystems.templateframework.sdk.metadata.IntegrationTemplateRequestPolicy;
import com.appian.connectedsystems.templateframework.sdk.metadata.IntegrationTemplateType;

@TemplateId(name = "SLBarCodeIntegrationTemplate")
@IntegrationTemplateType(IntegrationTemplateRequestPolicy.READ)
public class SLBarCodeIntegrationTemplate extends SimpleIntegrationTemplate {

	static String FILE_CONTENT = "file";
	public static final String INTEGRATION_PROP_KEY = "intProp";
	private static Logger logger = LoggerFactory.getLogger(SLBarCodeIntegrationTemplate.class);

	@Override
	protected SimpleConfiguration getConfiguration(SimpleConfiguration integrationConfiguration,
			SimpleConfiguration connectedSystemConfiguration, PropertyPath propertyPath,
			ExecutionContext executionContext) {

		return integrationConfiguration.setProperties(

				textProperty(INTEGRATION_PROP_KEY).label("Text Property").isRequired(false)
						.description("This will be concatenated with the connected system text property on execute")
						.build(),
				listTypeProperty(FILE_CONTENT).label("Upload NIC Barcode Page")
						.instructionText("Upload NIC barcode page.").itemType(SystemType.DOCUMENT).isRequired(true)
						.isExpressionable(true).build());
	}

	@Override
	protected IntegrationResponse execute(SimpleConfiguration integrationConfiguration,
			SimpleConfiguration connectedSystemConfiguration, ExecutionContext executionContext) {

		Map<String, Object> requestDiagnostic = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		final long start = System.currentTimeMillis();
		List<PropertyState> values = integrationConfiguration.getValue(FILE_CONTENT);

		// get the uploaded documents
		ArrayList<Document> documentArray = new ArrayList<Document>();

		for (PropertyState doc : values) {
			Document document = (Document) doc.getValue();
			documentArray.add(document);
		}
		// validations
		if (documentArray.isEmpty()) {
			logger.info("Files - No NIC files atatched.");
			IntegrationError error = templateError("Files - No NIC files atatched.");
			return IntegrationResponse.forError(error).build();
		}
		String key = connectedSystemConfiguration.getValue(SLBarCodeConnectedSystemTemplate.LICENSE_KEY);

		String[] results = null;
		try {
			results = ImageDecoding.extract(documentArray.get(0).getInputStream(), key);
		} catch (Exception e) {
			logger.error(" Exception while extarcting :" , e);
			IntegrationError error = templateError("Fail to initialize Barcode engine");
			return IntegrationResponse.forError(error).build();
		}

		if (results == null && results.length == 0) {
			IntegrationError error = templateError("Fail to extarct data from NIC bar code");
			return IntegrationResponse.forError(error).build();
		}

		result.put("NIC", results[1] != null ? results[1] : " ");
		result.put("DOB", results[2] != null ? results[2] : " ");
		result.put("SEX", results[3] != null ? results[3] : " ");
		result.put("ISSUE_DATE", results[4] != null ? results[4] : " ");
		result.put("NAME", results[6] != null ? results[6] : " ");
		// result.put("ADDRESS", results[7] != null ? results[7] : " ");
		result.put("PLACE_OF_BIRTH", results[8] != null ? results[8] : " ");

		Address address = Utils.getAddress(results[7]);
		if (address != null) {
			result.put("CITY", address.city);
			result.put("ADDRESS_LINE1", address.addrrssLine1);
			result.put("ADDRESS_LINE2", address.addressLine2);
		}

		final long end = System.currentTimeMillis();
		final long executionTime = end - start;
		final IntegrationDesignerDiagnostic diagnostic = IntegrationDesignerDiagnostic.builder()
				.addExecutionTimeDiagnostic(executionTime).addRequestDiagnostic(requestDiagnostic).build();

		return IntegrationResponse.forSuccess(result).withDiagnostic(diagnostic).build();
	}

	private IntegrationError templateError(String errorMessage) {
		return IntegrationError.builder().title("Something went wrong")
				.message("An error occurred in the IntegrationTemplate - " + errorMessage).build();
	}

}
