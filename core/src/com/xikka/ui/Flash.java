package com.xikka.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.xikka.testgame.TestGame;

public class Flash extends Actor {
	public Flash(float width, float height) {
		setSize(width, height);
	}
	
	public void flash(Color colour, final Runnable onFinish) {
		// There is already a flash running
		
		setColor(colour);
		setTouchable(Touchable.disabled);
		addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {

			@Override
			public void run() {
				if (onFinish != null)
					onFinish.run();
				remove();
			}
			
		})));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		{
			ShapeRenderer renderer = TestGame.renderer;
			renderer.setProjectionMatrix(batch.getProjectionMatrix());
		    renderer.setTransformMatrix(batch.getTransformMatrix());
		    renderer.translate(getX(), getY(), 0);
		    
		    Gdx.gl.glEnable(GL20.GL_BLEND);
		    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		    
		    renderer.begin(ShapeType.Filled);
		    Color c = getColor();
		    renderer.setColor(c.r, c.g, c.b, c.a);
		    renderer.rect(0, 0, getWidth(), getHeight());
		    renderer.end();
		    
		    Gdx.gl.glDisable(GL20.GL_BLEND);
		}
		batch.begin();
	}
}
