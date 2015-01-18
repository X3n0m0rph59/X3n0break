package org.x3n0m0rph59.breakout;

import org.x3n0m0rph59.breakout.SoundLayer;
//import org.x3n0m0rph59.breakout.SpaceBomb.Type;






import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameScreen implements Screen, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1326363941962583508L;

	public enum State {LOADING, NEW_STAGE, WAITING_FOR_BALL, RUNNING, STAGE_CLEARED, 
					   RESTART, PAUSED, GAME_OVER, LEVEL_SET_COMPLETED, TERMINATED};
	
	private enum ParticleEffect {BRICK_EXPLOSION, BALL_LOST};
	
	private transient OrthographicCamera camera;
	private transient StretchViewport viewport;
	  
	private State state = State.LOADING;
	
	private transient ScoreBoard scoreBoard;
	private final BottomWall bottomWall = new BottomWall();
	
	private HashMap<String, String> levelMetadata;	
	
	private final Paddle paddle = new Paddle();
	
	private final List<Ball> balls = new ArrayList<Ball>();
	private /*final*/ List<Brick> bricks = new ArrayList<Brick>();
	private final List<Powerup> powerups = new ArrayList<Powerup>();
	
	private final ObjectPool<Star> starsPool = new ObjectPool<Star>(Star.class);	
	private final List<Star> stars = new ArrayList<Star>();
	
	private final ObjectPool<Projectile> projectilePool = new ObjectPool<Projectile>(Projectile.class);
	private final List<Projectile> projectiles = new ArrayList<Projectile>();
	
	private final List<ParticleSystem> particleEffects = new ArrayList<ParticleSystem>();
	private final List<Background> backgrounds = new ArrayList<Background>();
	private final List<SpaceBomb> spaceBombs = new ArrayList<SpaceBomb>();
		
	private transient BitmapFont font;
	
	private int frameCounter = 0;
	
	private int spaceBombCoolDownTime = 0;
	private int cheatTouchCtr = 0;
	
	private Brick lastHitBrick;
	
	
	public GameScreen() {
		final GameInputProcessor inputProcessor = new GameInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		initializeTransients();
		
		initLevel(0);
		
		setState(GameScreen.State.NEW_STAGE);
	}
	
	public void initializeTransients() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
		camera.update();

		viewport = new StretchViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera);
		viewport.apply(true);		
		
		font = FontLoader.getInstance().getFont("font", Config.TOAST_FONT_SIZE);
		
		scoreBoard = new ScoreBoard();
		scoreBoard.updateState();
	}
	
	public void initLevel(int level) {				
		// check if the new level is in valid range
		final int totalLevelsInSet = Integer.parseInt(LevelLoader.getLevelSetMetaData().get("Total Levels"));
		
		if (level >= totalLevelsInSet) {
			setState(State.LEVEL_SET_COMPLETED);			
		} else {
			
			GameState.setLevel(level);
			
			scoreBoard.updateState();
			
	//		don't reset the frame counter for now (game balance)
	//		it influences whether new space bombs are spawned etc. 
	//		frameCounter = 0;
			
			// reset cooldown timers
			spaceBombCoolDownTime = 0;
			
			EffectManager.getInstance().clearEffects();
			TextAnimationManager.getInstance().clear();
			
			paddle.getGrapplingHook().resetState();
			paddle.setWidth(Config.PADDLE_DEFAULT_WIDTH);
			
			
			// Carry over "Multiballs" to the new level
			GameState.setBallsLeft(GameState.getBallsLeft() + (balls.size() - 1));			
			balls.clear();
			spawnBall(false);
							
			powerups.clear();
			projectiles.clear();
			spaceBombs.clear();		
					
			// Spawn initial set of particles
			stars.clear();
			starsPool.clear();
			for (int i = 0; i < Config.SYNC_FPS * Config.STAR_DENSITY; i++) {				
				
				try {
					final Star s = starsPool.get();
					
					s.setState(new Point(Util.random(0, (int) Config.getInstance().getClientWidth()), 
						   	   Util.random(0, (int) Config.getInstance().getScreenHeight())), 
						   	   Util.random((int) Config.STAR_MIN_SPEED, (int) Config.STAR_MAX_SPEED));
				
					stars.add(s);
					
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			
			backgrounds.clear();
			backgrounds.add(BackgroundFactory.getRandomBackground());
			
			particleEffects.clear();
			
			levelMetadata = LevelLoader.getLevelMetaData(level);
			bricks = LevelLoader.loadLevel(level);
		}
	}
	
	public void newGame() {
//		SoundLayer.getInstance().stopAllMusic();
		
		GameState.setLevel(0);
		GameState.setScore(0);
		GameState.setBallsLeft(Config.INITIAL_BALLS_LEFT);
		GameState.setSpaceBombsLeft(Config.INITIAL_SPACEBOMBS_LEFT);
		
		initLevel(0);
		
		setState(State.NEW_STAGE);
	}
	
	public void restartLevel() {
		initLevel(GameState.getLevel());
	}
	
	private String getLevelMetaData(String key) {
		String result = levelMetadata.get(key);
		
		if (result == null)
			result = "<no data>";
		
		return result;
	}
	
	private void drawCenteredText(SpriteBatch batch, String[] lines, boolean eraseBackground) {	
		int cnt = 0;
		for (final String line : lines) {
			final float width = font.getBounds(line).width;
			final float height = font.getBounds(line).height + 5;
			
			font.draw(batch, line, (Config.getInstance().getClientWidth() / 2) - (width / 2), 
							 (height * cnt) + Config.getInstance().getScreenHeight() / 2);
			cnt++;
		}
	}
	
	public void setStateAfterResume() {
		if (GameState.getBallsLeft() <= 0)
			setState(State.GAME_OVER);
		else if (balls.size() <= 0)
			setState(State.WAITING_FOR_BALL);
		else			
			setState(State.PAUSED);
		
		if (!SoundLayer.isMusicPlaying())
			SoundLayer.playMusic(Musics.BACKGROUND);
	}
	
	@Override
	public void show() {
		final GameInputProcessor inputProcessor = new GameInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		setStateAfterResume();		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {
		setState(State.PAUSED);
	}

	@Override
	public void resume() {
		Logger.debug("GameScreen: resume()");
		
		setStateAfterResume();
	}

	@Override
	public void hide() {
		setState(State.PAUSED);		
	}

	@Override
	public void dispose() {		
//		setState(State.TERMINATED);		
	}

	@Override
	public void render(float delta) {
		this.step(delta);
		
		final SpriteBatch batch = App.getSpriteBatch();
		
		batch.setProjectionMatrix(camera.combined);
		
		for (final Background b : backgrounds) {
			b.render(batch);
		}
		
		for (final Star p : stars) {
			p.render(batch);
		}
		
		for (final Brick b : bricks) {			
			b.render(batch);
		}
		
		for (final Powerup p : powerups) {
			p.render(batch);
		}
		
		for (final Projectile p : projectiles) {
			p.render(batch);
		}
		
		for (final SpaceBomb b : spaceBombs) {
			b.render(batch);
		}

		for (final Ball b : balls) {
			b.render(batch);
		}
		
		// Draw a wall on the bottom of the screen?
		if (EffectManager.getInstance().isEffectActive(Effect.Type.BOTTOM_WALL)) {
			bottomWall.render(batch);
		}
		
		for (final ParticleSystem p : particleEffects) {
			p.render(batch);
		}
		
		paddle.render(batch);
		
		scoreBoard.render(batch);

		
		boolean drawTextAnimations = true;
		
		switch (state) {
		case LOADING:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"Loading, please wait..."}, true);
			break;
			
		case NEW_STAGE:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"Level Set: " + getLevelMetaData("Level Set"),
										   "Level: " + getLevelMetaData("Level"), 
										   "\"" + getLevelMetaData("Name") + "\""},
										   true);
			break;
			
		case RUNNING:			
			break;
			
		case RESTART:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"Restarting stage"}, true);
			break;
			
		case PAUSED:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"*GAME PAUSED*",
										   "",
										   "Level Set: " + getLevelMetaData("Level Set"),
										   "Level: " + getLevelMetaData("Level"), 
										   "\"" + getLevelMetaData("Name") + "\""}, true);
			break;
			
		case WAITING_FOR_BALL:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"*BALL LOST*"}, true);
			break;
			
		case STAGE_CLEARED:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"*STAGE CLEARED*", "",
										   "Level Set: " + getLevelMetaData("Level Set"),
										   "Level: " + getLevelMetaData("Level"), 
										   "\"" + getLevelMetaData("Name") + "\""}, true);
			break;
			
		case GAME_OVER:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"*GAME OVER!*"}, true);
			break;
						
		case TERMINATED:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"Good bye!"}, true);
			break;
		
		case LEVEL_SET_COMPLETED:
			drawTextAnimations = false;
			drawCenteredText(batch, new String[] {"Congratulations!","*Level Set Completed*"}, true);
			break;			
			
		default:
			throw new RuntimeException("Unsupported state: " + state);			
		}
		
		if (drawTextAnimations)
			TextAnimationManager.getInstance().render(batch);
		
		// Debugging:
		// Draw bounding boxes around the paddle 
