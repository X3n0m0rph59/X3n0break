package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class MenuInputProcessor implements InputProcessor {

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
//		final Camera camera = ((MenuScreen) ((App) Gdx.app.getApplicationListener()).getCurrentScreen()).getCamera();
//		
//		final float mX = camera.unproject(new Vector3(screenX, screenY, 0.0f)).x;
//		final float mY = camera.unproject(new Vector3(screenX, screenY, 0.0f)).y;
		
		final float mX = screenX;
		final float mY = screenY;
		
		final Rectangle hotRectNewGame = new Rectangle(0, 200, (float) Gdx.graphics.getWidth() / 2, 100);
		final Rectangle hotRectSettings = new Rectangle(0, 500, (float) Gdx.graphics.getWidth() / 2, 100);
		final Rectangle hotRectHighscores = new Rectangle(0, 700, (float) Gdx.graphics.getWidth() / 2, 100);
		
		final Rectangle hotRectResumeGame = new Rectangle((float) Gdx.graphics.getWidth() / 2, 200, (float) Gdx.graphics.getWidth(), 100);
		final Rectangle hotRectHelp = new Rectangle((float) Gdx.graphics.getWidth() / 2, 500, (float) Gdx.graphics.getWidth(), 100);
		final Rectangle hotRectExit = new Rectangle((float) Gdx.graphics.getWidth() / 2, 700, (float) Gdx.graphics.getWidth(), 100);
		
		if (hotRectNewGame.contains(new Vector2(mX, mY))) {
			ScreenManager.getInstance().showScreen(ScreenType.GAME);
			((App) Gdx.app.getApplicationListener()).getGameScreen().newGame();;
			return true;
		}
		
		if (hotRectSettings.contains(new Vector2(mX, mY))) {
//			ScreenManager.getInstance().showScreen(ScreenType.SETTINGS);
			return true;
		}
		
		if (hotRectHighscores.contains(new Vector2(mX, mY))) {
//			ScreenManager.getInstance().showScreen(ScreenType.HIGHSCORES);
			return true;
		}
		
		if (hotRectResumeGame.contains(new Vector2(mX, mY))) {
			ScreenManager.getInstance().showScreen(ScreenType.GAME);
			return true;
		}
		
		if (hotRectHelp.contains(new Vector2(mX, mY))) {
			ScreenManager.getInstance().showScreen(ScreenType.HELP);
			return true;
		}
		
		if (hotRectExit.contains(new Vector2(mX, mY))) {
			Logger.debug("Exit requested (menu)");
			
			Config.getInstance().setTerminationUserInitiated(true);
			Gdx.app.exit();
			
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
