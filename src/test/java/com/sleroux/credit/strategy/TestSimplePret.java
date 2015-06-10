package com.sleroux.credit.strategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.Pret;

public class TestSimplePret {

	@Test
	public void test() throws Exception {

		Pret p1 = new Pret();
		p1.setNom("P1");
		p1.setNominal(new BigDecimal(4400));
		p1.addAssurance(1, 144, "4400", "0.00");
		p1.addSerieEcheances(1, 143, "0.0", "30.55");
		p1.addSerieEcheances(144, 144, "0.0", "31.35");
		p1.calcAmortization();
		p1.printAmortissement();
		
		Assert.assertEquals(new BigDecimal("0.00"), p1.getDerniereMensualite().getCapitalRestantDu());

	}
	
	@Test
	public void test2() throws Exception {

		Pret p1 = new Pret();
		p1.setNom("P1");
		p1.setNominal(new BigDecimal(1000));
		p1.addAssurance(1, 10, "1000", "0.00");
		p1.addSerieEcheances(1, 10, "0.0", "100");
		p1.calcAmortization();
		p1.printAmortissement();
		
		Assert.assertEquals(new BigDecimal("0.00"), p1.getDerniereMensualite().getCapitalRestantDu());

	}
	
	@Test
	public void test3() throws Exception {

		Pret p1 = new Pret();
		p1.setNom("P1");
		p1.setNominal(new BigDecimal(100000));
		p1.addAssurance(1, 120, "100000", "0.01");
		p1.addSerieEcheances(1, 119, "0.01", "959.37");
		p1.addSerieEcheances(120, 120, "0.01", "959.51");
		p1.calcAmortization();
		p1.printAmortissement();
		
		Assert.assertEquals(new BigDecimal("0.00"), p1.getDerniereMensualite().getCapitalRestantDu());

	}

}