//		Rectangle r1 = balls.get(0).getBoundingBox();
//		Rectangle r2 = paddle.getBoundingBox();		
//
//		Rectangle left 			= new Rectangle(r2.getX() - r1.getWidth(), r2.getY(), r1.getWidth(), r2.getHeight());
//		Rectangle top_left 		= new Rectangle(r2.getX() - r1.getWidth(), r2.getY() - r1.getHeight(), r1.getWidth(), r1.getHeight());
//		Rectangle top 			= new Rectangle(r2.getX(), r2.getY() - r1.getHeight(), r2.getWidth(), r1.getHeight());
//		Rectangle top_right 	= new Rectangle(r2.getX() + r2.getWidth(), r2.getY() - r1.getHeight(), r1.getWidth(), r1.getHeight());
//		Rectangle right 		= new Rectangle(r2.getX() + r2.getWidth(), r2.getY(), r1.getWidth(), r2.getHeight());
//		Rectangle bottom_right 	= new Rectangle(r2.getX() + r2.getWidth(), r2.getY() + r2.getHeight(), r1.getWidth(), r1.getHeight());
//		Rectangle bottom 		= new Rectangle(r2.getX(), r2.getY() + r2.getHeight(), r2.getWidth(), r1.getHeight());
//		Rectangle bottom_left 	= new Rectangle(r2.getX() - r1.getWidth(), r2.getY() + r2.getHeight(), r1.getWidth(), r1.getHeight());
//		
//		drawRect(left);
//		drawRect(top_left);
//		drawRect(top);
//		drawRect(top_right);
//		drawRect(right);
//		drawRect(bottom_right);
//		drawRect(bottom);
//		drawRect(bottom_left);
		
	}	
	
