package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class GrapplingHook extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1847648144682308278L;

	enum State {IDLE, EXTENDING, FULLY_EXTENDED, LOWERING}
	private State state = State.IDLE;
	
	private GrapplingHookSegment hook 	 = new GrapplingHookSegment(GrapplingHookSegment.Type.HOOK, new Point(0.0f,0.0f));
	private GrapplingHookSegment segment = new GrapplingHookSegment(GrapplingHookSegment.Type.SEGMENT, new Point(0.0f,0.0f));
	
	private float length = 0.0f;

	private boolean somethingAttached = false;
	
	public GrapplingHook(Point position) {
		super(null, position, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
		
		hook.setWidth(50.0f);
		hook.setHeight(40.0f);
		
		segment.setWidth(20.0f);
		segment.setHeight(20.0f);
	}
		
	public void toggleSwitch() {
		switch (state) {
		case IDLE:
		case LOWERING:
			setState(State.EXTENDING);
			break;
			
		case EXTENDING:
		case FULLY_EXTENDED:
			setState(State.LOWERING);
			break;
			
		default:
			throw new RuntimeException("Invalid state: " + state);
			
		}
	}
	
	public void resetState() {
		state = State.IDLE;
		length = 0.0f;
		somethingAttached = false;
		
		SoundLayer.stopLoop(Sounds.GRAPPLING_HOOK_LOOP);
	}
	
	public boolean collisionTest(Rectangle r) {
		return Util.collisionTest(getBoundingBox(), r);			
	}
	
	@Override
	public void render(SpriteBatch batch) {	
		if (state != State.IDLE) {
			hook.render(batch);
			
			for (int i = 0; i < (length / segment.getHeight()); i++) {
				final Point position = new Point(segment.getX(), hook.getY() + (hook.getHeight() + 
																				i * segment.getHeight()));
				segment.setPosition(position);
				segment.render(batch);
			}
		}
	}
	
	@Override
	public void step(float delta) {
		switch (state) {
		case IDLE:
			// do nothing
			break;
			
		case EXTENDING:
			length += Config.GRAPPLING_HOOK_EXTEND_SPEED;
			
			if (length >= Config.GRAPPLING_HOOK_LENGTH)
				setState(State.FULLY_EXTENDED);
			break;
			
		case FULLY_EXTENDED:
			// do nothing
			break;		
			
		case LOWERING:
			length -= Config.GRAPPLING_HOOK_LOWER_SPEED;
			
			if (length <= 0)
				setState(State.IDLE);
			break;
			
		default:
			throw new RuntimeException("Invalid state: " + state);		
		}
	}
	
	@Override
	public void setPosition(Point position) {				
		final Point hookPos = new Point(position.getX() - (hook.getWidth() / 2) - 7.0f, 
										position.getY() - (hook.getHeight() + length));
		final Point segmentPos = new Point(position.getX() - ((hook.getWidth() / 2) - (segment.getWidth() / 2)), 
										   position.getY());
		
		hook.setPosition(hookPos);
		segment.setPosition(segmentPos);
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return hook.getBoundingBox();
	}
	
	@Override
	public boolean isExcemptFromSpeedFactorChange() {
		return true;
	}
	
	public Point getHookCenterPoint() {
		return hook.getCenterPosition();
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		if (this.state == State.IDLE && state == State.EXTENDING)
			SoundLayer.loopSound(Sounds.GRAPPLING_HOOK_LOOP);
		
		if (this.state == State.FULLY_EXTENDED && state == State.LOWERING) {
			float pitch = 1.0f;
			
			if (isSomethingAttached())
				pitch = 0.85f;
			
			SoundLayer.loopSound(Sounds.GRAPPLING_HOOK_LOOP, pitch);
		}
		
		if (state == State.IDLE || state == State.FULLY_EXTENDED)
			SoundLayer.stopLoop(Sounds.GRAPPLING_HOOK_LOOP);
					
		this.state = state;
	}

	public boolean isSomethingAttached() {		
		return somethingAttached;
	}
	
	public void setSomethingAttached(boolean somethingAttached) {		
		this.somethingAttached = somethingAttached;
	}
}
