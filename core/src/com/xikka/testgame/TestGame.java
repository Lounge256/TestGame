package com.xikka.testgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.xikka.testgame.screens.game.Game;
import com.xikka.ui.Fonts;

public class TestGame extends ApplicationAdapter {
	
	public static ShapeRenderer renderer;
	
	Stage stage;
	
	@Override
	public void create () {
		Fonts.init();
		
		// Create a shape renderer.
		// We use this to draw shapes and the like -- it does a lot of mathematics for us!
		// There is no need to have more than one as we will only have on thread.
		renderer = new ShapeRenderer();
		
		// Create a stage with a viewport the size of the screen.
		// Any actors that we add to the stage will get drawn and will act (do things).
		stage = new Stage(new ScreenViewport());
		
		// Create an instance of "Game".
		Game game = new Game(stage.getWidth(), stage.getHeight());
		stage.addActor(game);
		
		Gdx.input.setInputProcessor(stage);
		
		// Set background to white (r, g, b, a)
		Gdx.gl.glClearColor(211f/256f, 169f/256f, 96f/256f, 1);
		
		//TODO: Ensure we dispose of all objects at program quit
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}
}
