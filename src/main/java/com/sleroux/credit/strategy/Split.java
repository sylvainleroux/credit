package com.sleroux.credit.strategy;

import java.util.Iterator;
import java.util.List;

import com.sleroux.credit.model.Loan;
import com.sleroux.credit.model.Series;

public class Split extends Strategy {

	int	splitAfter;

	@Override
	public void run(Loan _loan, List<Loan> _previousLoans) {
		// Split series
		{
			boolean delete = false;
			Iterator<Series> i = _loan.getSeries().iterator();
			while (i.hasNext()) {
				Series s = i.next();
				if (delete) {
					i.remove();
				} else if (s.getEnd() >= splitAfter) {
					s.setEnd(splitAfter);
					delete = true;
				}
			}
		}
		// Split insurances
		_loan.getInsurances().get(0).setEnd(splitAfter);

	}

	public int getSplitAfter() {
		return splitAfter;
	}

	public void setSplitAfter(int _splitAfter) {
		splitAfter = _splitAfter;
	}

	@Override
	public String getName() {
		return "Split";
	}

}
