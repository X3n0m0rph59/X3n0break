package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public final class MusicStream {
	private final Music music;
	
	public MusicStream(String filename) {		
		music = Gdx.audio.newMusic(Gdx.files.internal(filename));		
	}
	
	public void play() {
		music.play();
		music.setLooping(true);
	}
	
	public void playAt(float pos) {				
		this.play();
		
		// TODO: Bug in libGDX, why is this needed ??
//		while (!music.isPlaying())
//			;
		
		music.setPosition(pos);
		
		Logger.debug("Music pos: " + pos);
	}
	
	public void stop() {
		if (music.isPlaying())
			music.stop();
	}
	
	public boolean isPlaying() {
		return music.isPlaying();
	}
	
	public float getPosition() {
		final float pos = music.getPosition();
		return pos;
	}

	public void dispose() {
		music.dispose();
	}	
}