package org.x3n0m0rph59.breakout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class EffectManager {
	private static final EffectManager instance = new EffectManager();	
	private List<Effect> effectList = new ArrayList<Effect>();
	
		
	public void addEffect(Effect.Type type) {
		effectList.add(new Effect(type, Config.SYNC_FPS * Config.EFFECT_DURATION));
		
		switch (type) {
		case BOTTOM_WALL:
			App.getGameScreen().addTextAnimation("Bottom Wall!");
			break;
			
		case ENLARGE_PADDLE:
			App.getGameScreen().addTextAnimation("Enlarge!");
			break;
			
		case FIREBALL:
			App.getGameScreen().addTextAnimation("Fireball!");
			break;
			
		case MULTIBALL:
			App.getGameScreen().addTextAnimation("Multiball");
			break;
			
		case PADDLE_GUN:
			App.getGameScreen().addTextAnimation("Guns!");
			break;
			
		case SHRINK_PADDLE:
			App.getGameScreen().addTextAnimation("Shrink!");
			break;	
			
		case STICKY_BALL:
			App.getGameScreen().addTextAnimation("Sticky Ball!");
			break;
			
		case SPEED_UP:
			App.getGameScreen().addTextAnimation("Speed Up!");
			break;	
			
		case SLOW_DOWN:
			App.getGameScreen().addTextAnimation("Slow Down!");
			break;
			
		default:
			throw new RuntimeException("Unsupported type: " + type);		
		}
				
		Logger.debug("New active effect: " + type);
	}
	
	public void expireEffect(Effect e) {		
		switch (e.getType()) {
		case BOTTOM_WALL:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.BOTTOM_WALL))
				App.getGameScreen().addTextAnimation("No more Bottom Wall!");
			break;
			
		case ENLARGE_PADDLE:
			App.getGameScreen().addTextAnimation("Shrink again!");
			break;
			
		case FIREBALL:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.FIREBALL))
				App.getGameScreen().addTextAnimation("Fireball vanished!");
			break;
			
		case MULTIBALL:			
			break;
			
		case PADDLE_GUN:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.PADDLE_GUN))
				App.getGameScreen().addTextAnimation("Guns jammed!");
			break;
			
		case SHRINK_PADDLE:
			App.getGameScreen().addTextAnimation("Grow back!");
			break;	
			
		case STICKY_BALL:
			// Test if we really are the last active effect of this type
			if (!isEffectActive(Effect.Type.STICKY_BALL))
				App.getGameScreen().addTextAnimation("No more Sticky Ball!");
			break;
			
		case SPEED_UP:
			App.getGameScreen().addTextAnimation("Slow down again!");
			break;	
			
		case SLOW_DOWN:
			App.getGameScreen().addTextAnimation("Speed up again!");
			break;
			
		default:
			throw new RuntimeException("Unsupported type: " + e.getType());		
		}
		
		Logger.debug("Effect expired: " + e.getType());
	}
	
	public boolean isEffectActive(Effect.Type effect) {
		for (Effect e : effectList) {
			if (e.getType() == effect)
				return true;			
		}
		
		return false;
	}
	
	public boolean isEffectInGracePeriod(Effect.Type effect) {
		List<Effect> candidates = new ArrayList<Effect>();
		
		for (Effect e : effectList) {
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
	
	public void step() {		
		Iterator<Effect> i = effectList.iterator();				
		while (i.hasNext()) {
			Effect e = i.next();
			e.step();
			
			if (e.isExpired()) {
				expireEffect(e);		
				i.remove();
			}
		}
	}
	
	public void clearEffects() {
		for (Effect e : effectList)
			e.expire();
		
		effectList.clear();
	}
	
	public static EffectManager getInstance() {
		return instance;
	}	
}
