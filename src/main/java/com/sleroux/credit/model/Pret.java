package com.sleroux.credit.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sleroux.credit.strategy.StrategyBase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pret {

	private BigDecimal				nominal;
	private Date					debut;
	private BigDecimal				taux;
	private String					nom;

	private List<SerieEcheances>	seriesEcheances	= new ArrayList<>();
	private List<Mensualite>		mensualites		= new ArrayList<>();
	private List<StrategyBase>			strategyBases		= new ArrayList<>();
	private List<Assurance>			assurances		= new ArrayList<>();
	private List<Apport>			apports			= new ArrayList<>();

	public Pret() {
		// emtpy
	}

	public int getNbPeriodes() {
		int maxPeriode = 0;
		for (SerieEcheances e : seriesEcheances) {
			maxPeriode = Math.max(maxPeriode, e.getFin());
		}
		return maxPeriode;
	}

	public void runStrategies(List<Pret> _prets) throws Exception {
		for (StrategyBase s : strategyBases) {
			// Get only already computed loans
			List<Pret> previousLoans = new ArrayList<>();
			for (Pret l : _prets) {
				if (l.equals(this)) {
					break;
				}
				previousLoans.add(l);
			}
			s.run(this, previousLoans);
		}

	}

	public void addSerieEcheances(int _start, int _end, String _rate, String _amount) {
		seriesEcheances.add(new SerieEcheances(_start, _end, _rate, _amount));
	}

	public void augmenteDureeDerniereSerie() {
		getDerniereSerieEcheances().augmenteDuree(1);
	}

	public void reduitDureeDerniereSerie() {
		SerieEcheances derniereSerie = getDerniereSerieEcheances();
		if (derniereSerie.getDebut() == derniereSerie.getFin()) {
			seriesEcheances.remove(derniereSerie);
		} else {
			derniereSerie.augmenteDuree(-1);
		}

	}

	public void augmenteMensualiteDerniereSerie(BigDecimal _montant) {
		getDerniereSerieEcheances().augmenteMontant(_montant);
	}

	public void reduitMensualiteDerniereSerie(BigDecimal _montant) {
		getDerniereSerieEcheances().augmenteMontant(_montant.negate());
	}

	public void adjustLastPeriode(BigDecimal _restant) {
		SerieEcheances last = seriesEcheances.get(seriesEcheances.size() - 1);
		// reduce size by 1
		if (last.getDebut() == last.getFin()) {
			// Just update it
			last.setMontant(last.getMontant().add(_restant));
		} else {
			SerieEcheances e = new SerieEcheances();
			e.setFin(last.getFin());
			e.setDebut(last.getFin());
			e.setTaux(last.getTaux());
			e.setMontant(last.getMontant().add(_restant));
			last.setFin(last.getFin() - 1);
			seriesEcheances.add(e);
		}

	}

	public BigDecimal getCapitalRestantDu() {
		return mensualites.get(mensualites.size() - 1).getCapitalRestantDu();
	}

	public void printEcheances() {
		System.out.print("[" + nom + "] Echeances :\n");
		for (SerieEcheances e : seriesEcheances) {
			System.out.printf("%3d - %3d : %6.2f \n", e.getDebut(), e.getFin(), e.getMontant());
		}
	}

	public void printAssurances() {
		System.out.print("[" + nom + "] Assurances :\n");
		for (Assurance a : assurances) {
			System.out.println(a);
		}
	}

	public void printMensualites() {
		System.out.print("[" + nom + "] Menusalites :\n");
		for (Mensualite m : mensualites) {
			System.out.printf(m.toString());
		}
	}

	public void creeNouvellesSerieEcheances(int _mensualiteCible, String _rate, String _insuranceRate, BigDecimal _principal, List<Pret> _prets) {

		System.out.println("[" + nom + "] Création séries de paiement pour lissage");

		LinkedHashMap<Integer, BigDecimal> payments = new LinkedHashMap<>();

		for (Pret p : _prets) {
			for (SerieEcheances e : p.seriesEcheances) {
				for (int i = e.getDebut(); i <= e.getFin(); i++) {
					if (payments.get(i) == null) {
						payments.put(i, BigDecimal.ZERO);
					}
					payments.put(i, payments.get(i).add(e.getMontant()));
				}
			}
		}

		// Create new

		SerieEcheances current = null;
		BigDecimal previous = BigDecimal.ZERO;

		int lastDefinedPeriod = 0;
		if (getDerniereSerieEcheances() != null) {
			lastDefinedPeriod = getDerniereSerieEcheances().getFin();
		}
		int i;

		for (i = lastDefinedPeriod + 1; i < payments.size(); i++) {
			BigDecimal restant = new BigDecimal(_mensualiteCible).subtract(payments.get(i));
			if (!restant.equals(previous)) {
				if (current != null) {
					current.setFin(i - 1);
				}
				previous = restant;
				current = new SerieEcheances();
				current.setMontant(restant);
				current.setTaux(new BigDecimal(_rate));
				current.setDebut(i);
				seriesEcheances.add(current);
			}
		}

		current.setFin(i);

		addSerieEcheances(i + 1, i + 1, _rate, _mensualiteCible + "");

		// Create matching insurance
		Assurance assurance = new Assurance();
		assurance.setDebut(lastDefinedPeriod + 1);
		assurance.setFin(i + 1);
		assurance.setCapital(_principal);
		assurance.setTaux(new BigDecimal(_insuranceRate));
		assurances.add(assurance);

	}

	public void calcAmortization() throws Exception {

		Calendar c = Calendar.getInstance();
		if (debut != null) {
			c.setTime(debut);
		}

		// Reset collection
		mensualites = new ArrayList<Mensualite>();

		// Insert term #0
		Mensualite p0 = new Mensualite();
		p0.setCapitalRestantDu(nominal);
		mensualites.add(p0);

		for (int n = 1; n <= getNbPeriodes(); n++) {

			SerieEcheances e = getEcheance(n);
			Assurance a = getAssurance(n);
			Apport apport = getApport(n);

			// Calcul assurance
			BigDecimal mensualiteAssurance = a.getMensualite();
			BigDecimal tauxNominalProportionnel = e.getTaux().setScale(12, RoundingMode.HALF_UP).divide(new BigDecimal(12), RoundingMode.HALF_UP);

			BigDecimal capital = mensualites.get(n - 1).getCapitalRestantDu();

			Mensualite pn = new Mensualite();
			pn.setTerme(n);
			pn.setMontantAssurances(mensualiteAssurance);
			pn.setMontantInterets(capital.multiply(tauxNominalProportionnel).setScale(2, RoundingMode.HALF_UP));
			pn.setEcheance(e.getMontant().setScale(2, RoundingMode.HALF_UP));
			pn.setCapitalRestantDu(capital.add(pn.getMontantInterets()).subtract(pn.getEcheance().subtract(mensualiteAssurance)));
			pn.setCapitalAmorti(capital.subtract(pn.getCapitalRestantDu()));
			pn.setDate(c.getTime());

			if (apport != null) {
				pn.setCapitalRestantDu(pn.getCapitalRestantDu().subtract(apport.getMontant()));
				pn.setCapitalAmorti(pn.getCapitalAmorti().add(apport.getMontant()));
				pn.setApport(apport.getMontant());
			}

			mensualites.add(pn);

			c.add(Calendar.MONTH, 1);
		}

		getMensualites(mensualites);

	}

	private Apport getApport(int _n) {
		for (Apport a : apports) {
			if (a.getTerme() == _n) {
				return a;
			}
		}
		return null;
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

	private SerieEcheances getEcheance(int _n) throws Exception {
		// System.out.println("--- " + _n);
		for (SerieEcheances e : seriesEcheances) {
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
			calcAmortization();
			Mensualite last = mensualites.get(mensualites.size() - 1);
			if (_print)
				System.out.print("+");

			if (last.getCapitalRestantDu().intValue() > 0) {
				augmenteDureeDerniereSerie();
				getDerniereAssurance().increaseFrame();
			} else {
				break;
			}
		}

		while (true) {
			calcAmortization();
			Mensualite last = mensualites.get(mensualites.size() - 1);
			if (_print)
				System.out.print("-");
			if (last.getCapitalRestantDu().intValue() < 0) {
				reduitDureeDerniereSerie();
			} else {
				break;
			}
		}
		if (_print)
			System.out.print("\n");

	}

	public void adjustConstantPayments() throws Exception {

		adjustAmmout(50, new BigDecimal("1"));
		adjustAmmout(5, new BigDecimal("0.1"));
		adjustAmmout(0, new BigDecimal("0.01"));
		adjustLastPeriode(getCapitalRestantDu());

	}

	private void adjustAmmout(int _threathold, BigDecimal _scale) throws Exception {
		System.out.print("[" + nom + "] Ajuste les menusualités pour avoir des menusalités égales (scale:" + _scale + ") ");
		int count = 100;
		while (true) {
			calcAmortization();
			Mensualite last = mensualites.get(mensualites.size() - 1);
			// System.out.println(last.getCapitalRestantDu().intValue());
			if (last.getCapitalRestantDu().intValue() > _threathold) {
				System.out.print("+");
				// Adjust mensualité
				augmenteMensualiteDerniereSerie(_scale);
			} else if (last.getCapitalRestantDu().intValue() < -_threathold) {
				System.out.print("-");
				reduitMensualiteDerniereSerie(_scale.multiply(new BigDecimal("1.5")));
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

	public SerieEcheances getDerniereSerieEcheances() {
		if (seriesEcheances.size() == 0) {
			return null;
		}
		return seriesEcheances.get(seriesEcheances.size() - 1);
	}

	public BigDecimal sommeInterets() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Mensualite p : mensualites) {
			if (p.getMontantInterets() != null)
				sum = sum.add(p.getMontantInterets());
		}
		return sum;
	}

	public Mensualite getDerniereMensualite() {
		if (mensualites.size() == 0) {
			return null;
		}
		return mensualites.get(mensualites.size() - 1);
	}

	private Assurance getDerniereAssurance() {
		if (assurances.size() == 0) {
			return null;
		}
		return assurances.get(assurances.size() - 1);
	}

	public void printAmortissement() {
		System.out.println("[" + nom + "] Tableau d'amortissement : \n"
				+ String.format("terme%12s%10s%10s%10s%10s%10s", "date", "interets", "assur", "cp remb.", "cp rest.", "echeance"));

		for (Mensualite m : mensualites) {
			System.out.println(m.toString());
		}

	}

	public List<Assurance> getAssurances() {
		return assurances;
	}

	public List<SerieEcheances> getEcheances() {
		return seriesEcheances;
	}

	public void setEcheances(List<SerieEcheances> _serieEcheances) {
		seriesEcheances = _serieEcheances;
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
		mensualites = _menusalites;
	}

	public List<Mensualite> getMensualites() {
		return mensualites;
	}

	public List<StrategyBase> getStrategies() {
		return strategyBases;
	}

	public void setStrategies(List<StrategyBase> _strategyBases) {
		strategyBases = _strategyBases;
	}

	public void setNom(String _name) {
		nom = _name;
	}

	public String getNom() {
		return nom;
	}

	public List<Apport> getApports() {
		return apports;
	}

	public void setApports(List<Apport> _apports) {
		apports = _apports;
	}

	public List<SerieEcheances> getSeriesEcheances() {
		return seriesEcheances;
	}

	public void setSeriesEcheances(List<SerieEcheances> _seriesEcheances) {
		seriesEcheances = _seriesEcheances;
	}

	public void setMensualites(List<Mensualite> _mensualites) {
		mensualites = _mensualites;
	}

	public void setAssurances(List<Assurance> _assurances) {
		assurances = _assurances;
	}


}
