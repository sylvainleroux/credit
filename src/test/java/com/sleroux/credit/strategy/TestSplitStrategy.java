package com.sleroux.credit.strategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.Pret;

public class TestSplitStrategy {
	@Test
	public void testSplit() throws Exception {

		Pret l = new Pret();
		l.setNom("Test Split");
		l.setNominal(new BigDecimal(1000));
		l.setEcheances(1, 10, "0.2", "100");
		
		l.addAssurance(1, 10, l.getNominal() + "", "0.0");

		
		l.printSeries();
		l.printAssurances();
		
		Split split = new Split();
		split.setSplitAfter(5);
		split.run(l, null);
		
		l.printSeries();
		l.printAssurances();
		
		Assert.assertEquals(5, l.getDerniereEcheance().getFin());

	}

}
