package com.sleroux.credit.strategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.SerieEcheances;
import com.sleroux.credit.model.Pret;

public class TestLengthStrategy {
	@Test
	public void testLength() throws Exception {

		int amount = 1000;

		Pret l = new Pret();
		l.setNom("Test");
		l.setNominal(new BigDecimal(amount));
		l.addAssurance(1, amount, "1", "0.00");

		DureeStrategy dureeStrategy = new DureeStrategy();
		dureeStrategy.setNbMois(10);
		dureeStrategy.setTaux("0.0");
		dureeStrategy.setTauxAssurance("0.0");

		l.calcAmortization();

		dureeStrategy.run(l, null);

		BigDecimal interests = l.sommeInterets();
		BigDecimal payed = BigDecimal.ZERO;
		for (SerieEcheances s : l.getEcheances()) {
			payed = payed.add(s.getMontant().multiply(new BigDecimal(s.getFin() - s.getDebut() + 1)));
		}

		l.printMensualites();
		Assert.assertEquals(new BigDecimal(amount).add(interests), payed);
		Assert.assertEquals(new BigDecimal(100).setScale(2), l.getDerniereMensualite().getEcheance());
	}

	@Test
	public void testLength2() throws Exception {

		int amount = 100;

		Pret l = new Pret();
		l.setNom("Test");
		l.setNominal(new BigDecimal(amount));
		l.addAssurance(1, amount, "1", "0.00");

		DureeStrategy dureeStrategy = new DureeStrategy();
		dureeStrategy.setNbMois(10);
		dureeStrategy.setTaux("0.0");
		dureeStrategy.setTauxAssurance("0.0");

		l.calcAmortization();

		dureeStrategy.run(l, null);

		BigDecimal interests = l.sommeInterets();
		BigDecimal payed = BigDecimal.ZERO;
		for (SerieEcheances s : l.getEcheances()) {
			payed = payed.add(s.getMontant().multiply(new BigDecimal(s.getFin() - s.getDebut() + 1)));
		}
		Assert.assertEquals(new BigDecimal(amount).add(interests), payed);
		Assert.assertEquals(new BigDecimal(10).setScale(2), l.getDerniereMensualite().getEcheance());
	}
	
	@Test
	public void testLengthInterests() throws Exception {

		int amount = 1000;

		Pret l = new Pret();
		l.setNom("Test");
		l.setNominal(new BigDecimal(amount));
		l.addAssurance(1, amount, "1", "0.00");

		DureeStrategy dureeStrategy = new DureeStrategy();
		dureeStrategy.setNbMois(10);
		dureeStrategy.setTaux("0.1");
		dureeStrategy.setTauxAssurance("0.0");

		l.calcAmortization();

		dureeStrategy.run(l, null);
		
		l.printAmortissement();

		BigDecimal interests = l.sommeInterets();
		BigDecimal payed = BigDecimal.ZERO;
		for (SerieEcheances s : l.getEcheances()) {
			payed = payed.add(s.getMontant().multiply(new BigDecimal(s.getFin() - s.getDebut() + 1)));
		}
		
		
		
		Assert.assertEquals(new BigDecimal(amount).add(interests).setScale(2), payed.setScale(2));
		Assert.assertEquals(new BigDecimal("103.80").setScale(2), l.getDerniereMensualite().getEcheance().setScale(2));
	}
}
