package com.sleroux.credit.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Insurance {

	public Insurance() {
		// Empty
	}
	
	@JsonProperty("principal")
	public BigDecimal	principal;
	
	@JsonProperty("rate")
	public BigDecimal	rate;

	@JsonProperty("start")
	int					start;
	
	@JsonProperty("end")
	int					end;

	public Insurance(int _start, int _end, String _principal, String _rate) {
		principal = new BigDecimal(_principal);
		rate = new BigDecimal(_rate);
		start = _start;
		end = _end;
	}

	public BigDecimal getMensualite() {
		
		int nbPeriodes = end - start +1;
		return principal.multiply(rate).multiply(new BigDecimal((int) nbPeriodes / 12)).divide(new BigDecimal(nbPeriodes), RoundingMode.HALF_UP)
				.setScale(2, RoundingMode.HALF_UP);

	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal _principal) {
		principal = _principal;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal _rate) {
		rate = _rate;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int _start) {
		start = _start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int _end) {
		end = _end;
	}

	public void increaseFrame() {
		end++;
	}
	
	
	
}
