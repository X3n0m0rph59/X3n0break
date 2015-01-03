package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.IntMap;

public class ScreenManager {	
	private static final ScreenManager instance = new ScreenManager();
	
	private IntMap<Screen> screens;

	private ScreenManager() {
		screens = new IntMap<Screen>();
	}
	
	public void showScreen(ScreenType screen) {		
		if (!screens.containsKey(screen.ordinal())) {
			screens.put(screen.ordinal(), screen.getScreenInstance());
		}
		
		((App) Gdx.app.getApplicationListener()).setScreen(screens.get(screen.ordinal()));
	}

	public Screen getScreen(ScreenType screen) {		
		if (!screens.containsKey(screen.ordinal())) {
			screens.put(screen.ordinal(), screen.getScreenInstance());
		}
		
		return screens.get(screen.ordinal());
	}
	
	public void overrideAndShowScreen(ScreenType type, Screen screen) { 
		screens.put(type.ordinal(), screen);		
		showScreen(type);
	}

	public void dispose(ScreenType screen) {
		Logger.debug("Disposing screen: " + screen);
		
		if (!screens.containsKey(screen.ordinal()))
			return;
		
		screens.remove(screen.ordinal()).dispose();
	}

	public void dispose() {
		Logger.debug("Disposing all screens");
		
		for (Screen screen : screens.values()) {
			screen.dispose();
		}
		
		screens.clear();
	}
	
	public static ScreenManager getInstance() {
		return instance;
	}
}