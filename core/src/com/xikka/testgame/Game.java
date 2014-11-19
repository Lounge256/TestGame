package com.xikka.testgame;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Game extends Group {
	// Create a statically available reference to this class.
	// Ie. we can call Game.self anywhere in our code to refer to this singleton object.
	public static Game self; {
		// We should only ever have one instance.
		if (self != null) System.exit(1);
		self = this;
	}
	
	Game(float width, float height) {
		setSize(width, height);
		
		// Add a GemGrid
		final GemGrid gemGrid = new GemGrid(4,4);
		// Centre the GemGrid on the stage.
		// Note: (0, 0) is in the bottom-left hand corner when drawing/positioning.
		//       but (0, 0) is in the top-left hand corner when dealing with Mouse events.
		//       That's just the way OpenGL does it, and although you can set a y-down camera,
		//       I would hypothesise it isn't worth it.
		gemGrid.setPosition(getWidth()/2 - gemGrid.getWidth()/2, getHeight()/2 - gemGrid.getWidth()/2);
		addActor(gemGrid);
		
		// Add a "button" below the GemGrid
		Button button = new Button();
		button.setSize(gemGrid.getWidth(), 44);
		button.setPosition(getWidth()/2 - button.getWidth()/2, gemGrid.getY() - 2 * button.getHeight());
		addActor(button);
		
		// Add an "onClick" event to the button.
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				// Delete selected gems!
				gemGrid.deleteSelectedGems();
				return true;
			}
		});
	}
}
