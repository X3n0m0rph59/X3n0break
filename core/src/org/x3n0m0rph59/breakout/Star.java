package org.x3n0m0rph59.breakout;

public class Star extends GameObject {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3978171188445682023L;

	public Star(Point position, float speed) {
		super(new SpriteObject("data/sprites/star.png", Config.STAR_WIDTH, Config.STAR_HEIGHT, 20, 20), 
			  position, Config.STAR_WIDTH, Config.STAR_HEIGHT, 0.0f, 0.0f, 0.0f, speed);		
	}
	
	@Override
	public void step() {
		super.step();
		
		if(getY() >= Config.getInstance().getScreenHeight())
			setDestroyed(true);
	}
}
