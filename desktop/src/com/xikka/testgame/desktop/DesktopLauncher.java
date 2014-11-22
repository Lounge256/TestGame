package com.xikka.testgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.xikka.testgame.TestGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// Match the ~16:9 aspect ratio on iPhone 5
		// Should work with any screen-size, but may as well match roughly mobile dimensions
		config.width = 320;
		config.height = 568;
		new LwjglApplication(new TestGame(), config);
	}
}
