package com.randerson.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Ambience {

	Texture TEXTURE;
	String NAME;
	Boolean HAS_TREASURE;
	Item TREASURE;
	Fixture FIXTURE;
	
	public Ambience(Texture texture, String name, boolean hasTreasure)
	{
		TEXTURE = texture;
		NAME = name;
		HAS_TREASURE = hasTreasure;
	}
	
	public void setTreasure(Item item)
	{
		TREASURE = item;
	}
	
	public Item getTreasure()
	{
		return TREASURE;
	}
	
	public void definePhysics(World world, int x, int y, float width, float height)
	{
		// get a new body definition for the ambient object
		BodyDef ambDef = new BodyDef();
		
		// set the type and coordinates for the object
		ambDef.type = BodyType.StaticBody;
		ambDef.position.set(x, y);
		
		// create a body object within the defined world with the body definition
		Body body = world.createBody(ambDef);
		
		// get a boundary shape
		PolygonShape boundingShape = new PolygonShape();
		
		// set the shape to be a box
		boundingShape.setAsBox(width, height);
		
		// create a fixture from the bounding shape
		FIXTURE = body.createFixture(boundingShape, 0.0f);
		
		// dispose of the shape
		boundingShape.dispose();
	}
	
	public Fixture getFixture()
	{
		return FIXTURE;
	}
	
	public Texture getTexture()
	{
		return TEXTURE;
	}
}
