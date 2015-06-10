package com.sleroux.credit.strategy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sleroux.credit.model.Pret;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "nom")
@JsonSubTypes({ 
	@Type(value = SplitStrategy.class, name = "split"), 
	@Type(value = DureeStrategy.class, name = "length"),
	@Type(value = LissageStrategy.class, name = "lissage-mensualite"),
	@Type(value = LissageDuree.class, name = "lissage-duree"),
	@Type(value = ApportStrategy.class, name = "apport") 
})
public abstract class StrategyBase {
	public StrategyBase() {
		// Empty
	}

	public abstract void run(Pret _pret, List<Pret> _previousLoans) throws Exception;
}
