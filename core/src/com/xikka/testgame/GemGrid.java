package com.xikka.testgame;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
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
	static final float SIZE = 70, PADDING = 5;
	Gem [][] grid;
	Gem lastGem;
	
	boolean canClick = true;
	
	List<Gem> selectedGems = new LinkedList<Gem>();
	
	GemGrid(int columns, int rows) {
		// Create a grid of gems!
		grid = new Gem[columns][rows];
		
		// Set the size of this actor to be large enough to accomodate all gems
		setSize(columns * SIZE, rows * SIZE);
		
		// Add all gems to the array, and add them as children
		// See bottom of page
		replenish();
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
							grid[i][j].addAction(Actions.moveBy(0, -diff * SIZE, 0.2f * diff));
							grid[i][j].row = newRow;
							grid[i][newRow] = grid[i][j];
							grid[i][j] = null;
						}
					}
				}
				addAction(sequence(delay(maxWait), Actions.run(new Runnable() {
					@Override
					public void run() {
						// Grow new crystals!
						replenish();
						addAction(sequence(delay(0.2f), Actions.run(new Runnable() {
							@Override
							public void run() {
								lastGem = null;
								canClick = true;
							}
						})));
					}
				})));
			}
		})));
	}
	
	void replenish() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				final float x = i * SIZE, y = getHeight() - j * SIZE;
				if (grid[i][j] == null) {
					final Gem gem = new Gem();
					
					grid[i][j] = gem;
					
					gem.column = i;
					gem.row = j;
					
					gem.setSize(0, 0);
					gem.setPosition(x + SIZE/2, y + SIZE/2);
					//gem.grow
					gem.addAction(parallel(
							moveTo(x + PADDING, y + PADDING, 0.2f),
							sizeTo(SIZE - 2 * PADDING, SIZE - 2 * PADDING, 0.2f)
					));
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
}
