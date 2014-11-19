package com.xikka.testgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Gem extends Actor {
	// Store a shape and colour for each gem
	// The definitions are at the bottom of the page (I like to keep inner classes at the bottom)
	Shape shape;
	Colour colour;
	
	boolean selected;
	
	int column, row;
	
	// Initialise a gem with a random shape and colour.
	Gem() {
		shape = Shape.random();
		colour = Colour.random();
	}
	
	boolean isOrthogonallyAdjacent(Gem gem) {
		final int dx = (int) Math.abs(column - gem.column), dy = (int) Math.abs(row - gem.row);
		return dx * dy == 0 && dx + dy == 1;
	}
	
	// Do these gems share an attribute?
	boolean matches(Gem gem) {
		return colour == gem.colour || shape == gem.shape;
	}
	
	// Add a little animation to show "explosion"
	void explode() {
		addAction(Actions.sequence(Actions.parallel(
			Actions.moveBy(getWidth()/2, getHeight()/2, 0.2f),
			Actions.sizeTo(0, 0, 0.2f)
		),
		Actions.run(
			new Runnable() {
				@Override
				public void run() {
					remove();
				}
			}
		)));
	}
	
	// Draw the gem according to its colour and shape!
	//The draw method of an actor can be overridden to draw whatever we want!
	@Override
	public void draw(Batch batch, float parentAlpha) {
		// End the sprite batch (used for drawing images)
		batch.end();
		{
			// Start rendering basic shapes
			ShapeRenderer renderer = TestGame.renderer;
			renderer.setProjectionMatrix(batch.getProjectionMatrix());
			renderer.setTransformMatrix(batch.getTransformMatrix());
			
			// Translate the renderer so we draw relative to this Actor's position
			renderer.translate(getX(), getY(), 0);

			renderer.begin(ShapeType.Filled);
			renderer.setColor(colour.glColour);
			
			if (shape == Shape.Square) {
				renderer.rect(0, 0, getWidth(), getHeight());
			} else if (shape == Shape.Circle) {
				renderer.circle(getWidth()/2, getHeight()/2, getWidth()/2);
			} else if (shape == Shape.Triangle) {
				renderer.triangle(0, 0, getWidth(), 0, getWidth()/2, getHeight());
			} else if (shape == Shape.Pentagon) {
				renderer.triangle((float)(getWidth()*0.191), 0, getWidth()/2, getHeight(), (float)(getWidth()*0.809), 0);
				renderer.triangle((float)(getWidth()*0.191), 0, getWidth()/2, getHeight(), 0, (float)(getHeight()*0.637));
				renderer.triangle((float)(getWidth()*0.809), 0, getWidth()/2, getHeight(), getWidth(), (float)(getHeight()*0.637));
			} else if (shape == Shape.Star) {
				renderer.triangle((float)(getWidth()*0.191), 0, getWidth()/2, getHeight(), (float)(getWidth()*0.724), (float)(getHeight()*0.406));
				renderer.triangle((float)(getWidth()*0.191), 0, (float)(getWidth()*0.388), (float)(getHeight()*0.637), getWidth(), (float)(getHeight()*0.637));
				renderer.triangle(0, (float)(getHeight()*0.637), (float)(getWidth()*0.612), (float)(getHeight()*0.637), (float)(getWidth()*0.809), 0);
			}
			
			renderer.end();
			
			// Draw a border around the shape if it's selected
			if (selected) {
				renderer.begin(ShapeType.Line);
				Gdx.gl.glLineWidth( 9 );
				renderer.setColor(Color.ORANGE);
				renderer.circle(getWidth()/2, getHeight()/2, getWidth()/2);
				renderer.end();
			}
		}
		// Make sure to start drawing the previous batch again
		batch.begin();
	}
	
	enum Shape {
		Circle,
		Square,
		Triangle,
		Pentagon,
		Star
		;
		static Shape random() {
			return Shape.values()[(int) (Math.random() * Shape.values().length)];
		}
	}
	enum Colour {
		Red(Color.RED),
		Green(Color.GREEN),
		Blue(Color.BLUE),
		Yellow(Color.YELLOW)
		;
		Color glColour;
		Colour(Color c) {
			glColour = c;
		}
		static Colour random() {
			return Colour.values()[(int) (Math.random() * Colour.values().length)];
		}
	}
}
