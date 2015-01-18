package org.x3n0m0rph59.breakout;


public final class BackgroundFactory {
	
	public static Background getRandomBackground() {
		SpriteObject sprite;
		
		switch (Util.random(0, 4))
		{
		case 0:
			sprite = new SpriteObject(ResourceMapper.getPath("00.png", ResourceType.BACKGROUND), 256, 256, 256, 256);
			break;
			
		case 1:				
			sprite = new SpriteObject(ResourceMapper.getPath("01.png", ResourceType.BACKGROUND), 256, 256, 256, 256);
			break;
			
		case 2:
			sprite = new SpriteObject(ResourceMapper.getPath("02.png", ResourceType.BACKGROUND), 256, 256, 256, 256);
			break;
			
		case 3:
			sprite = new SpriteObject(ResourceMapper.getPath("03.png", ResourceType.BACKGROUND), 256, 256, 256, 256);
			break;
			
		case 4:
			sprite = new SpriteObject(ResourceMapper.getPath("04.png", ResourceType.BACKGROUND), 256, 256, 256, 256);
			break;
			
		default:
			throw new RuntimeException("Invalid background requested");
		}
		
		
		final float width  = (float) Util.random((int) Config.WORLD_WIDTH  / 2, (int) Config.WORLD_WIDTH);
		final float height = (float) Util.random((int) Config.WORLD_HEIGHT / 2, (int) Config.WORLD_HEIGHT);
		
		final float angle  = (float) Util.random(0, 360);
		
		final float speed  = (float) Util.random((int) Config.BACKGROUND_MIN_SPEED, 
												 (int) Config.BACKGROUND_MAX_SPEED);
		
		return new Background(sprite, new Point((float) Util.random(0, (int) Config.getInstance().getClientWidth() - (int) width), -height * 1.5f), 
							  width, height, angle, speed);
	}
}
