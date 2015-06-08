package com.sleroux.credit.utils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import com.sleroux.credit.model.Echeance;
import com.sleroux.credit.model.Mensualite;
import com.sleroux.credit.model.Pret;

public class CalcTools {

	public static void compileSeries(List<Pret> _prets) {

		System.out.println("[Total] Mensualités");
		LinkedHashMap<Integer, BigDecimal> mensualites = new LinkedHashMap<>();
		for (Pret p : _prets) {
			for (Echeance e : p.getEcheances()) {
				for (int i = e.getDebut(); i <= e.getFin(); i++) {
					if (mensualites.get(i) == null) {
						mensualites.put(i, BigDecimal.ZERO);
					}
					mensualites.put(i, mensualites.get(i).add(e.getMontant()));
				}
			}
		}

		BigDecimal previous = BigDecimal.ZERO;

		// System.out.printf("%3d - %3d : %6.2f \n", e.getDebut(), e.getFin(),
		// e.getMontant());
		String out = String.format("%3d ", 1);

		int last = 0;
		for (Integer i : mensualites.keySet()) {
			last = i;
			if (!mensualites.get(i).equals(previous)) {
				out += String.format("%3d : %6.2f", (i - 1), previous.doubleValue());
				if (i > 1) {
					System.out.println(out);
				}
				out = i + " - ";
				previous = mensualites.get(i);
			}
		}
		System.out.println(out + String.format("%3d : %6.2f", last, mensualites.get(last)));
	}

	public static void sumOfInterests(List<Pret> prets) {
		System.out.println("=TOTAL================================================================");
		BigDecimal bitTotalAssurance = BigDecimal.ZERO;
		BigDecimal bigTotal = BigDecimal.ZERO;
		for (Pret _pret3 : prets) {
			BigDecimal total = BigDecimal.ZERO;
			BigDecimal ass = BigDecimal.ZERO;
			for (Mensualite p : _pret3.getMensualites()) {
				if (p.getMontantInterets() != null) {
					total = total.add(p.getMontantInterets());
					bigTotal = bigTotal.add(p.getMontantInterets());

				}
				if (p.getMontantAssurances() != null) {
					ass = ass.add(p.getMontantAssurances());
					bitTotalAssurance = bitTotalAssurance.add(p.getMontantAssurances());
				}
			}
			System.out.printf("[%-20s] Intérêts : %8.2f  Assurance : %8.2f\n", _pret3.getNom(), total, ass);
		}
		System.out.println("TOTAL INTERETS   : " + bigTotal);
		System.out.println("TOTAL ASSURANCES : " + bitTotalAssurance);
		System.out.println("COUT CREDIT      : " + bigTotal.add(bitTotalAssurance));

	}

	public static int sumOfInterestsLastPeriodes(int nbPeriodes, Pret... prets) {
		System.out.println("=NOUVEAU==============================================================");
		BigDecimal bitTotalAssurance = BigDecimal.ZERO;
		BigDecimal bigTotal = BigDecimal.ZERO;
		for (Pret _pret3 : prets) {
			BigDecimal total = BigDecimal.ZERO;
			BigDecimal ass = BigDecimal.ZERO;
			for (Mensualite p : _pret3.getMensualites()) {
				if (p.getTerme() < nbPeriodes + 1) {
					continue;
				}
				if (p.getMontantInterets() != null) {
					total = total.add(p.getMontantInterets());
					bigTotal = bigTotal.add(p.getMontantInterets());

				}
				if (p.getMontantAssurances() != null) {
					ass = ass.add(p.getMontantAssurances());
					bitTotalAssurance = bitTotalAssurance.add(p.getMontantAssurances());
				}
			}
			System.out.printf("[%-20s] Intérêts : %8.2f  Assurance : %8.2f\n", _pret3.getNom(), total, ass);
		}
		System.out.println("TOTAL INTERETS   : " + bigTotal);
		System.out.println("TOTAL ASSURANCES : " + bitTotalAssurance);
		System.out.println("COUT CREDIT      : " + bigTotal.add(bitTotalAssurance));

		return bigTotal.add(bitTotalAssurance).intValue();

	}

}
