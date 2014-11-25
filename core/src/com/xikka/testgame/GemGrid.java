package com.xikka.testgame;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * GemGrid
 * A group to contain a grid of gems.
 * A Group is an Actor that have children.
 */
public class GemGrid extends Group {
	
	// Gem grid properties
		// adding prop_ so we can ctrl-f them easily
	boolean prop_replenishOnDelete = false;
	boolean prop_columnSquidge = false;
	
	static float size = 100, PADDING = 5;
	int initialColumns, filledColumns;
	Gem [][] grid;
	Gem lastGem;
	Game game;
	
	boolean canClick = true;
	
	List<Gem> selectedGems = new LinkedList<Gem>();
	
	// Generate a random level
	GemGrid(Game game, int columns, int rows) {
		// Maintain reference to the game it controls
		this.game = game;
		
		// Create a grid of gems!
		initialColumns = filledColumns = columns;
		grid = new Gem[columns][rows];
		
		// Set the size of this actor to be large enough to accommodate all gems
		if (columns * size > Game.self.getWidth()) {
			size = (Game.self.getWidth() / columns)-10;
		}
		setSize(columns * size, rows * size);
		
		// Add all gems to the array, and add them as children
		// See bottom of page
		replenish(null, true);
	}
	
	// Generate a predesigned level
	GemGrid(Game game, Gem [][] level) {
		// Maintain reference to the game it controls
		this.game = game;
		
		// Create a grid of gems!
		int columns = level.length, rows = level[0].length; 
		initialColumns = filledColumns = columns;
		grid = new Gem[columns][rows];
		
		// Set the size of this actor to be large enough to accommodate all gems
		if (columns * size > Game.self.getWidth()) {
			size = (Game.self.getWidth() / columns)-10;
		}
		setSize(columns * size, rows * size);
		
		replenish(level, true);
	}
	
	void deleteSelectedGems() {
		//Can't click on the grid whilst doing this
		canClick = false;
		
		// Delete each selected gem!
		for (Gem gem : selectedGems) {
			gem.selected = false;
			gem.explode();
			grid[gem.column][gem.row] = null;
		}
		selectedGems = new LinkedList<Gem>();
		
		addAction(sequence(delay(0.2f), run(new Runnable() {
			@Override
			public void run() {
				// Go through all gems and see if they have anything below them!
				// (Start at the row one from the bottom as the bottom row can't fall further)
				assert(grid.length > 0);
				int rows = grid[0].length, cols = grid.length;
				float maxWait = 0;
				for (int j = rows - 2; j >= 0; j--) {
					// In this row see if there is a gem in any column
					for (int i = cols - 1; i >= 0; i--) {
						if (grid[i][j] != null) {
							// There is a gem here. Move it as far down as we can!
							int newRow = -1;
							for (int k = j + 1; k < grid[i].length; k++) {
								if (grid[i][k] == null) {
									// There is no gem here - let's slide down.
									newRow = k;
									continue;
								} else {
									// There is a gem here. We can't move here.
									break;
								}
							}
							if (newRow == -1) {
								// It was gems all the way down: no movement.
								continue;
							}
							
							// Animation
							int diff = newRow - j;
							if (0.2f * diff > maxWait) maxWait = 0.2f * diff;
							grid[i][j].addAction(Actions.moveBy(0, -diff * size, 0.2f * diff));
							grid[i][j].row = newRow;
							grid[i][newRow] = grid[i][j];
							grid[i][j] = null;
						}
					}
				}
				// Column squidge!
				// Gotta love a good algorithm
				// Go through each column and see if there are any empty ones
				if (prop_columnSquidge) {
					int currentColumns = initialColumns;
					column:
					for (int i = 0; i < grid.length; i++) {
						for (int j = 0; j < grid[i].length; j++) {
							if (grid[i][j] != null) {
								continue column;
							}
						}
						
						// Column is empty
						boolean didSwap = false;
						// Bring every Gem which is further right as far left as possible
						for (int ii = i + 1; ii < grid.length; ii++) {
							
							// Find the furthest left empty column before this one (we might not need it):
							int empty = ii - 1;
							findEmpty:
							for (int emptyCandidate = ii - 2; emptyCandidate >= 0; emptyCandidate--) {
								// Is this column empty?
								for (int jj = 0; jj < grid[emptyCandidate].length; jj++)
									if (grid[emptyCandidate][jj] != null)
										break findEmpty;
								empty = emptyCandidate;
							}
							
							for (int j = 0; j < grid[ii].length; j++) {
								// No Gem, no need to move over
								if (grid[ii][j] == null)
									continue;
								
								grid[empty][j] = grid[ii][j];
								grid[ii][j] = null;
								didSwap = true;
							}
						}
						// This column is definitely empty since we haven't put any gems there.
						if (!didSwap)
							currentColumns--;
					}
					if (currentColumns != filledColumns) {
						// There has been a squidge!
						//int squidgeSize = filledColumns - currentColumns;
						final float time = (filledColumns - currentColumns) * 0.2f/2;
						final float offset = (getWidth() - (currentColumns * size))/2;
						
						filledColumns = currentColumns;
						
						addAction(sequence(delay(maxWait), Actions.run(new Runnable() {
							@Override
							public void run() {
								// Recentre grid (only left-most columns are filled)
								for (int i = 0; i < grid.length; i++) {
									for (int j = 0; j < grid[i].length; j++) {
										if (grid[i][j] != null) {
											grid[i][j].column = i;
											//grid[i][j].setX(i * size + offset + PADDING);
											grid[i][j].addAction(
													Actions.moveTo(i * size + offset + PADDING, grid[i][j].getY(), time)
											);
										}
									}
								}
							}
						})));
						maxWait += time;
					}
				}
				addAction(sequence(delay(maxWait), Actions.run(new Runnable() {
					@Override
					public void run() {
						// Grow new crystals!
						if (prop_replenishOnDelete) { 
							replenish(null, true);
							addAction(sequence(delay(0.2f), Actions.run(new Runnable() {
								@Override
								public void run() {
									lastGem = null;
									canClick = true;
								}
							})));
						} else {
							lastGem = null;
							canClick = true;
						}
						
						// Is the level complete?
						gemCheck: {
							for (int i = 0; i < grid.length; i++) {
								for (int j = 0; j < grid[i].length; j++) {
									if (grid[i][j] != null) {
										break gemCheck;
									}
								}
							}
							game.levelComplete();
						}
					}
				})));
			}
		})));
	}
	
