package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class ScoreBoard implements Renderable {
	private List<Brick> bricks = new ArrayList<Brick>();
	
//	private SpriteObject grapplingHook = new SpriteObject("data/sprites/hook.png", 
//			  											  60, 60, 150, 150);
	
	private SpriteObject spaceBomb = new SpriteObject("data/sprites/spacebomb.png", 
													  Config.SPACEBOMB_WIDTH, 
													  Config.SPACEBOMB_HEIGHT, 
													  150, 150);
	
	public ScoreBoard() {
		final float y_start = 285.0f;
		final float line_height = 78.5f;
		final EnumSet<Brick.Behavior> normalBehavior = EnumSet.noneOf(Brick.Behavior.class);
		
		bricks.add(new Brick(Brick.Type.NORMAL, normalBehavior, 0.0f, 0.0f, 
							 new Point(Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 
							 y_start + line_height * 1), 
							 65.0f, Config.BRICK_HEIGHT));
		
		bricks.add(new Brick(Brick.Type.WEAK, normalBehavior, 0.0f, 0.0f,
							 new Point(Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 
							 y_start + line_height * 2), 
							 65.0f, Config.BRICK_HEIGHT));
		
		bricks.add(new Brick(Brick.Type.HARD, normalBehavior, 0.0f,  0.0f,
							 new Point(Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 
							 y_start + line_height * 3), 
							 65.0f, Config.BRICK_HEIGHT));
		
		bricks.add(new Brick(Brick.Type.SOLID, normalBehavior, 0.0f, 0.0f,
							 new Point(Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 
							 y_start + line_height * 4), 
							 65.0f, Config.BRICK_HEIGHT));
		
		bricks.add(new Brick(Brick.Type.POWERUP, normalBehavior, 0.0f, 0.0f,
							 new Point(Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 
							 y_start + line_height * 5), 
							 65.0f, Config.BRICK_HEIGHT));
		
//		grapplingHook.setWidth(90.0f);		
		spaceBomb.setAngle(45.0f);
	}
	
	public void render(SpriteBatch batch) {
		BitmapFont font = FontLoader.getInstance().getFont("small_font", 28);
		BitmapFont smallFont = FontLoader.getInstance().getFont("small_font", 24);
		
		final float fps = Gdx.graphics.getFramesPerSecond();
		final int score = GameState.getScore();
		final int level = GameState.getLevel();
		final int ballsLeft = GameState.getBallsLeft();
		final int spaceBombsLeft = GameState.getSpaceBombsLeft();
		
		font.draw(batch, "FPS: " + fps, Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 0);		
		font.draw(batch, "Score: " + score, Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 50);
		font.draw(batch, "Level: " + (level + 1), Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 90);
		font.draw(batch, "Balls: " + ballsLeft, Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 130);
		font.draw(batch, "Bombs: " + spaceBombsLeft, Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 170);
		
		font.draw(batch, "Brick Types", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 270);
		
		final float lineHeight = smallFont.getLineHeight() + (int) Config.BRICK_HEIGHT + 20;
		final float y_start = 250;
		smallFont.draw(batch, "NORMAL", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, y_start + lineHeight * 1);
		smallFont.draw(batch, "WEAK", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, y_start + lineHeight * 2);
		smallFont.draw(batch, "HARD", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, y_start + lineHeight * 3);
		smallFont.draw(batch, "SOLID", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, y_start + lineHeight * 4);
		smallFont.draw(batch, "POWERUP", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, y_start + lineHeight * 5);		
		
		for (Brick b : bricks)
			b.render(batch);
		
		
		font.draw(batch, "MAIN MENU", Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 25, 
									  Config.WORLD_HEIGHT - (175 + 150));
		
		
		spaceBomb.render(batch, new Point(Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 40,
										  Config.WORLD_HEIGHT - 175), 
						 150, 150);
		
		smallFont.draw(batch, "PRESS HERE", (Config.getInstance().getScreenWidth() - Config.SCOREBOARD_WIDTH + 40) + 45, 
											 Config.WORLD_HEIGHT - 125);
		
		
		
		// Render Separator bar	
		final float segment_height = smallFont.getLineHeight();
		
		for (int i = 0; i <= Config.getInstance().getScreenHeight() / segment_height; i++) {
			
			float x = (Config.WORLD_WIDTH - Config.SCOREBOARD_WIDTH) - 8.0f;
			float y = i * segment_height;			
						
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			
			smallFont.draw(batch, "I", x, y);
		}		
	}
}
