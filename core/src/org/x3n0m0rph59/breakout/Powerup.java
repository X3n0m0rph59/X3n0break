package org.x3n0m0rph59.breakout;

import java.util.EnumMap;
import java.util.Map;

import org.x3n0m0rph59.breakout.Effect.Type;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Powerup extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7418474324088350613L;

	public enum State {FLOATING, STUCK_TO_GRAPPLING_HOOK}
	private State state = State.FLOATING;
	
	private Effect.Type type;
	
	private final Map<Effect.Type, SpriteObject> sprites = new EnumMap<Type, SpriteObject>(Effect.Type.class);
	
	private final ParticleSystem trail = new ParticleSystem(new SpriteTuple[]{
										 new SpriteTuple(ResourceMapper.getPath("star1.png", ResourceType.SPRITE), 255.0f, 255.0f, 255, 255), 
										 new SpriteTuple(ResourceMapper.getPath("star2.png", ResourceType.SPRITE), 345.0f, 342.0f, 345, 342), 
										 new SpriteTuple(ResourceMapper.getPath("star3.png", ResourceType.SPRITE), 270.0f, 261.0f, 270, 261), 
										 new SpriteTuple(ResourceMapper.getPath("star4.png", ResourceType.SPRITE), 264.0f, 285.0f, 264, 285)}, 
										 position, -1.0f, 1.75f, 0.0f, 360.0f, 10.0f,  95.0f, 150.0f, 3.5f);
	
	public Powerup(Point position, Effect.Type type) {
		super(null, position, Config.POWERUP_WIDTH, Config.POWERUP_HEIGHT, 0.0f, 0.0f, 0.0f, Config.POWERUP_SPEED);
		
		this.type = type;
		
		
		sprites.put(Effect.Type.BOTTOM_WALL, new SpriteObject(
				ResourceMapper.getPath("powerup_bottom_wall.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));
		
		sprites.put(Effect.Type.PADDLE_GUN, new SpriteObject(
				ResourceMapper.getPath("powerup_paddle_gun.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));

		sprites.put(Effect.Type.SHRINK_PADDLE, new SpriteObject(
				ResourceMapper.getPath("powerup_shrink.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));
		
		sprites.put(Effect.Type.EXPAND_PADDLE, new SpriteObject(
				ResourceMapper.getPath("powerup_expand.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));

		sprites.put(Effect.Type.FIREBALL, new SpriteObject(
				ResourceMapper.getPath("powerup_fireball.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));
		
		sprites.put(Effect.Type.MULTIBALL, new SpriteObject(
				ResourceMapper.getPath("powerup_multiball.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));

		sprites.put(Effect.Type.SLOW_DOWN, new SpriteObject(
				ResourceMapper.getPath("powerup_slow_down.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));
		
		sprites.put(Effect.Type.SPEED_UP, new SpriteObject(
				ResourceMapper.getPath("powerup_speed_up.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));

		sprites.put(Effect.Type.STICKY_BALL, new SpriteObject(
				ResourceMapper.getPath("powerup_sticky_ball.png", ResourceType.SPRITE), Config.POWERUP_WIDTH,
				Config.POWERUP_HEIGHT, 100, 100));
	}
	
	@Override
	public void render(SpriteBatch batch) {
		trail.render(batch);
		
		final SpriteObject sprite = sprites.get(type);
		if (sprite != null) {
			setSprite(sprite);			
		}
		
		super.render(batch);
	}

	@Override
	public void step(float delta) {		
		trail.setPositionAndAngle(new Point(getBoundingBox().getX(), getBoundingBox().getY()), 180.0f);
		trail.step(delta);
		
		for (final SpriteObject s : sprites.values())
			s.step(delta);
		
		if (state != State.STUCK_TO_GRAPPLING_HOOK)
			super.step(delta);		
		
		if (getY() >= Config.getInstance().getScreenHeight())
				setDestroyed(true);
	}
	
	public Effect.Type getType() {
		return type;
	}

	@Override
	public void setPosition(Point position) {
		super.setPosition(position);
		
		trail.setPositionAndAngle(new Point(getBoundingBox().getX(), 
				  					getBoundingBox().getY()), 
				  			        (float) Math.toRadians(180.0f));
	}
	
	@Override
	public boolean isExcemptFromSpeedFactorChange() {
		return true;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
}
