package org.x3n0m0rph59.breakout.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.x3n0m0rph59.breakout.App;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		config.useWakelock = true;
		config.useImmersiveMode = true;
//		config.useGLSurfaceView20API18 = true;
		
		initialize(new App(), config);
	}
}
