package com.sleroux.credit.strategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.Loan;
import com.sleroux.credit.model.Series;

public class TestLengthStrategy {
	@Test
	public void testLenght() throws Exception {

		int amount = 100;

		Loan l = new Loan();
		l.setName("Test");
		l.setNominal(amount);
		l.addInsurance(1, 10000, "1", "0.00");

		Length length = new Length();
		length.setNbPeriods(20);
		length.setRate("0.2");

		length.run(l, null);

		BigDecimal interests = l.sumInterests();

		BigDecimal payed = BigDecimal.ZERO;
		for (Series s : l.getSeries()) {
			payed = payed.add(s.getAmount().multiply(new BigDecimal(s.getEnd() - s.getStart() + 1)));
		}
		Assert.assertEquals(new BigDecimal(amount).add(interests), payed);

	}
}