//	private void drawRect(Rectangle r) {
//		GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glBegin(GL11.GL_QUADS);
//			GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
//			GL11.glVertex2f(r.getX(), r.getY());			
//			GL11.glVertex2f(r.getX() + r.getWidth(), r.getY());			
//			GL11.glVertex2f(r.getX() + r.getWidth(), r.getY() + r.getHeight());			
//			GL11.glVertex2f(r.getX(), r.getY() + r.getHeight());
//		GL11.glEnd();
//	}
	
	public void step(float delta) {
		frameCounter++;
		
		// input handling
		final float idX = Gdx.input.getDeltaX();
		
		final float mdX = camera.unproject(new Vector3(idX, 0, 0)).x;
		
		final float iX = Gdx.input.getX();
		final float iY = Gdx.input.getY();
		
		final Vector3 unprojectediXY = camera.unproject(new Vector3(iX, iY, 0));
		
		final float mX = unprojectediXY.x;
//		final float mY = unprojectediXY.y;

		
		// avoid div by zero with Math.max()
		if (frameCounter % (10 * Math.max(Gdx.graphics.getFramesPerSecond(), 1)) == 0)
				cheatTouchCtr = 0;
		
		// cooldown timers
		spaceBombCoolDownTime--;
						
		switch (state) {
		case LOADING:
			break;
			
		case NEW_STAGE:
			if (Gdx.input.justTouched()) {
				setState(State.RUNNING);
			}
			
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				Config.getInstance().setTerminationUserInitiated(true);
				setState(State.TERMINATED);
			}
			break;
			
		case PAUSED:
			if (Gdx.input.justTouched()) {
				setState(State.RUNNING);
			}
			
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				setState(State.TERMINATED);
			}
			break;
			
		case STAGE_CLEARED:
			if (Gdx.input.justTouched()) {
				initLevel(GameState.getLevel() + 1);
				setState(State.NEW_STAGE);
			}
			
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				setState(State.TERMINATED);
			}
			break;
			
		case RUNNING:
			if (Gdx.input.isKeyPressed(Keys.P)) {
				setState(State.PAUSED);
			}
			
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				setState(State.TERMINATED);
			}
			
			if (Gdx.input.isKeyPressed(Keys.F2)) {
				setState(State.RESTART);
			}

			
			EffectManager.getInstance().step(delta);
			TextAnimationManager.getInstance().step(delta);
			
			for (final Background b : backgrounds) {
				b.step(delta);
			}
			
			for (final Star p : stars) {
				p.step(delta);
			}
			
			for (final Brick b : bricks) {
				b.step(delta);
			}
			
			for (final Powerup p : powerups) {
				p.step(delta);
			}
			
			for (final Projectile p : projectiles) {
				p.step(delta);
			}
			
			for (final SpaceBomb b : spaceBombs) {
				b.step(delta);
			}			
			
			for (final ParticleSystem p : particleEffects) {
				p.step(delta);
			}
			
			
			for (final Ball b : balls) {
				b.step(delta);
			}
			
			paddle.step(delta);
			
			bottomWall.step(delta);
			
			doCollisionDetection();
			doCleanup();
			
			// check for exploding space bombs
			// and apply effect to bricks
			for (final SpaceBomb b : spaceBombs) {
				if (b.getState() == SpaceBomb.State.EXPLODING) {
					final Point centerOfExplosion = b.getCenterOfExplosion();
					final Circle explosionCircle = new Circle(centerOfExplosion.getX(), 
															  centerOfExplosion.getY(), 
															  Config.SPACEBOMB_EXPLOSION_RADIUS);					
									
					for (final Brick brick : bricks) {
						if (explosionCircle.contains(brick.getBoundingBox().getX() + brick.getBoundingBox().getWidth() / 2, 
													 brick.getBoundingBox().getY() + brick.getBoundingBox().getHeight() / 2)) {
//							brickHit(brick, null, true);
							
							if (brick.getType() != Brick.Type.SOLID) {
								GameState.changeScore(100);
							}
							
							if (brick.getType() == Brick.Type.POWERUP) {
								GameState.changeScore(1000);			
								spawnPowerup(b.getPosition());
							}							
							
							brick.setDestroyed(true);
						}
					}
					
					
					// Detonate other space bombs that are inside the explosion circle
					for (final SpaceBomb bomb : spaceBombs) {
						if (explosionCircle.contains(bomb.getBoundingBox().getX() + bomb.getBoundingBox().getWidth() / 2, 
													 bomb.getBoundingBox().getY() + bomb.getBoundingBox().getHeight() / 2)) {
							bomb.detonate();
						}
					}
				}
			}
			
			// Spawn new stars
			for (int i = 0; i < Config.STAR_DENSITY; i++) {				
				try {
					final Star s = starsPool.get();
					
					s.setState(new Point(Util.random(0, (int) Config.getInstance().getClientWidth()), 0.0f), 
						   	   Util.random((int) Config.STAR_MIN_SPEED,
						   			   	   (int) Config.STAR_MAX_SPEED));
				
					stars.add(s);
					
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			
			// Spawn a new background?
			if ((frameCounter % Config.BACKGROUND_DENSITY) == 0) {
				backgrounds.add(BackgroundFactory.getRandomBackground());
			}
			
			// Fire projectiles?
			if (EffectManager.getInstance().isEffectActive(Effect.Type.PADDLE_GUN)) {
				if (true /*(Gdx.input.isTouched(2) || lastKeyID == Keyboard.KEY_SPACE)*/ && 
					(frameCounter % Config.PROJECTILE_FIRE_RATE == 0)) {
					for (int i = 0; i < 2; i++) {
						final float x = (frameCounter % (Config.PROJECTILE_FIRE_RATE * 2) == 0) ? 
								paddle.getX() : paddle.getX() + paddle.getWidth() - Config.PROJECTILE_WIDTH; 
						
						try {
							final Projectile p = projectilePool.get();
							
							p.setState(new Point(x, paddle.getY()));
						
							projectiles.add(p);
							
						} catch (Exception e) {
							e.printStackTrace();
						}				
						
						SoundLayer.playSound(Sounds.BULLET_FIRED);
					}
				}
			}
			
			// Spawn a new bonus SpaceBomb?
			if ((frameCounter % Config.SPACEBOMB_DENSITY) == 0) {
				spaceBombs.add(new SpaceBomb(new Point((float) Util.random(50, (int) Config.getInstance().getClientWidth() - 200), -50.0f), 
											 SpaceBomb.Type.BONUS));
			}
			
			// Move sticky balls with the paddle and
			// release them if a mouse button is pushed
			for (final Ball ball : balls) {
				if (ball.getState() == Ball.State.STUCK_TO_PADDLE) {
					if ((mX + mdX + (paddle.getWidth() / 2) >= 0) && 
						(mX + mdX + (paddle.getWidth() / 2) <= Config.getInstance().getClientWidth())) {						
						
						// TODO BUG: 
						// ball may be moved relative to the paddle!
						ball.changePosition(mdX, 0);
					}
					
					if (Gdx.input.justTouched()) {
						
						ball.setState(Ball.State.ROLLING);						
					}
				}
			}
			
			// Move caught power ups with the paddle
			for (final Powerup p : powerups) {
				if (p.getState() == Powerup.State.STUCK_TO_GRAPPLING_HOOK) {
					paddle.getGrapplingHook().setSomethingAttached(true);
					
					if ((mX + mdX + (paddle.getWidth() / 2) >= 0) && 
						(mX + mdX + (paddle.getWidth() / 2) <= Config.getInstance().getClientWidth())) {						
						
						// TODO BUG: 
						// bomb may be moved relative to the paddle!
						p.setCenterPosition(paddle.getGrapplingHook().getHookCenterPoint());
					}										
				}
			}
			
			// Move caught space bombs with the paddle
			for (final SpaceBomb bomb : spaceBombs) {
				if (bomb.getState() == SpaceBomb.State.STUCK_TO_GRAPPLING_HOOK) {
					paddle.getGrapplingHook().setSomethingAttached(true);
					
					if ((mX + mdX + (paddle.getWidth() / 2) >= 0) && 
						(mX + mdX + (paddle.getWidth() / 2) <= Config.getInstance().getClientWidth())) {						
						
						// TODO BUG: 
						// bomb may be moved relative to the paddle!
						bomb.setCenterPosition(paddle.getGrapplingHook().getHookCenterPoint());
					}										
				}
			}
			
			// Stage cleared?			
			if (isStageCleared()) {
				setState(State.STAGE_CLEARED);
			}
			break;
			
		case RESTART:
			initLevel(GameState.getLevel());			
			setState(State.NEW_STAGE);
			break;
			
		case TERMINATED:
			Gdx.app.exit();
			break;
			
		case WAITING_FOR_BALL:
			if (Gdx.input.justTouched()) {
				balls.clear();
				spawnBall(false);
				
				setState(State.RUNNING);
			}
			
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				setState(State.TERMINATED);
			}
			
			if (Gdx.input.isKeyPressed(Keys.F2)) {
				setState(State.RESTART);
			}
			break;
			
		case GAME_OVER:
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				setState(State.TERMINATED);
			}
			
			if (Gdx.input.isKeyPressed(Keys.F2) || Gdx.input.justTouched()) {				
				if (HighScoreManager.getInstance().isNewHighScore(GameState.getScore())) {
					
					HighScoreManager.getInstance().addHighScore(Config.getInstance().getUserName(), 
																GameState.getScore(), GameState.getLevel() + 1, 
																GameState.getLevelSet() + 1);
					ScreenManager.getInstance().showScreen(ScreenType.HIGHSCORE);
					
				} else				
					setState(State.RESTART);
				
			}
			break;
		
		case LEVEL_SET_COMPLETED:			
			if (Gdx.input.justTouched()) {
				if (HighScoreManager.getInstance().isNewHighScore(GameState.getScore()))					
					HighScoreManager.getInstance().addHighScore(Config.getInstance().getUserName(), 
																GameState.getScore(), GameState.getLevel() + 1, 
																GameState.getLevelSet() + 1);									
				
				ScreenManager.getInstance().showScreen(ScreenType.HIGHSCORE);
			}
			break;			
			
		default:
			throw new RuntimeException("Unsupported state: " + state);
		}
	}
		
	public void releaseSpaceBomb() {			
		if (GameState.getSpaceBombsLeft() > 0) {
			if (spaceBombCoolDownTime > 0)
			{
				TextAnimationManager.getInstance().add("Bomb not ready yet!");
				
				SoundLayer.playSound(Sounds.ACTION_DENIED);
				
			} else {
				spaceBombs.add(new SpaceBomb(new Point(paddle.getCenterPoint().getX(), 
													   paddle.getCenterPoint().getY() - 10.0f), 
													   SpaceBomb.Type.USER_FIRED));
				
				GameState.decrementSpaceBombsLeft();				
				spaceBombCoolDownTime = Config.SPACEBOMB_COOLDOWN_TIME;
				
				SoundLayer.playSound(Sounds.SPACEBOMB_LAUNCH);
			}
		} else {
			TextAnimationManager.getInstance().add("No bomb available!");
			
			SoundLayer.playSound(Sounds.ACTION_DENIED);
		}
	}

	public boolean detonateSpaceBombs() {
		boolean result = false;
		for (final SpaceBomb b : spaceBombs) {
			if (b.getType() == SpaceBomb.Type.USER_FIRED) {
				b.detonate();
				result = true;
			}
		}
		
		return result;
	}
	
	public boolean isStageCleared() {
		return getDestructibleBricksLeft() == 0;
	}

	public int getDestructibleBricksLeft() {
		if (bricks.isEmpty())
			return 0;
		
		int destructibleBricks = 0;
		
		for (final Brick b : bricks)
			if (b.getType() != Brick.Type.SOLID)
				destructibleBricks++;
		
		return destructibleBricks;
	}

	public void doCollisionDetection() {		
		float pdx = paddle.getdX();		
		
		// Ball vs. Edges
		for (final Ball ball : balls) {			
			// sanity check ball coordinates
			// clamp ball to client area
			if ((ball.getX() + ball.getWidth()) >= Config.getInstance().getClientWidth()) {
				final float newX = (Config.getInstance().getClientWidth() - (ball.getWidth()));				
				final float newY = ball.getY(); 
				
				ball.setPosition(new Point(newX, newY));
			}
			
			if (ball.getX() <= 0) {
				final float newX = 0;				
				final float newY = ball.getY(); 
				
				ball.setPosition(new Point(newX, newY));
			}
			
			if (ball.getY() <= 0) {
				final float newX = ball.getX();				
				final float newY = 0.0f; 
				
				ball.setPosition(new Point(newX, newY));
			}	
			
			// Reflect the ball if it's not stuck to the paddle	
			if (ball.getState() != Ball.State.STUCK_TO_PADDLE) {
				if (ball.getX() <= 0 || 
					ball.getX() >= Config.getInstance().getClientWidth() - ball.getWidth()) {
					
					ball.invertXVelocity();
					
					lastHitBrick = null;
					
					ForceFeedback.wallHit();					
					SoundLayer.playSound(Sounds.WALL_HIT);
				}
			}
		}

		for (final Ball ball : balls) {
			if (ball.getState() != Ball.State.STUCK_TO_PADDLE) {
				if (ball.getY() <= 0 || 
					(EffectManager.getInstance().isEffectActive(Effect.Type.BOTTOM_WALL)) && 
					 ball.getY() >= (Config.getInstance().getScreenHeight() - Config.BOTTOM_WALL_HEIGHT) - 
					 								  ball.getHeight()) {
					ball.invertYVelocity();
					
					if (ball.getY() <= 0) {
						// avoid double collisions by placing the ball below the wall
						ball.setPosition(new Point(ball.getX(), 1.0f));
						
					} else {
						// avoid double collisions by placing the ball above the wall
						ball.setPosition(new Point(ball.getX(), Config.getInstance().getScreenHeight() - 
																Config.BOTTOM_WALL_HEIGHT - ball.getHeight() + 1.0f));
					}
					
					lastHitBrick = null;
					
					ForceFeedback.wallHit();
					SoundLayer.playSound(Sounds.WALL_HIT);
				}
			}
		}
		
		// Ball lost?
		Iterator<Ball> bi = balls.iterator();
		while (bi.hasNext()) {			
			final Ball ball = bi.next();
			if (ball.getBoundingBox().getY() >= Config.getInstance().getScreenHeight() && 				
				!EffectManager.getInstance().isEffectActive(Effect.Type.BOTTOM_WALL)) {					
				ballLost(ball, bi);
			}
		}
		
		// Ball vs. Paddle
		for (final Ball ball : balls) {
			if (ball.getState() != Ball.State.STUCK_TO_PADDLE) {
				if (Util.collisionTest(paddle.getBoundingBox(), ball.getBoundingBox())) {					
//					final Edge edge = Util.getCollisionEdge(ball.getBoundingBox(), paddle.getBoundingBox());
					
					// Calculate reflection vector based on the formula
					// V - Velocity Vector
					// N - The Normal Vector of the plane
					// Vnew = -2*(V dot N)*N + V
													
					final Vector ballVector = new Vector(ball.getDeltaX(), ball.getDeltaY(), ball.getMovementAngleInDegrees());					
					final Vector paddleVector =  new Vector(pdx, 1.0f, 0.0f);
					final Vector surfaceNormal = paddleVector.cross(ballVector).normalize();
					
					Vector result = surfaceNormal.mult(-2 * (ballVector.dot(surfaceNormal))).add(ballVector);
					result = result.mult(Config.PADDLE_DAMPENING_FACTOR);					
					result = result.add(paddleVector);
					
					ball.setDeltaX(result.getX());
					ball.setDeltaY(-result.getY());
										
					// Apply speed constraints
					if (ball.getSpeed() > Config.BALL_SPEED_MAX)
						ball.setSpeed(Config.BALL_SPEED_MAX);
					else if (ball.getSpeed() < Config.BALL_SPEED_MIN)
						ball.setSpeed(Config.BALL_SPEED_MIN);
						
					// avoid double collisions by placing the ball above the paddle
					ball.setPosition(new Point(ball.getX(), paddle.getY() - (ball.getHeight() + 1.0f)));

					
					// Sticky ball?
					if (EffectManager.getInstance().isEffectActive(Effect.Type.STICKY_BALL))
						ball.setState(Ball.State.STUCK_TO_PADDLE);
					
					lastHitBrick = null;
					
					ForceFeedback.paddleHit();
					SoundLayer.playSound(Sounds.PADDLE_HIT);
				}
			}
		}
		
		// Ball vs. Bricks
		for (final Ball ball : balls) {
			for (final Brick b : bricks) {
				if (Util.collisionTest(ball.getBoundingBox(), b.getBoundingBox())) {
					brickHit(b, ball, false);
				}
			}
		}
		
		// Caught a powerup with the paddle?
		for (final Powerup p : powerups) {
			if (Util.collisionTest(paddle.getBoundingBox(), p.getBoundingBox())) {
				EffectManager.getInstance().addEffect(p.getType());
				p.setDestroyed(true);
			}
		}
		
		// Projectile vs. Bricks		
		for (final Projectile p : projectiles) {
			for (final Brick b : bricks) {
				if (Util.collisionTest(p.getBoundingBox(), b.getBoundingBox())) {
					brickHit(b, null, true);
					p.setDestroyed(true);
				}
			}
		}
		
		// Caught a power up with the grappling hook?
		if (paddle.getGrapplingHook().getState() != GrapplingHook.State.IDLE) {
			for (final Powerup p : powerups) {
				if (paddle.getGrapplingHook().collisionTest(p.getBoundingBox())) {
					
					paddle.getGrapplingHook().setSomethingAttached(true);
					
					p.setState(Powerup.State.STUCK_TO_GRAPPLING_HOOK);
					
					Logger.debug("Caught a Power up!");
				}
			}
		}
		
		// Consume caught power ups if they are 
		// drawn in with the grappling hook
		for (final Powerup p : powerups) {
			if (p.getState() == Powerup.State.STUCK_TO_GRAPPLING_HOOK) {
				if (Util.collisionTest(paddle.getBoundingBox(), p.getBoundingBox())) {
					EffectManager.getInstance().addEffect(p.getType());
					p.setDestroyed(true);
					
					paddle.getGrapplingHook().setSomethingAttached(false);
				}
			}
		}		
		
		// Caught a space bomb with the grappling hook?
		if (paddle.getGrapplingHook().getState() != GrapplingHook.State.IDLE) {
			for (final SpaceBomb b : spaceBombs) {
				//if (b.getType() == SpaceBomb.Type.BONUS) {
					if (paddle.getGrapplingHook().collisionTest(b.getBoundingBox())) {
						
						paddle.getGrapplingHook().setSomethingAttached(true);
						
						b.setState(SpaceBomb.State.STUCK_TO_GRAPPLING_HOOK);
						
						Logger.debug("Caught a Space Bomb!");
					}
				//}
			}
		}
		
		// Consume caught space bombs if they are 
		// drawn in with the grappling hook
		for (final SpaceBomb b : spaceBombs) {
			if (b.getState() == SpaceBomb.State.STUCK_TO_GRAPPLING_HOOK) {
				if (Util.collisionTest(paddle.getBoundingBox(), b.getBoundingBox())) {
					GameState.incrementSpaceBombsLeft();
					b.setDestroyed(true);
					
					paddle.getGrapplingHook().setSomethingAttached(false);
				}
			}
		}
		
		// Caught a space bomb with the paddle?
//		for (final SpaceBomb b : spaceBombs) {
//			if (b.getState() == SpaceBomb.State.FLOATING && b.getType() == Type.BONUS) {
//				if (Util.collisionTest(paddle.getBoundingBox(), b.getBoundingBox())) {
//					GameState.incrementSpaceBombsLeft();
//					b.setDestroyed(true);
//				}
//			}
//		}
	}

	private void brickHit(Brick b, Ball ball, boolean hitByProjectile) {
		// avoid double collisions		
		if (b != lastHitBrick) {
			lastHitBrick = b;
		
			b.hit();
			
			if (b.isDestroyed()) {
				addParticleEffect(new Point(b.getBoundingBox().getX(), b.getBoundingBox().getY()), 
								  ParticleEffect.BRICK_EXPLOSION);
			}
			
			if (hitByProjectile) {
				if (b.getType() != Brick.Type.SOLID) {
					GameState.changeScore(100);
				}
				
				if (b.getType() == Brick.Type.POWERUP) {
					GameState.changeScore(1000);			
					spawnPowerup(b.getPosition());
				}
			}
			else {
				// Reflect the ball?			
				if (!EffectManager.getInstance().isEffectActive(Effect.Type.FIREBALL) &&
					b.getType() != Brick.Type.WEAK) {
					
					switch (Util.getCollisionEdge(ball.getBoundingBox(), b.getBoundingBox())) {
					case LEFT:
						ball.invertXVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(b.getX() - (ball.getWidth() + 1.0f), ball.getY()));
						break;
						
					case TOP_LEFT:
						ball.invertXVelocity();
//						ball.invertYVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(b.getX() - (ball.getWidth() + 1.0f), ball.getY()));
						break;
						
					case TOP:
						ball.invertYVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(ball.getX(), b.getY() - (ball.getHeight() + 1.0f)));
						break;
						
					case TOP_RIGHT:
						ball.invertXVelocity();
//						ball.invertYVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(ball.getX(), b.getY() - (ball.getHeight() + 1.0f)));
						break;
						
					case RIGHT:
						ball.invertXVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(b.getX() + b.getWidth() + 1.0f, ball.getY()));
						break;									
						
					case BOTTOM_RIGHT:
						ball.invertXVelocity();
//						ball.invertYVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(b.getX() + b.getWidth() + 1.0f, 
												   b.getY() + b.getHeight() + 1.0f));
						break;
						
					case BOTTOM:
						ball.invertYVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(ball.getX(), b.getY() + b.getHeight() + 1.0f));
						break;
						
					case BOTTOM_LEFT:
						ball.invertXVelocity();
