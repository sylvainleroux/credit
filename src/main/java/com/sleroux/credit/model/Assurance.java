package com.sleroux.credit.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Assurance {

	private BigDecimal	capital;
	private BigDecimal	taux;
	private int			debut;
	private int			fin;

	public Assurance() {
		// Empty
	}

	public Assurance(int _debut, int _fin, String _nominal, String _taux) {
		capital = new BigDecimal(_nominal);
		taux = new BigDecimal(_taux);
		debut = _debut;
		fin = _fin;
	}

	public String toString() {
		return String.format("%3d - %3d : %6.2f \n", debut, fin, capital);
	}

	public BigDecimal getMensualite() {

		int nbPeriodes = fin - debut + 1;
		return capital.multiply(taux).multiply(new BigDecimal((int) nbPeriodes / 12))
				.divide(new BigDecimal(nbPeriodes), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal _principal) {
		capital = _principal;
	}

	public BigDecimal getTaux() {
		return taux;
	}

	public void setTaux(BigDecimal _rate) {
		taux = _rate;
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

	public void increaseFrame() {
		fin++;
	}

}
