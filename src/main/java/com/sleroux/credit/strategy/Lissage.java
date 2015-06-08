package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.sleroux.credit.model.Pret;
import com.sleroux.credit.model.SerieEcheances;

public class Lissage extends Strategy {

	private int		mensualiteCible;

	private String	taux;

	private String	tauxAssurance;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) throws Exception {

		BigDecimal capital = BigDecimal.ZERO;

		SerieEcheances derniereSerie = _pret.getDerniereSerieEcheances();
		if (derniereSerie == null) {
			capital = _pret.getNominal();
		} else {
			capital = _pret.getMensualites().get(_pret.getDerniereSerieEcheances().getFin()).getCapitalRestantDu();
		}

		_pret.printEcheances();
		_pret.creeNouvellesSerieEcheances(mensualiteCible, taux, tauxAssurance, capital, _previousLoans);
		_pret.adjustLengthForTargetPayment(true);
		_pret.adjustConstantPayments();
		_pret.printEcheances();

	}

	public void setMensualiteCible(int _target) {
		mensualiteCible = _target;
	}

	public void setTaux(String _rate) {
		taux = _rate;
	}

	public void setTauxAssurance(String _insuranceRate) {
		tauxAssurance = _insuranceRate;
	}

}
