package org.x3n0m0rph59.breakout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.x3n0m0rph59.breakout.Effect.Type;

import com.badlogic.gdx.Gdx;

public final class EffectManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8434448834555883310L;
	
	private static EffectManager instance = new EffectManager();	
	private List<Effect> effectList = new ArrayList<Effect>();
	
		
	public void addEffect(Effect.Type type) {		
		// perform basic sanity checking on new effects
		if (!isEffectAllowed(type)) {
			Logger.debug("Disallowed effect: " + type);
			
			return;
			
		} else {
			effectList.add(new Effect(type, Gdx.graphics.getFramesPerSecond()
					* Config.EFFECT_DURATION));

			switch (type) {
			case BOTTOM_WALL:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Bottom Wall!");
				break;

			case EXPAND_PADDLE:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Expand!");
				break;

			case FIREBALL:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Fireball!");
				break;

			case MULTIBALL:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Multiball");
				break;

			case PADDLE_GUN:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Guns!");
				break;

			case SHRINK_PADDLE:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Shrink!");
				break;

			case STICKY_BALL:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Sticky Ball!");
				break;

			case SPEED_UP:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Speed Up!");
				break;

			case SLOW_DOWN:
				((App) Gdx.app.getApplicationListener()).getGameScreen()
						.addTextAnimation("Slow Down!");
				break;

			default:
				throw new RuntimeException("Unsupported type: " + type);
			}

			Logger.debug("New active effect: " + type);
		}
	}
	
	public void expireEffect(Effect e) {		
		switch (e.getType()) {
		case BOTTOM_WALL:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.BOTTOM_WALL))
				((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("No more Bottom Wall!");
			break;
			
		case EXPAND_PADDLE:
			((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("Shrink again!");
			break;
			
		case FIREBALL:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.FIREBALL))
				((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("Fireball vanished!");
			break;
			
		case MULTIBALL:			
			break;
			
		case PADDLE_GUN:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.PADDLE_GUN))
				((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("Guns jammed!");
			break;
			
		case SHRINK_PADDLE:
			((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("Grow back!");
			break;	
			
		case STICKY_BALL:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.STICKY_BALL))
				((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("No more Sticky Ball!");
			break;
			
		case SPEED_UP:
			((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("Slow down again!");
			break;	
			
		case SLOW_DOWN:
			((App) Gdx.app.getApplicationListener()).getGameScreen().addTextAnimation("Speed up again!");
			break;
			
		default:
			throw new RuntimeException("Unsupported type: " + e.getType());		
		}
		
		Logger.debug("Effect expired: " + e.getType());
	}
	
	public boolean isEffectActive(Effect.Type effect) {
		for (final Effect e : effectList) {
			if (e.getType() == effect)
				return true;			
		}
		
		return false;
	}
	
	public boolean isEffectInGracePeriod(Effect.Type effect) {
		List<Effect> candidates = new ArrayList<Effect>();
		
		for (final Effect e : effectList) {
			if (e.getType() == effect) {
				candidates.add(e);
			}
		}
		
		Collections.sort(candidates, new Comparator<Effect>() {
							public int compare(Effect o1, Effect o2) { 
								return (int) (o2.getEffectDuration() - o1.getEffectDuration());
							};
						});
		
		if (candidates.size() > 0)
			if (candidates.get(0).getEffectDuration() <= Config.EFFECT_GRACE_PERIOD)
				return true;
		
		return false;
	}
	
	public boolean isEffectAllowed(Effect.Type type) {
		boolean allowed = true;
		
		switch (type) {
		case BOTTOM_WALL:
			break;
			
		case EXPAND_PADDLE:
			if (((App) Gdx.app.getApplicationListener()).getGameScreen().getPaddle().getWidth() >= 
					Config.PADDLE_MAX_WIDTH)
				allowed = false;
			break;
			
		case FIREBALL:
			break;
			
		case MULTIBALL:
			break;
			
		case PADDLE_GUN:
			break;
			
		case SHRINK_PADDLE:
			if (((App) Gdx.app.getApplicationListener()).getGameScreen().getPaddle().getWidth() <= 
				Config.PADDLE_MIN_WIDTH)
				allowed = false;
			break;
			
		case SLOW_DOWN:
			if (isEffectActive(Type.SLOW_DOWN))
				allowed = false;
			break;
			
		case SPEED_UP:
			if (isEffectActive(Type.SPEED_UP))
				allowed = false;
			break;
			
		case STICKY_BALL:
			break;
			
		default:
			break;		
		}
		
		return allowed;
	}
	
	public void step(float delta) {		
		final Iterator<Effect> i = effectList.iterator();				
		while (i.hasNext()) {
			Effect e = i.next();
			e.step(delta);
			
			if (e.isExpired()) {
				expireEffect(e);		
				i.remove();
			}
		}
	}
	
	public void clearEffects() {
		for (final Effect e : effectList)
			e.expire();
		
		effectList.clear();
	}
	
	public static EffectManager getInstance() {
		return instance;
	}

	static void setInstance(EffectManager e) {
		instance = e;
	}	
}
