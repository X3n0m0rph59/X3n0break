package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class Paddle extends GameObject {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2359831494748103391L;

	private float lastX = 0.0f, lastY = 0.0f;
	
	private float thrust = 0.0f;
	
	boolean drawFlash = false;
	
//	private final ParticleSystem leftEngine = new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
//			    new Point(0.0f, 0.0f), -1.0f, 15.0f, 0.0f, 25.0f, 0.0f, 15.0f, 10.0f, 8.5f);
//	
//	private final ParticleSystem rightEngine = new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
//				new Point(0.0f, 0.0f), -1.0f, 15.0f, 0.0f, 25.0f, 0.0f, 15.0f, 10.0f, 8.5f);
	
//	private final ParticleSystem engine = new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
//			new Point(0.0f, 0.0f), -1.0f, 15.0f, 0.0f, 25.0f, 0.0f, 15.0f, 10.0f, 8.5f);
	
	private final ParticleSystem thruster = new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
				new Point(0.0f, 0.0f), -1.0f, 15.0f, 0.0f, 25.0f, 0.0f, 15.0f, 10.0f, 8.5f);

	private GrapplingHook grapplingHook;
	
	public Paddle() {
		super(new SpriteObject("data/sprites/paddle.png", Config.PADDLE_DEFAULT_WIDTH, Config.PADDLE_HEIGHT, 600, 150), 
			  new Point((Config.getInstance().getClientWidth() / 2) - (Config.PADDLE_DEFAULT_WIDTH / 2), 
					    (Config.getInstance().getScreenHeight() - Config.PADDLE_BOTTOM_SPACING)), 
			  Config.PADDLE_DEFAULT_WIDTH, Config.PADDLE_HEIGHT, 0.0f, 0.0f, 0.0f, 0.0f);
		
		grapplingHook = new GrapplingHook(new Point(0.0f, 0.0f));
		
		updateEnginePosition();
		updateGrapplingHookPosition();
	}
	
	@Override
	public void render(SpriteBatch batch) {
//		leftEngine.render(batch);
//		rightEngine.render(batch);
		
//		engine.render(batch);
		
		thruster.render(batch);
		
		getGrapplingHook().render(batch);
		
		final boolean inGracePeriod = EffectManager.getInstance().isEffectInGracePeriod(Effect.Type.EXPAND_PADDLE) || 
									  EffectManager.getInstance().isEffectInGracePeriod(Effect.Type.SHRINK_PADDLE);
		
		if (inGracePeriod && drawFlash)
			getSprite().setFlashed(true);
		else
			getSprite().setFlashed(false);
		
		super.render(batch);
	}
	
	@Override
	public void step(float delta) {
		// Do not move the paddle if the 
		// user tapped on "menu" or "fire space bomb" 				
		final float iX = Gdx.input.getX();
		final float iY = Gdx.input.getY();
		
		final Vector3 unprojectediXY = ((App) Gdx.app.getApplicationListener()).getGameScreen().
													getCamera().unproject(new Vector3(iX, iY, 0));
		
		final float mX = unprojectediXY.x;
		final float mY = unprojectediXY.y;
		
		final Rectangle hotRect = new Rectangle((int) (Config.getInstance().getScreenWidth() - 
					 								   Config.SCOREBOARD_WIDTH + 25),
					 								   (int) Config.WORLD_HEIGHT - (175 + 150), 350, (300 * 2));

		if (!hotRect.contains(new Vector2(mX, mY))) {
			setCenteredPosition(new Point(mX, mY));
		}
		
		thrust /= 2.0f;
		updateEnginePosition();		
		
		thruster.step(delta);		
		
//		engine.step(delta);
		
//		leftEngine.step(delta);
//		rightEngine.step(delta);		
		
		grapplingHook.step(delta);
		
		if ((frameCounter % (Config.SYNC_FPS * Config.GRACE_PERIOD_BLINK_RATE)) == 0)
			drawFlash = !drawFlash;
		
		super.step(delta);
	}
	
	public void setCenteredPosition(Point position) {
		setPosition(new Point(position.getX() - (getWidth() / 2), Config.getInstance().getScreenHeight() - Config.PADDLE_BOTTOM_SPACING));
		
		if (getX() < 0) 
			setPosition(new Point(0.0f, Config.getInstance().getScreenHeight() - Config.PADDLE_BOTTOM_SPACING));
		
		if (getX() > Config.getInstance().getClientWidth() - getWidth()) 
			setPosition(new Point(Config.getInstance().getClientWidth() - getWidth(), 
								  Config.getInstance().getScreenHeight() - Config.PADDLE_BOTTOM_SPACING));
	}

	private void updateEnginePosition() {		
		final float dX = getX() - lastX;
		thrust += Math.abs(dX);
		
//		Logger.debug("Thrust: " + thrust + "; deltaX: " + dX);
		
		if (dX == 0.0f) {			
			// do nothing
		} else if (dX > 0) {			
			thruster.setParticleDensity(thrust);
			thruster.setPositionAndAngle(new Point(getX(), getY() + getHeight() / 2), 180.0f);
			
		} else if (dX < 0) {			
			thruster.setParticleDensity(thrust);
			thruster.setPositionAndAngle(new Point(getX() + getWidth(), getY() + getHeight() / 2), -180.0f);
			
		}
		
		if (thrust < 0.1f) {
			thruster.setVisible(false);
		} else {
			thruster.setVisible(true);
		}
		
//		engine.setPositionAndAngle(new Point(getCenterPoint().getX(), 
//										     getY() + getHeight() - 5.0f), 45.0f);
				
//		leftEngine.setPositionAndAngle(new Point(getX() + Config.PADDLE_ENGINE_OFFSET, 
//												 getY() + getHeight() - 5.0f), 45.0f);
//		
//		rightEngine.setPositionAndAngle(new Point((getX() + getWidth()) - Config.PADDLE_ENGINE_OFFSET, 
//												   getY() + getHeight() - 5.0f), 45.0f);
	}
	
	private void updateGrapplingHookPosition() {
		grapplingHook.setPosition(new Point(getX() + getWidth() / 2, getY()));
	}
	
	public void expand() {
		this.width += Config.PADDLE_EXPANSION;
		
		sprite.setWidth(width);
		
		updateEnginePosition();
		updateGrapplingHookPosition();
	}
	
	public void shrink() {
		this.width -= Config.PADDLE_EXPANSION;
		
		sprite.setWidth(width);
		
		updateEnginePosition();
		updateGrapplingHookPosition();
	}

	@Override
	public void setPosition(Point position) {
		super.setPosition(position);
		
		updateEnginePosition();
		updateGrapplingHookPosition();
	}

	public float getdX() {
		final float result = getX() - lastX;		
		lastX = getX();
		
		return result;
	}
	
	public float getdY() {
		final float result = getY() - lastY;		
		lastY = getY();
		
		return result;
	}

	public Point getCenterPoint() {
		return new Point(getBoundingBox().getX() + getWidth() / 2, getBoundingBox().getY() + getHeight() / 2);
	}

	public GrapplingHook getGrapplingHook() {
		return grapplingHook;
	}
}
