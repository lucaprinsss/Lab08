package it.polito.tdp.extflightdelays.db;

import java.util.Comparator;

import it.polito.tdp.extflightdelays.model.Coppia;

public class ComparatoreCoppie implements Comparator<Coppia> {

	@Override
	public int compare(Coppia c1, Coppia c2) {
		return c1.getArrivo()-c2.getArrivo();
	}

}
