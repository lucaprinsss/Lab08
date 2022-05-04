package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Coppia;
import it.polito.tdp.extflightdelays.model.Flight;

@SuppressWarnings("unused")
public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	

	public List<Coppia> getCollegamentiDistantiAlmenoSemplice(int distMin, Airport aero) {
		String sql = "SELECT DESTINATION_AIRPORT_ID,ORIGIN_AIRPORT_ID, AVG(DISTANCE) AS DISTANCE "  //sto facendo la media sui viaggi di andata e di ritorno ma non tutti insieme
				+ "FROM flights "                                                                   //mi ritorna le tratte di andata e ritorno separate e le aggiungo allo stesso arco due volte 
				+ "WHERE DESTINATION_AIRPORT_ID=? OR ORIGIN_AIRPORT_ID=? "                    	    //(in teoria non me lo fa aggiungere il secodo perché esiste già il primo)
				+ "GROUP BY DESTINATION_AIRPORT_ID,ORIGIN_AIRPORT_ID ";                             //Vale questo solo perché non è un grafo orientato
		List<Coppia> result = new ArrayList<Coppia>();
		try { 
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, aero.getId());
			st.setInt(2, aero.getId());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if(rs.getInt("DISTANCE")>=distMin) {
					if(rs.getInt("ORIGIN_AIRPORT_ID")!=aero.getId() ) {
						Coppia c=new Coppia(rs.getInt("ORIGIN_AIRPORT_ID"), rs.getInt("DISTANCE"));
						result.add(c);
					} else {
						Coppia c=new Coppia(rs.getInt("DESTINATION_AIRPORT_ID"), rs.getInt("DISTANCE"));
						result.add(c);
					}
				}
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore col database");
		throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Coppia> getCollegamentiDistantiAlmeno(int distMin, Airport aero) {
		String sql = "SELECT DESTINATION_AIRPORT_ID,ORIGIN_AIRPORT_ID, DISTANCE "
				+ "FROM flights "                                                              
				+ "WHERE DESTINATION_AIRPORT_ID=? OR ORIGIN_AIRPORT_ID=? ";
		List<Coppia> result = new ArrayList<Coppia>();
		try { 
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, aero.getId());
			st.setInt(2, aero.getId());
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				if(rs.getInt("ORIGIN_AIRPORT_ID")==aero.getId() ) {
					Coppia c=new Coppia(rs.getInt("DESTINATION_AIRPORT_ID"), rs.getInt("DISTANCE"));
					result.add(c);
				} else {
					Coppia c=new Coppia(rs.getInt("ORIGIN_AIRPORT_ID"), rs.getInt("DISTANCE"));						
					result.add(c);
				}
			}
			conn.close();
			
			result.sort(new ComparatoreCoppie());
			
			List<Coppia> lista=new ArrayList<Coppia>();
			Map<Integer,Coppia> mappa=new HashMap<Integer, Coppia>();
			
			for(Coppia c:result) {       //SALVO NELLA MAPPA<ID, COPPIA> DOVE COPPIA CONTIENE, AL POSTO DELL'ARRIVO, UN CONATTORE E , AL POSTO DELLA DISTANZA, LA SOMMA DI TUTTE LE DISTANZE DI QUELLA TRATTA
				if(!mappa.containsKey(c.getArrivo())) {
					mappa.put(c.getArrivo(), new Coppia(1,c.getDistanza()));
				} else {
					Coppia vecchia=mappa.get(c.getArrivo());
					mappa.remove(c.getArrivo());
					mappa.put(c.getArrivo(), new Coppia(vecchia.getArrivo()+1,vecchia.getDistanza()+c.getDistanza()));
				}
			}
			
			for(Integer id:mappa.keySet()) {
				int distanza=mappa.get(id).getDistanza()/mappa.get(id).getArrivo();
				if(distanza>distMin)
					lista.add(new Coppia(id,distanza));
			}
			
			return lista;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore col database");
		throw new RuntimeException("Error Connection Database");
		}
	}
}
