package com.randerson.entities;

public class Heart extends PowerUp{

	int restoration;
	
	public Heart(float x, float y, String texturePath, String name, int healingPts) {
		super(x, y, texturePath, name);
		
		restoration = healingPts;
	}
	
	public int getRestoration()
	{
		return restoration;
	}

}
