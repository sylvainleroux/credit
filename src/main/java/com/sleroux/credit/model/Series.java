package com.sleroux.credit.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Series {

	@JsonProperty("start")
	int					start;
	@JsonProperty("end")
	int					end;
	@JsonProperty("amount")
	BigDecimal			amount;
	@JsonProperty("rate")
	BigDecimal			rate;
	public BigDecimal	apport;

	public Series(int _start, int _end, String _rate, String _amount) {
		start = _start;
		end = _end;
		rate = new BigDecimal(_rate);
		amount = new BigDecimal(_amount);
	}

	public Series() {
	}

	public int getStart() {
		return start;
	}

	public void setStart(int _debut) {
		start = _debut;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int _fin) {
		end = _fin;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal _montant) {
		amount = _montant;
	}


}
