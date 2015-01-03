package org.x3n0m0rph59.breakout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class SpriteObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7452691659892992793L;

	private transient Sprite sprite;
	
	private String filename;
	private int tw,th;
	
	private int frameCounter = 0;
	
	private float width, height, alpha = 1.0f, angleInDegrees = 0.0f;
	
	private Point centerOfRotation = new Point(0.0f, 0.0f);
	
	private boolean flashed = false;
	
	public SpriteObject(String filename, float width, float height, int tw, int th) {
		this(filename, width, height, tw, th, false);
	}
	
	public SpriteObject(String filename, float width, float height, int tw, int th, boolean hasAlphaChannel) {
		this.width = width;
		this.height = height;
		
		this.filename = filename;
		this.tw = tw;
		this.th = th;
		
		sprite = SpriteLoader.getInstance().getSprite(filename, tw, th);		
	}
	
	public void render(SpriteBatch batch, Point position, float width, float height) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		if (!flashed)
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		else
			batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE);
		
		if (angleInDegrees != 0) {
			batch.draw(sprite, position.getX(), position.getY(), 
					   centerOfRotation.getX(), centerOfRotation.getY(), 
					   getWidth(), getHeight(), 1.0f, 1.0f, 
					   angleInDegrees, true);
		} else {		
			batch.draw(sprite, position.getX(), position.getY(), 
					   getWidth(), getHeight());
		}
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public void render(SpriteBatch batch, Point position) {
		this.render(batch, position, width, height);
	}
	
	public void render(SpriteBatch batch, Rectangle r) {
		this.render(batch, new Point(r.getX(), r.getY()), r.getWidth(), r.getHeight());
	}
	
	public void step() {
//		if (++frameCounter >= sprite.getHorizontalCount());
//			frameCounter = 0;
			
		++frameCounter;
	}

	public int getFrameCounter() {
		return frameCounter;
	}

	public void setFrameCounter(int frameCounter) {
		this.frameCounter = frameCounter;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}	
	
	public float getAngle() {
		return angleInDegrees;
	}

	public void setAngle(float angleInDegrees) {
		this.angleInDegrees = angleInDegrees;
	}
	
	public Point getCenterOfRotation() {	
		return centerOfRotation;
	}
	
	public void setCenterOfRotation(Point point) {	
		this.centerOfRotation = point;
	}

	public boolean isFlashed() {
		return flashed;
	}

	public void setFlashed(boolean flashed) {
		this.flashed = flashed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(alpha);
		result = prime * result + Float.floatToIntBits(angleInDegrees);		
		result = prime * result + frameCounter;
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result
				+ ((sprite == null) ? 0 : sprite.hashCode());
		result = prime * result + Float.floatToIntBits(width);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpriteObject other = (SpriteObject) obj;
		if (Float.floatToIntBits(alpha) != Float.floatToIntBits(other.alpha))
			return false;
		if (Float.floatToIntBits(angleInDegrees) != Float
				.floatToIntBits(other.angleInDegrees))
			return false;		
		if (frameCounter != other.frameCounter)
			return false;		
		if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
			return false;
		if (sprite == null) {
			if (other.sprite != null)
				return false;
		} else if (!sprite.equals(other.sprite))
			return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
			return false;
		return true;
	}
	
	private synchronized void writeObject(ObjectOutputStream stream) 
			throws IOException {
		stream.defaultWriteObject();
		
		// work around non-serializability of sprites
		stream.writeObject(filename);
		stream.writeInt(tw);
		stream.writeInt(th);		
	}
	
	private synchronized void readObject(ObjectInputStream stream)
			throws IOException {
		
		try {
			stream.defaultReadObject();
			
			// work around non-serializability of sprites
			final String filename = (String) stream.readObject();
			final int tw = stream.readInt();
			final int th = stream.readInt();
			
			sprite = SpriteLoader.getInstance().getSprite(filename, tw, th);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
