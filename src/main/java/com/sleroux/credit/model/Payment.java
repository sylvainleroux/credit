package com.sleroux.credit.model;

import java.math.BigDecimal;

public class Payment {

	public int			terme;
	public BigDecimal	capitalRestantDu;
	public BigDecimal	montantInterets;
	public BigDecimal	montantAssurances;
	public BigDecimal	capitalAmorti;
	public BigDecimal	amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getCapitalRestantDu() {
		return capitalRestantDu;
	}

}
