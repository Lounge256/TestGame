package com.xikka.testgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class LevelSelect extends Group{

	public static LevelSelect self; {
		if (self != null) System.exit(1);
		self = this;
	}
	
	

	LevelSelect(float width, float height){
		setSize(width,height);
		
		//load file containing level button positions
		FileHandle file = Gdx.files.local("levelmap.txt");
		String levelmap = file.readString();
		
		for (String nodedata:levelmap.split(";")){
			String[] nodevalues = nodedata.split("/");
			//  [0]xpos/[1]ypos/[2]symbol/[3]size/[4]name;
			final LevelNode node;
			node = new LevelNode(Integer.parseInt(nodevalues[2]), Integer.parseInt(nodevalues[3]), nodevalues[4]);
			node.setPosition(Float.parseFloat(nodevalues[0])-(Float.parseFloat(nodevalues[3])/2), Float.parseFloat(nodevalues[1])-(Float.parseFloat(nodevalues[3])/2));
			addActor(node);
		}
		
		//read level properties and populate
		
	}
}