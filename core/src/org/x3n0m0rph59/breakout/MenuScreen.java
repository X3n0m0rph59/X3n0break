package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MenuScreen implements Screen {

	private OrthographicCamera camera;
	private StretchViewport viewport;

	BitmapFont font;
	BitmapFont smallFont;

	public MenuScreen() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
		camera.update();

		viewport = new StretchViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT,
				camera);
		viewport.apply(true);

		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		smallFont = FontLoader.getInstance().getFont("small_font",
				Config.TOAST_FONT_SIZE);
	}

	@Override
	public void show() {
		MenuInputProcessor inputProcessor = new MenuInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		smallFont = FontLoader.getInstance().getFont("small_font",
				Config.TOAST_FONT_SIZE);
	}

	@Override
	public void render(float delta) {
		SpriteBatch batch = App.getSpriteBatch();

		batch.setProjectionMatrix(camera.combined);

		font.draw(batch, "Main Menu", 50, 50);
		
		font.draw(batch, "NEW GAME", 150, 250);
		
		if (Config.getInstance().isGameResumeable())
			font.draw(batch, "RESUME GAME", 1300, 250);
			
		font.draw(batch, "SETTINGS", 150, 550);
		font.draw(batch, "HIGHSCORES", 150, 750);
		
		font.draw(batch, "HELP", 1300, 550);
		font.draw(batch, "EXIT", 1300, 750);

		smallFont.draw(batch, Config.APP_NAME + " " + Config.APP_VERSION, 1700, 1000);
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
		 font.dispose();
		 smallFont.dispose();
	}

	public Camera getCamera() {
		return camera;
	}
}
