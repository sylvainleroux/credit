package com.sleroux.credit.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.sleroux.credit.model.Mensualite;
import com.sleroux.credit.model.Pret;

public class CalcTools {

	private final static SimpleDateFormat	formatter	= new SimpleDateFormat("dd/MM/yyyy");

	public static void compileSeries(List<Pret> _prets) throws Exception {

		compileSeriesV2(_prets);
	}

	private static void compileSeriesV2(List<Pret> _prets) throws Exception {

		System.out.println("[Total] Mensualités");
		LinkedHashMap<Integer, BigDecimal> mensualites = new LinkedHashMap<>();
		LinkedHashMap<Integer, Date> dates = new LinkedHashMap<>();

		for (Pret p : _prets) {
			for (Mensualite m : p.getMensualites()) {
				int i = m.getTerme();
				if (mensualites.get(i) == null) {
					mensualites.put(i, BigDecimal.ZERO);
				}
				mensualites.put(i, mensualites.get(i).add(m.getEcheance()));

				if (dates.get(i) == null) {
					dates.put(i, m.getDate());
				} else {
					// Check
					Calendar cal1 = Calendar.getInstance();
					Calendar cal2 = Calendar.getInstance();
					cal1.setTime(m.getDate());
					cal2.setTime(dates.get(i));
					boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

					if (!sameDay) {
						for (Integer in : dates.keySet()) {
							System.out.println(in + " " + dates.get(in));
						}
						System.out.println("compared to : " + m.getDate() + " with index " + m.getTerme());

						throw new Exception("Different dates have been found for the same term");
					}
				}

			}
		}

		BigDecimal previous = BigDecimal.ZERO;

		// System.out.printf("%3d - %3d : %6.2f \n", e.getDebut(), e.getFin(),
		// e.getMontant());
		String out = "";
		int last = 0;
		String dateDebut = formatter.format(dates.get(1));
		for (Integer i : mensualites.keySet()) {
			last = i;
			if (!mensualites.get(i).equals(previous)) {
				if (i > 1) {
					System.out.print(out);
					out = String.format("%3s\t%6.2f\t%10s\t%10s", (i - 1), previous.doubleValue(), dateDebut, formatter.format(dates.get(i - 1)));
					System.out.println(out);
					dateDebut = formatter.format(dates.get(i));
				}
				out = String.format("%3d\t", i);
				previous = mensualites.get(i);
			}
		}
		System.out.println(out + String.format("%3d\t%6.2f\t%10s\t%10s", last, mensualites.get(last), dateDebut, formatter.format(dates.get(last))));
	}

	public static void summary(int nbPeriodes, boolean _excludePtz, List<Pret> _loans) {

		// Print header
		String ptzParts = _excludePtz ? "and PTZ " : "========";
		String excludePart = nbPeriodes > 0 ? "Exclude " + nbPeriodes + " firsts periods " : "TOTAL ====================";
		System.out.println("= " + excludePart + ptzParts + "================================================");

		BigDecimal checkSum = BigDecimal.ZERO;
		BigDecimal totalAssurance = BigDecimal.ZERO;
		BigDecimal totalInterets = BigDecimal.ZERO;
		BigDecimal totalCapital = BigDecimal.ZERO;

		for (Pret _pret3 : _loans) {
			if (_excludePtz && _pret3.getNom().contains("PTZ")) {
				continue;
			}

			BigDecimal interets = BigDecimal.ZERO;
			BigDecimal assurance = BigDecimal.ZERO;
			BigDecimal capital = BigDecimal.ZERO;
			for (Mensualite p : _pret3.getMensualites()) {
				if (p.getTerme() < nbPeriodes + 1) {
					continue;
				}
				if (p.getEcheance() != null) {
					checkSum = checkSum.add(p.getEcheance());
				}
				if (p.getMontantInterets() != null) {
					interets = interets.add(p.getMontantInterets());
					totalInterets = totalInterets.add(p.getMontantInterets());

				}
				if (p.getMontantAssurances() != null) {
					assurance = assurance.add(p.getMontantAssurances());
					totalAssurance = totalAssurance.add(p.getMontantAssurances());
				}
				if (p.getCapitalAmorti() != null) {
					capital = capital.add(p.getCapitalAmorti());
					totalCapital = totalCapital.add(p.getCapitalAmorti());
				}
				if (p.getApport() != null) {
					checkSum = checkSum.add(p.getApport());
				}
			}
			System.out.printf("[%-20s] Intérêts : %8.2f  Assurance : %8.2f  Capital : %8.2f\n", _pret3.getNom(), interets, assurance, capital);
		}
		System.out.println("TOTAL CAPITAL    : " + totalCapital);
		System.out.println("TOTAL INTERETS   : " + totalInterets);
		System.out.println("TOTAL ASSURANCES : " + totalAssurance);
		System.out.println("COUT CREDIT      : " + totalInterets.add(totalAssurance));
		System.out.println("COUT TOTAL       : " + totalCapital.add(totalInterets).add(totalAssurance));
		System.out.println("CHECKSUM         : " + checkSum);
	}

}
