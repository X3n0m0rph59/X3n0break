package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
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
		
//		final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);
//		
//		prefs.flush();
	}

	@Override
	public void render(float delta) {
		final SpriteBatch batch = App.getSpriteBatch();		
			
		batch.setProjectionMatrix(camera.combined);
		
		font.draw(batch, "Highscores", 50, 50);
		
		normalFont.draw(batch, "Rank",    50, 150);
		normalFont.draw(batch, "User",   250, 150);
		normalFont.draw(batch, "Date",   700, 150);
		normalFont.draw(batch, "Score", 1400, 150);
		normalFont.draw(batch, "Level", 1750, 150);
			
		final int row_spacing = 50;
		int row = 0;
		for (final HighScore hs : HighScoreManager.getInstance().getTop15Scores()) {			
			// Highlight "current" highscore
			if (hs == HighScoreManager.getInstance().getCurrentHighScore()) {
				normalFont.draw(batch, ">",    5, 220 + row * row_spacing);
				normalFont.draw(batch, "<", 1890, 220 + row * row_spacing);
			}
											
			normalFont.draw(batch, String.format("%02d", row + 1),		   120, 220 + row * row_spacing);
			normalFont.draw(batch, hs.getName(),   					       250, 220 + row * row_spacing);			
			normalFont.draw(batch, hs.getDate(),  						   700, 220 + row * row_spacing);
			normalFont.draw(batch, String.format("%08d", hs.getScore()),  1400, 220 + row * row_spacing);
			normalFont.draw(batch, String.format("%02d", hs.getLevel()),  1820, 220 + row * row_spacing);
			
			row++;
		}
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
