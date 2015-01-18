package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public final class SoundSprite {
	private final Sound sound;
	
	public SoundSprite(String filename) {		
		sound = Gdx.audio.newSound(Gdx.files.internal(filename));
	}
	
	public void play(float pitch, float volume) {
		sound.play(volume, pitch, 0);
	}
	
	public void stop() {		
		sound.stop();
	}

	public void dispose() {
		sound.dispose();
	}
}