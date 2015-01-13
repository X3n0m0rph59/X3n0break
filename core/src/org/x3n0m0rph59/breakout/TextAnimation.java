package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class TextAnimation implements Renderable, Stepable, Destroyable {
	private String text;
	private Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	
	private final BitmapFont font;
	
	private int frameCounter = 0;
	private boolean destroyed = false;
	
	
	public TextAnimation(String text) {
		this.text = text;
		font = FontLoader.getInstance().getFont("font", 44);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		float width = font.getBounds(text).width;
		
		font.draw(batch, text, Config.getInstance().getClientWidth() / 2 - width / 2, 
						 	   Config.getInstance().getScreenHeight() / 2);
			
	}

	@Override
	public void step(float delta) {
		frameCounter += Math.round(delta);

		color.a -= 0.015f;
		
		if (frameCounter > 60 * Config.TOAST_DELAY || color.a <= 0.0f) {
			setDestroyed(true);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
}
