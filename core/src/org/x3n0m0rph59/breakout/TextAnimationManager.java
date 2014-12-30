package org.x3n0m0rph59.breakout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextAnimationManager implements Stepable, Renderable {
	private static TextAnimationManager instance = new TextAnimationManager();
	
	private List<TextAnimation> textAnimations = new ArrayList<TextAnimation>();
	
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
//		for (TextAnimation ta : textAnimations) {
//			ta.render();
//		}
		
		if (textAnimations.size() > 0) {
			TextAnimation ta = textAnimations.get(0);
			ta.render(batch);
		}
	}

	@Override
	public void step() {
//		for (TextAnimation ta : textAnimations) {
//			ta.step();
//		}
		
		if (textAnimations.size() > 0) {
			TextAnimation ta = textAnimations.get(0);
			ta.step();
		}
		
		doCleanup();
	}
	
	private void doCleanup() {
		Iterator<TextAnimation> i = textAnimations.iterator();		
		while (i.hasNext()) {
			TextAnimation t = i.next();
			
			if (t.isDestroyed()) {
				i.remove();
			}
		}
	}

}
