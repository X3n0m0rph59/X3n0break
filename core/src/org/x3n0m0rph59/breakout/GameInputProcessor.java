package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.H:
			((App) Gdx.app.getApplicationListener()).getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();
			
			return true;
			
		case Keys.B:										
			((App) Gdx.app.getApplicationListener()).getGameScreen().releaseSpaceBomb();
			
			return true;
			
		case Keys.PLUS:
			((App) Gdx.app.getApplicationListener()).getGameScreen().cheat(false);
			
			return true;
			
			
			// TODO: Make this work
//			if (lastKeyID == Keyboard.KEY_LEFT) {				
//				paddle.changeX(-50.0f);
//			}
//			
//			if (lastKeyID == Keyboard.KEY_RIGHT) {				
//				paddle.changeX(+50.0f);
//			}
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {						
		switch (pointer) {
		case 0:
			switch (button) {
			case 2:			
				final boolean detonated = ((App) Gdx.app.getApplicationListener()).getGameScreen().detonateSpaceBombs();
				
				if (!detonated)
					((App) Gdx.app.getApplicationListener()).getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();				
				
				return true;						
			}
			break;
			
		case 1:
			final boolean detonated = ((App) Gdx.app.getApplicationListener()).getGameScreen().detonateSpaceBombs();
			
			if (!detonated)
				((App) Gdx.app.getApplicationListener()).getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();
			
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		final Camera camera = ((GameScreen) ((App) Gdx.app.getApplicationListener()).getCurrentScreen()).getCamera();
		
		final float mX = camera.unproject(new Vector3(screenX, screenY, 0.0f)).x;
		final float mY = camera.unproject(new Vector3(screenX, screenY, 0.0f)).y;
		
		final Rectangle hotRectCheat = new Rectangle((int) (Config.getInstance()
				.getScreenWidth() - Config.SCOREBOARD_WIDTH + 25),
				(int) Config.WORLD_HEIGHT - (175 + 150*2), 150, 150);
		
		final Rectangle hotRectMainMenu = new Rectangle((int) (Config.getInstance()
				.getScreenWidth() - Config.SCOREBOARD_WIDTH + 25),
				(int) Config.WORLD_HEIGHT - (175 + 150), 150, 150);

		final Rectangle hotRectBomb = new Rectangle((int) (Config.getInstance()
				.getScreenWidth() - Config.SCOREBOARD_WIDTH + 25),
				(int) Config.WORLD_HEIGHT - 175, 350, 300);

		
		if (hotRectCheat.contains(new Vector2(mX, mY))) {
			((App) Gdx.app.getApplicationListener()).getGameScreen().cheat(true);
			return true;
		}
		
		if (hotRectMainMenu.contains(new Vector2(mX, mY))) {			
			ScreenManager.getInstance().showScreen(ScreenType.MENU);
			return true;
		}
		
		if (hotRectBomb.contains(new Vector2(mX, mY))) {
			((App) Gdx.app.getApplicationListener()).getGameScreen().releaseSpaceBomb();
			return true;
		}		
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {		
//		((App) Gdx.app.getApplicationListener()).getGameScreen().setPointerCoords(x, y);
		
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}