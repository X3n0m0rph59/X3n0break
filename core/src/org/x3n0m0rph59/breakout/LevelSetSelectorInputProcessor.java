package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class LevelSetSelectorInputProcessor implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		final Camera camera = ((LevelSetSelectorScreen) ((App) Gdx.app.getApplicationListener()).getCurrentScreen()).getCamera();
		
		final float mX = camera.unproject(new Vector3(screenX, screenY, 0.0f)).x;
		final float mY = camera.unproject(new Vector3(screenX, screenY, 0.0f)).y;
		
		final Rectangle hotRectChangeLevelSet = new Rectangle(0, 200, (float) Gdx.graphics.getWidth(), 200);
		final Rectangle hotRectStartGame = new Rectangle(0, 600, (float) Gdx.graphics.getWidth(), 200);
		final Rectangle hotRectBack = new Rectangle(1700, 900, (float) Gdx.graphics.getWidth(), 200);
		
		if (hotRectChangeLevelSet.contains(new Vector2(mX, mY))) {
			final int totalLevelSets = Integer.parseInt(LevelLoader.getGlobalMetaData().get("Total Level Sets"));
			
			final int levelSet;
			if (GameState.getLevelSet() + 1 >= totalLevelSets)
				levelSet = 0;
			else
				levelSet = GameState.getLevelSet() + 1;
			
			GameState.setLevelSet(levelSet);
			
			((LevelSetSelectorScreen) ((App) Gdx.app.getApplicationListener()).getCurrentScreen()).updateState();
			
			return true;
		}
		
		if (hotRectStartGame.contains(new Vector2(mX, mY))) {			
			ScreenManager.getInstance().showScreen(ScreenType.GAME);			
			((App) Gdx.app.getApplicationListener()).getGameScreen().newGame();
			
			return true;
		}
		
		if (hotRectBack.contains(new Vector2(mX, mY))) {
			ScreenManager.getInstance().showScreen(ScreenType.MENU);
			return true;
		}		
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
