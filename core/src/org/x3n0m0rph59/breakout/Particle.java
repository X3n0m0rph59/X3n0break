package org.x3n0m0rph59.breakout;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Particle implements Renderable, Stepable, Destroyable, Serializable/*, Poolable*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8999099247387857149L;

	private SpriteObject sprite;
	
	private Point position;
	private int initialttl, ttl; 
	private float dx, dy, angle, angularDeviation, 
				  angularVelocity, size, sizeIncrease;

	private boolean destroyed = false;
	
	public Particle() {				
	}
			
	public Particle(SpriteObject sprite, Point position, float angleInDegrees, 
					float angularDeviation, float speed, float angularVelocity, 
					int ttl, float sizeIncrease) {
		
		this.sprite = sprite;
		this.position = position;
		this.angle = angleInDegrees;
		
		this.angularDeviation = (float) Util.random((int) -angularDeviation, (int) +angularDeviation);
		this.dx = (float) Math.cos(Math.toRadians(angleInDegrees + 90 + this.angularDeviation)) * speed;
		this.dy = (float) Math.sin(Math.toRadians(angleInDegrees + 90 + this.angularDeviation)) * speed;
		
		this.angularVelocity = angularVelocity;
		
		this.initialttl = ttl;
		this.ttl = ttl;
		
		this.size = 1.0f;
		this.sizeIncrease = sizeIncrease;
		
		if (sprite != null)
			sprite.setAlphaBlending(true);
	}
	
	public void setState(SpriteObject sprite, Point position, float angleInDegrees, 
						 float angularDeviation, float speed, float angularVelocity, 
						 int ttl, float sizeIncrease) {
		this.destroyed = false;
		
		this.sprite = sprite;
		this.position = position;
		this.angle = angleInDegrees;
		
		this.angularDeviation = (float) Util.random((int) -angularDeviation, (int) +angularDeviation);
		this.dx = (float) Math.cos(Math.toRadians(angleInDegrees + 90 + this.angularDeviation)) * speed;
		this.dy = (float) Math.sin(Math.toRadians(angleInDegrees + 90 + this.angularDeviation)) * speed;
		
		this.angularVelocity = angularVelocity;
		
		this.initialttl = ttl;
		this.ttl = ttl;
		
		this.size = 1.0f;
		this.sizeIncrease = sizeIncrease;
		
		if (sprite != null)
			sprite.setAlphaBlending(true);
	}

	@Override
	public void render(SpriteBatch batch) {	
		final SpriteObject sprite = this.getSprite();
		
		sprite.setAlpha(initialttl / (getAge() + 0.1f) / 150.0f);		
		
		sprite.setWidth(size);
		sprite.setHeight(size);
		
		sprite.setCenterOfRotation(new Point(sprite.getWidth() / 2, sprite.getHeight() / 2));
		if (angularVelocity != 0)
			sprite.setAngle(angle + 90);		
		
		sprite.render(batch, new Point(position.getX(), position.getY()));
	}

	@Override
	public void step(float delta) {
		if ((ttl -= 1.0f /* delta */) <= 0)
			setDestroyed(true);
		
		angle += angularVelocity /* * delta */;
		size += sizeIncrease /* * delta */;
		
		position = new Point(position.getX() + (dx /* * delta */), position.getY() + (dy /* * delta */));
		
		getSprite().step(delta);
	}
	
	/*
	@Override
	public void resetState() {
		
	}	
	
	@Override
	public void dispose() {
		
	}
	*/
	
	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getAge() {
		return initialttl - ttl;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public SpriteObject getSprite() {
		return sprite;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public int getInitialttl() {
		return initialttl;
	}

	public void setInitialttl(int initialttl) {
		this.initialttl = initialttl;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;		
	}

	public float getAngularDeviation() {
		return angularDeviation;
	}

	public void setAngularDeviation(float angularDeviation) {
		this.angularDeviation = angularDeviation;
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getSizeIncrease() {
		return sizeIncrease;
	}

	public void setSizeIncrease(float sizeIncrease) {
		this.sizeIncrease = sizeIncrease;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(angle);
		result = prime * result + Float.floatToIntBits(angularDeviation);
		result = prime * result + Float.floatToIntBits(angularVelocity);
		result = prime * result + (destroyed ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(dx);
		result = prime * result + Float.floatToIntBits(dy);
		result = prime * result + initialttl;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + Float.floatToIntBits(size);
		result = prime * result + Float.floatToIntBits(sizeIncrease);
		result = prime * result + ((sprite == null) ? 0 : sprite.hashCode());
		result = prime * result + ttl;
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
		Particle other = (Particle) obj;
		if (Float.floatToIntBits(angle) != Float.floatToIntBits(other.angle))
			return false;
		if (Float.floatToIntBits(angularDeviation) != Float
				.floatToIntBits(other.angularDeviation))
			return false;
		if (Float.floatToIntBits(angularVelocity) != Float
				.floatToIntBits(other.angularVelocity))
			return false;
		if (destroyed != other.destroyed)
			return false;
		if (Float.floatToIntBits(dx) != Float.floatToIntBits(other.dx))
			return false;
		if (Float.floatToIntBits(dy) != Float.floatToIntBits(other.dy))
			return false;
		if (initialttl != other.initialttl)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size))
			return false;
		if (Float.floatToIntBits(sizeIncrease) != Float
				.floatToIntBits(other.sizeIncrease))
			return false;
		if (sprite == null) {
			if (other.sprite != null)
				return false;
		} else if (!sprite.equals(other.sprite))
			return false;
		if (ttl != other.ttl)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Particle [sprite=" + sprite + ", position=" + position
				+ ", initialttl=" + initialttl + ", ttl=" + ttl + ", dx=" + dx
				+ ", dy=" + dy + ", angle=" + angle + ", angularDeviation="
				+ angularDeviation + ", angularVelocity=" + angularVelocity
				+ ", size=" + size + ", sizeIncrease=" + sizeIncrease
				+ ", destroyed=" + destroyed + "]";
	}
}
