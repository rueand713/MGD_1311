package com.randerson.entities;

public class Coin extends PowerUp {

	int value;
	
	public Coin(float x, float y, String texturePath, String name, int value) {
		super(x, y, texturePath, name);
		
		this.value = value;
	}
	
	// return the value
	public int getValue()
	{
		return value;
	}

}
