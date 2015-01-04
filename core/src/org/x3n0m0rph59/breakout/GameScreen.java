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
	private BottomWall bottomWall = new BottomWall();
	
	private HashMap<String, String> levelMetadata;	
	
	private Paddle paddle = new Paddle();
	
	private List<Ball> balls = new ArrayList<Ball>();
	private List<Brick> bricks = new ArrayList<Brick>();
	private List<Powerup> powerups = new ArrayList<Powerup>();
	private List<Star> stars = new ArrayList<Star>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();		
	private List<ParticleSystem> particleEffects = new ArrayList<ParticleSystem>();
	private List<Background> backgrounds = new ArrayList<Background>();
	private List<SpaceBomb> spaceBombs = new ArrayList<SpaceBomb>();
		
	private transient BitmapFont font;
	
	private int frameCounter = 0;
	
	private int spaceBombCoolDownTime = 0;
	private int cheatTouchCtr = 0;;
	
	
	public GameScreen() {
		GameInputProcessor inputProcessor = new GameInputProcessor();
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
	}
	
	public void initLevel(int level) {
		GameState.setLevel(level);
		
//		don't reset the frame counter for now (game balance)
//		it influences whether new space bombs are spawned etc. 
//		frameCounter = 0;
		
		// reset cooldown timers
		spaceBombCoolDownTime = 0;
		
		EffectManager.getInstance().clearEffects();
		TextAnimationManager.getInstance().clear();
		
		paddle.getGrapplingHook().resetState();
		paddle.setWidth(Config.PADDLE_DEFAULT_WIDTH);
		
		balls.clear();
		balls.add(new Ball(new Point((Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH) / 2, 
									  Config.getInstance().getScreenHeight() / 2)));
						
		powerups.clear();
		projectiles.clear();
		spaceBombs.clear();		
				
		// Spawn initial set of particles
		stars.clear();
		for (int i = 0; i < Config.SYNC_FPS * Config.STAR_DENSITY; i++) {
			stars.add(new Star(new Point(Util.random(0, (int) Config.getInstance().getClientWidth()), 
									   	 Util.random(0, (int) Config.getInstance().getScreenHeight())), 
									   	 Util.random((int) Config.STAR_MIN_SPEED, 
											   	   	 (int) Config.STAR_MAX_SPEED)));
		}
		
		backgrounds.clear();
		backgrounds.add(BackgroundFactory.getRandomBackground());
		
		levelMetadata = LevelLoader.getLevelMetaData(level);
		bricks = LevelLoader.loadLevel(level);
	}
	
	public void newGame() {
		SoundLayer.getInstance().stopAllMusic();
		
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
		for (String line : lines) {
			float width = font.getBounds(line).width;
			float height = font.getBounds(line).height + 5;
			
			font.draw(batch, line, (Config.getInstance().getClientWidth() / 2) - (width / 2), 
							 (height * cnt) + Config.getInstance().getScreenHeight() / 2);
			cnt++;
		}
	}
	
	@Override
	public void show() {
		GameInputProcessor inputProcessor = new GameInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		setState(State.RUNNING);
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
		
		if (balls.isEmpty())
			setState(State.WAITING_FOR_BALL);
		else			
			setState(State.PAUSED);
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
		
		SpriteBatch batch = App.getSpriteBatch();
		
		batch.setProjectionMatrix(camera.combined);
		
		for (Background b : backgrounds) {
			b.render(batch);
		}
		
		for (Star p : stars) {
			p.render(batch);
		}
		
		for (Brick b : bricks) {			
			b.render(batch);
		}
		
		for (Powerup p : powerups) {
			p.render(batch);
		}
		
		for (Projectile p : projectiles) {
			p.render(batch);
		}
		
		for (SpaceBomb b : spaceBombs) {
			b.render(batch);
		}

		for (Ball b : balls) {
			b.render(batch);
		}
		
		// Draw a wall on the bottom of the screen?
		if (EffectManager.getInstance().isEffectActive(Effect.Type.BOTTOM_WALL)) {
			bottomWall.render(batch);
		}
		
		for (ParticleSystem p : particleEffects) {
			p.render(batch);
		}
		
		paddle.render(batch);
		
		scoreBoard.render(batch);

		TextAnimationManager.getInstance().render(batch);
		
		switch (state) {
		case LOADING:
			drawCenteredText(batch, new String[] {"Loading, please wait..."}, true);
			break;
			
		case NEW_STAGE:
			drawCenteredText(batch, new String[] {"Level Set: " + getLevelMetaData("Level Set"),
										   "Level: " + getLevelMetaData("Level"), 
										   "\"" + getLevelMetaData("Name") + "\""},
										   true);
			break;
			
		case RUNNING:			
			break;
			
		case RESTART:
			drawCenteredText(batch, new String[] {"Restarting stage"}, true);
			break;
			
		case PAUSED:
			drawCenteredText(batch, new String[] {"*GAME PAUSED*",
										   "",
										   "Level Set: " + getLevelMetaData("Level Set"),
										   "Level: " + getLevelMetaData("Level"), 
										   "\"" + getLevelMetaData("Name") + "\""}, true);
			break;
			
		case WAITING_FOR_BALL:
			drawCenteredText(batch, new String[] {"*BALL LOST*"}, true);
			break;
			
		case STAGE_CLEARED:
			drawCenteredText(batch, new String[] {"*STAGE CLEARED*", "",
										   "Level Set: " + getLevelMetaData("Level Set"),
										   "Level: " + getLevelMetaData("Level"), 
										   "\"" + getLevelMetaData("Name") + "\""}, true);
			break;
			
		case GAME_OVER:
			drawCenteredText(batch, new String[] {"*GAME OVER!*"}, true);
			break;
						
		case TERMINATED:
			drawCenteredText(batch, new String[] {"Good bye!"}, true);
			break;
			
		default:
			throw new RuntimeException("Unsupported state: " + state);			
		}
		
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
			
			for (Background b : backgrounds) {
				b.step(delta);
			}
			
			for (Star p : stars) {
				p.step(delta);
			}
			
			for (Brick b : bricks) {
				b.step(delta);
			}
			
			for (Powerup p : powerups) {
				p.step(delta);
			}
			
			for (Projectile p : projectiles) {
				p.step(delta);
			}
			
			for (SpaceBomb b : spaceBombs) {
				b.step(delta);
			}			
			
			for (ParticleSystem p : particleEffects) {
				p.step(delta);
			}
			
			
			for (Ball b : balls) {
				b.step(delta);
			}
			
			paddle.step(delta);
			
			bottomWall.step(delta);
			
			doCollisionDetection();
			doCleanup();
			
			// check for exploding space bombs
			// and apply effect to bricks
			for (SpaceBomb b : spaceBombs) {
				if (b.getState() == SpaceBomb.State.EXPLODING) {
					final Point centerOfExplosion = b.getCenterOfExplosion();
					final Circle explosionCircle = new Circle(centerOfExplosion.getX(), 
															  centerOfExplosion.getY(), 
															  Config.SPACEBOMB_EXPLOSION_RADIUS);					
									
					for (Brick brick : bricks) {
						if (explosionCircle.contains(brick.getBoundingBox().getX(), 
													 brick.getBoundingBox().getY())) {
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
				}
			}
			
			// Spawn new stars
			for (int i = 0; i < Config.STAR_DENSITY; i++) {
				stars.add(new Star(new Point(Util.random(0, (int) Config.getInstance().getClientWidth()), 0.0f), 
								   Util.random((int) Config.STAR_MIN_SPEED, 
										       (int) Config.STAR_MAX_SPEED)));
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
						float x = (frameCounter % (Config.PROJECTILE_FIRE_RATE * 2) == 0) ? 
								paddle.getX() : paddle.getX() + paddle.getWidth() - Config.PROJECTILE_WIDTH; 
						
						projectiles.add(new Projectile(new Point(x, paddle.getY())));
						
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
			for (Ball ball : balls) {
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
			for (Powerup p : powerups) {
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
			for (SpaceBomb bomb : spaceBombs) {
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
				setState(State.RESTART);
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

	public void detonateSpaceBombs() {
		for (SpaceBomb b : spaceBombs) {
			if (b.getType() == SpaceBomb.Type.USER_FIRED) {
				b.detonate();
			}
		}
	}
	
	public boolean isStageCleared() {
		return getDestructibleBricksLeft() == 0;
	}

	public int getDestructibleBricksLeft() {
		if (bricks.isEmpty())
			return 0;
		
		int destructibleBricks = 0;
		
		for (Brick b : bricks)
			if (b.getType() != Brick.Type.SOLID)
				destructibleBricks++;
		
		return destructibleBricks;
	}

	public void doCollisionDetection() {		
		float pdx = paddle.getdX();		
		
		// Ball vs. Edges
		for (Ball ball : balls) {			
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
					
					ForceFeedback.wallHit();					
					SoundLayer.playSound(Sounds.WALL_HIT);
				}
			}
		}

		for (Ball ball : balls) {
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
					
					ForceFeedback.wallHit();
					SoundLayer.playSound(Sounds.WALL_HIT);
				}
			}
		}
		
		// Ball lost?
		Iterator<Ball> bi = balls.iterator();
		while (bi.hasNext()) {			
			Ball ball = bi.next();
			if (ball.getBoundingBox().getY() >= Config.getInstance().getScreenHeight() && 				
				!EffectManager.getInstance().isEffectActive(Effect.Type.BOTTOM_WALL)) {					
				ballLost(ball, bi);
			}
		}
		
		// Ball vs. Paddle
		for (Ball ball : balls) {
			if (ball.getState() != Ball.State.STUCK_TO_PADDLE) {
				if (Util.collisionTest(paddle.getBoundingBox(), ball.getBoundingBox())) {					
//					final Edge edge = Util.getCollisionEdge(ball.getBoundingBox(), paddle.getBoundingBox());
					
					// Calculate reflection vector based on the formula
					// V - Velocity Vector
					// N - The Normal Vector of the plane
					// Vnew = -2*(V dot N)*N + V
													
					Vector ballVector = new Vector(ball.getDeltaX(), ball.getDeltaY(), ball.getMovementAngleInDegrees());					
					Vector paddleVector =  new Vector(pdx, 1.0f, 0.0f);
					Vector surfaceNormal = paddleVector.cross(ballVector).normalize();
					
					Vector result = surfaceNormal.mult(-2 * (ballVector.dot(surfaceNormal))).add(ballVector);
					result = result.mult(Config.PADDLE_DAMPENING_FACTOR);					
					result = result.add(paddleVector);
					
					ball.setDeltaX(result.getX());
					ball.setDeltaY(-result.getY());
						
					// avoid double collisions by placing the ball above the paddle
					ball.setPosition(new Point(ball.getX(), paddle.getY() - (ball.getHeight() + 1.0f)));

					
					// Sticky ball?
					if (EffectManager.getInstance().isEffectActive(Effect.Type.STICKY_BALL))
						ball.setState(Ball.State.STUCK_TO_PADDLE);
					
					ForceFeedback.paddleHit();
					SoundLayer.playSound(Sounds.PADDLE_HIT);
				}
			}
		}
		
		// Ball vs. Bricks
		for (Ball ball : balls) {
			for (Brick b : bricks) {
				if (Util.collisionTest(ball.getBoundingBox(), b.getBoundingBox())) {
					brickHit(b, ball, false);
				}
			}
		}
		
		// Caught a powerup with the paddle?
		for (Powerup p : powerups) {
			if (Util.collisionTest(paddle.getBoundingBox(), p.getBoundingBox())) {
				EffectManager.getInstance().addEffect(p.getType());
				p.setDestroyed(true);
			}
		}
		
		// Projectile vs. Bricks		
		for (Projectile p : projectiles) {
			for (Brick b : bricks) {
				if (Util.collisionTest(p.getBoundingBox(), b.getBoundingBox())) {
					brickHit(b, null, true);
					p.setDestroyed(true);
				}
			}
		}
		
		// Caught a power up with the grappling hook?
		if (paddle.getGrapplingHook().getState() != GrapplingHook.State.IDLE) {
			for (Powerup p : powerups) {
				if (paddle.getGrapplingHook().collisionTest(p.getBoundingBox())) {
					
					paddle.getGrapplingHook().setSomethingAttached(true);
					
					p.setState(Powerup.State.STUCK_TO_GRAPPLING_HOOK);
					
					Logger.debug("Caught a Power up!");
				}
			}
		}
		
		// Consume caught power ups if they are 
		// drawn in with the grappling hook
		for (Powerup p : powerups) {
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
			for (SpaceBomb b : spaceBombs) {
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
		for (SpaceBomb b : spaceBombs) {
			if (b.getState() == SpaceBomb.State.STUCK_TO_GRAPPLING_HOOK) {
				if (Util.collisionTest(paddle.getBoundingBox(), b.getBoundingBox())) {
					GameState.incrementSpaceBombsLeft();
					b.setDestroyed(true);
					
					paddle.getGrapplingHook().setSomethingAttached(false);
				}
			}
		}
		
		// Caught a space bomb with the paddle?
//		for (SpaceBomb b : spaceBombs) {
//			if (b.getState() == SpaceBomb.State.FLOATING && b.getType() == Type.BONUS) {
//				if (Util.collisionTest(paddle.getBoundingBox(), b.getBoundingBox())) {
//					GameState.incrementSpaceBombsLeft();
//					b.setDestroyed(true);
//				}
//			}
//		}
	}

	private void brickHit(Brick b, Ball ball, boolean hitByProjectile) {
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
					break;
					
				case TOP_LEFT:
					ball.invertXVelocity();
					ball.invertYVelocity();
					break;
					
				case TOP:
					ball.invertYVelocity();
					break;
					
				case TOP_RIGHT:
					ball.invertXVelocity();
					ball.invertYVelocity();
					break;
					
				case RIGHT:
					ball.invertXVelocity();
					break;									
					
				case BOTTOM_RIGHT:
					ball.invertXVelocity();
					ball.invertYVelocity();
					break;
					
				case BOTTOM:
					ball.invertYVelocity();
					break;
					
				case BOTTOM_LEFT:
					ball.invertXVelocity();
					ball.invertYVelocity();
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

	public void spawnBall(boolean isMultiball) {
		balls.add(new Ball(new Point((Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH) / 2, 
									  Config.getInstance().getScreenHeight() / 2), isMultiball));
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
		
		if (GameState.getBallsLeft() <= 0) {
			setState(State.GAME_OVER);
		}
	}
	
	private <T extends Destroyable> void cleanupList(List<T> list) {
		Iterator<T> i = list.iterator();		
		while (i.hasNext()) {
			T t = i.next();
			
			if (t.isDestroyed()) {
				i.remove();
			}
		}
	}
	
	public void doCleanup() {
		cleanupList(balls);
		cleanupList(bricks);
		cleanupList(stars);
		cleanupList(backgrounds);
		cleanupList(powerups);
		cleanupList(projectiles);
		cleanupList(spaceBombs);
		cleanupList(stars);
		cleanupList(particleEffects);
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
			
			SoundLayer.getInstance().stopAllMusic();
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
			Config.getInstance().setGameResumeable(true);
			break;
			
		case LOADING:
			break;
			
		case PAUSED:
			Config.getInstance().setGameResumeable(true);
			break;
			
		case RUNNING:
			Config.getInstance().setGameResumeable(true);
			break;
			
		case STAGE_CLEARED:
			break;
			
		case TERMINATED:
			Config.getInstance().setGameResumeable(false);
			break;
			
		case WAITING_FOR_BALL:
			Config.getInstance().setGameResumeable(true);
			break;
			
		default:
			throw new RuntimeException("Invalid state: " + state);
		}
	}
	
	private void addParticleEffect(Point position, ParticleEffect effect) {		
		switch (effect) {
		case BRICK_EXPLOSION:		
			particleEffects.add(new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
					position, 10.0f, 15.0f, 0.0f, 360.0f, 0.0f, 15.0f, 155.0f, 4.5f));
			break;
			
		case BALL_LOST:		
//			particleEffects.add(new ParticleSystem(new SpriteTuple[]{new SpriteTuple("sprites/Star1.png", 255.0f, 255.0f, 255, 255), 
//					  new SpriteTuple("sprites/Star2.png", 345.0f, 342.0f, 345, 342), 
//					  new SpriteTuple("sprites/Star3.png", 270.0f, 261.0f, 270, 261), 
//					  new SpriteTuple("sprites/Star4.png", 264.0f, 285.0f, 264, 285)}, 
//			x, y, 150.0f, 5.0f, 0.0f, 180.0f, 0.0f, 15.0f, 15.0f, 5.0f));
			break;
		}
	}
	
	public void cheat(boolean withCounter) {		
		if (withCounter) {
			if (cheatTouchCtr++ > 5) {
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
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		
		try {			
			out = new ObjectOutputStream(bos);			
			
			out.writeInt(GameState.getLevel());
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
		
		FileHandle handle = Gdx.files.local(Config.APP_NAME + ".sav");
		byte[] bytes = handle.readBytes();
		
		Logger.debug(handle.path());
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		
		try {
			in = new ObjectInputStream(bis);
			
			GameState.setLevel(in.readInt());
			GameState.setBallsLeft(in.readInt());
			GameState.setSpaceBombsLeft(in.readInt());
			GameState.setScore(in.readInt());
			
			Object o = in.readObject();
			EffectManager e = (EffectManager) in.readObject();
			EffectManager.setInstance(e);
			
			((GameScreen) o).initializeTransients();			
			
			ScreenManager.getInstance().overrideAndShowScreen(ScreenType.GAME, (GameScreen) o);
			
			((App) Gdx.app.getApplicationListener()).getGameScreen().setState(State.PAUSED);

		} catch (IOException e) {
			// TODO Auto-generated catch block
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
