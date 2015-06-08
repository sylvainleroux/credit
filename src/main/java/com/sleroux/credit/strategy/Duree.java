package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sleroux.credit.model.Pret;

public class Duree extends Strategy {

	@JsonProperty
	private int		nbMois;

	@JsonProperty
	private String	taux;

	@JsonProperty
	private String	tauxAssurance;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) throws Exception {

		if (taux == null)
			throw new Exception("le taux ne doit pas être nul");

		System.out.printf("[" + _pret.getNom() + "] Augmente la mensualité jusqu'à obtenir la durée choisie");

		// Vérifie si les objects échéance et assurance existent
		if (_pret.getEcheances().size() == 0) {
			_pret.addSerieEcheances(1, nbMois, taux, "1");
			_pret.addAssurance(1, nbMois, _pret.getNominal() + "", tauxAssurance);
		} else {
			BigDecimal capitalAssurance = _pret.getMensualites().get(_pret.getDerniereSerieEcheances().getFin()).getCapitalRestantDu();
			int start = _pret.getDerniereSerieEcheances().getFin() + 1;
			_pret.addSerieEcheances(start, nbMois, taux, "1");
			_pret.addAssurance(start, nbMois, capitalAssurance + "", tauxAssurance);
		}

		_pret.printEcheances();
		_pret.printAssurances();

		_pret.amortization();
		while (_pret.getDerniereMensualite().getCapitalRestantDu().compareTo(BigDecimal.ZERO) > 0) {
			_pret.augmenteMensualiteDerniereSerie(BigDecimal.ONE);
			_pret.amortization();
			System.out.print(".");
		}
		System.out.print("\n");
		_pret.adjustConstantPayments();
		_pret.printEcheances();

		_pret.amortization();

	}

	public void setNbMois(int _nbMois) {
		nbMois = _nbMois;
	}

	public void setTaux(String _taux) {
		taux = _taux;
	}

	public void setTauxAssurance(String _tauxAssurance) {
		tauxAssurance = _tauxAssurance;
	}

}
