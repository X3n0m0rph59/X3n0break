package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;


public final class SpriteLoader {
	private static final SpriteLoader instance = new SpriteLoader();
	private final Map<String, Sprite> map = new HashMap<String, Sprite>();
	
	private final AssetManager assetManager;
	
	public SpriteLoader() {		
		assetManager = new AssetManager();
		Texture.setAssetManager(assetManager);
		
		primeCache();
	}
	
	public static SpriteLoader getInstance() {
		return instance;
	}
	
	public Sprite getSprite(String filename, int tw, int th) {
		Sprite sprite;		
		if ((sprite = getSpriteFromCache(filename)) == null) {
			final TextureParameter param = new TextureParameter();
			
			param.minFilter = TextureFilter.Linear;
			param.genMipMaps = true;
			
			assetManager.load(filename, Texture.class, param);			
			assetManager.finishLoading();
			
			final Texture t = assetManager.get(filename);
			
			sprite = new Sprite(t, tw, th);			
			sprite.flip(false, true);
//			sprite.rotate90(true);
						
			addSpriteToCache(filename, sprite);
			
			Logger.debug("# managed textures: " + Texture.getNumManagedTextures());
			
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

	public void dispose() {
		for (final Sprite s : map.values()) {
			s.getTexture().dispose();
//			map.remove(s);
		}
		
//		map.clear();
	}

	public void resumeAssets() {		
		Logger.debug("Reloading managed textures");
		Logger.debug("# managed textures: " + Texture.getNumManagedTextures());
		
		Texture.invalidateAllTextures(Gdx.app);
		
		assetManager.update();
		assetManager.finishLoading();
	}
}
