package com.sleroux.credit.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mensualite {

	private final static SimpleDateFormat	formatter			= new SimpleDateFormat("dd/MM/yyyy");

	private int								terme;
	private Date							date;
	private BigDecimal						capitalRestantDu	= BigDecimal.ZERO;
	private BigDecimal						montantInterets		= BigDecimal.ZERO;
	private BigDecimal						montantAssurances	= BigDecimal.ZERO;
	private BigDecimal						capitalAmorti		= BigDecimal.ZERO;
	private BigDecimal						echeance			= BigDecimal.ZERO;

	public BigDecimal getEcheance() {
		return echeance;
	}

	public int getTerme() {
		return terme;
	}

	public void setTerme(int _terme) {
		terme = _terme;
	}

	public BigDecimal getMontantInterets() {
		return montantInterets;
	}

	public void setMontantInterets(BigDecimal _montantInterets) {
		montantInterets = _montantInterets;
	}

	public BigDecimal getMontantAssurances() {
		return montantAssurances;
	}

	public void setMontantAssurances(BigDecimal _montantAssurances) {
		montantAssurances = _montantAssurances;
	}

	public BigDecimal getCapitalAmorti() {
		return capitalAmorti;
	}

	public void setCapitalAmorti(BigDecimal _capitalAmorti) {
		capitalAmorti = _capitalAmorti;
	}

	public void setCapitalRestantDu(BigDecimal _capitalRestantDu) {
		capitalRestantDu = _capitalRestantDu;
	}

	public void setEcheance(BigDecimal _montant) {
		echeance = _montant;
	}

	public BigDecimal getCapitalRestantDu() {
		return capitalRestantDu;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date _date) {
		date = _date;
	}

	public String toString() {

		String dateFormatted = date == null ? "" : formatter.format(date);

		return String.format(" %4d%12s%10.2f%10.2f%10.2f%10.2f%10.2f", terme, dateFormatted, montantInterets, montantAssurances, capitalAmorti,
				capitalRestantDu, echeance);
	}

}
