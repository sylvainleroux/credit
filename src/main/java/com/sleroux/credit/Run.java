package com.sleroux.credit;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleroux.credit.model.Loans;
import com.sleroux.credit.utils.CamelCaseNamingStrategy;

public class Run {
	
	private final static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) throws Exception {
		
		mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());

		InputStream inputStream = Run.class.getResourceAsStream("/sample.json");
		Loans loans = mapper.readValue(inputStream, Loans.class);

		loans.printAmo();
		loans.runStrategies();
		loans.printAmo();
		loans.printSeries();
	}

}
