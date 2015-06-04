package com.sleroux.credit.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sleroux.credit.strategy.Strategy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Loan {

	List<Payment>			payments;
	private BigDecimal		principal;

	@JsonProperty("start_date")
	private Date			startDate;
	private BigDecimal		rate;
	public String			name;

	public List<Strategy>	strategies	= new ArrayList<>();

	public Loan() {
		// emtpy
	}

	@JsonProperty("series")
	private List<Series>	series	= new ArrayList<>();

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> _series) {
		series = _series;
	}

	@JsonProperty("insurances")
	List<Insurance>					insurances	= new ArrayList<>();

	private static SimpleDateFormat	formatter	= new SimpleDateFormat("dd/MM/yyyy");

	public Loan(int _nominal, String _tauxAssurance) {
		this(_nominal, "Pret " + _nominal, null);
	}

	public Loan(int _nominal, String _name, String _premiereEcheance) {
		principal = new BigDecimal(_nominal);
		name = _name;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = formatter.parse(_premiereEcheance);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getNbPeriodes() {
		int maxPeriode = 0;
		for (Series e : series) {
			maxPeriode = Math.max(maxPeriode, e.getEnd());
		}

		return maxPeriode;
	}

	public void runStrategies(List<Loan> _loans) throws Exception {
		for (Strategy s : strategies) {
			// Get only aleardy computed loans
			List<Loan> previousLoans = new ArrayList<>();
			for (Loan l : _loans) {
				if (l.equals(this)) {
					break;
				}
				previousLoans.add(l);
			}

			System.out.println("RUN Strategy: " + s.getName());
			s.run(this, previousLoans);
		}

	}

	public void addSeries(int _start, int _end, String _rate, String _amount) {
		series.add(new Series(_start, _end, _rate, _amount));

	}

	public void increaseLastSeries() {
		Series last = series.get(series.size() - 1);
		last.end = last.end + 1;
	}

	public void reduceLastSeries() {
		Series last = series.get(series.size() - 1);
		if (last.start == last.end) {
			System.out.println("Removing last series");
			series.remove(last);
		} else {
			last.end = last.end - 1;
		}

	}

	public void increaseLastSerieAmount(String _i) {
		Series last = series.get(series.size() - 1);
		last.amount = last.amount.add(new BigDecimal(_i));

	}

	public void decreaseLastSerieAmount(String _scale) {
		Series last = series.get(series.size() - 1);
		last.amount = last.amount.subtract(new BigDecimal(_scale));

	}

	public void adjustLastPeriode(BigDecimal _restant) {
		Series last = series.get(series.size() - 1);
		// reduce size by 1
		if (last.start == last.end) {
			// Just update it
			last.amount = last.amount.add(_restant);
		} else {
			Series e = new Series();
			e.end = last.end;
			e.start = last.end;
			e.rate = last.rate;
			e.amount = last.amount.add(_restant);
			last.end = last.end - 1;
			series.add(e);
		}

	}

	public BigDecimal getCapitalRestantDu() {
		return payments.get(payments.size() - 1).capitalRestantDu;
	}

	public void printSeries() {
		System.out.print("[" + name + "] Echeances :\n");
		for (Series e : series) {
			System.out.printf("%3d - %3d : %6.2f \n", e.getStart(), e.getEnd(), e.getAmount());
		}
	}

	public void createNewSeries(int _target, String _rate, String _insuranceRate, BigDecimal _principal, List<Loan> _prets) {

		System.out.println("[" + name + "] Création séries de paiement pour lissage");

		LinkedHashMap<Integer, BigDecimal> payments = new LinkedHashMap<>();

		for (Loan p : _prets) {
			for (Series e : p.getSeries()) {
				for (int i = e.getStart(); i <= e.getEnd(); i++) {
					if (payments.get(i) == null) {
						payments.put(i, BigDecimal.ZERO);
					}
					payments.put(i, payments.get(i).add(e.getAmount()));
				}
			}
		}

		// Create new

		Series current = null;
		BigDecimal previous = BigDecimal.ZERO;

		int lastDefinedPeriod = getLastSeries().getEnd();
		int i;

		for (i = lastDefinedPeriod + 1; i < payments.size(); i++) {
			BigDecimal restant = new BigDecimal(_target).subtract(payments.get(i));
			if (!restant.equals(previous)) {
				if (current != null) {
					current.end = i - 1;
				}
				previous = restant;
				current = new Series();
				current.amount = restant;
				current.rate = new BigDecimal(_rate);
				current.start = i;
				series.add(current);
			}
		}

		current.end = i;

		addSeries(i + 1, i + 1, _rate, _target + "");

		// Create matching insurance
		Insurance insurance = new Insurance();
		insurance.setStart(lastDefinedPeriod + 1);
		insurance.setEnd(i+1);
		insurance.setPrincipal(_principal);
		insurance.setRate(new BigDecimal(_insuranceRate));
		insurances.add(insurance);

	}

	public void amortization(boolean _print) throws Exception {

		Calendar c = Calendar.getInstance();
		if (startDate != null) {
			c.setTime(startDate);
		}

		List<Payment> payments = new ArrayList<Payment>();
		Payment p0 = new Payment();
		p0.capitalRestantDu = getPrincipal();
		payments.add(p0);

		if (_print) {
			System.out.println("[" + name + "] Tableau d'amortissement : ");
			System.out.printf("terme%12s%10s%10s%10s%10s%10s\n", "date", "interets", "assur", "cp remb.", "cp rest.", "echeance");
		}

		int nbPeriodes = getNbPeriodes();

		for (int n = 1; n <= nbPeriodes; n++) {

			Series e = getEcheance(n);
			Insurance a = getAssurance(n);

			// Calcul assurance
			BigDecimal mensualiteAssurance = a.getMensualite();
			BigDecimal tauxNominalProportionnel = e.rate.setScale(12, RoundingMode.HALF_UP)
					.divide(new BigDecimal(12), RoundingMode.HALF_UP);

			Payment pn = new Payment();
			payments.add(pn);
			pn.terme = n;
			pn.montantAssurances = mensualiteAssurance;
			BigDecimal capital = payments.get(n - 1).capitalRestantDu;
			pn.montantInterets = capital.multiply(tauxNominalProportionnel).setScale(2, RoundingMode.HALF_UP);
			BigDecimal echeance = e.getAmount().setScale(2, RoundingMode.HALF_UP);
			pn.capitalRestantDu = capital.add(pn.montantInterets).subtract(echeance).add(mensualiteAssurance);

			if (e.apport != null) {
				pn.capitalRestantDu = pn.capitalRestantDu.subtract(e.apport);
			}

			BigDecimal capitalAmorti = capital.subtract(pn.capitalRestantDu);
			if (_print) {
				System.out.printf(" %4d%12s%10.2f%10.2f%10.2f%10.2f%10.2f\n", n, formatter.format(c.getTime()), pn.montantInterets,
						mensualiteAssurance, capitalAmorti, pn.capitalRestantDu, echeance);
			}

			c.add(Calendar.MONTH, 1);
		}

		setPayments(payments);

	}

	private Insurance getAssurance(int _n) throws Exception {
		// System.out.println("--- " + _n);
		for (Insurance a : insurances) {
			// System.out.println(a.debut +" " + a.fin);
			if (a.start <= _n && a.end >= _n) {
				return a;
			}
		}

		throw new Exception("Assurance not found for period [" + _n + "]");
	}

	private Series getEcheance(int _n) throws Exception {
		// System.out.println("--- " + _n);
		for (Series e : series) {
			// System.out.println(e.getDebut() + " " + e.getFin());
			if (e.getStart() <= _n && e.getEnd() >= _n) {
				return e;
			}
		}
		throw new Exception("Series not found for period [" + _n + "]");
	}

	public void adjustLengthForTargetPayment(boolean _print) throws Exception {

		if (_print)
			System.out.print("[" + name + "] Ajuste le nombre de menusalités ");

		while (true) {
			amortization(false);
			Payment last = payments.get(payments.size() - 1);
			if (_print)
				System.out.print("+");

			if (last.capitalRestantDu.intValue() > 0) {
				increaseLastSeries();
				getLastInsurance().increaseFrame();
			} else {
				break;
			}
		}

		while (true) {
			amortization(false);
			Payment last = payments.get(payments.size() - 1);
			if (_print)
				System.out.print("-");
			if (last.capitalRestantDu.intValue() < 0) {
				reduceLastSeries();
			} else {
				break;
			}
		}
		if (_print)
			System.out.print("\n");

	}

	public void adjustConstantPayments() throws Exception {

		adjustAmmout(50, "1");
		adjustAmmout(5, "0.1");
		adjustAmmout(0, "0.01");
		adjustLastPeriode(getCapitalRestantDu());

	}

	private void adjustAmmout(int _threathold, String _scale) throws Exception {
		System.out.print("[" + name + "] Ajuste les menusualités pour avoir des menusalités égales (scale:" + _scale + ") ");
		int count = 100;
		while (true) {
			amortization(false);
			Payment last = payments.get(payments.size() - 1);

			if (last.capitalRestantDu.intValue() > _threathold) {
				System.out.print("+");
				// Adjust mensualité
				increaseLastSerieAmount(_scale);
			} else if (last.capitalRestantDu.intValue() < -_threathold) {
				System.out.print("-");
				decreaseLastSerieAmount(_scale);
			} else {
				break;
			}
			count--;
			if (count < 0) {
				System.exit(1);
			}
		}
		System.out.print("\n");

	}

	public void addInsurance(int _debut, int _fin, String _montant, String _taux) {
		insurances.add(new Insurance(_debut, _fin, _montant + "", _taux));
	}

	public List<Series> getEcheances() {
		return series;
	}

	public void setEcheances(List<Series> _series) {
		series = _series;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setNominal(int _nominal) {
		principal = new BigDecimal(_nominal);
	}

	public void setPrincipal(BigDecimal _nominal) {
		principal = _nominal;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date _souscription) {
		startDate = _souscription;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal _tauxNominal) {
		rate = _tauxNominal;
	}

	public void setPayments(List<Payment> _payments) {
		payments = _payments;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public List<Strategy> getStrategies() {
		return strategies;
	}

	public void setStrategies(List<Strategy> _strategies) {
		strategies = _strategies;
	}

	public Series getLastSeries() {
		if (series.size() == 0) {
			return null;
		}
		return series.get(series.size() - 1);
	}

	public void setName(String _name) {
		name = _name;
	}

	public String getName() {
		return name;
	}

	public BigDecimal sumInterests() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Payment p : payments) {
			if (p.montantInterets != null)
				sum = sum.add(p.montantInterets);
		}
		return sum;
	}

	public Payment getLastPayment() {
		if (payments.size() == 0) {
			return null;
		}
		return payments.get(payments.size() - 1);
	}

	private Insurance getLastInsurance() {
		if (insurances.size() == 0) {
			return null;
		}
		return insurances.get(insurances.size() - 1);
	}

	public List<Insurance> getInsurances() {
		return insurances;
	}
}
