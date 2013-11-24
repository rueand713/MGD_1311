package com.randerson.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.randerson.mgd_game.Box2D;

public class Ambience {

	Texture TEXTURE;
	String NAME;
	Boolean HAS_TREASURE;
	Item TREASURE;
	Fixture FIXTURE;
	Vector2 POSITION;
	
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
	
	public void setStaticPosition(float x, float y)
	{
		POSITION = new Vector2(x, y);
	}
	
	public Vector2 getPosition()
	{
		return POSITION;
	}
	
	public void setFixture(Fixture fixture)
	{
		FIXTURE = fixture;
	}
	
	public void definePhysics(World world, BodyType bodytype, int x, int y, float width, float height)
	{
		// get a new body definition for the ambient object
		BodyDef ambDef = new BodyDef();
		
		// set the type and coordinates for the object
		ambDef.type = bodytype;
		ambDef.position.set(x, y);
		
		// create a body object within the defined world with the body definition
		Body body = world.createBody(ambDef);
		
		// get a boundary shape
		PolygonShape boundingShape = Box2D.getPolygon(width, height, new Vector2(width / 2, height / 2));
		
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
