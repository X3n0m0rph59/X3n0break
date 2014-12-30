package org.x3n0m0rph59.breakout;

import java.util.Random;

import com.badlogic.gdx.math.Rectangle;


enum Edge {LEFT, TOP_LEFT, TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT}

public final class Util {
	public static Edge getCollisionEdge(Rectangle r1, Rectangle r2) {
		if (collisionTest(r1, r2)) {
			
			Point r1Center = new Point(r1.x + r1.getWidth() / 2, r1.y + r1.getHeight() / 2);

			Rectangle left 			= new Rectangle(r2.getX() - r1.getWidth(), r2.getY(), r1.getWidth(), r2.getHeight());
			Rectangle top_left 		= new Rectangle(r2.getX() - r1.getWidth(), r2.getY() - r1.getHeight(), r1.getWidth(), r1.getHeight());
			Rectangle top 			= new Rectangle(r2.getX(), r2.getY() - r1.getHeight(), r2.getWidth(), r1.getHeight());
			Rectangle top_right 	= new Rectangle(r2.getX() + r2.getWidth(), r2.getY() - r1.getHeight(), r1.getWidth(), r1.getHeight());
			Rectangle right 		= new Rectangle(r2.getX() + r2.getWidth(), r2.getY(), r1.getWidth(), r2.getHeight());
			Rectangle bottom_right 	= new Rectangle(r2.getX() + r2.getWidth(), r2.getY() + r2.getHeight(), r1.getWidth(), r1.getHeight());
			Rectangle bottom 		= new Rectangle(r2.getX(), r2.getY() + r2.getHeight(), r2.getWidth(), r1.getHeight());
			Rectangle bottom_left 	= new Rectangle(r2.getX() - r1.getWidth(), r2.getY() + r2.getHeight(), r1.getWidth(), r1.getHeight());
			
			if (left.contains(r1Center.getX(), r1Center.getY()))
				return Edge.LEFT;
			else if (top_left.contains(r1Center.getX(), r1Center.getY()))
				return Edge.TOP_LEFT;
			else if (top.contains(r1Center.getX(), r1Center.getY()))
				return Edge.TOP;
			else if (top_right.contains(r1Center.getX(), r1Center.getY()))
				return Edge.TOP_RIGHT;
			else if (right.contains(r1Center.getX(), r1Center.getY()))
				return Edge.RIGHT;
			else if (bottom_right.contains(r1Center.getX(), r1Center.getY()))
				return Edge.BOTTOM_RIGHT;
			else if (bottom.contains(r1Center.getX(), r1Center.getY()))
				return Edge.BOTTOM;
			else if (bottom_left.contains(r1Center.getX(), r1Center.getY()))
				return Edge.BOTTOM_LEFT;
			
			// TODO: Fix this
			// HACK: If all else fails simply guess
			else if (r2.contains(r1Center.getX(), r1Center.getY()))
				return Edge.BOTTOM;
			
			throw new RuntimeException("Bug in collision detector");
		}
		else
			return null;		
	}
	
	public static boolean collisionTest(Rectangle r1, Rectangle r2) {
		return r1.overlaps(r2);
	}

	public static int random(int min, int max) {
		Random rnd = new Random();
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
