package com.sleroux.credit;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleroux.credit.model.Prets;
import com.sleroux.credit.utils.CalcTools;
import com.sleroux.credit.utils.CamelCaseNamingStrategy;

public class Run {

	private final static ObjectMapper	mapper	= new ObjectMapper();

	public static void main(String[] args) throws Exception {

		String file = null;
		if (args.length == 0) {
			file = "/sample.json";
		} else {
			file = args[0];
		}

		System.out.println(file);

		mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());

		InputStream inputStream = Run.class.getResourceAsStream(file);
		Prets prets = mapper.readValue(inputStream, Prets.class);

		prets.printAmo();
		CalcTools.sumOfInterests(prets.getLoans());
		prets.runStrategies();
		prets.printAmo();
		prets.printSeries();
		CalcTools.sumOfInterests(prets.getLoans());

	}

}
