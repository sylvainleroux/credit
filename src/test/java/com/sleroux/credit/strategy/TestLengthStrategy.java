package com.sleroux.credit.strategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.sleroux.credit.model.Echeance;
import com.sleroux.credit.model.Pret;

public class TestLengthStrategy {
	@Test
	public void testLength() throws Exception {

		int amount = 1000;

		Pret l = new Pret();
		l.setNom("Test");
		l.setNominal(new BigDecimal(amount));
		l.addAssurance(1, amount, "1", "0.00");

		Duree duree = new Duree();
		duree.setNbMois(10);
		duree.setTaux("0.0");
		duree.setTauxAssurance("0.0");

		l.amortization();

		duree.run(l, null);

		BigDecimal interests = l.sommeInterets();
		BigDecimal payed = BigDecimal.ZERO;
		for (Echeance s : l.getEcheances()) {
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

		Duree duree = new Duree();
		duree.setNbMois(10);
		duree.setTaux("0.0");
		duree.setTauxAssurance("0.0");

		l.amortization();

		duree.run(l, null);

		BigDecimal interests = l.sommeInterets();
		BigDecimal payed = BigDecimal.ZERO;
		for (Echeance s : l.getEcheances()) {
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

		Duree duree = new Duree();
		duree.setNbMois(10);
		duree.setTaux("0.1");
		duree.setTauxAssurance("0.0");

		l.amortization();

		duree.run(l, null);
		
		l.printAmortissement();

		BigDecimal interests = l.sommeInterets();
		BigDecimal payed = BigDecimal.ZERO;
		for (Echeance s : l.getEcheances()) {
			payed = payed.add(s.getMontant().multiply(new BigDecimal(s.getFin() - s.getDebut() + 1)));
		}
		
		
		
		Assert.assertEquals(new BigDecimal(amount).add(interests), payed);
		Assert.assertEquals(new BigDecimal("103.80").setScale(2), l.getDerniereMensualite().getEcheance());
	}
}
