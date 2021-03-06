package org.x3n0m0rph59.breakout;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class GameObject implements Renderable, Stepable, Destroyable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5132831131796375252L;

	protected SpriteObject sprite;
	
	protected Point position;
	protected float deltaX, deltaY;
	protected float width, height;
	
	protected float angleInDegrees;
	protected float angularVelocity;
	
	protected boolean visible = true;
	
	protected boolean destroyed = false;
	
	protected int frameCounter = 0;
//	protected int tag;
	
	
	public GameObject(SpriteObject sprite, Point position, float width, float height,
					  float angleInDegrees, float angularVelocity, float deltaX, float deltaY) {
		super();
						
		this.position = position;
		this.width = width;
		this.height = height;
		this.angleInDegrees = angleInDegrees;
		this.angularVelocity = angularVelocity;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		
		setSprite(sprite);
	}
	
	public void render(SpriteBatch batch) {
		if (sprite != null && isVisible())
			sprite.render(batch, position);
	}
	
	public void step(float delta) {
		frameCounter += Math.round(delta);
		
		float speedFactor = 1.0f;
		if (!this.isExcemptFromSpeedFactorChange())
			speedFactor = Config.getInstance().getSpeedFactor();
		
		setAngleInDegrees(getAngleInDegrees() + (getAngularVelocity() * speedFactor * delta));
		
		changePosition(getDeltaX() * speedFactor * delta,
				       getDeltaY() * speedFactor * delta);

		if (sprite != null)
			sprite.step(delta);
	}

	public boolean isExcemptFromSpeedFactorChange() {
		return false;
	}

	public Rectangle getBoundingBox() {
//		final Transform t = Transform.createRotateTransform((float) Math.toRadians(angleInDegrees), 
//															position.getX() + width / 2, 
//															position.getY() + height / 2);
//		final Shape boundingShape = new Rectangle(position.getX(), position.getY(), getWidth(), getHeight()).transform(t);
//		final Rectangle boundingBox = Util.getBoundingBoxFromShape(boundingShape);
//		
//		return boundingBox;
		
		// TODO: Fix bounding box calculations (incorporate rotation angle)
		return new Rectangle(position.getX(), position.getY(), getWidth(), getHeight());
	}

	public Point getPosition() {
		return position;
	}
	
	public Point getCenterPosition() {		
		return new Point(getBoundingBox().getX() + (getBoundingBox().getWidth() / 2), 
						 getBoundingBox().getY() + (getBoundingBox().getHeight() / 2));
	}
	
	public void setCenterPosition(Point newCenter) {		
		this.setPosition(new Point(newCenter.getX() - (this.getWidth() / 2), 
								   newCenter.getY() - (this.getHeight() / 2)));
	}
	
	public float getX() {
		return position.getX();
	}
	
	public void changeX(float delta) {
		position = new Point(position.getX() + delta, position.getY());
	}
	
	public float getY() {
		return position.getY();
	}
	
	public void changeY(float delta) {
		position = new Point(position.getX(), position.getY() + delta);
	}
	
	public void changePosition(float deltaX, float deltaY) {
		position = new Point(position.getX() + deltaX, position.getY() + deltaY);
	}
	
	public float getDeltaX() {
		return deltaX;
	}

	public float getDeltaY() {
		return deltaY;
	}

	public void setDeltaX(float deltaX) {
		this.deltaX = deltaX;
	}

	public void setDeltaY(float deltaY) {
		this.deltaY = deltaY;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getAngleInDegrees() {
		return angleInDegrees;
	}

//	public int getTag() {
//		return tag;
//	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setWidth(float width) {
		this.width = width;
		
		if (sprite != null)
			sprite.setWidth(width);
	}

	public void setHeight(float height) {
		this.height = height;
		
		if (sprite != null)
			sprite.setHeight(height);
	}

	public void setAngleInDegrees(float angleInDegrees) {
		this.angleInDegrees = angleInDegrees;
		
		if (sprite != null)
			sprite.setAngle(angleInDegrees);
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public int getFrameCounter() {
		return frameCounter;
	}

	public void setFrameCounter(int frameCounter) {
		this.frameCounter = frameCounter;
	}

//	public void setTag(int tag) {
//		this.tag = tag;
//	}

	public SpriteObject getSprite() {
		return sprite;
	}

	public void setSprite(SpriteObject sprite) {
		this.sprite = sprite;
		
		if (sprite != null) {
			sprite.setWidth(width);
			sprite.setHeight(height);
			
			sprite.setCenterOfRotation(new Point(sprite.getWidth() / 2, sprite.getHeight() / 2));
			sprite.setAngle(angleInDegrees);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(angleInDegrees);
		result = prime * result + Float.floatToIntBits(angularVelocity);
		result = prime * result + Float.floatToIntBits(deltaX);
		result = prime * result + Float.floatToIntBits(deltaY);
		result = prime * result + (destroyed ? 1231 : 1237);
		result = prime * result + frameCounter;
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((sprite == null) ? 0 : sprite.hashCode());
//		result = prime * result + tag;
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
		GameObject other = (GameObject) obj;
		if (Float.floatToIntBits(angleInDegrees) != Float
				.floatToIntBits(other.angleInDegrees))
			return false;
		if (Float.floatToIntBits(angularVelocity) != Float
				.floatToIntBits(other.angularVelocity))
			return false;
		if (Float.floatToIntBits(deltaX) != Float.floatToIntBits(other.deltaX))
			return false;
		if (Float.floatToIntBits(deltaY) != Float.floatToIntBits(other.deltaY))
			return false;
		if (destroyed != other.destroyed)
			return false;
		if (frameCounter != other.frameCounter)
			return false;
		if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (sprite == null) {
			if (other.sprite != null)
				return false;
		} else if (!sprite.equals(other.sprite))
			return false;
//		if (tag != other.tag)
//			return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GameObject [sprite=" + sprite + ", position=" + position
				+ ", deltaX=" + deltaX + ", deltaY=" + deltaY + ", width="
				+ width + ", height=" + height + ", angleInDegrees="
				+ angleInDegrees + ", angularVelocity=" + angularVelocity
				+ ", destroyed=" + destroyed + ", frameCounter=" + frameCounter + "]";
//				+ ", tag=" + tag + "]";
	}
}
