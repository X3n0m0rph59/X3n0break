package org.x3n0m0rph59.breakout;

import java.util.Random;

import com.badlogic.gdx.math.Rectangle;


enum Edge {LEFT, TOP_LEFT, TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, CENTER}

public final class Util {
	public static Edge getCollisionEdge(Rectangle r1, Rectangle r2) {
		if (collisionTest(r1, r2)) {
			
			final Point r1Center 		= new Point(r1.x + r1.getWidth() / 2, r1.y + r1.getHeight() / 2);
			final Point r1TopLeft 		= new Point(r1.x, r1.y);
			final Point r1BottomRight 	= new Point(r1.x + r1.getWidth(), r1.y + r1.getHeight());
			final Point r1BottomCenter 	= new Point(r1.x + r1.getWidth() / 2, r1.y + r1.getHeight());
			final Point r1BottomLeft 	= new Point(r1.x, r1.y + r1.getHeight());
			final Point r1LeftCenter 	= new Point(r1.x, r1.y + r1.getHeight() / 2);
			final Point r1TopCenter 	= new Point(r1.x + r1.getWidth() / 2, r1.y);
			final Point r1TopRight 		= new Point(r1.x + r1.getWidth(), r1.y);

			final Rectangle left 			= new Rectangle(r2.getX() - r1.getWidth(), r2.getY(), r1.getWidth(), r2.getHeight());
			final Rectangle top_left 		= new Rectangle(r2.getX() - r1.getWidth(), r2.getY() - r1.getHeight(), r1.getWidth(), r1.getHeight());
			final Rectangle top 			= new Rectangle(r2.getX(), r2.getY() - r1.getHeight(), r2.getWidth(), r1.getHeight());
			final Rectangle top_right 		= new Rectangle(r2.getX() + r2.getWidth(), r2.getY() - r1.getHeight(), r1.getWidth(), r1.getHeight());
			final Rectangle right 			= new Rectangle(r2.getX() + r2.getWidth(), r2.getY(), r1.getWidth(), r2.getHeight());
			final Rectangle bottom_right 	= new Rectangle(r2.getX() + r2.getWidth(), r2.getY() + r2.getHeight(), r1.getWidth(), r1.getHeight());
			final Rectangle bottom 			= new Rectangle(r2.getX(), r2.getY() + r2.getHeight(), r2.getWidth(), r1.getHeight());
			final Rectangle bottom_left 	= new Rectangle(r2.getX() - r1.getWidth(), r2.getY() + r2.getHeight(), r1.getWidth(), r1.getHeight());
			
			if (left.contains(r1Center.getX(), r1Center.getY()) ||
				left.contains(r1TopRight.getX(), r1TopRight.getY()) ||
				left.contains(r1TopLeft.getX(), r1TopLeft.getY()))
				return Edge.LEFT;
			else if (right.contains(r1Center.getX(), r1Center.getY()) ||
					 right.contains(r1TopLeft.getX(), r1TopLeft.getY()) ||
					 right.contains(r1LeftCenter.getX(), r1LeftCenter.getY()))
				return Edge.RIGHT;
			else if (bottom.contains(r1Center.getX(), r1Center.getY()) || 
					 bottom.contains(r1TopCenter.getX(), r1TopCenter.getY()))
				return Edge.BOTTOM;
			else if (top.contains(r1Center.getX(), r1Center.getY()) || 
					 top.contains(r1BottomCenter.getX(), r1BottomCenter.getY()))
				return Edge.TOP;			
			else if (top_left.contains(r1Center.getX(), r1Center.getY()) || 
					 top_left.contains(r1BottomRight.getX(), r1BottomRight.getY()))
				return Edge.TOP_LEFT;			
			else if (top_right.contains(r1Center.getX(), r1Center.getY()) || 
					 top_right.contains(r1BottomLeft.getX(), r1BottomLeft.getY()))
				return Edge.TOP_RIGHT;			
			else if (bottom_right.contains(r1Center.getX(), r1Center.getY()) || 
					 bottom_right.contains(r1TopLeft.getX(), r1TopLeft.getY()) )
				return Edge.BOTTOM_RIGHT;			
			else if (bottom_left.contains(r1Center.getX(), r1Center.getY()) || 
					 bottom_left.contains(r1TopRight.getX(), r1TopRight.getY()))
				return Edge.BOTTOM_LEFT;
			
			// Collision occurred in the center of the rectangle?
			else if (r2.contains(r1Center.getX(), r1Center.getY())) {				
				if (r1Center.getX() >= r2.getX() + (r2.getWidth() / 2))
					return Edge.RIGHT;
				else if (r1Center.getX() <= r2.getX() + (r2.getWidth() / 2))
					return Edge.LEFT;
				
				else {
					Logger.error("WARNING: Collision detector failed to detect exact collision edge!");
					return Edge.CENTER;
				}
			}
			
			throw new RuntimeException("Bug in collision detector");
		}
		else
			return null;		
	}
	
	public static boolean collisionTest(Rectangle r1, Rectangle r2) {
		return r1.overlaps(r2);
	}

	public static int random(int min, int max) {
		final Random rnd = new Random();
		return rnd.nextInt(Math.abs(max - min) + 1) + min;
	}

	public static int signum(float val) {
		return (int) Math.signum(val);
	}

	public static int max(int p1, int p2) {
		if (p1 > p2)
			return p1;
		else
			return p2;
	}
}
