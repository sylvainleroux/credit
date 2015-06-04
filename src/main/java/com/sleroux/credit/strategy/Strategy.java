package com.sleroux.credit.strategy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sleroux.credit.model.Loan;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "name")
@JsonSubTypes({ @Type(value = Split.class, name = "split"), @Type(value = Length.class, name = "length"),
		@Type(value = Smoothing.class, name = "smoothing") })
public abstract class Strategy {
	public Strategy() {
		// Empty
	}

	public abstract String getName();

	public abstract void run(Loan _loan, List<Loan> _previousLoans) throws Exception;
}
