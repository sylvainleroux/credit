package com.sleroux.credit.strategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.Pret;

public class TestSplitStrategy {
	@Test
	public void testSplit() throws Exception {

		Pret l = new Pret();
		l.setNom("Test SplitStrategy");
		l.setNominal(new BigDecimal(1000));
		l.addSerieEcheances(1, 10, "0.2", "100");
		
		l.addAssurance(1, 10, l.getNominal() + "", "0.0");

		
		l.printEcheances();
		l.printAssurances();
		
		SplitStrategy splitStrategy = new SplitStrategy();
		splitStrategy.setSplitAfter(5);
		splitStrategy.run(l, null);
		
		l.printEcheances();
		l.printAssurances();
		
		Assert.assertEquals(5, l.getDerniereSerieEcheances().getFin());

	}

}
