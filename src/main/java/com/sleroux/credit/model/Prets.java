package com.sleroux.credit.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sleroux.credit.utils.CalcTools;

public class Prets {

	public List<Pret>	prets		= new ArrayList<>();

	@JsonProperty(required = false)
	private int			reportAfter	= 0;

	@JsonProperty(required = false)
	private String		description;

	@JsonProperty(required = false)
	private boolean		ptzFree;

	public void printAmo() throws Exception {
		for (Pret pret : prets) {
			pret.printEcheances();
			pret.printAmortissement();
		}
	}

	public void calcAmortization() throws Exception {
		for (Pret pret : prets) {
			pret.calcAmortization();
		}
	}

	public void runStrategies() throws Exception {
		for (Pret pret : prets) {
			pret.runStrategies(prets);
		}
	}

	public void printSerieEcheances() throws Exception {
		CalcTools.compileSeries(prets);
	}

	public int getReportAfter() {
		return reportAfter;
	}

	public boolean isPtzFree() {
		return ptzFree;
	}

	public void setPtzFree(boolean _ptzFree) {
		ptzFree = _ptzFree;
	}

	public List<Pret> getLoans() {
		return prets;
	}

	public void setLoans(List<Pret> _prets) {
		prets = _prets;
	}

}
