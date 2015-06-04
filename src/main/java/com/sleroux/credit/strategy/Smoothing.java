package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.sleroux.credit.model.Loan;

public class Smoothing extends Strategy {

	public int		target;

	public String	rate;

	public String	insuranceRate;

	@Override
	public void run(Loan _loan, List<Loan> _previousLoans) throws Exception {
		
		BigDecimal principal = _loan.getPayments().get(_loan.getLastSeries().getEnd()).getCapitalRestantDu();
		
		_loan.printSeries();
		_loan.createNewSeries(target, rate, insuranceRate, principal, _previousLoans);
		_loan.adjustLengthForTargetPayment(true);
		_loan.adjustConstantPayments();
		_loan.printSeries();

	}

	@Override
	public String getName() {
		return "Smoothing with target : " + target;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int _target) {
		target = _target;
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