//						ball.invertYVelocity();
						
						// avoid double collisions and tunneling
						ball.setPosition(new Point(b.getX() - (ball.getWidth() + 1.0f), 
												   b.getY() + b.getHeight() + 1.0f));
						break;
					
					case CENTER:
						final float angle = ball.getAngleInDegrees();
						
						if (angle >= -90.0f && angle <= +90.0f) {						
//							ball.invertXVelocity();
							ball.invertYVelocity();
							
							// avoid double collisions and tunneling
							ball.setPosition(new Point(b.getX() - ball.getWidth() / 2, 
													   b.getY() + b.getHeight() + 1.0f));
						} else {
//							ball.invertXVelocity();
							ball.invertYVelocity();
							
							// avoid double collisions and tunneling
							ball.setPosition(new Point(b.getX() - ball.getWidth() / 2, 
													   b.getY() - 1.0f));
						}
						break;												
	
					default:
						throw new RuntimeException("Invalid egde type");				
					}				
				}			
				
				if (b.getType() != Brick.Type.SOLID) {
					GameState.changeScore(100);		
				}
				
				if (b.getType() == Brick.Type.POWERUP) {
					GameState.changeScore(1000);			
					spawnPowerup(b.getPosition());
				}
			}
		}
	}

	public void spawnBall(boolean isMultiball) {
		final float y;
		float lowestY = Config.getInstance().getScreenHeight() / 2;
		
		// find y coordinate of the bottom most row		
		for (final Brick b : bricks) {
			if (b.getPosition().getY() > lowestY)
				lowestY = b.getPosition().getY(); 
		}
		
		y = lowestY + Config.BRICK_HEIGHT;
		
		balls.add(new Ball(new Point((Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH) / 2, 
									  y + Config.BALL_SPAWN_Y_OFFSET), isMultiball));
	}
	
	private void ballLost(Ball ball, Iterator<Ball> bi) {
		if (!ball.isMultiball()) {
			GameState.decrementBallsLeft();
			GameState.changeScore(-1000);
		}
		
		bi.remove();

		if (balls.isEmpty()) {
			paddle.getGrapplingHook().resetState();
			
			EffectManager.getInstance().clearEffects();
			
			setState(State.WAITING_FOR_BALL);
		}			
		
		ForceFeedback.ballLost();
		SoundLayer.playSound(Sounds.BALL_LOST);
		
		if (GameState.getBallsLeft() <= 0 && balls.isEmpty()) {
			setState(State.GAME_OVER);
		}
	}
	
	private <T extends Destroyable> void cleanupList(List<T> list) {
		final Iterator<T> i = list.iterator();		
		while (i.hasNext()) {
			T t = i.next();
			
			if (t.isDestroyed()) {
				i.remove();
			}
		}
	}
	
	private <T extends Destroyable> void cleanupListPooled(List<T> list, ObjectPool<T> pool) {
		final Iterator<T> i = list.iterator();		
		while (i.hasNext()) {
			T t = i.next();
			
			if (t.isDestroyed()) {
				pool.put(t);
				i.remove();
			}
		}
	}
	
	public void doCleanup() {
		cleanupList(balls);
		cleanupList(bricks);		
		cleanupList(backgrounds);
		cleanupList(powerups);		
		cleanupList(spaceBombs);
		cleanupList(particleEffects);
		
		cleanupListPooled(stars, starsPool);
		cleanupListPooled(projectiles, projectilePool);
	}
	
	public void spawnPowerup(Point position) {		
		Effect.Type effectType = Effect.Type.values()[Util.random(0, Effect.Type.values().length - 1)];
		
		// Filter out some effects if they don't 
		// make sense at this time in the game		
		if (effectType == Effect.Type.SLOW_DOWN && 
			EffectManager.getInstance().isEffectActive(Effect.Type.SLOW_DOWN))
			effectType = Effect.Type.PADDLE_GUN;		
		
		if (effectType == Effect.Type.SPEED_UP && 
			EffectManager.getInstance().isEffectActive(Effect.Type.SPEED_UP))
			effectType = Effect.Type.BOTTOM_WALL;		
		
		if (effectType == Effect.Type.SHRINK_PADDLE && 
			getPaddle().getWidth() <= Config.PADDLE_MIN_WIDTH)
			effectType = Effect.Type.EXPAND_PADDLE;
		
		if (effectType == Effect.Type.EXPAND_PADDLE && 
			getPaddle().getWidth() >= Config.PADDLE_MAX_WIDTH)
			effectType = Effect.Type.SHRINK_PADDLE;
		
		powerups.add(new Powerup(position, effectType));
		
		Logger.debug("Spawned powerup: " + effectType);
				
		SoundLayer.playSound(Sounds.POWERUP_SPAWNED);		
	}
	
	public void addTextAnimation(String text) {		
		TextAnimationManager.getInstance().add(text);
	}

	public Paddle getPaddle() {
		return paddle;		
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
				
		switch (state) {					
		case NEW_STAGE:
			Config.getInstance().setGameResumeable(true);
			
//			SoundLayer.getInstance().stopAllMusic();
			
			if (!SoundLayer.isMusicPlaying())
				SoundLayer.playMusic(Musics.BACKGROUND);
			break;
			
		case RESTART:
			Config.getInstance().setGameResumeable(true);
			
			GameState.setLevel(0);
			GameState.setScore(0);
			GameState.setBallsLeft(Config.INITIAL_BALLS_LEFT);
			GameState.setSpaceBombsLeft(Config.INITIAL_SPACEBOMBS_LEFT);
			break;
			
		case GAME_OVER:
			Config.getInstance().setGameResumeable(false);
			break;
			
		case LOADING:
			break;
			
		case PAUSED:
			Config.getInstance().setGameResumeable(true);			
			break;
			
		case RUNNING:
			Config.getInstance().setGameResumeable(true);
			Config.getInstance().setGameStateBeforeQuit(state);
			break;
			
		case STAGE_CLEARED:
			Config.getInstance().setGameResumeable(false);
			
			// check if we completed the whole level set
			final int totalLevelsInSet = Integer.parseInt(LevelLoader.getLevelSetMetaData().get("Total Levels"));
			
			if (GameState.getLevel() + 1 >= totalLevelsInSet)
				setState(State.LEVEL_SET_COMPLETED);
			break;
			
		case TERMINATED:
			Config.getInstance().setGameResumeable(false);
			break;
			
		case WAITING_FOR_BALL:
			Config.getInstance().setGameResumeable(true);
			break;
		
		case LEVEL_SET_COMPLETED:
			break;
			
		default:
			throw new RuntimeException("Invalid state: " + state);
		}
	}
	
	private void addParticleEffect(Point position, ParticleEffect effect) {		
		switch (effect) {
		case BRICK_EXPLOSION:		
			particleEffects.add(new ParticleSystem(new SpriteTuple[]{new SpriteTuple(ResourceMapper.getPath("fire.png", ResourceType.SPRITE), 198.0f, 197.0f, 198, 197)}, 
					position, 10.0f, 15.0f, 0.0f, 360.0f, 0.0f, 15.0f, 155.0f, 4.5f));
			break;
		
		case BALL_LOST:
			// Do nothing
			break;
			
		default:
			throw new RuntimeException("Invalid ParticleEffect type!");
		}
	}
	
	public void cheat(boolean withCounter) {		
		if (withCounter) {
			if (cheatTouchCtr++ > 3) {
				cheatTouchCtr = 0;
				
				initLevel(GameState.getLevel() + 1);
				
				Logger.debug("Cheating to next level");
			}			
		} else {
			initLevel(GameState.getLevel() + 1);
			
			Logger.debug("Cheating to next level");
		}
	}

	public Viewport getViewport() {
		return viewport;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public void saveGameState() {
		Logger.debug("Saving game state...");
		
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		
		try {			
			out = new ObjectOutputStream(bos);			
			
			out.writeInt(GameState.getLevel());
			out.writeInt(GameState.getLevelSet());
			out.writeInt(GameState.getBallsLeft());
			out.writeInt(GameState.getSpaceBombsLeft());
			out.writeInt(GameState.getScore());
			
			out.writeObject(this);			
			out.writeObject(EffectManager.getInstance());
			
			byte[] bytes = bos.toByteArray();
			
			FileHandle handle = Gdx.files.local(Config.APP_NAME + ".sav");
			handle.writeBytes(bytes, false);
		}	
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}
	
	public void loadGameState() throws IOException {
		Logger.debug("Restoring game state...");
		
		final FileHandle handle = Gdx.files.local(Config.APP_NAME + ".sav");
		final byte[] bytes = handle.readBytes();
		
		Logger.debug(handle.path());
		
		final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		
		try {
			in = new ObjectInputStream(bis);
			
			GameState.setLevel(in.readInt());
			GameState.setLevelSet(in.readInt());
			GameState.setBallsLeft(in.readInt());
			GameState.setSpaceBombsLeft(in.readInt());
			GameState.setScore(in.readInt());
			
			Object o = in.readObject();
			final EffectManager e = (EffectManager) in.readObject();
			EffectManager.setInstance(e);
			
			((GameScreen) o).initializeTransients();
			
			ScreenManager.getInstance().overrideAndShowScreen(ScreenType.GAME, (GameScreen) o);
			
			((App) Gdx.app.getApplicationListener()).getGameScreen().setStateAfterResume();;

		} catch (IOException e) {
			e.printStackTrace();
						
			Logger.error("Deleting saved state due to invalid format!");
			handle.delete();
			
			throw e;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}
}
