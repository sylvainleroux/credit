package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.sleroux.credit.model.Loan;
import com.sleroux.credit.model.Series;

public class TestSmoothingStrategy {

	@Test
	public void testSmoothingStrategy() throws Exception {

		Loan l1 = new Loan();
		l1.setName("Test 1");
		l1.setNominal(500);
		l1.addSeries(1, 5, "0", "100");
		l1.addInsurance(1, 5, "500", "0");

		l1.amortization(true);

		Loan l = new Loan();
		l.setName("Test");
		l.setNominal(10000);
		l.addInsurance(1, 1, "1", "0.00");
		l.addSeries(1, 1, "0.2", "500");

		l.amortization(true);

		Smoothing smoothing = new Smoothing();
		smoothing.setTarget(800);
		smoothing.setRate("0.1");
		smoothing.setInsuranceRate("0.000");

		smoothing.run(l, Arrays.asList(l1));

		BigDecimal checksum = l1.sumInterests().add(l1.getPrincipal()).add(l.getPrincipal()).add(l.sumInterests());
		BigDecimal payed = BigDecimal.ZERO;
		
		for (Series s : l1.getSeries()) {
			payed = payed.add(s.getAmount().multiply(new BigDecimal(s.getEnd() - s.getStart() + 1)));
		}
		
		for (Series s : l.getSeries()) {
			payed = payed.add(s.getAmount().multiply(new BigDecimal(s.getEnd() - s.getStart() + 1)));
		}

		
		Assert.assertEquals(checksum, payed);

	}

}
