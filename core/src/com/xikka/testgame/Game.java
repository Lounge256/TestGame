package com.xikka.testgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

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
	boolean playing = false;
	LevelSelect LevelSelect;
	GemGrid gemGrid;
	Button button;
	
	boolean finished;
	float totalTime, remainingTime;
	
	Game(float width, float height) {
		setSize(width, height);
		
		// Time limit
		totalTime = remainingTime = 60; // 1 minute
		
		// Add a _random_ GemGrid with size equal to the current level
		//gemGrid = new GemGrid(this, level, level++);
		
		// Add a predesigned GemGrid
		// For now, something we can complete in one chain
		// The more interesting levels have gravity to take into consideration etc.
		// That can wait! We should make a level editor if we want to pump out quality levels...
		
		/*
		gemGrid = new GemGrid(this, new Gem[][] {
			{
				// Column 1
				new Gem(Gem.Shape.Circle, Gem.Colour.Red), new Gem(Gem.Shape.Circle, Gem.Colour.Yellow), new Gem(Gem.Shape.Triangle, Gem.Colour.Green)
			},
			{
				// Column 2
				new Gem(Gem.Shape.Star, Gem.Colour.Yellow), new Gem(Gem.Shape.Square, Gem.Colour.Yellow), new Gem(Gem.Shape.Star, Gem.Colour.Green)
			},
			{
				// Column 3
				new Gem(Gem.Shape.Pentagon, Gem.Colour.Yellow), new Gem(Gem.Shape.Pentagon, Gem.Colour.Blue), new Gem(Gem.Shape.Star, Gem.Colour.Blue)
			}
		});
		*/
		LevelSelect = new LevelSelect(width,height);
		addActor(LevelSelect);
		// Configure the grid
		
		//gemGrid.prop_replenishOnDelete = false;
		//gemGrid.prop_columnSquidge = true;
		
		// Centre the GemGrid on the stage.
		// Note: (0, 0) is in the bottom-left hand corner when drawing/positioning.
		//       but (0, 0) is in the top-left hand corner when dealing with Mouse events.
		//       That's just the way OpenGL does it, and although you can set a y-down camera,
		//       I would hypothesise it isn't worth it.
		/*gemGrid.setPosition(getWidth()/2 - gemGrid.getWidth()/2, getHeight()/2 - gemGrid.getHeight()/2);
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
				if (linkLength == 1) {
					// TODO: Ten points is a bit too drastic. Perhaps lose a life?
					score -= 1;
				} else {
					addLinkScore();
				}
				gemGrid.deleteSelectedGems();
				linkLength = 0;
				return true;
			}
		});*/
	}
	
	void loadLevel(String level){
		//read level file
		FileHandle file = Gdx.files.internal(level+".txt");
		String levelData = file.readString();
		
		// Default values
		int gs = 5;
		boolean replen = false;
		boolean squidge = true;
		boolean rand = true;
		
		//parse key pairs
		String[] values=levelData.split(";");
		for(String str : values){
			String[] keypair=str.split("=");
			// Swap this for simple String comparisons
			if ("gridSize".equals(keypair[0])) {
				gs=Integer.parseInt(keypair[1]);
			} else if ("replenish".equals(keypair[0])) {
				replen=Boolean.parseBoolean(keypair[1]);
			} else if ("squidge".equals(keypair[0])) {
				squidge=Boolean.parseBoolean(keypair[1]);
			} else if ("random".equals(keypair[0])) {
				rand=Boolean.parseBoolean(keypair[1]);
			} else {
				// Unknown property encountered
			}
		}
		
		// TODO : We should refactor to move LevelSelect to it's own class, and have TestGame.java manage all screen transitions
		// This is hacky and gross, but it removes the LevelSelect from the screen.
		clearChildren();
		
		//create gem grid based on key pairs loaded, with defaults above
		gemGrid=new GemGrid(Game.self, gs, gs);
		
		// Assign properties (this avoids a constructor which would probably get too huge)
		gemGrid.prop_replenishOnDelete = replen;
		gemGrid.prop_columnSquidge = squidge;
		
		gemGrid.setPosition(getWidth()/2 - gemGrid.getWidth()/2, getHeight()/2 - gemGrid.getHeight()/2);
		addActor(gemGrid);
		
		//start the timer
		playing=true;
		
		//Sunil's button code
		
		// Add a "button" at the bottom of the screen
		button = new Button();
		button.setSize(gemGrid.getWidth(), 44);
		button.setPosition(getWidth()/2 - button.getWidth()/2, (getWidth() - gemGrid.getWidth())/2);
		addActor(button);
		
		// Add an "onClick" event to the button.
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				// Delete selected gems!
				if (linkLength == 1) {
					// TODO: Ten points is a bit too drastic. Perhaps lose a life?
					score -= 1;
				} else {
					addLinkScore();
				}
				gemGrid.deleteSelectedGems();
				linkLength = 0;
				return true;
			}
		});
	}
	
	void levelComplete() {
		// TODO : Display a "congratulations" modal dialog?
		//        Which in turn takes you back to the level select screen?
		//        For now I'm reinstating the infini-level because it's fun on the train
		
		// What to do when the level is over?
		gemGrid.remove();
		button.remove();
		playing=false;
		
		// TO BE REMOVED:
		// For now, delete the last grid and make a new, bigger one!
		gemGrid = new GemGrid(this, level, level++);
		gemGrid.setPosition(getWidth()/2 - gemGrid.getWidth()/2, getHeight()/2 - gemGrid.getHeight()/2);
		gemGrid.prop_columnSquidge = true;
		if (level >= 7) {
			// At max level, do an infini-grid
			level = 7;
			gemGrid.prop_replenishOnDelete = false;
		}
		addActor(gemGrid);
		
	}
	
	void addLinkScore(){
		//score += (Math.floor(linkLength/10))*5;
		// A more rewarding score -- took out the point-per-click mechanic for now
		// This makes pushing the button feel a bit more exciting
		score += linkLength * linkLength;
		// +1/2 second per gem deleted
		remainingTime += linkLength / 4f;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Fonts.smallFont.setColor((score >= 0) ? Color.BLACK : Color.RED);
		Fonts.smallFont.draw(batch, "Score: "+score, 5, getHeight() - 25);
		batch.end();
		{
			ShapeRenderer renderer = TestGame.renderer;
			renderer.setProjectionMatrix(batch.getProjectionMatrix());
			renderer.setTransformMatrix(batch.getTransformMatrix());
			renderer.translate(getX(), getY(), 0);
			
			renderer.begin(ShapeType.Filled);
			renderer.setColor(Color.BLUE);
			renderer.rect(0, getHeight() - 20, getWidth(), 20);
			
			renderer.setColor(Color.RED);
			renderer.rect(0, getHeight() - 20, getWidth() * (remainingTime/totalTime), 20);
			
			renderer.end();
		}
		batch.begin();
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void act(float delta) {
		if (playing){
			super.act(delta);
			if (!finished && (remainingTime -= delta) <= 0) {
				// Level over -- you lose!
				gemGrid.setTouchable(Touchable.disabled);
				
				Flash flash = new Flash(getWidth(), getHeight());
				flash.flash(Color.RED, null);
				addActor(flash);
				
				finished = true;
				playing=false;
			}
		}
	}
}
