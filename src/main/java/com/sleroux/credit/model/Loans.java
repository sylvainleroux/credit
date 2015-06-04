package com.sleroux.credit.model;

import java.util.ArrayList;
import java.util.List;

import com.sleroux.credit.utils.CalcTools;

public class Loans {

	public List<Loan>	loans	= new ArrayList<>();

	public List<Loan> getLoans() {
		return loans;
	}

	public void setLoans(List<Loan> _loans) {
		loans = _loans;
	}

	public void printAmo() throws Exception {
		for (Loan l : loans){
			l.amortization(true);
		}
		
	}

	public void runStrategies() throws Exception {
		for (Loan l : loans){
			l.runStrategies(loans);
		}
	}

	public void printSeries() {
		CalcTools.compileSeries(loans);
		
	}

}
