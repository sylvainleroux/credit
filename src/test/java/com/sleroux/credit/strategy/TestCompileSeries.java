package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;

import com.sleroux.credit.model.Pret;
import com.sleroux.credit.utils.CalcTools;

public class TestCompileSeries {

	@Test
	public void testCompile() throws Exception {

		Pret p1 = new Pret();
		p1.setNom("P1");
		p1.setNominal(new BigDecimal(500));
		p1.addAssurance(1, 100, "500", "0.00");
		p1.addSerieEcheances(1, 100, "0.0", "5");
		p1.calcAmortization();
		p1.printAmortissement();

		Pret p2 = new Pret();
		p2.setNom("P2");
		p2.setNominal(new BigDecimal(10000));
		p2.addAssurance(1, 1000, "10000", "0.00");
		p2.addSerieEcheances(1, 1000, "0.0", "10");

		p2.calcAmortization();
		p2.printAmortissement();

		CalcTools.compileSeries(Arrays.asList(p1, p2));
	}

	@Test(expected = Exception.class)
	public void testCompile2() throws Exception {

		Calendar c = Calendar.getInstance();
		c.set(2010, 01, 01);

		Pret p1 = new Pret();
		p1.setDebut(c.getTime());
		p1.setNom("P1");
		p1.setNominal(new BigDecimal(50));
		p1.addAssurance(1, 5, "50", "0.00");
		p1.addSerieEcheances(1, 5, "0.0", "10");
		p1.calcAmortization();
		p1.printAmortissement();

		Pret p2 = new Pret();
		p2.setNom("P2");
		p2.setNominal(new BigDecimal(100));
		p2.addAssurance(1, 10, "100", "0.00");
		p2.addSerieEcheances(1, 10, "0.0", "10");

		p2.calcAmortization();
		p2.printAmortissement();

		CalcTools.compileSeries(Arrays.asList(p1, p2));
	}

}
