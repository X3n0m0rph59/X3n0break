package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class FontLoader {
	private static final FontLoader instance = new FontLoader();
	private Map<FontTuple, BitmapFont> map = new HashMap<FontTuple, BitmapFont>();
	
	public FontLoader() {
		primeCache();
	}
	
	public static FontLoader getInstance() {
		return instance;
	}
	
	public BitmapFont getFont(String name, int size) {
		BitmapFont font;
		if ((font = getFontFromCache(name, size)) == null) {
			BitmapFont f = new BitmapFont(Gdx.files.internal("data/fonts/" + name + ".fnt"), true);
			font = f;
			
			addFontToCache(name, size, font);
			
			return font;
		}
		else {
			return font;
		}
	}
	
	private void addFontToCache(String name, int size, BitmapFont font) {
		map.put(new FontTuple(name, size), font);
	}
	
	private BitmapFont getFontFromCache(String name, int size) {
		return map.get(new FontTuple(name, size));		
	}
	
	private void primeCache() {
				
	}

	public void dispose() {
		for (final BitmapFont f : map.values()) {
			f.dispose();
			map.remove(f);
		}
	}
}
