package com.sleroux.credit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleroux.credit.model.Prets;
import com.sleroux.credit.utils.CalcTools;
import com.sleroux.credit.utils.CamelCaseNamingStrategy;

public class Run {

	public static void main(String[] args) throws Exception {

		// Load Json config
		InputStream inputStream = null;
		if (args.length == 0) {
			System.out.println("Pass a filename as parameter. Using default file");
			inputStream = Run.class.getResourceAsStream("/sample.json");
		} else {
			System.out.println("Importing file : " + args[0]);
			inputStream = new FileInputStream(new File(args[0]));
		}

		// Convert Json to object model
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());
		Prets prets = mapper.readValue(inputStream, Prets.class);

		// Calculate a first amortization with model data
		prets.calcAmortization();

		// Run strategies that will complete the model
		prets.runStrategies();

		// Calculate with the updated model
		prets.calcAmortization();

		// Display results
		prets.printAmo();
		prets.printSerieEcheances();
		CalcTools.summary(0, false, prets.getLoans());

		// Display summary, considering only remaining payments
		// Help to compare costs for :
		// - a running loan that would be re-negotiated, have rates changed at a specified time
		// - a new loans that would be contracted in another bank, starting at the same specified time
		if (prets.getReportAfter() > 0) {
			CalcTools.summary(prets.getReportAfter(), false, prets.getLoans());
		}

		// Display summary, excluding any loan containing "PTZ" in the name 
		// Helps to compare costs for :
		// - a running loan, that contain a "PTZ"
		// - a new loan, contracted in a new bank, but considering the payment of the PTZ loan.
		if (prets.isPtzFree()) {
			CalcTools.summary(prets.getReportAfter(), true, prets.getLoans());
		}

		// Debug
		// mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
		// System.out.println(mapper.writeValueAsString(prets));

	}

}
