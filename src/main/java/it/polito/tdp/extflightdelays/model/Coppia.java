package it.polito.tdp.extflightdelays.model;

public class Coppia {
	
	private int arrivo;
	private int distanza;
	
	public Coppia(int arrivo, int distanza) {
		this.arrivo = arrivo;
		this.distanza=distanza;
	}

	public int getArrivo() {
		return arrivo;
	}

	public void setArrivo(int arrivo) {
		this.arrivo = arrivo;
	}

	public int getDistanza() {
		return distanza;
	}

	public void setDistanza(int distanza) {
		this.distanza = distanza;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arrivo;
		result = prime * result + distanza;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coppia other = (Coppia) obj;
		if (arrivo != other.arrivo)
			return false;
		if (distanza != other.distanza)
			return false;
		return true;
	}

}