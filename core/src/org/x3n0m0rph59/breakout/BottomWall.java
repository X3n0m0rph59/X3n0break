package org.x3n0m0rph59.breakout;

import java.io.Serializable;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class BottomWall implements Stepable, Renderable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1144551017184114286L;
	
	private int frameCounter = 0;
	boolean drawFlash = false;

	@Override
	public void render(SpriteBatch batch) {
		BitmapFont f = FontLoader.getInstance().getFont("font", 44);
		
		final float segment_width = f.getBounds("-").width; //Config.BOTTOM_WALL_SEGMENT_WIDTH
		final float segment_height = f.getLineHeight(); //Config.BOTTOM_WALL_SEGMENT_HEIGHT
		
		for (int i = 0; i <= Config.getInstance().getClientWidth() / 
							 (segment_width + Config.BOTTOM_WALL_SEGMENT_SPACING); i++) {
			
			float x = i * (segment_width + Config.BOTTOM_WALL_SEGMENT_SPACING);
			float y = Config.WORLD_HEIGHT - segment_height;
			
			final boolean inGracePeriod = EffectManager.getInstance().isEffectInGracePeriod(Effect.Type.BOTTOM_WALL);			
			
			if (inGracePeriod && drawFlash)
				batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE);
			else
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			
			f.draw(batch, "-", x, y);
		}		
	}

	@Override
	public void step() {
		if ((frameCounter % (Config.SYNC_FPS * Config.GRACE_PERIOD_BLINK_RATE)) == 0)
			drawFlash = !drawFlash;
			
		frameCounter++;
	}
}
