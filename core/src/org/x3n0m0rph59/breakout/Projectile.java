package org.x3n0m0rph59.breakout;

public class Projectile extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1345871286909398104L;

	public Projectile(Point position) {
		super(new SpriteObject("data/sprites/projectile.png", Config.PROJECTILE_WIDTH, Config.PROJECTILE_HEIGHT, 50, 100), position, 
			  Config.PROJECTILE_WIDTH, Config.PROJECTILE_HEIGHT, 0.0f, 0.0f, 0.0f, -Config.PROJECTILE_SPEED);
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
