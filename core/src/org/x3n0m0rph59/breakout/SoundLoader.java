package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;


public final class SoundLoader {
	private static final SoundLoader instance = new SoundLoader();
	private final Map<String, SoundSprite> map = new HashMap<String, SoundSprite>();
		
	public SoundLoader() {					
		primeCache();
	}

	public static SoundLoader getInstance() {
		return instance;
	}
	
	public SoundSprite getSound(String filename) {
		SoundSprite sound;		
		if ((sound = getSoundFromCache(filename)) == null) {
			sound = new SoundSprite(filename);			
									
			addSoundToCache(filename, sound);
			
			return sound;
		}
		else {
			return sound;
		}
	}
	
	private void addSoundToCache(String filename, SoundSprite sound) {
		map.put(filename, sound);
	}
	
	private SoundSprite getSoundFromCache(String filename) {
		return map.get(filename);		
	}
	
	public void dispose() {
		for (final SoundSprite s : map.values()) {
			s.dispose();
			map.remove(s);
		}
		
		map.clear();
	}
	
	private void primeCache() {
		// TODO Auto-generated method stub
		
	}
}
