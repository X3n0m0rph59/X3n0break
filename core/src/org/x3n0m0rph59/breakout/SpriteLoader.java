package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class SpriteLoader {
	private static final SpriteLoader instance = new SpriteLoader();
	private Map<String, Sprite> map = new HashMap<String, Sprite>();
	
	public SpriteLoader() {
		primeCache();
	}
	
	public static SpriteLoader getInstance() {
		return instance;
	}
	
	public Sprite getSprite(String filename, int tw, int th) {
		Sprite sprite;		
		if ((sprite = getSpriteFromCache(filename)) == null) {			
			Texture t = new Texture(Gdx.files.internal(filename));			
			t.setFilter(Texture.TextureFilter.Linear, 
						Texture.TextureFilter.Linear);			
			
			sprite = new Sprite(t, tw, th);			
			sprite.flip(false, true);
//			sprite.rotate90(true);
						
			addSpriteToCache(filename, sprite);
			
			return sprite;
		}
		else {
			return sprite;
		}
	}
	
	private void addSpriteToCache(String filename, Sprite sprite) {
		map.put(filename, sprite);
	}
	
	private Sprite getSpriteFromCache(String filename) {
		return map.get(filename);		
	}
	
	private void primeCache() {
				
	}
}
