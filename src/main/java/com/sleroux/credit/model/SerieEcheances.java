package com.sleroux.credit.model;

import java.math.BigDecimal;

public class SerieEcheances {

	private int			debut;
	private int			fin;
	private BigDecimal	montant;
	private BigDecimal	taux;

	public SerieEcheances(int _start, int _end, String _rate, String _amount) {
		debut = _start;
		fin = _end;
		taux = new BigDecimal(_rate);
		montant = new BigDecimal(_amount);
	}

	public SerieEcheances() {
		// Empty
	}

	public void augmenteMontant(BigDecimal _montant) {
		montant = montant.add(_montant);
	}

	public void augmenteDuree(int _nb) {
		fin = fin + _nb;
	}

	public int getDebut() {
		return debut;
	}

	public void setDebut(int _debut) {
		debut = _debut;
	}

	public int getFin() {
		return fin;
	}

	public void setFin(int _fin) {
		fin = _fin;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal _montant) {
		montant = _montant;
	}

	public BigDecimal getTaux() {
		return taux;
	}

	public void setTaux(BigDecimal _taux) {
		taux = _taux;
	}

}
