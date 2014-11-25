package com.xikka.testgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class LevelNode extends Actor{
	
	Shape shape;
	Colour colour;
	String levelname;
	
	LevelNode(int type, float size, String lvlname){
		if (type==1){
			shape=Shape.Circle;
			colour=Colour.Red;
		}
		setHeight(size);
		setWidth(size);
		levelname=lvlname;

	}
	
	
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
			
			if (shape == Shape.Circle) {
				renderer.circle(getWidth()/2, getHeight()/2, getWidth()/2);
			}
			Fonts.smallFont.setColor(Color.BLACK);
			Fonts.smallFont.draw(batch, levelname, getHeight()/2, getWidth()/2);
			renderer.end();
		}
		// Make sure to start drawing the previous batch again
		batch.begin();
	}
	
	
	enum Shape{
		Circle
		;
	}
	enum Colour{
		Red(Color.RED)
		;
		Color glColour;
		Colour(Color c) {
			glColour = c;
		}
	}
}
