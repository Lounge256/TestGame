package com.xikka.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.xikka.testgame.TestGame;

public class Button extends Actor {
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
			renderer.setColor(Color.GRAY);
			
			renderer.rect(0, 0, getWidth(), getHeight());
			
			renderer.end();
		}
		// Make sure to start drawing the previous batch again
		batch.begin();
	}
}
