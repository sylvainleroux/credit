package com.sleroux.credit.utils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import com.sleroux.credit.model.Loan;
import com.sleroux.credit.model.Payment;
import com.sleroux.credit.model.Series;

public class CalcTools {

	public static void compileSeries(List<Loan> _loans) {

		System.out.println("[Total] Mensualités");
		LinkedHashMap<Integer, BigDecimal> mensualites = new LinkedHashMap<>();
		for (Loan p : _loans) {
			for (Series e : p.getEcheances()) {
				for (int i = e.getStart(); i <= e.getEnd(); i++) {
					if (mensualites.get(i) == null) {
						mensualites.put(i, BigDecimal.ZERO);
					}
					mensualites.put(i, mensualites.get(i).add(e.getAmount()));
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

	public static void sumOfInterests(Loan... prets) {
		System.out.println("=TOTAL================================================================");
		BigDecimal bitTotalAssurance = BigDecimal.ZERO;
		BigDecimal bigTotal = BigDecimal.ZERO;
		for (Loan _pret3 : prets) {
			BigDecimal total = BigDecimal.ZERO;
			BigDecimal ass = BigDecimal.ZERO;
			for (Payment p : _pret3.getPayments()) {
				if (p.montantInterets != null) {
					total = total.add(p.montantInterets);
					bigTotal = bigTotal.add(p.montantInterets);

				}
				if (p.montantAssurances != null) {
					ass = ass.add(p.montantAssurances);
					bitTotalAssurance = bitTotalAssurance.add(p.montantAssurances);
				}
			}
			System.out.printf("[%-20s] Intérêts : %8.2f  Insurance : %8.2f\n", _pret3.name, total, ass);
		}
		System.out.println("TOTAL INTERETS   : " + bigTotal);
		System.out.println("TOTAL ASSURANCES : " + bitTotalAssurance);
		System.out.println("COUT CREDIT      : " + bigTotal.add(bitTotalAssurance));

	}

	public static int sumOfInterestsLastPeriodes(int nbPeriodes, Loan... prets) {
		System.out.println("=NOUVEAU==============================================================");
		BigDecimal bitTotalAssurance = BigDecimal.ZERO;
		BigDecimal bigTotal = BigDecimal.ZERO;
		for (Loan _pret3 : prets) {
			BigDecimal total = BigDecimal.ZERO;
			BigDecimal ass = BigDecimal.ZERO;
			for (Payment p : _pret3.getPayments()) {
				if (p.terme < nbPeriodes + 1) {
					continue;
				}
				if (p.montantInterets != null) {
					total = total.add(p.montantInterets);
					bigTotal = bigTotal.add(p.montantInterets);

				}
				if (p.montantAssurances != null) {
					ass = ass.add(p.montantAssurances);
					bitTotalAssurance = bitTotalAssurance.add(p.montantAssurances);
				}
			}
			System.out.printf("[%-20s] Intérêts : %8.2f  Insurance : %8.2f\n", _pret3.name, total, ass);
		}
		System.out.println("TOTAL INTERETS   : " + bigTotal);
		System.out.println("TOTAL ASSURANCES : " + bitTotalAssurance);
		System.out.println("COUT CREDIT      : " + bigTotal.add(bitTotalAssurance));

		return bigTotal.add(bitTotalAssurance).intValue();

	}

}
