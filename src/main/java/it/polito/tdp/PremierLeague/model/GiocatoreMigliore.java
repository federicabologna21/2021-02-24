package it.polito.tdp.PremierLeague.model;

public class GiocatoreMigliore {

	Player best;
	double delta;
	
	public GiocatoreMigliore(Player best, double delta) {
		super();
		this.best = best;
		this.delta = delta;
	}

	public Player getBest() {
		return best;
	}

	public void setBest(Player best) {
		this.best = best;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	@Override
	public String toString() {
		return "GiocatoreMigliore [best=" + best + ", delta=" + delta + "]";
	}
	
}
