package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.sleroux.credit.model.Echeance;
import com.sleroux.credit.model.Pret;

public class Duree extends Strategy {

	private int		nbMois;

	private String	taux;

	private String	tauxAssurance;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) throws Exception {

		if (taux == null)
			throw new Exception("le taux ne doit pas être nul");

		System.out.printf("[" + _pret.getNom() + "] Augmente la mensualité jusqu'à obtenir la durée choisie");
		
		// Vérifie si les objects échéance et assurance existent
		if (_pret.getEcheances().size() == 0){
			_pret.setEcheances(1, nbMois, taux, "1");
			_pret.addAssurance(1, nbMois, _pret.getNominal() + "", tauxAssurance);	
		}else{
			BigDecimal capitalAssurance = _pret.getMensualites().get(_pret.getDerniereEcheance().getFin()).getCapitalRestantDu();
			int start = _pret.getDerniereEcheance().getFin() + 1;
			_pret.setEcheances(start, nbMois, taux, "1");
			_pret.addAssurance(start, nbMois, capitalAssurance +"", tauxAssurance);	
		}
		
		_pret.printSeries();
		_pret.printAssurances();

		_pret.amortization(false);
		while (_pret.getDerniereMensualite().getCapitalRestantDu().compareTo(BigDecimal.ZERO) > 0) {
			_pret.increaseLastSerieAmount("1");
			_pret.amortization(false);
			System.out.print(".");
		}
		System.out.print("\n");
		_pret.adjustConstantPayments();
		_pret.printSeries();
		
		_pret.amortization(true);

	}

	@Override
	public String getName() {
		return "Duree";
	}

	public int getNbMois() {
		return nbMois;
	}

	public void setNbMois(int _nbPeriods) {
		nbMois = _nbPeriods;
	}

	public String getTaux() {
		return taux;
	}

	public void setTaux(String _rate) {
		taux = _rate;
	}

	public String getTauxAssurance() {
		return tauxAssurance;
	}

	public void setTauxAssurance(String _insuranceRate) {
		tauxAssurance = _insuranceRate;
	}

}
