package org.x3n0m0rph59.breakout;

public class Projectile extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1345871286909398104L;

	public Projectile() {
		super(new SpriteObject(ResourceMapper.getPath("projectile.png", ResourceType.SPRITE), 
							   Config.PROJECTILE_WIDTH, Config.PROJECTILE_HEIGHT, 50, 100), 
			  new Point(0.0f, 0.0f), Config.PROJECTILE_WIDTH, Config.PROJECTILE_HEIGHT, 
			  0.0f, 0.0f, 0.0f, -Config.PROJECTILE_SPEED);
	}
	
	public Projectile(Point position) {
		super(new SpriteObject(ResourceMapper.getPath("projectile.png", ResourceType.SPRITE), 
							   Config.PROJECTILE_WIDTH, Config.PROJECTILE_HEIGHT, 50, 100), 
			  position, Config.PROJECTILE_WIDTH, Config.PROJECTILE_HEIGHT, 0.0f, 0.0f, 0.0f, 
			  -Config.PROJECTILE_SPEED);
	}
	
	public void setState(Point position) {
		this.destroyed = false;
		
		setPosition(position);
	}
	
	@Override
	public void step(float delta) {
		super.step(delta);
		
		if (getY() < 0)
			setDestroyed(true);
	}
	
	@Override
	public boolean isExcemptFromSpeedFactorChange() {
		return true;
	}
	
}
