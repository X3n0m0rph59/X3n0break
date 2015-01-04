package org.x3n0m0rph59.breakout;

//import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Star extends GameObject {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3978171188445682023L;

	public Star(Point position, float speed) {
		super(new SpriteObject("data/sprites/star.png", Config.STAR_WIDTH, Config.STAR_HEIGHT, 20, 20), 
			  position, Config.STAR_WIDTH, Config.STAR_HEIGHT, 0.0f, 0.0f, 0.0f, speed);		
	}
	
//	@Override
//	public void render(SpriteBatch batch) {
//		batch.disableBlending();
//		
//		super.render(batch);
//		
//		batch.enableBlending();
//	}
	
	@Override
	public void step(float delta) {
		super.step(delta);
		
		if(getY() >= Config.getInstance().getScreenHeight())
			setDestroyed(true);
	}
}
