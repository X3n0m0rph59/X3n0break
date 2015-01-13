package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class SettingsInputProcessor implements InputProcessor {

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
		final Camera camera = ((SettingsScreen) ((App) Gdx.app.getApplicationListener()).getCurrentScreen()).getCamera();
		
		final float mX = camera.unproject(new Vector3(screenX, screenY, 0.0f)).x;
		final float mY = camera.unproject(new Vector3(screenX, screenY, 0.0f)).y;
		
		final Rectangle hotRectToggleSound = new Rectangle(0, 200, (float) Gdx.graphics.getWidth(), 200);
		final Rectangle hotRectToggleMusic = new Rectangle(0, 400, (float) Gdx.graphics.getWidth(), 200);
		final Rectangle hotRectOKBack = new Rectangle(1700, 900, (float) Gdx.graphics.getWidth(), 200);
		
		if (hotRectToggleSound.contains(new Vector2(mX, mY))) {
			Config.getInstance().setSoundMuted(!Config.getInstance().isSoundMuted());
			
			final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);			
			prefs.putBoolean("soundMuted", Config.getInstance().isSoundMuted());
			
			return true;
		}
		
		if (hotRectToggleMusic.contains(new Vector2(mX, mY))) {
			Config.getInstance().setMusicMuted(!Config.getInstance().isMusicMuted());
			
			final Preferences prefs = Gdx.app.getPreferences(Config.APP_NAME);			
			prefs.putBoolean("musicMuted", Config.getInstance().isMusicMuted());
			
			return true;
		}
		
		if (hotRectOKBack.contains(new Vector2(mX, mY))) {
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
