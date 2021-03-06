package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpaceBomb extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4830991047432550595L;

	public enum Type {USER_FIRED, BONUS};
	public enum State {FLOATING, EXPLODING, EXPLODED, STUCK_TO_GRAPPLING_HOOK}
	
	private Type type;
	private State state = State.FLOATING;
	
	private int ttl = 3 * Gdx.graphics.getFramesPerSecond();
	
	private final ParticleSystem trail = new ParticleSystem(new SpriteTuple[]{new SpriteTuple(ResourceMapper.getPath("fire.png", ResourceType.SPRITE), 198.0f, 197.0f, 198, 197)}, 
											  				new Point(0.0f, 0.0f), -1.0f, 10.0f, 180.0f, 45.0f, 0.0f, 15.0f, 55.0f, 2.0f);
	
	private final ParticleSystem explosion = new ParticleSystem(new SpriteTuple[]{new SpriteTuple(ResourceMapper.getPath("fire.png", ResourceType.SPRITE), 198.0f, 197.0f, 198, 197)},
											   					new Point(0.0f, 0.0f), -1.0f, 25.0f, 0.0f, 360.0f, 2.0f, 55.0f, 55.0f, 25.0f);
	private int explosionframeCounter = 0; 
	
	public SpaceBomb(Point position, Type type) {
		super(new SpriteObject(ResourceMapper.getPath("spacebomb.png", ResourceType.SPRITE), 
							   Config.SPACEBOMB_WIDTH, Config.SPACEBOMB_HEIGHT, 145, 130), position, 
							   Config.SPACEBOMB_WIDTH, Config.SPACEBOMB_HEIGHT, 0.0f, -5.0f, 0.0f, 0.0f);
		
		this.type = type;
		
		switch (type) {
		case BONUS:
			setDeltaY(+Config.SPACEBOMB_LURKING_SPEED);
			break;
			
		case USER_FIRED:
			setDeltaY(-Config.SPACEBOMB_SPEED);
			break;
			
		default:
			throw new RuntimeException("Invalid state");		
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		switch (state) {
		case FLOATING:
			trail.render(batch);			
			super.render(batch);
			break;
			
		case STUCK_TO_GRAPPLING_HOOK:
			trail.render(batch);			
			super.render(batch);
			break;
			
		case EXPLODING:
			explosion.render(batch);
			break;
			
		case EXPLODED:
			break;		
			
		default:
			throw new RuntimeException("Invalid state");
		}
	}

	@Override
	public void step(float delta) {
		if (type == Type.USER_FIRED) {
			switch (state) {
			case FLOATING:
				super.step(delta);
				
				trail.setPositionAndAngle(getCenterPosition(), getAngleInDegrees());
				trail.step(delta);
						
				if (ttl-- <= 0 || getY() <= 0) {
					setState(State.EXPLODING);
				}
				break;
						
			case EXPLODING:
				super.step(delta);
				
				explosion.setPositionAndAngle(getCenterPosition(), getAngleInDegrees());
				explosion.step(delta);
				
				if (explosionframeCounter++ >= Config.SPACEBOMB_EXPLOSION_DURATION + Gdx.graphics.getFramesPerSecond()) {
					setState(State.EXPLODED);
				}
				break;
				
			case EXPLODED:
				setDestroyed(true);
				break;
				
			case STUCK_TO_GRAPPLING_HOOK:
				super.step(delta);
				
				trail.setPositionAndAngle(getCenterPosition(), getAngleInDegrees());
				trail.step(delta);
						
				if (ttl-- <= 0 || getY() <= 0) {
					setState(State.EXPLODING);
				}
				break;
				
			default:
				throw new RuntimeException("Invalid state");
			}
		} else if (type == Type.BONUS) {
			if (state != State.STUCK_TO_GRAPPLING_HOOK)
				super.step(delta);
		} else
			throw new RuntimeException("Invalid type: " + type);			
	}
	
	@Override
	public boolean isExcemptFromSpeedFactorChange() {
		return true;
	}

	public Point getCenterOfExplosion() {
		return getCenterPosition();
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		if (this.state == State.FLOATING && state == State.EXPLODING) {
			ForceFeedback.spaceBombExplosion();
			SoundLayer.playSound(Sounds.SPACEBOMB_EXPLOSION);
		}
		
		this.state = state;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void detonate() {
		setState(State.EXPLODING);
	}
	
}
