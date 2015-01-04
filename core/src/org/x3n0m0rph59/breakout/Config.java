package org.x3n0m0rph59.breakout;


public final class Config {
	// Global configuration parameters

	/** The name of the application */
	public static final String APP_NAME = "X3n0break";

	/** The version of the application */
	public static final String APP_VERSION = "0.0.1";

	public static final float WORLD_WIDTH = 1920.0f;

	public static final float WORLD_HEIGHT = 1080.0f;

	/** Target framerate */
	public static final int SYNC_FPS = 60;

	/** The width of the scoreboard on the right edge of the screen */
	public static final float SCOREBOARD_WIDTH = 250;

	/** Size of the font used to render messages on the screen */
	public static final int TOAST_FONT_SIZE = 44;

	/** How long a message is being displayed */
	public static final float TOAST_DELAY = 3.5f;
	
	/** Initial number of lives left */
	public static final int INITIAL_BALLS_LEFT = 5;
	
	/** Initial number of Space Bombs left */
	public static final int INITIAL_SPACEBOMBS_LEFT = 3;
	
	/** Default ball speed */
	public static final float BALL_SPEED = 8.0f;
	
	/** The maximum amount at which the ball speed is capped */
	public static final float BALL_SPEED_MAX = 16.0f;
	
	/** The minimum amount at which the ball speed is capped */
	public static final float BALL_SPEED_MIN = 2.0f;
	
	/** Ball radius */
	public static final float BALL_RADIUS = 15.0f;
	
	/** Get a free ball every nth points */
	public static final int BONUS_BALL_SCORE = 50000;
	
	/** Speed up the game by this factor when a speedup powerup is caught */
	public static final float POWERUP_SPEEDUP_FACTOR = 2.0f;
	
	/** Slow down the game by this factor when a speed powerup is caught */
	public static final float POWERUP_SLOWDOWN_FACTOR = 2.0f;
	
	/** Time in seconds before an active effect is expired */
	public static final float EFFECT_DURATION = 20.0f;
	
	public static final float EFFECT_GRACE_PERIOD = SYNC_FPS * 3;
	
	/** How many times a second an object flashes when in "effect grace period" */
	public static final float GRACE_PERIOD_BLINK_RATE = 0.25f;
	
	/**
	 * Lower end of the range of randomized scroll speeds for background sprites
	 */
	public static final float BACKGROUND_MIN_SPEED = 0.75f;
	
	/**
	 * Upper end of the range of randomized scroll speeds for background sprites
	 */
	public static final float BACKGROUND_MAX_SPEED = 1.5f;
	
	/**
	 * Density of background sprites (the lower the value the more backgrounds
	 * are generated per time unit)
	 */
	public static final int BACKGROUND_DENSITY = 350;
	
	/** Transparency level of background sprites */
	public static final float BACKGROUND_ALPHA = 0.25f;
	
	/** Force width of bricks */
	// public static final float BRICK_WIDTH = 33.5f;
	
	/** Height of bricks */
	public static final float BRICK_HEIGHT = 30.0f;
	
	/** Left and right free space between wall and bricks */
	public static final float BRICK_OFFSET_X = 50.0f;
	
	/** Top free space between wall and bricks */
	public static final float BRICK_OFFSET_Y = 50.0f;
	
	/** Horizontal spacing between bricks */
	public static final float BRICK_SPACING_X = 5.0f;
	
	/** Vertical spacing between bricks */
	public static final float BRICK_SPACING_Y = 5.0f;
	
	/** Movement speed of animated bricks */
	public static final float BRICK_MOVEMENT_SPEED = 0.5f;
	
	/** Rotation speed of animated bricks */
	public static final float BRICK_ROTATION_SPEED = 1.0f;
	
	/** Multiplier used to compute bricks with multiple movement specifiers */
	public static final float BRICK_MOVEMENT_MULTIPLIER = 2.0f;
	
	/** Specifies how many hits are needed to destroy a hard brick */
	public static final int HARD_BRICK_HITS_NEEDED = 3;
	
	/** Distance of the paddle from the bottom of the screen */
	public static final float PADDLE_BOTTOM_SPACING = 180.0f;
	
	/** Default width of the paddle */
	public static final float PADDLE_DEFAULT_WIDTH = 150.0f;
	
	/** Minimum width of the paddle */
	public static final float PADDLE_MIN_WIDTH = 50.0f;
	
	/** Maximum width of the paddle */
	public static final float PADDLE_MAX_WIDTH = 500.0f;
	
	/**
	 * Specifies how much the paddle grows when an extender bonus has been
	 * caught
	 */
	public static final float PADDLE_EXPANSION = 50.0f;
	
	/** The height of the paddle */
	public static final float PADDLE_HEIGHT = 35.0f;
	
