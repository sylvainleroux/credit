package com.sleroux.credit.model;

import java.math.BigDecimal;

public class Echeance {

	private int			debut;
	private int			fin;
	private BigDecimal	montant;
	private BigDecimal	taux;

	public Echeance(int _start, int _end, String _rate, String _amount) {
		debut = _start;
		fin = _end;
		taux = new BigDecimal(_rate);
		montant = new BigDecimal(_amount);
	}

	public Echeance() {
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
