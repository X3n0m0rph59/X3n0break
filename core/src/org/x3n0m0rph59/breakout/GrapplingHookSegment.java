package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class GrapplingHookSegment extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8986716497179709278L;

	public enum Type {HOOK, SEGMENT};
	Type type;
	
	private final SpriteObject hook    = new SpriteObject(ResourceMapper.getPath("hook.png", ResourceType.SPRITE), 60, 60, 60, 60);
	private final SpriteObject segment = new SpriteObject(ResourceMapper.getPath("hook_segment.png", ResourceType.SPRITE), 60, 105, 60, 105);
	
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
	public void step(float delta) {
		super.step(delta);
		
		hook.step(delta);
		segment.step(delta);
	}
	
	@Override
	public boolean isExcemptFromSpeedFactorChange() {
		return true;
	}
}
