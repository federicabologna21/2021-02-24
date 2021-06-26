package it.polito.tdp.PremierLeague.model;

public class BestPlayer {

	Player p;
	double delta;
	
	
	public BestPlayer(Player p, double delta) {
		super();
		this.p = p;
		this.delta = delta;
	}
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public double getDelta() {
		return delta;
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}
	@Override
	public String toString() {
		return p.getPlayerID()+" - " + p.getName()+", delta efficienza=" + delta;
	}
	
	
	
}
