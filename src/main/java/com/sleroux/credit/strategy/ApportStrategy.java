package com.sleroux.credit.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sleroux.credit.model.Apport;
import com.sleroux.credit.model.Pret;

public class ApportStrategy extends StrategyBase {

	@JsonProperty
	public BigDecimal	montant;

	@JsonProperty
	public int			terme;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) throws Exception {
		System.out.println("APPORT");
		
		Apport apport = new Apport();
		apport.setTerme(terme);
		apport.setMontant(montant);
		_pret.getApports().add(apport);
	}

}
