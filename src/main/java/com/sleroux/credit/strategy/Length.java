package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.sleroux.credit.model.Loan;
import com.sleroux.credit.model.Series;

public class Length extends Strategy {

	private int		nbPeriods;

	private String	rate;

	private String	insuranceRate;

	@Override
	public void run(Loan _loan, List<Loan> _previousLoans) throws Exception {

		if (rate == null)
			throw new Exception("rate should not be null");

		System.out.printf("[" + _loan.name + "] Augmente la mensualité jusqu'à obtenir la durée choisie");

		Series series = _loan.getLastSeries();
		int start = series == null ? 1 : _loan.getLastSeries().getEnd() + 1;

		_loan.addInsurance(start, nbPeriods, _loan.getPayments().get(_loan.getLastSeries().getEnd()).getCapitalRestantDu().toString(),
				insuranceRate);
		_loan.addSeries(start, nbPeriods, rate, "1");

		_loan.amortization(false);
		while (_loan.getLastPayment().getCapitalRestantDu().compareTo(BigDecimal.ZERO) > 0) {
			_loan.increaseLastSerieAmount("1");
			_loan.amortization(false);
			System.out.print(".");
		}
		System.out.print("\n");
		_loan.adjustConstantPayments();
		_loan.printSeries();

	}

	@Override
	public String getName() {
		return "Length";
	}

	public int getNbPeriods() {
		return nbPeriods;
	}

	public void setNbPeriods(int _nbPeriods) {
		nbPeriods = _nbPeriods;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String _rate) {
		rate = _rate;
	}

	public String getInsuranceRate() {
		return insuranceRate;
	}

	public void setInsuranceRate(String _insuranceRate) {
		insuranceRate = _insuranceRate;
	}

}
