package com.sleroux.credit.strategy;

import java.util.Iterator;
import java.util.List;

import com.sleroux.credit.model.Pret;
import com.sleroux.credit.model.SerieEcheances;

public class SplitStrategy extends StrategyBase {

	int	splitAfter;

	@Override
	public void run(Pret _pret, List<Pret> _previousLoans) {
		// SplitStrategy series
		{
			boolean delete = false;
			Iterator<SerieEcheances> i = _pret.getEcheances().iterator();
			while (i.hasNext()) {
				SerieEcheances s = i.next();
				if (delete) {
					i.remove();
				} else if (s.getFin() >= splitAfter) {
					s.setFin(splitAfter);
					delete = true;
				}
			}
		}
		// SplitStrategy assurances
		//_pret.getAssurances().get(0).setFin(splitAfter);

	}

	public int getSplitAfter() {
		return splitAfter;
	}

	public void setSplitAfter(int _splitAfter) {
		splitAfter = _splitAfter;
	}

}
