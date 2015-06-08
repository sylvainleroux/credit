package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.sleroux.credit.model.Pret;

public class Lissage extends Strategy {

	public int		mensualiteCible;

	public String	taux;

	public String	tauxAssurance;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) throws Exception {
		
		BigDecimal principal = _pret.getMensualites().get(_pret.getDerniereEcheance().getFin()).getCapitalRestantDu();
		
		_pret.printSeries();
		_pret.createNewSeries(mensualiteCible, taux, tauxAssurance, principal, _previousLoans);
		_pret.adjustLengthForTargetPayment(true);
		_pret.adjustConstantPayments();
		_pret.printSeries();

	}

	@Override
	public String getName() {
		return "Lissage with mensualiteCible : " + mensualiteCible;
	}

	public int getMensualiteCible() {
		return mensualiteCible;
	}

	public void setMensualiteCible(int _target) {
		mensualiteCible = _target;
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
