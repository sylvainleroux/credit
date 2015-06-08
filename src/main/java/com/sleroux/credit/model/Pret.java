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
import com.sleroux.credit.strategy.Strategy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pret {

	private BigDecimal			nominal;
	private Date				debut;
	private BigDecimal			taux;
	private String				nom;

	private List<Echeance>		echeance	= new ArrayList<>();
	private List<Mensualite>	mensualite	= new ArrayList<>();
	private List<Strategy>		strategies	= new ArrayList<>();
	private List<Assurance>		assurances	= new ArrayList<>();

	public Pret() {
		// emtpy
	}

	public Pret(int _nominal, String _tauxAssurance) {
		this(_nominal, "Pret " + _nominal, null);
	}

	public Pret(int _nominal, String _name, String _premiereEcheance) {
		nominal = new BigDecimal(_nominal);
		nom = _name;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			debut = formatter.parse(_premiereEcheance);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getNbPeriodes() {
		int maxPeriode = 0;
		for (Echeance e : echeance) {
			maxPeriode = Math.max(maxPeriode, e.getFin());
		}
		return maxPeriode;
	}

	public void runStrategies(List<Pret> _prets) throws Exception {
		for (Strategy s : strategies) {
			// Get only aleardy computed prets
			List<Pret> previousLoans = new ArrayList<>();
			for (Pret l : _prets) {
				if (l.equals(this)) {
					break;
				}
				previousLoans.add(l);
			}

			System.out.println("RUN Strategy: " + s.getName());
			s.run(this, previousLoans);
		}

	}

	public void setEcheances(int _start, int _end, String _rate, String _amount) {
		echeance.add(new Echeance(_start, _end, _rate, _amount));

	}

	public void increaseLastSeries() {
		Echeance last = echeance.get(echeance.size() - 1);
		last.setFin(last.getFin() + 1);
	}

	public void reduceLastSeries() {
		Echeance last = echeance.get(echeance.size() - 1);
		if (last.getDebut() == last.getFin()) {
			System.out.println("Removing last echeance");
			echeance.remove(last);
		} else {
			last.setFin(last.getFin() - 1);
		}

	}

	public void increaseLastSerieAmount(String _i) {
		Echeance last = echeance.get(echeance.size() - 1);
		last.setMontant(last.getMontant().add(new BigDecimal(_i)));

	}

	public void decreaseLastSerieAmount(String _scale) {
		Echeance last = echeance.get(echeance.size() - 1);
		last.setMontant(last.getMontant().subtract(new BigDecimal(_scale)));

	}

	public void adjustLastPeriode(BigDecimal _restant) {
		Echeance last = echeance.get(echeance.size() - 1);
		// reduce size by 1
		if (last.getDebut() == last.getFin()) {
			// Just update it
			last.setMontant(last.getMontant().add(_restant));
		} else {
			Echeance e = new Echeance();
			e.setFin(last.getFin());
			e.setDebut(last.getFin());
			e.setTaux(last.getTaux());
			e.setMontant(last.getMontant().add(_restant));
			last.setFin(last.getFin() - 1);
			echeance.add(e);
		}

	}

	public BigDecimal getCapitalRestantDu() {
		return mensualite.get(mensualite.size() - 1).getCapitalRestantDu();
	}

	public void printEcheances() {
		System.out.print("[" + nom + "] Echeances :\n");
		for (Echeance e : echeance) {
			System.out.printf("%3d - %3d : %6.2f \n", e.getDebut(), e.getFin(), e.getMontant());
		}
	}

	public void printAssurances() {
		System.out.print("[" + nom + "] Assurances :\n");
		for (Assurance a : assurances) {
			System.out.printf("%3d - %3d : %6.2f \n", a.getDebut(), a.getFin(), a.getCapital());
		}
	}

	public void printMensualites() {
		System.out.print("[" + nom + "] Menusalites :\n");
		for (Mensualite m : mensualite) {
			System.out.printf(m.toString());
		}
	}

	public void createNewSeries(int _target, String _rate, String _insuranceRate, BigDecimal _principal, List<Pret> _prets) {

		System.out.println("[" + nom + "] Création séries de paiement pour lissage");

		LinkedHashMap<Integer, BigDecimal> payments = new LinkedHashMap<>();

		for (Pret p : _prets) {
			for (Echeance e : p.echeance) {
				for (int i = e.getDebut(); i <= e.getFin(); i++) {
					if (payments.get(i) == null) {
						payments.put(i, BigDecimal.ZERO);
					}
					payments.put(i, payments.get(i).add(e.getMontant()));
				}
			}
		}

		// Create new

		Echeance current = null;
		BigDecimal previous = BigDecimal.ZERO;

		int lastDefinedPeriod = getDerniereEcheance().getFin();
		int i;

		for (i = lastDefinedPeriod + 1; i < payments.size(); i++) {
			BigDecimal restant = new BigDecimal(_target).subtract(payments.get(i));
			if (!restant.equals(previous)) {
				if (current != null) {
					current.setFin(i - 1);
				}
				previous = restant;
				current = new Echeance();
				current.setMontant(restant);
				current.setTaux(new BigDecimal(_rate));
				current.setDebut(i);
				echeance.add(current);
			}
		}

		current.setFin(i);

		setEcheances(i + 1, i + 1, _rate, _target + "");

		// Create matching insurance
		Assurance assurance = new Assurance();
		assurance.setDebut(lastDefinedPeriod + 1);
		assurance.setFin(i + 1);
		assurance.setCapital(_principal);
		assurance.setTaux(new BigDecimal(_insuranceRate));
		assurances.add(assurance);

	}

	public void amortization() throws Exception {

		Calendar c = Calendar.getInstance();
		if (debut != null) {
			c.setTime(debut);
		}

		mensualite = new ArrayList<Mensualite>();
		Mensualite p0 = new Mensualite();
		p0.setCapitalRestantDu(nominal);
		mensualite.add(p0);

		int nbPeriodes = getNbPeriodes();

		for (int n = 1; n <= nbPeriodes; n++) {

			Echeance e = getEcheance(n);
			Assurance a = getAssurance(n);

			// Calcul assurance
			BigDecimal mensualiteAssurance = a.getMensualite();
			BigDecimal tauxNominalProportionnel = e.getTaux().setScale(12, RoundingMode.HALF_UP).divide(new BigDecimal(12), RoundingMode.HALF_UP);

			Mensualite pn = new Mensualite();
			mensualite.add(pn);
			pn.setTerme(n);
			pn.setMontantAssurances(mensualiteAssurance);
			BigDecimal capital = mensualite.get(n - 1).getCapitalRestantDu();
			pn.setMontantInterets(capital.multiply(tauxNominalProportionnel).setScale(2, RoundingMode.HALF_UP));
			pn.setEcheance(e.getMontant().setScale(2, RoundingMode.HALF_UP));
			pn.setCapitalRestantDu(capital.add(pn.getMontantInterets()).subtract(pn.getEcheance()).add(mensualiteAssurance));

			pn.setCapitalAmorti(capital.subtract(pn.getCapitalRestantDu()));
			pn.setDate(c.getTime());

			c.add(Calendar.MONTH, 1);
		}

		getMensualites(mensualite);

	}

	private Assurance getAssurance(int _n) throws Exception {
		// System.out.println("--- " + _n);
		for (Assurance a : assurances) {
			// System.out.println(a.debut +" " + a.fin);
			if (a.getDebut() <= _n && a.getFin() >= _n) {
				return a;
			}
		}

		throw new Exception("Assurance not found for period [" + _n + "]");
	}

	private Echeance getEcheance(int _n) throws Exception {
		// System.out.println("--- " + _n);
		for (Echeance e : echeance) {
			// System.out.println(e.getDebut() + " " + e.getFin());
			if (e.getDebut() <= _n && e.getFin() >= _n) {
				return e;
			}
		}
		throw new Exception("Echeance not found for period [" + _n + "]");
	}

	public void adjustLengthForTargetPayment(boolean _print) throws Exception {

		if (_print)
			System.out.print("[" + nom + "] Ajuste le nombre de menusalités ");

		while (true) {
			amortization();
			Mensualite last = mensualite.get(mensualite.size() - 1);
			if (_print)
				System.out.print("+");

			if (last.getCapitalRestantDu().intValue() > 0) {
				increaseLastSeries();
				getDerniereAssurance().increaseFrame();
			} else {
				break;
			}
		}

		while (true) {
			amortization();
			Mensualite last = mensualite.get(mensualite.size() - 1);
			if (_print)
				System.out.print("-");
			if (last.getCapitalRestantDu().intValue() < 0) {
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
		System.out.print("[" + nom + "] Ajuste les menusualités pour avoir des menusalités égales (scale:" + _scale + ") ");
		int count = 100;
		while (true) {
			amortization();
			Mensualite last = mensualite.get(mensualite.size() - 1);

			if (last.getCapitalRestantDu().intValue() > _threathold) {
				System.out.print("+");
				// Adjust mensualité
				increaseLastSerieAmount(_scale);
			} else if (last.getCapitalRestantDu().intValue() < -_threathold) {
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

	public void addAssurance(int _debut, int _fin, String _montant, String _taux) {
		assurances.add(new Assurance(_debut, _fin, _montant + "", _taux));
	}

	public List<Echeance> getEcheances() {
		return echeance;
	}

	public void setEcheances(List<Echeance> _echeance) {
		echeance = _echeance;
	}

	public BigDecimal getNominal() {
		return nominal;
	}

	public void setNominal(BigDecimal _nominal) {
		nominal = _nominal;
	}

	public Date getDebut() {
		return debut;
	}

	public void setDebut(Date _souscription) {
		debut = _souscription;
	}

	public BigDecimal getTaux() {
		return taux;
	}

	public void setTaux(BigDecimal _tauxNominal) {
		taux = _tauxNominal;
	}

	public void getMensualites(List<Mensualite> _menusalites) {
		mensualite = _menusalites;
	}

	public List<Mensualite> getMensualites() {
		return mensualite;
	}

	public List<Strategy> getStrategies() {
		return strategies;
	}

	public void setStrategies(List<Strategy> _strategies) {
		strategies = _strategies;
	}

	public Echeance getDerniereEcheance() {
		if (echeance.size() == 0) {
			return null;
		}
		return echeance.get(echeance.size() - 1);
	}

	public void setNom(String _name) {
		nom = _name;
	}

	public String getNom() {
		return nom;
	}

	public BigDecimal sommeInterets() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Mensualite p : mensualite) {
			if (p.getMontantInterets() != null)
				sum = sum.add(p.getMontantInterets());
		}
		return sum;
	}

	public Mensualite getDerniereMensualite() {
		if (mensualite.size() == 0) {
			return null;
		}
		return mensualite.get(mensualite.size() - 1);
	}

	private Assurance getDerniereAssurance() {
		if (assurances.size() == 0) {
			return null;
		}
		return assurances.get(assurances.size() - 1);
	}

	public List<Assurance> getAssurances() {
		return assurances;
	}

	public void printAmortissement() {
		System.out.println("[" + nom + "] Tableau d'amortissement : \n"
				+ String.format("terme%12s%10s%10s%10s%10s%10s", "date", "interets", "assur", "cp remb.", "cp rest.", "echeance"));

		for (Mensualite m : mensualite) {
			System.out.println(m.toString());
		}

	}

}
