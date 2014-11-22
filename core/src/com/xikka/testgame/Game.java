package com.xikka.testgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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
	
	int level = 4;
	int score;
	int linkLength;
	GemGrid gemGrid;
	
	Game(float width, float height) {
		setSize(width, height);
		
		// Add a GemGrid with size equal to the current level
		gemGrid = new GemGrid(this, level, level++);
		
		// Configure the grid
		gemGrid.prop_replenishOnDelete = false;
		
		// Centre the GemGrid on the stage.
		// Note: (0, 0) is in the bottom-left hand corner when drawing/positioning.
		//       but (0, 0) is in the top-left hand corner when dealing with Mouse events.
		//       That's just the way OpenGL does it, and although you can set a y-down camera,
		//       I would hypothesise it isn't worth it.
		gemGrid.setPosition(getWidth()/2 - gemGrid.getWidth()/2, getHeight()/2 - gemGrid.getHeight()/2);
		addActor(gemGrid);
		
		// Add a "button" at the bottom of the screen
		Button button = new Button();
		button.setSize(gemGrid.getWidth(), 44);
		button.setPosition(getWidth()/2 - button.getWidth()/2, (getWidth() - gemGrid.getWidth())/2);
		addActor(button);
		
		// Add an "onClick" event to the button.
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				// Delete selected gems!
				gemGrid.deleteSelectedGems();
				if (linkLength == 1) {
					score -= 10;
				} else {
					addLinkScore();
				}
				linkLength = 0;
				return true;
			}
		});
	}
	
	void levelComplete() {
		// What to do when the level is over?
		gemGrid.remove();
		
		// For now, delete the last grid and make a new, bigger one!
		gemGrid = new GemGrid(this, level, level++);
		gemGrid.setPosition(getWidth()/2 - gemGrid.getWidth()/2, getHeight()/2 - gemGrid.getHeight()/2);
		addActor(gemGrid);
	}
	
	void addLinkScore(){
		score += (Math.floor(linkLength/10))*5;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Fonts.smallFont.setColor((score >= 0) ? Color.BLACK : Color.RED);
		Fonts.smallFont.draw(batch, "Score: "+score, 5, getHeight() - 5);
		super.draw(batch, parentAlpha);
	}
}
