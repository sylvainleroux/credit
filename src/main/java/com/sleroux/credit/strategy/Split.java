package com.sleroux.credit.strategy;

import java.util.Iterator;
import java.util.List;

import com.sleroux.credit.model.Pret;
import com.sleroux.credit.model.Echeance;

public class Split extends Strategy {

	int	splitAfter;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) {
		// Split series
		{
			boolean delete = false;
			Iterator<Echeance> i = _pret.getEcheances().iterator();
			while (i.hasNext()) {
				Echeance s = i.next();
				if (delete) {
					i.remove();
				} else if (s.getFin() >= splitAfter) {
					s.setFin(splitAfter);
					delete = true;
				}
			}
		}
		// Split assurances
		_pret.getAssurances().get(0).setFin(splitAfter);

	}

	public int getSplitAfter() {
		return splitAfter;
	}

	public void setSplitAfter(int _splitAfter) {
		splitAfter = _splitAfter;
	}

	@Override
	public String getName() {
		return "Split";
	}

}