	void replenish(Gem [][] level, boolean animateGrowth) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				// Should be j+1 otherwise the last row isn't actually at the y-position
				final float x = i * size, y = getHeight() - (j + 1) * size;
				if (grid[i][j] == null) {
					final Gem gem;
					if (level == null)
						gem = new Gem();
					else
						gem = level[i][j];
					
					grid[i][j] = gem;
					
					gem.column = i;
					gem.row = j;
					
					if (animateGrowth) {
						gem.setSize(0, 0);
						gem.setPosition(x + size/2, y + size/2);
						gem.addAction(parallel(
								moveTo(x + PADDING, y + PADDING, 0.2f),
								sizeTo(size - 2 * PADDING, size - 2 * PADDING, 0.2f)
						));
					} else {
						gem.setSize(size - 2 * PADDING, size - 2 * PADDING);
						gem.setPosition(x + PADDING, y + PADDING);
					}
					addActor(gem);
					
					// Add an "onClick" event to the gem.
					gem.addListener(new InputListener() {
						@Override
						public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
							if (!canClick) return false;
							
							// If there was no lastGem, then we are definitely allowed to select this one.
							// Otherwise all of the following must be true:
							//      - this gem must be adjacent
							//      - this gem must share an attribute
							//      - this gem must not be selected
							if (
								lastGem == null
									||
								(
									lastGem.isOrthogonallyAdjacent(gem) &&
									lastGem.matches(gem) &&
									!gem.selected
								)
							) {
								game.linkLength+=1;
								lastGem = gem;
								gem.selected = true;
								selectedGems.add(gem);
							} else {
								// Not allowed to select that gem!
								Group parent = getParent();
								Flash flash = new Flash(parent.getWidth(), parent.getHeight());
								flash.flash(Color.RED, null);
								parent.addActor(flash);
							}
							return true;
						}
					});
				}
			}
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();
		ShapeRenderer renderer = TestGame.renderer;
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.translate(getX(), getY(), 0);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    
		renderer.begin(ShapeType.Line);
		renderer.setColor(new Color(.8f,0,0,0.8f));
		float px = -1, py = -1; 
		for (Gem g : selectedGems) {
			if (px > 0) {
				renderer.line(px, py, g.getX() + g.getWidth()/2, g.getY() + g.getHeight()/2);
			}
			px = g.getX() + g.getWidth()/2;
			py = g.getY() + g.getHeight()/2;
			
			// If this is the last gem, draw a border around the shape
			if (g == lastGem) {
				Gdx.gl.glLineWidth( 9 );
				renderer.setColor(Color.ORANGE);
				renderer.circle(g.getX() + g.getWidth()/2, g.getY() + g.getHeight()/2, g.getWidth()/2);
			}
		}
		renderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
