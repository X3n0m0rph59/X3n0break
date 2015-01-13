package org.x3n0m0rph59.breakout;

public class HighScore {
	private String name;
	private String date;
	private int score;
		
	public HighScore() {
		
	}
	
	public HighScore(String name, String date, int score) {
		this.name = name;
		this.date = date;
		this.score = score;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}	
}
