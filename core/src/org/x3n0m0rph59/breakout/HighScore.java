package org.x3n0m0rph59.breakout;

public final class HighScore {
	private String name;
	private String date;
	private int score;
	private int level;
	private int levelSet;
		
	public HighScore() {
		
	}
	
	public HighScore(String name, String date, int score, int level, int levelSet) {
		this.name = name;
		this.date = date;
		this.score = score;
		this.level = level;
		this.levelSet = levelSet;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevelSet() {
		return levelSet;
	}
	
	public void setLevelSet(int levelSet) {
		this.levelSet = levelSet;
	}

}
