package org.x3n0m0rph59.breakout.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.x3n0m0rph59.breakout.App;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1920;
		config.height = 1080;
		config.fullscreen = true;
		
		// DEBUG
//		config.width = 800;
//		config.height = 600;
//		config.fullscreen = false;
		
		config.vSyncEnabled = true;		
//		config.useGL30 = true;
		
		new LwjglApplication(new App(), config);
	}
}
