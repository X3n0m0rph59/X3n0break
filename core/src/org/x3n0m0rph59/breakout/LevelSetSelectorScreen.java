package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class LevelSetSelectorScreen implements Screen {
	
	private final OrthographicCamera camera;
	private final StretchViewport viewport;
	
	BitmapFont font;
	BitmapFont normalFont;
	
	private int levelSet;
	private int totalLevelSets;
	private String description;
	
	public LevelSetSelectorScreen() {
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
		final LevelSetSelectorInputProcessor inputProcessor = new LevelSetSelectorInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		normalFont = FontLoader.getInstance().getFont("normal_font", Config.TOAST_FONT_SIZE);				
		
		updateState();
	}

	@Override
	public void render(float delta) {
		final SpriteBatch batch = App.getSpriteBatch();		
			
		batch.setProjectionMatrix(camera.combined);
		
		font.draw(batch, "Select Level Set", 50, 50);
						
		font.drawMultiLine(batch, "Selected: " + (levelSet + 1) + "/" + totalLevelSets , 50, 250);						
		normalFont.drawMultiLine(batch, "Description: " + description, 50, 350);
		
		font.drawMultiLine(batch, "Start Game!", 800, 650);
		
		normalFont.drawMultiLine(batch, "Back", 1740, 990);
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

	public Camera getCamera() {
		return camera;
	}
	
	public void updateState() {
		levelSet = GameState.getLevelSet();
		totalLevelSets = Integer.parseInt(LevelLoader.getGlobalMetaData().get("Total Level Sets"));
		description = LevelLoader.getLevelSetMetaData().get("Description");
	}

}
