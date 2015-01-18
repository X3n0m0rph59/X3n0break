package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;


public final class MusicLoader {
	private static final MusicLoader instance = new MusicLoader();
	private final Map<String, MusicStream> map = new HashMap<String, MusicStream>();
		
	public MusicLoader() {					
		primeCache();
	}

	public static MusicLoader getInstance() {
		return instance;
	}
	
	public MusicStream getMusic(String filename) {
		MusicStream music;		
		if ((music = getMusicFromCache(filename)) == null) {
			music = new MusicStream(filename);			
									
			addMusicToCache(filename, music);
			
			return music;
		}
		else {
			return music;
		}
	}
	
	private void addMusicToCache(String filename, MusicStream music) {
		map.put(filename, music);
	}
	
	private MusicStream getMusicFromCache(String filename) {
		return map.get(filename);		
	}
	
	public void dispose() {
		for (final MusicStream m : map.values()) {
			m.dispose();
			map.remove(m);
		}
		
		map.clear();
	}
	
	private void primeCache() {
		
	}
}
