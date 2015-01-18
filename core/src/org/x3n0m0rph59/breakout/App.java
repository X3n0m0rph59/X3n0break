package org.x3n0m0rph59.breakout;

import java.io.IOException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public final class App extends ApplicationAdapter {
	private SpriteBatch batch;
	
	protected Screen currentScreen;
	
	@Override
	public void create () {
		Gdx.graphics.setTitle(Config.APP_NAME);
//		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.setLogLevel(Application.LOG_ERROR);
		
		Logger.debug("App: create()");
		
//		super.create();
		
		batch = new SpriteBatch();
		
		final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);
		
		Config.getInstance().setSoundMuted(prefs.getBoolean("soundMuted"));
		Config.getInstance().setMusicMuted(prefs.getBoolean("musicMuted"));

		final GameScreen.State stateBeforeQuit = GameScreen.State.values()[(int) prefs.getLong("gameStateBeforeQuit")];
		Config.getInstance().setGameStateBeforeQuit(stateBeforeQuit);

		if (!prefs.getBoolean("helpRead")) {
			ScreenManager.getInstance().showScreen(ScreenType.HELP);
		} else {
			ScreenManager.getInstance().showScreen(ScreenType.MENU);
		}
		
		// restore saved state?
		final boolean lastExitWasUserInitiated = prefs.getBoolean("userExitedApp");
		
		final FileHandle handle = Gdx.files.local(Config.APP_NAME + ".sav");
		
		if (Gdx.files.isLocalStorageAvailable() && handle.exists()) {
			try {
				final GameScreen gameScreen = getGameScreen();
							
				try {
					gameScreen.loadGameState();
					
					if (lastExitWasUserInitiated)
						ScreenManager.getInstance().showScreen(ScreenType.MENU);
					else
						ScreenManager.getInstance().showScreen(ScreenType.GAME);
				}
				catch(IOException e) {
					ScreenManager.getInstance().showScreen(ScreenType.MENU);
				}
			}
			catch (RuntimeException e) {
				// do nothing
			}
				
		} else {
			ScreenManager.getInstance().showScreen(ScreenType.MENU);
		}
	}
	
	@Override
	public void resize(int width, int height) {
		Logger.debug("App: resize()");
		
		super.resize(width, height);
				
		currentScreen.resize(width, height);
	}

	@Override
	public void render () {
//		Logger.debug("App: render()");
		
		super.render();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.enableBlending();
		
		batch.begin();
		
		currentScreen.render(Gdx.graphics.getRawDeltaTime() * Config.SYNC_FPS);
		
		batch.end();
	}	
	
	@Override
	public void pause() {
		Logger.debug("App: pause()");
		
		currentScreen.pause();
		
		super.pause();
	}
	
	@Override
	public void resume() {
		Logger.debug("App: resume()");
		
		SpriteLoader.getInstance().resumeAssets();		
		currentScreen.resume();
		
		super.resume();
	}
	
	@Override
	public void dispose() {
		Logger.debug("App: dispose()");
		
		// store saved state?
		try {
			final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);
			
			prefs.putLong("gameStateBeforeQuit", getGameScreen().getState().ordinal());
			prefs.putBoolean("userExitedApp", Config.getInstance().isTerminationUserInitiated());
			prefs.flush();
			
			final GameScreen gameScreen = getGameScreen();
			
			if (gameScreen != null) {
				gameScreen.saveGameState();					
			}
		}
		catch (RuntimeException e) {
			final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);
			
			prefs.putBoolean("userExitedApp", Config.getInstance().isTerminationUserInitiated());
			prefs.flush();
		}
		
		ScreenManager.getInstance().dispose();				
		
		SpriteLoader.getInstance().dispose();
		FontLoader.getInstance().dispose();
		SoundLayer.getInstance().dispose();
		
		batch.dispose();
		
//		super.dispose();
		
		Gdx.app.exit();
		System.exit(0);
	}

	public static SpriteBatch getSpriteBatch() {
		return ((App) Gdx.app.getApplicationListener()).batch;
	}
	
	public GameScreen getGameScreen() {		
		if (currentScreen instanceof GameScreen)
			return (GameScreen) ((App) Gdx.app.getApplicationListener()).currentScreen;
		else 
			return (GameScreen) ScreenManager.getInstance().getScreen(ScreenType.GAME);
	}

	public void setScreen(Screen screen) {
		if (currentScreen != screen) {
//			if (currentScreen != null)
//				currentScreen.dispose();
			
			currentScreen = screen;
			currentScreen.show();
			currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	public Screen getCurrentScreen() {
		return currentScreen;
	}	
}
