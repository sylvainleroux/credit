package com.sleroux.credit.model;

import java.util.ArrayList;
import java.util.List;

import com.sleroux.credit.utils.CalcTools;

public class Prets {

	public List<Pret>	prets	= new ArrayList<>();

	public List<Pret> getLoans() {
		return prets;
	}

	public void setLoans(List<Pret> _prets) {
		prets = _prets;
	}

	public void printAmo() throws Exception {
		for (Pret l : prets){
			l.amortization(true);
		}
		
	}

	public void runStrategies() throws Exception {
		for (Pret l : prets){
			l.runStrategies(prets);
		}
	}

	public void printSeries() {
		CalcTools.compileSeries(prets);
		
	}

}
