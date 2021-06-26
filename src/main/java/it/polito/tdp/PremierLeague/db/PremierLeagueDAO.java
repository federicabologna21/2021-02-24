package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
	//	List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("PlayerID"))) {
				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
			//	result.add(player);
				idMap.put(player.getPlayerID(), player);
			}
			}
			conn.close();
		//	return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
		//	return null;
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			
			Collections.sort(result, new Comparator<Match>() {

				@Override
				public int compare(Match o1, Match o2) {
					Integer i1 = o1.getMatchID();
					Integer i2 = o2.getMatchID();
					return i1.compareTo(i2);
				}
				
			});
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertici(Match m, Map<Integer, Player> idMap){
		String sql = "SELECT DISTINCT a.PlayerID AS id "
				+ "FROM actions a "
				+ "WHERE a.MatchID =?";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(idMap.get(res.getInt("id")));
				
				

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	public double getEfficienza(Player p, Match m) {
		String sql ="SELECT (a.TotalSuccessfulPassesAll+ a.Assists)/ a.TimePlayed AS efficienza "
				+ "FROM actions a "
				+ "WHERE a.MatchID =? AND a.PlayerID =?";
		
		double efficienza = 0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			st.setInt(2, p.getPlayerID());
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				efficienza = res.getDouble("efficienza");
				

			}
			conn.close();
			return efficienza;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return efficienza;
		}
	} */
	
	public List<Adiacenza> getAdiacenze(Match m, Map<Integer, Player> idMap){
		String sql = "SELECT a1.PlayerID AS p1, a2.PlayerID AS p2, (a1.TotalSuccessfulPassesAll+ a1.Assists)/ a1.TimePlayed AS e1,(a2.TotalSuccessfulPassesAll+ a2.Assists)/ a2.TimePlayed AS e2 "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.MatchID =? AND a1.MatchID = a2.MatchID "
				+ "AND a1.PlayerID > a2.PlayerID "
				+ "AND a1.TeamID <> a2.TeamID "
				+ "GROUP BY a1.PlayerID, a2.PlayerID";
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Player p1 = idMap.get(res.getInt("p1"));
				Player p2 = idMap.get(res.getInt("p2"));
				
				if (p1!=null && p2!=null) {
					double peso = (res.getDouble("e1")-res.getDouble("e2"));
					
					Adiacenza a = new Adiacenza (p1, p2, peso);
					result.add(a);
				}
				
				

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
}