package com.randerson.mgd_game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2D {

	// static references to the body types
	public static final BodyDef.BodyType DYNAMIC_BODY = BodyType.DynamicBody;
	public static final BodyDef.BodyType STATIC_BODY = BodyType.StaticBody;
	public static final BodyDef.BodyType KINEMATIC_BODY = BodyType.KinematicBody;
	
	
	// method for creating a body definition
	public static BodyDef getBodyDef(BodyDef.BodyType bodyType, float x, float y)
	{
		BodyDef body = new BodyDef();
		
		body.type = bodyType;
		body.position.set(x, y);
		
		return body;
	}
	
	// method for creating a circle shape object
	public static CircleShape getCircle(float radius)
	{
		CircleShape circle = new CircleShape();
		
		circle.setRadius(radius);
		
		return circle;
	}
	
	// method for creating a circle shape object
	public static PolygonShape getPolygon(float width, float height)
	{
		PolygonShape polygon = new PolygonShape();
		
		polygon.setAsBox((width / 2), (height / 2));
		
		return polygon;
	}
	
	// method for creating a circle shape object
	public static PolygonShape getPolygon(float width, float height, Vector2 center)
	{
		PolygonShape polygon = new PolygonShape();
		
		polygon.setAsBox((width / 2), (height / 2), center, 0);
		
		return polygon;
	}
	
	// method for creating a fixture definition
	public static FixtureDef getFixtureDef(Shape shape, float density, float friction, float restitution)
	{
		FixtureDef fxDef = new FixtureDef();
		
		fxDef.shape = shape;
		fxDef.density = density;
		fxDef.friction = friction;
		fxDef.restitution = restitution;
		
		return fxDef;
	}
	
	// method for generating a fixture object
	public static Fixture getFixture(Body body, FixtureDef fixture)
	{
		return body.createFixture(fixture);
	}
	
	// method for generating a body object
	public static Body getBody(World world, BodyDef bodydef)
	{
		return world.createBody(bodydef);
	}
	
}
