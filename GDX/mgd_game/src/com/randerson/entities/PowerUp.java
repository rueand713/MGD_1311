package com.randerson.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.randerson.mgd_game.GameManager;

public class PowerUp {
	
	String name;
	Rectangle bounds;
	Texture texture;
	int index = -1;
	
	public PowerUp(float x, float y, String texturePath, String name)
	{
		// set the object coordinates
		bounds = GameManager.getRect(x, y, 32, 32);
		
		// set the texture path
		texture = GameManager.getTexture(texturePath);
	}
	
	// getter methods for retrieving class values
	
	//return the x position
	public float getX()
	{	
		return bounds.x;
	}
	
	// return the y position
	public float getY()
	{
		return bounds.y;
	}
	
	// return the rectangle frame
	public Rectangle getBoundary()
	{
		return bounds;
	}
	
	// returns the texture object
	public Texture getTexture()
	{
		return texture;
	}
	
	// returns the object index value (a sort of id)
	public int getIndex()
	{
		return index;
	}
	
	// method for setting the class index value
	public void setIndex(int id)
	{
		index = id;
	}
}
