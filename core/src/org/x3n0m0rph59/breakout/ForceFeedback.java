package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;

public class ForceFeedback {
	public static void ballLost() {
		vibrate(Config.VIBRATION_DURATION_BALL_LOST);
	}
	
	public static void wallHit() {
		vibrate(Config.VIBRATION_DURATION_BALL_VS_WALL);
	}
	
	public static void brickHit(Brick.Type type) {
		if (type == Brick.Type.HARD)
			vibrate(Config.VIBRATION_DURATION_BALL_VS_BRICK);
	}
	
	public static void brickDestroyed(Brick.Type type) {
		vibrate(Config.VIBRATION_DURATION_BRICK_DESTROYED);
	}
	
	public static void paddleHit() {
		vibrate(Config.VIBRATION_DURATION_BALL_VS_PADDLE);
	}
	
	public static void spaceBombExplosion() {
		vibrate(Config.VIBRATION_DURATION_SPACEBOMB_EXPLOSION);
	}
	
	public static void vibrate(int milliseconds) {
		Gdx.input.vibrate(milliseconds);
	}
}
