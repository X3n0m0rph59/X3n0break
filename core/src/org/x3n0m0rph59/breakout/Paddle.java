package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Paddle extends GameObject {	
	private float lastX = 0.0f, lastY = 0.0f;
	
	boolean drawFlash = false;
	
	private final ParticleSystem leftEngine = new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
			    new Point(0.0f, 0.0f), -1.0f, 15.0f, 0.0f, 25.0f, 0.0f, 15.0f, 10.0f, 8.5f);
	
	private final ParticleSystem rightEngine = new ParticleSystem(new SpriteTuple[]{new SpriteTuple("data/sprites/fire.png", 198.0f, 197.0f, 198, 197)}, 
				new Point(0.0f, 0.0f), -1.0f, 15.0f, 0.0f, 25.0f, 0.0f, 15.0f, 10.0f, 8.5f);

	private GrapplingHook grapplingHook;
	
	public Paddle() {
		super(new SpriteObject("data/sprites/paddle.png", Config.PADDLE_DEFAULT_WIDTH, Config.PADDLE_HEIGHT, 600, 150), 
			  new Point((Config.getInstance().getClientWidth() / 2) - (Config.PADDLE_DEFAULT_WIDTH / 2), 
					    (Config.getInstance().getScreenHeight() - Config.PADDLE_BOTTOM_SPACING)), 
			  Config.PADDLE_DEFAULT_WIDTH, Config.PADDLE_HEIGHT, 0.0f, 0.0f, 0.0f, 0.0f);
		
		grapplingHook = new GrapplingHook(new Point(0.0f, 0.0f));
	}
	
	@Override
	public void render(SpriteBatch batch) {
		leftEngine.render(batch);
		rightEngine.render(batch);
		
		getGrapplingHook().render(batch);
		
		final boolean inGracePeriod = EffectManager.getInstance().isEffectInGracePeriod(Effect.Type.ENLARGE_PADDLE) || 
									  EffectManager.getInstance().isEffectInGracePeriod(Effect.Type.SHRINK_PADDLE);
		
		if (inGracePeriod && drawFlash)
			getSprite().setFlashed(true);
		else
			getSprite().setFlashed(false);
		
		super.render(batch);
	}
	
	@Override
	public void step() {
		// Clamp mouse to client rect to avoid 
		// choppy paddle movement
//		if (!Config.getInstance().isWindowed()) {
//			// TODO: fix this
//			int new_x = Mouse.getX(), 
//				new_y = Mouse.getY();
//			
//			if (new_x > Config.getInstance().getClientWidth())
//				new_x = (int) (Config.getInstance().getClientWidth());
//			
//			Mouse.setCursorPosition(new_x, new_y);
//			
//			setCenteredPosition(new Point(Mouse.getX(), Mouse.getY()));
//		}
		
		
		// Do not move the paddle if the 
		// user tapped on "fire space bomb" 
		final float x = Gdx.input.getX();
		final float y = Gdx.input.getY();
		
		final Rectangle hotRect = new Rectangle((int) (Config.getInstance().getScreenWidth() - 
					 Config.SCOREBOARD_WIDTH + 25),
			  (int) Config.WORLD_HEIGHT - (175 + 150), 350, (300 * 2));

		if (!hotRect.contains(new Vector2(x, y))) {
			setCenteredPosition(new Point(x, y));
		}		
		
		
		leftEngine.step();
		rightEngine.step();
		
		grapplingHook.step();
		
		if ((frameCounter % (Config.SYNC_FPS * Config.GRACE_PERIOD_BLINK_RATE)) == 0)
			drawFlash = !drawFlash;
		
		super.step();
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
		leftEngine.setPositionAndAngle(new Point(getX() + Config.PADDLE_ENGINE_OFFSET, getY() + getHeight()), 140);
		rightEngine.setPositionAndAngle(new Point((getX() + getWidth()) - Config.PADDLE_ENGINE_OFFSET, getY() + getHeight()), 140);
	}
	
	private void updateGrapplingHookPosition() {
		grapplingHook.setPosition(new Point(getX() + getWidth() / 2, getY()));
	}
	
	public void expand() {
		this.width += Config.PADDLE_EXPANSION;
	}
	
	public void shrink() {
		this.width -= Config.PADDLE_EXPANSION;
	}

	@Override
	public void setPosition(Point position) {
		super.setPosition(position);
		
		updateEnginePosition();
		updateGrapplingHookPosition();
	}

	public float getdX() {
		float result = getX() - lastX;		
		lastX = getX();
		
		return result;
	}
	
	public float getdY() {
		float result = getY() - lastY;		
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
