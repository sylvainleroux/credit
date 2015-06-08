package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.sleroux.credit.model.SerieEcheances;
import com.sleroux.credit.model.Pret;

public class TestSmoothingStrategy {

	@Test
	public void testSmoothingStrategy() throws Exception {

		Pret l1 = new Pret();
		l1.setNom("Test 1");
		l1.setNominal(new BigDecimal(500));
		l1.addSerieEcheances(1, 5, "0", "100");
		l1.addAssurance(1, 5, "500", "0");

		l1.amortization();

		Pret l = new Pret();
		l.setNom("Test");
		l.setNominal(new BigDecimal(10000));
		l.addAssurance(1, 1, "1", "0.00");
		l.addSerieEcheances(1, 1, "0.2", "500");

		l.amortization();

		Lissage lissage = new Lissage();
		lissage.setMensualiteCible(800);
		lissage.setTaux("0.1");
		lissage.setTauxAssurance("0.000");

		lissage.run(l, Arrays.asList(l1));

		BigDecimal checksum = l1.sommeInterets().add(l1.getNominal()).add(l.getNominal()).add(l.sommeInterets());
		BigDecimal payed = BigDecimal.ZERO;

		for (SerieEcheances s : l1.getEcheances()) {
			payed = payed.add(s.getMontant().multiply(new BigDecimal(s.getFin() - s.getDebut() + 1)));
		}

		for (SerieEcheances s : l.getEcheances()) {
			payed = payed.add(s.getMontant().multiply(new BigDecimal(s.getFin() - s.getDebut() + 1)));
		}

		Assert.assertEquals(checksum, payed);

	}

}
