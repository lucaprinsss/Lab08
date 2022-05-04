package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	Graph<Airport, DefaultWeightedEdge> grafo;
	List<Airport> lista;
	Map<Integer, Airport> mappaAero;
	ExtFlightDelaysDAO dao;
	
	public Model() {
		dao=new ExtFlightDelaysDAO();		
	} 
	
	public Graph<Airport, DefaultWeightedEdge> creaGrafoDistanzaMin(int distMin) {
		
		grafo= new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		lista=new ArrayList<Airport>(dao.loadAllAirports());
		
		Graphs.addAllVertices(this.grafo, lista);
		
		mappaAero=new HashMap<Integer, Airport>();     //CREO ANCHE UNA MAPPA DI AEROPORTI
		for(Airport a : lista) {
			mappaAero.put(a.getId(),a);
		}
		
		for(Airport partenza:lista) {
			List<Coppia> connessoCon=new ArrayList<Coppia>(dao.getCollegamentiDistantiAlmeno(distMin, partenza));
			for(Coppia c:connessoCon) 
				Graphs.addEdge(grafo, partenza, mappaAero.get(c.getArrivo()), (double)c.getDistanza());
		}
		
		return grafo;
	}
	
}
