/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="distanzaMinima"
    private TextField distanzaMinima; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalizza"
    private Button btnAnalizza; // Value injected by FXMLLoader

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {
    	txtResult.clear();
    	int x=Integer.parseInt(distanzaMinima.getText());
    	Graph<Airport, DefaultWeightedEdge> grafo=new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    	grafo=model.creaGrafoDistanzaMin(x);
    	txtResult.setText("Numero vertici: "+grafo.vertexSet().size()+"\nNumero archi: "+
    				grafo.edgeSet().size()+"\n");
    	
    	Set<DefaultWeightedEdge> set=new HashSet<DefaultWeightedEdge>(grafo.edgeSet());
    	List<DefaultWeightedEdge> lista=new ArrayList<DefaultWeightedEdge>(set);
    	Collections.sort(lista, new comparatoreArchi(grafo));
    	
    	for(DefaultWeightedEdge arco : lista) {
    		String partenza=grafo.getEdgeSource(arco).getId()+"";
    		String arrivo=grafo.getEdgeTarget(arco).getId()+"";
    		String peso=grafo.getEdgeWeight(arco)+"";
    		txtResult.appendText(partenza+" "+arrivo+" "+peso+"\n");
    	}
    		
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert distanzaMinima != null : "fx:id=\"distanzaMinima\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