	/** Horizontal offset of the "Engine" particle systems */
	public static final float PADDLE_ENGINE_OFFSET = 15f;
	
	/**
	 * Dampening factor for the collision between ball and paddle (Use 1.0f for
	 * a perfect elastic collision)
	 */
	public static final float PADDLE_DAMPENING_FACTOR = 0.85f;
	
	public static final int STAR_DENSITY = 3;
	public static final float STAR_WIDTH = 6.5f;
	public static final float STAR_HEIGHT = 6.5f;
	public static final float STAR_MIN_SPEED = 5.0f;
	public static final float STAR_MAX_SPEED = 20.0f;
	
	public static final float POWERUP_WIDTH = 50.0f;
	public static final float POWERUP_HEIGHT = 50.0f;
	public static final float POWERUP_SPEED = 10f;
	
	public static final float PROJECTILE_WIDTH = 7.0f;
	public static final float PROJECTILE_HEIGHT = 18.0f;
	public static final float PROJECTILE_SPEED = 17.0f;
	public static final int PROJECTILE_FIRE_RATE = 4;
	
	public static final float SPACEBOMB_WIDTH = 100.0f;
	public static final float SPACEBOMB_HEIGHT = 100.0f;
	public static final float SPACEBOMB_SPEED = 3.5f;
	public static final float SPACEBOMB_LURKING_SPEED = 1.5f;
	public static final float SPACEBOMB_EXPLOSION_DURATION = 3.5f;
	public static final float SPACEBOMB_EXPLOSION_RADIUS = 300.0f;
	public static final int SPACEBOMB_DENSITY = SYNC_FPS * 35;
	public static final int SPACEBOMB_COOLDOWN_TIME = SYNC_FPS * 25;
	
	public static final float GRAPPLING_HOOK_EXTEND_SPEED = 10.0f;
	public static final float GRAPPLING_HOOK_LOWER_SPEED = GRAPPLING_HOOK_EXTEND_SPEED;
	public static final float GRAPPLING_HOOK_LENGTH = 400.0f;

	public static final float BOTTOM_WALL_HEIGHT = 15.0f;
	public static final float BOTTOM_WALL_SEGMENT_WIDTH = 45.0f;
	public static final float BOTTOM_WALL_SEGMENT_HEIGHT = 25.0f;
	public static final float BOTTOM_WALL_SEGMENT_SPACING = 5.0f;

	public static final int VIBRATION_DURATION_BALL_VS_BRICK = 1;

	public static final int VIBRATION_DURATION_BRICK_DESTROYED = 1;

	public static final int VIBRATION_DURATION_BALL_VS_WALL = 15;

	public static final int VIBRATION_DURATION_BALL_VS_PADDLE = 25;

	public static final int VIBRATION_DURATION_BALL_LOST = 100;

	public static final int VIBRATION_DURATION_SPACEBOMB_EXPLOSION = 2000;

	private static final Config instance = new Config();

	private float speedFactor = 1.0f;

	private boolean noMusic;

	private boolean gameResumeable = false;

	private boolean userTerminated = false;
	
	
	public Config() {
		
	}
	
	public static Config getInstance() {
		return instance;
	}
	
	public void parseCommandLine(String[] args) {		
	}
	
	public float getScreenWidth() {
		return WORLD_WIDTH;
	}

	public float getScreenHeight() {
		return WORLD_HEIGHT;
	}
	
	public float getClientWidth() {
		return WORLD_WIDTH - SCOREBOARD_WIDTH;
	}

	public float getSpeedFactor() {				
		return speedFactor;
	}
	
	public void setSpeedFactor(float f) {				
		speedFactor = f;
		
		changeMusicPitch();
	}
	
	public void increaseGameSpeed(float factor) {
		speedFactor *= factor;
		
		changeMusicPitch();
	}
	
	public void decreaseGameSpeed(float factor) {
		speedFactor /= factor;
		
		changeMusicPitch();
	}
	
	private void changeMusicPitch() { 
		if (speedFactor == 1.0f)
			SoundLayer.getInstance().changeMusicPitch(MusicPitch.NORMAL);
		else if (speedFactor < 1.0f)			
			SoundLayer.getInstance().changeMusicPitch(MusicPitch.LOW);
		else if (speedFactor > 1.0f)			
			SoundLayer.getInstance().changeMusicPitch(MusicPitch.HIGH);
	}
	
	public boolean isMusicMuted() {		
		return noMusic;
	}

	public void setGameResumeable(boolean resumeable) {
		gameResumeable = resumeable;
	}
	
	public boolean isGameResumeable() {
		return gameResumeable;
	}
	
	public void setTerminationUserInitiated(boolean userInitiated) {
		userTerminated = userInitiated;
	}
	
	public boolean isTerminationUserInitiated() {
		return userTerminated;
	}
}
