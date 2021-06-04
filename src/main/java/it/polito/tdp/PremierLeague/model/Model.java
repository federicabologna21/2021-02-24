package it.polito.tdp.PremierLeague.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<Integer, Player>();
		this.dao.listAllPlayers(idMap);
		
	}
	
	public void creaGrafo(Match m) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo vertici
		Graphs.addAllVertices(grafo, dao.getVertici(m, idMap));
		
		// aggiungo archi
		for (Adiacenza a: dao.getAdiacenze(m, idMap)) {
			if (grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
				if(a.getPeso()>= 0) {
					// p1 migliore di p2
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
					
				} else {
					// p2 migliore di p1
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
				}
			}
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public Graph<Player, DefaultWeightedEdge> getGrafo(){
		return this.grafo;
	}
	
	
	
	
	public List<Match> listAllMatches(){
		List<Match>matches = dao.listAllMatches();
		Collections.sort(matches, new Comparator<Match>() {

			@Override
			public int compare(Match o1, Match o2) {
				return o1.getMatchID().compareTo(o2.getMatchID());
			}
	
	
		});
		return matches;
	}
	
	public GiocatoreMigliore getMigliore() {
		if(grafo == null) {
			return null;
		}
		Player best = null;
		Double maxDelta = (double) Integer.MIN_VALUE;
		
		for(Player p: this.grafo.vertexSet()) {
			
			// calcolo somma pesi archi uscenti
			double pesoUscente = 0.0;
			for (DefaultWeightedEdge edge: this.grafo.outgoingEdgesOf(p)) {
				pesoUscente += this.grafo.getEdgeWeight(edge);
			}
			
			// calcolo somma pesi archi entranti
			double pesoEntrante = 0.0;
			for (DefaultWeightedEdge edge: this.grafo.incomingEdgesOf(p)) {
				pesoEntrante += this.grafo.getEdgeWeight(edge);
			}
			
			double delta = pesoUscente - pesoEntrante;
			if(delta>maxDelta) {
				maxDelta = delta;
				best = p;
			}
		}
		return new GiocatoreMigliore (best, maxDelta);
	}
}
