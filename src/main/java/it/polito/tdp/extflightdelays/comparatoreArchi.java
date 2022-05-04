package it.polito.tdp.extflightdelays;

import java.util.Comparator;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.extflightdelays.model.Airport;

public class comparatoreArchi implements Comparator<DefaultWeightedEdge> {

	Graph<Airport, DefaultWeightedEdge> grafo;
	
	public comparatoreArchi(Graph<Airport, DefaultWeightedEdge> g) {
		grafo=g;
	}
	
	@Override
	public int compare(DefaultWeightedEdge a1, DefaultWeightedEdge a2) {
		if(grafo.getEdgeSource(a1).getId()==grafo.getEdgeSource(a2).getId()) {
			return grafo.getEdgeTarget(a1).getId()-grafo.getEdgeTarget(a2).getId();
		} else {
			return grafo.getEdgeSource(a1).getId()-grafo.getEdgeSource(a2).getId();
		}
	}

}
