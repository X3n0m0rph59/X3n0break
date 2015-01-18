package org.x3n0m0rph59.breakout;

import java.util.HashMap;
import java.util.Map;

public class ResourceMapperDecisionCache {
	private static ResourceMapperDecisionCache instance = new ResourceMapperDecisionCache();
	
	private Map<String,String> cacheMap = new HashMap<String,String>();
	
	public static ResourceMapperDecisionCache getInstance() {
		return instance;
	}
	
	private ResourceMapperDecisionCache() {
		
	}

	public String getCachedPath(String filename) {		
		if (cacheMap.containsKey(filename))
			return cacheMap.get(filename);
		else
			return null;
	}
	
	public void addDecisionToCache(String filename, String path) {
		cacheMap.put(filename, path);
	}
	
	public void clearCache() {
		cacheMap.clear();
	}
}
