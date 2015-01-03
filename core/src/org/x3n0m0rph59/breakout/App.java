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


public class App extends ApplicationAdapter {
	private SpriteBatch batch;
	
	protected Screen currentScreen;
	
	@Override
	public void create () {
		Gdx.graphics.setTitle(Config.APP_NAME);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Logger.debug("App: create()");
		
//		super.create();
		
		batch = new SpriteBatch();
		
		Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);
		
		if (!prefs.getBoolean("helpRead")) {
			ScreenManager.getInstance().showScreen(ScreenType.HELP);
		} else {
			ScreenManager.getInstance().showScreen(ScreenType.MENU);
		}
		
		
		// restore saved state?
		FileHandle handle = Gdx.files.local(Config.APP_NAME + ".sav");
		if (Gdx.files.isLocalStorageAvailable() && handle.exists()) {
			ScreenManager.getInstance().showScreen(ScreenType.GAME);
			
			try {
				GameScreen gameScreen = getGameScreen();
							
				try {
					gameScreen.loadGameState();
				}
				catch(IOException e) {
					ScreenManager.getInstance().showScreen(ScreenType.MENU);
				}
			}
			catch (RuntimeException e) {
				// do nothing
			}
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

		batch.begin();		
		
		currentScreen.render(Gdx.graphics.getDeltaTime());
		
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
			GameScreen gameScreen = getGameScreen();
			
			if (!Config.getInstance().isTerminationUserInitiated())
				gameScreen.saveGameState();
			else {
				Logger.debug("Deleting saved state (user requested exit)");
				
				FileHandle handle = Gdx.files.local(Config.APP_NAME + ".sav");				
				handle.delete();
			}
		}
		catch (RuntimeException e) {
			// do nothing
		}
		
		currentScreen.dispose();				
		
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
		if (currentScreen instanceof GameScreen) {
			return (GameScreen) ((App) Gdx.app.getApplicationListener()).currentScreen;
		}
		else throw new RuntimeException("Current Screen is not the GameScreen!");			
	}

	public void setScreen(Screen screen) {
//		if (currentScreen != null)
//			currentScreen.dispose();
		
		currentScreen = screen;
		currentScreen.show();
		currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public Screen getCurrentScreen() {
		return currentScreen;
	}	
}
