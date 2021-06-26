package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private SimpleDirectedWeightedGraph <Player, DefaultWeightedEdge> grafo;
	private Map <Integer, Player> idMap;
	
	public Model() {
		dao = new PremierLeagueDAO();
		idMap = new HashMap<Integer, Player>();
		dao.listAllPlayers(idMap);
	}
	
	public List<Match> listAllMatches(){
		return dao.listAllMatches();
	}
	
	public void creaGrafo(Match m) {
		grafo = new SimpleDirectedWeightedGraph <> (DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		Graphs.addAllVertices(grafo, dao.getVertici(m, idMap));
		
		// aggiungo gli archi
		for (Adiacenza a : dao.getAdiacenze(m, idMap)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				
				//se peso > 0 --> e1>e2 --> da p1 a p2
				
				if (a.getPeso()>0) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), Math.abs(a.getPeso()));
					
				}
				else {
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
				}
			}
		}
	}

	public int getNumVertici() {
		if(this.grafo!=null) {
			return this.grafo.vertexSet().size();
		}
		
		return 0;
		
	}
	
	public int getNumArchi() {
		if(this.grafo!=null) {
			return this.grafo.edgeSet().size();
		}
		
		return 0;
		
	}
	
	public BestPlayer getBestPlayer(){
		Player best = null;
		double bestDelta = 0;
		
		
		for (Player p : this.grafo.vertexSet()) {
			
			double delta = 0;
			double pesoU = 0;
			double pesoE = 0;
			
			for (DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(p)) {
				double peso = this.grafo.getEdgeWeight(e);
				pesoU += peso;
			}
			
			for (DefaultWeightedEdge e: this.grafo.incomingEdgesOf(p)) {
				double peso = this.grafo.getEdgeWeight(e);
				pesoE += peso;
			}
			
			delta = pesoU -pesoE;
			
			if( delta > bestDelta) {
				bestDelta = delta;
				best = p;
				
			}
		}
		
		BestPlayer bp = new BestPlayer(best, bestDelta);
		return bp;
	}
}
