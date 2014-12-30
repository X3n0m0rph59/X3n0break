package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.H:
			App.getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();
			
			return true;
			
		case Keys.B:										
			App.getGameScreen().releaseSpaceBomb();
			
			return true;
			
		case Keys.PLUS:
			App.getGameScreen().cheat(false);
			
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
	public boolean touchDown(int x, int y, int pointer, int button) {		
		switch (pointer) {
		case 0:
			switch (button) {
			case 2:
				App.getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();
				
				return true;
			
			case 3:
				App.getGameScreen().detonateSpaceBombs();
				
				return true;
			}
			break;
			
		case 1:
			App.getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();
			
			break;
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		final Rectangle hotRectCheat = new Rectangle((int) (Config.getInstance()
				.getScreenWidth() - Config.SCOREBOARD_WIDTH + 25),
				(int) Config.WORLD_HEIGHT - (175 + 150*2), 150, 150);
		
//		final Rectangle hotRectHook = new Rectangle((int) (Config.getInstance()
//				.getScreenWidth() - Config.SCOREBOARD_WIDTH + 25),
//				(int) Config.WORLD_HEIGHT - (175 + 150), 150, 150);

		final Rectangle hotRectBomb = new Rectangle((int) (Config.getInstance()
				.getScreenWidth() - Config.SCOREBOARD_WIDTH + 25),
				(int) Config.WORLD_HEIGHT - 175, 350, 300);

		if (hotRectCheat.contains(new Vector2(x, y))) {
			App.getGameScreen().cheat(true);

			return true;
		}
		
//		if (hotRectHook.contains(new Vector2(x, y))) {			
//			App.getGameScreen().getPaddle().getGrapplingHook().toggleSwitch();
//
//			return true;
//		}
		
		if (hotRectBomb.contains(new Vector2(x, y))) {
			App.getGameScreen().releaseSpaceBomb();

			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {		
//		App.getGameScreen().setPointerCoords(x, y);
		
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}