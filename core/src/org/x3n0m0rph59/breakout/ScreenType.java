package org.x3n0m0rph59.breakout;

import com.badlogic.gdx.Screen;

public enum ScreenType {
	HELP {

		@Override
		protected Screen getScreenInstance() {
			return new HelpScreen();
		}
		
	},
	
	MENU {
		
		@Override
		protected Screen getScreenInstance() {
			return new MenuScreen();
		}
	},
	
	GAME {

		@Override
		protected Screen getScreenInstance() {
			return new GameScreen();
		}
		
	};
	
	protected abstract Screen getScreenInstance();
}
