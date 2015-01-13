package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class HighScoreScreen implements Screen {
	
	private final OrthographicCamera camera;
	private final StretchViewport viewport;
	
	BitmapFont font;
	BitmapFont normalFont;
	
	public HighScoreScreen() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
		camera.update();

		viewport = new StretchViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera);
		viewport.apply(true);
		
		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		normalFont = FontLoader.getInstance().getFont("normal_font", Config.TOAST_FONT_SIZE);
	}

	@Override
	public void show() {
		final HighscoreInputProcessor inputProcessor = new HighscoreInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		normalFont = FontLoader.getInstance().getFont("normal_font", Config.TOAST_FONT_SIZE);
		
		final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);
		
		prefs.flush();
	}

	@Override
	public void render(float delta) {
		final SpriteBatch batch = App.getSpriteBatch();		
			
		batch.setProjectionMatrix(camera.combined);
		
		font.draw(batch, "Highscores", 50, 50);
		
		String highscores = "User                Date                                  Score\n\n";
		
		for (final HighScore hs : HighScoreManager.getInstance().getTop10Scores()) {
			highscores += hs.getName()  + "                ";			
			highscores += hs.getDate()  + "                ";
			highscores += hs.getScore();
			highscores += "\n";
		}
		
		normalFont.drawMultiLine(batch, highscores, 50, 150);
		
	}

	@Override
	public void resize(int width, int height) { 
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
//		font.dispose();
//		smallFont.dispose();
	}

}
