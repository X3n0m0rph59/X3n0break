package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class App extends ApplicationAdapter {
	private SpriteBatch batch;
	
	protected GameScreen gameScreen;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		super.create();
		
		batch = new SpriteBatch();		
				
		gameScreen = new GameScreen();
		gameScreen.setState(GameScreen.State.NEW_STAGE);		
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
				
		gameScreen.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		super.render();
		
		gameScreen.step();
						
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();		
		
		gameScreen.render(1.0f);
		
		batch.end();
	}	
	
	@Override
	public void pause() {						
		super.pause();	
	}
	
	@Override
	public void resume() {		
		super.resume();
		
//		System.exit(0);
	}

	public static SpriteBatch getSpriteBatch() {
		return ((App) Gdx.app.getApplicationListener()).batch;
	}
	
	public static GameScreen getGameScreen() {
		return ((App) Gdx.app.getApplicationListener()).gameScreen;
	}	
}
