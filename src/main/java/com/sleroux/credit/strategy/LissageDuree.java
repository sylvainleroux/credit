package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleroux.credit.model.Pret;
import com.sleroux.credit.model.SerieEcheances;

public class LissageDuree extends StrategyBase {

	@JsonProperty(required = false)
	private int		dureeCible;

	@JsonProperty(required = false)
	private int		dureeCibleAnnees;

	private String	taux;

	private String	tauxAssurance;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) throws Exception {

		if (dureeCible == 0 && dureeCibleAnnees == 0) {
			throw new Exception("La durée doit être précisée, mois ou années");
		}

		if (dureeCible == 0) {
			dureeCible = dureeCibleAnnees * 12;
		}

		System.out.println("TARGET : " + dureeCible);

		BigDecimal capital = BigDecimal.ZERO;

		SerieEcheances derniereSerie = _pret.getDerniereSerieEcheances();
		if (derniereSerie == null) {
			capital = _pret.getNominal();
		} else {
			capital = _pret.getMensualites().get(_pret.getDerniereSerieEcheances().getFin()).getCapitalRestantDu();
		}

		int mensualiteCible = 1000;
		while (true) {

			ObjectMapper mapper = new ObjectMapper();
			Pret clone = mapper.readValue(mapper.writeValueAsBytes(_pret), Pret.class);

			clone.creeNouvellesSerieEcheances(mensualiteCible, taux, tauxAssurance, capital, _previousLoans);
			clone.adjustLengthForTargetPayment(true);
			clone.adjustConstantPayments();

			int nbMens = clone.getMensualites().size() - 1;

			int scale = dureeCible - nbMens;
			if (nbMens > dureeCible) {
				mensualiteCible += Math.abs(scale);
			} else if (nbMens < dureeCible) {
				mensualiteCible -= Math.abs(scale);
			} else {
				break;
			}

		}

		System.out.println("MENSUALITE : " + mensualiteCible);

		_pret.creeNouvellesSerieEcheances(mensualiteCible, taux, tauxAssurance, capital, _previousLoans);
		_pret.adjustLengthForTargetPayment(true);
		_pret.adjustConstantPayments();

	}

	public void setDureeCible(int _target) {
		dureeCible = _target;
	}

	public void setTaux(String _rate) {
		taux = _rate;
	}

	public void setTauxAssurance(String _insuranceRate) {
		tauxAssurance = _insuranceRate;
	}

}
