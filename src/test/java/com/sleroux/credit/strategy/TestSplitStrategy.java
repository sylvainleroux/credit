package com.sleroux.credit.strategy;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.Loan;

public class TestSplitStrategy {
	@Test
	public void testSplit() {

		Loan l = new Loan();
		l.addSeries(1, 10, "0.2", "100");
		l.addSeries(1, 10, "0.1", "100");

		Split split = new Split();
		split.setSplitAfter(5);

		split.run(l, null);

		Assert.assertEquals(5, l.getLastSeries().getEnd());

	}

}
