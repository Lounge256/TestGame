package com.xikka.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Fonts {
	public static BitmapFont smallFont;
	public static void init(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("pixelfont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		parameter.size = 32;

		smallFont = generator.generateFont(parameter);
		generator.dispose();
	}
}

