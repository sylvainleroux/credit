package com.sleroux.credit.strategy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sleroux.credit.model.Pret;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "nom")
@JsonSubTypes({ @Type(value = Split.class, name = "split"), @Type(value = Duree.class, name = "length"),
		@Type(value = Lissage.class, name = "smoothing") })
public abstract class Strategy {
	public Strategy() {
		// Empty
	}

	public abstract void run(Pret _pret, List<Pret> _previousLoans) throws Exception;
}
