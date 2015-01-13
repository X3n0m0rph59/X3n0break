package org.x3n0m0rph59.breakout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextAnimationManager implements Stepable, Renderable {
	private static final TextAnimationManager instance = new TextAnimationManager();
	
	private final List<TextAnimation> textAnimations = new ArrayList<TextAnimation>();
	
	public static TextAnimationManager getInstance() {
		return instance;
	}
	
	public void add(String text) {
		textAnimations.add(new TextAnimation(text));
	}
	
	public void clear() {
		textAnimations.clear();
	}
	
	@Override
	public void render(SpriteBatch batch) {
//		for (final TextAnimation ta : textAnimations) {
//			ta.render();
//		}
		
		if (textAnimations.size() > 0) {
			final TextAnimation ta = textAnimations.get(0);
			ta.render(batch);
		}
	}

	@Override
	public void step(float delta) {
//		for (final TextAnimation ta : textAnimations) {
//			ta.step();
//		}
		
		if (textAnimations.size() > 0) {
			final TextAnimation ta = textAnimations.get(0);
			ta.step(delta);
		}
		
		doCleanup();
	}
	
	private void doCleanup() {
		final Iterator<TextAnimation> i = textAnimations.iterator();		
		while (i.hasNext()) {
			final TextAnimation t = i.next();
			
			if (t.isDestroyed()) {
				i.remove();
			}
		}
	}

}
