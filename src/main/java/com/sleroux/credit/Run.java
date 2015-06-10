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

		if (prets.getReportAfter() > 0) {
			CalcTools.summary(prets.getReportAfter(), false, prets.getLoans());
		}

		if (prets.isPtzFree()) {
			CalcTools.summary(prets.getReportAfter(), true, prets.getLoans());
		}
	}

}
