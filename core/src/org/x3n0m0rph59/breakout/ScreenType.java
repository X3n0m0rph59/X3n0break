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
	
	LEVEL_SET_SELECTOR {
		
		@Override
		protected Screen getScreenInstance() {
			return new LevelSetSelectorScreen();
		}
	},
	
	GAME {

		@Override
		protected Screen getScreenInstance() {
			return new GameScreen();
		}
		
	},
	
	SETTINGS {

		@Override
		protected Screen getScreenInstance() {
			return new SettingsScreen();
		}
		
	},
	
	HIGHSCORE {

		@Override
		protected Screen getScreenInstance() {
			return new HighScoreScreen();
		}
		
	};
	
	protected abstract Screen getScreenInstance();
}
