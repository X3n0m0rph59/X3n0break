package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

enum ResourceType {
	BACKGROUND,
	FONT,
	LEVEL,
	MUSIC,
	SOUND,
	SPRITE
}

public class ResourceMapper {
	public static String getPath(String filename, ResourceType type) {
		final String path;
		
		if ((path = ResourceMapperDecisionCache.getInstance().getCachedPath(filename)) != null)
			return path;
		else {		
			final String set_path = "data/sets/" +  String.format("%02d", (GameState.getLevelSet() + 1)) + ".set/" + 
									resourceTypeToPath(type) + "/" + filename;
			
			final FileHandle fh = Gdx.files.internal(set_path);
			
			if (fh.exists()) {
	//			Logger.debug("Found '" + filename + "' in level set");
				
				ResourceMapperDecisionCache.getInstance().addDecisionToCache(filename, set_path);
				
				return set_path;
				
			} else {
				final String shared_path = "data/shared/" + resourceTypeToPath(type) + "/" + filename;
				
				final FileHandle fh2 = Gdx.files.internal(shared_path);
				
				if (fh2.exists()) {
	//				Logger.debug("Found '" + filename + "' in shared data");
					
					ResourceMapperDecisionCache.getInstance().addDecisionToCache(filename, shared_path);
				
					return shared_path;
				}
			}
			
			throw new RuntimeException("Resource '" + filename + "' unavailable!");
		}
	}
	
	public static void clearCache() {
		ResourceMapperDecisionCache.getInstance().clearCache();
	}

	private static String resourceTypeToPath(ResourceType type) {
		switch (type) {		
		case BACKGROUND:
			return "backgrounds";
			
		case FONT:
			return "fonts";
			
		case LEVEL:
			return "levels";
			
		case MUSIC:
			return "music";
			
		case SOUND:
			return "sounds";
			
		case SPRITE:
			return "sprites";
			
		default:
			throw new RuntimeException("Invalid ResourceType specified!");
		}
	}
}
