package org.x3n0m0rph59.breakout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;


public final class HighScoreManager {
	private static final HighScoreManager instance = new HighScoreManager();
	
	private List<HighScore> highScores = new LinkedList<HighScore>();
	private HighScore currentHighScore = null;
	
	public static HighScoreManager getInstance() {
		return instance;
	}
	
	private HighScoreManager() {
		loadHighScores();
	}
	
	private void sort() {
		Collections.sort(highScores, new Comparator<HighScore>() {

			@Override
			public int compare(HighScore o1, HighScore o2) {
				return o2.getScore() - o1.getScore();
			} 
		});		
	}
	
	@SuppressWarnings("unchecked")
	private void loadHighScores() {		
		final FileHandle fh = Gdx.files.external("." + Config.APP_NAME + "/highscores.json");				
		final Json json = new Json();
				
		try {
			if (fh.exists()) {
				highScores.clear();
				highScores = (LinkedList<HighScore>) json.fromJson(LinkedList.class, fh);
				
			} else {
				highScores.clear();
			}
		} catch (Exception e) {
//			highScores.clear();
		}
						
		sort();
	}
	
	private void saveHighScores() {
		sort();
		
		final FileHandle fh = Gdx.files.external("." + Config.APP_NAME + "/highscores.json");		
		final Json json = new Json();
		
		json.toJson(highScores, fh);		
		
//		json.setWriter(fh.writer(false));
//		
//		json.writeArrayStart("HighScores");
//		
//		for (final HighScore hs : highScores) {
//			json.writeFields(hs);
//		}
//		
//		json.writeArrayEnd();		
	}

	public boolean isNewHighScore(int score) {		
		if (highScores.isEmpty())
			return true;
		
		int lowestScore = Integer.MAX_VALUE;
		
		for (HighScore hs : highScores) {
			if (hs.getScore() <= lowestScore)
				lowestScore = hs.getScore(); 
		}
		
		if (score >= lowestScore)
			return true;
		else
			return false;
	}
	
	public void addHighScore(String name, int score, int level) {
		final String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime());		
		HighScore hs = new HighScore(name, date, score, level);
		
		highScores.add(hs);
		setCurrentHighScore(hs);
		
		sort();
		
		saveHighScores();
	}
	
	public List<HighScore> getTop15Scores() {
		List<HighScore> top15Scores = new LinkedList<HighScore>();
		
		sort();
		
		for (int i = 0; i < 15; i++)
			if (i >= highScores.size())
				break;
			else
				top15Scores.add(highScores.get(i));
		
		return top15Scores;
	}

	public HighScore getCurrentHighScore() {
		return currentHighScore;
	}

	public void setCurrentHighScore(HighScore currentHighScore) {
		this.currentHighScore = currentHighScore;
	}
}