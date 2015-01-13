package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class HelpScreen implements Screen {
	
	private final OrthographicCamera camera;
	private final StretchViewport viewport;
	
	BitmapFont font;
	BitmapFont normalFont;
	
	public HelpScreen() {
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
		final HelpInputProcessor inputProcessor = new HelpInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		normalFont = FontLoader.getInstance().getFont("normal_font", Config.TOAST_FONT_SIZE);
		
		final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);		
		prefs.putBoolean("helpRead", true);
		prefs.flush();
	}

	@Override
	public void render(float delta) {
		final SpriteBatch batch = App.getSpriteBatch();		
			
		batch.setProjectionMatrix(camera.combined);
		
		font.draw(batch, "Welcome to " + Config.APP_NAME + " " + Config.APP_VERSION, 50, 50);
		
		normalFont.drawMultiLine(batch, "NOTE:\nThis is an early access version of the game.\n" + 
							  "It may still contain some bugs!", 50, 150);
		
		normalFont.drawMultiLine(batch, "Usage:\nSwipe with one finger to move the paddle.\n\n" +
							  "Tap with two fingers to toggle the grappling hook.\n\n" +
							  "Press Main Menu in the lower right corner of the screen\n" + 
							  "to go to the main menu.\n\n"+
							  "Tap PRESS HERE field in the lower right corner of\n" + 
							  "the screen to release a space bomb\n\nTAP now to CONTINUE", 50, 350);
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
