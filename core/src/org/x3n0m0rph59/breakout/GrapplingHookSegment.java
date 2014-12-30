package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class GrapplingHookSegment extends GameObject {
	public enum Type {HOOK, SEGMENT};
	Type type;
	
	private SpriteObject hook    = new SpriteObject("data/sprites/hook.png", 60, 60, 60, 60);
	private SpriteObject segment = new SpriteObject("data/sprites/hook_segment.png", 60, 105, 60, 105);
	
	public GrapplingHookSegment(Type type, Point position) {
		super(null, position, 60.0f, 105.0f, 0.0f, 0.0f, 0.0f, 0.0f);
		
		this.type = type;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		switch (type) {
		case HOOK:
			setSprite(hook);
			break;
			
		case SEGMENT:
			setSprite(segment);
			break;
		
		default:
			throw new RuntimeException("Invalid type: " + type);
		}
		
		super.render(batch);
	}
	
	@Override
	public void step() {
		super.step();
		
		hook.step();
		segment.step();
	}
	
	@Override
	public boolean isExcemptFromSpeedFactorChange() {
		return true;
	}
}
