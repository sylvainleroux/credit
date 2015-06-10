package com.sleroux.credit.model;

import java.math.BigDecimal;

public class Apport {

	private BigDecimal	montant;
	private int			terme;

	public Apport() {
		// emtpy
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal _montant) {
		montant = _montant;
	}

	public int getTerme() {
		return terme;
	}

	public void setTerme(int _terme) {
		terme = _terme;
	}

}
