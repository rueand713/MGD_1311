package com.randerson.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Actors {

	private int MAX_HP;
	private int HP;
	private int STR;
	private int END;
	private int CON;
	private int PDAM;
	private int SDAM;
	private int MOD;
	private int[] XP = {0, 0};
	private int LEVEL;
	private Texture TEXTURE;
	private Rectangle BOUNDARY;
	private PolygonShape POLY_BOUNDARY;
	private CircleShape CIRC_BOUNDARY;
	private ArrayList<Item> INVENTORY;
	private Fixture FIXTURE;
	
	protected int friction;
	protected int density;
	protected int restitution;
	
	public Actors(int hp_modifier, int strength, int endurance, int constitution, int level, Rectangle boundary, Texture texture)
	{
		// set the object texture
		TEXTURE = texture;
		
		// set the object boundings
		BOUNDARY = boundary;
		
		// set the mod level
		MOD = hp_modifier;
		
		// set the entity level
		LEVEL = level;
		
		// determine entity hitpoint level
		HP = MAX_HP = (int) (strength * (level * 0.5)) + (endurance * hp_modifier) + (int) (constitution * (level * 0.25));
		
		// determine physical damage level
		PDAM = (int) (strength * 0.25);
		
		// determine the special damage level
		SDAM = (int) (constitution * 0.5);
	}
	
	public void definePhysics(World world, boolean usePolygon, float radius)
	{
		Shape shape;
		
		if (usePolygon)
		{
			shape = Box2D.getPolygon(radius);
		}
		else
		{
			shape = Box2D.getCircle(radius);
		}
		
		BodyDef bodydef = Box2D.getBodyDef(Box2D.DYNAMIC_BODY, BOUNDARY.x, BOUNDARY.y);
		Body body = Box2D.getBody(world, bodydef);
		
		FixtureDef fxDef = Box2D.getFixtureDef(shape, density, friction, restitution);
		FIXTURE = Box2D.getFixture(body, fxDef);
	}
	
	// ***** SETTER METHODS *****
	
	public void setPolyBoundary(float radius)
	{
		POLY_BOUNDARY = Box2D.getPolygon(radius);
	}
	
	public void setCircleBoundary(float radius)
	{
		CIRC_BOUNDARY = Box2D.getCircle(radius);
	}
	
	// method for adding item to inventory
	public void addItem(Item item)
	{
		INVENTORY.add(item);
	}
	
	// method for increasing entity xp
	public void addXp(int xp)
	{
		XP[0] += xp;
		XP[1] += xp;
	}
	
	public void setTexture(Texture texture)
	{
		TEXTURE = texture;
	}
	
	// method for setting total xp value
	public void setTotalXp(int xp)
	{
		XP[1] = xp;
	}
	
	// method for setting current xp value
	public void setCurrentXp(int xp)
	{
		XP[0] = xp;
	}
	
	public void incrementLevel()
	{
		LEVEL++;
	}
	
	public void setSdam(int sdam)
	{
		SDAM = sdam;
	}
	
	public void setPdam(int pdam)
	{
		PDAM = pdam;
	}
	
	public void setHp(int hp)
	{
		HP = MAX_HP = hp;
	}
	
	// ***** GETTER METHODS *****
	
	// method for getting array of items from the inventory
	public ArrayList<Item> getInventory()
	{
		return INVENTORY;
	}
	
	// method for getting hp
	public int getHp()
	{
		return HP;
	}
	
	// method for getting max hp
	public int getMaxHp()
	{
		return MAX_HP;
	}
	
	// method for getting the str value
	public int getStr()
	{
		return STR;
	}
	
	// method getting the endurance value
	public int getEnd()
	{
		return END;
	}
	
	// method for getting constitution value
	public int getCon()
	{
		return CON;
	}
	
	// method for getting the physical damage value
	public int getPdam()
	{
		return PDAM;
	}
	
	// method for getting the special damage value
	public int getSdam()
	{
		return SDAM;
	}
	
	// method for getting the modifier level
	public int getMod()
	{
		return MOD;
	}
	
	// method for getting the level
	public int getLvl()
	{
		return LEVEL;
	}
	
	// method for getting the xp
	public int getTotalXp()
	{
		return XP[1];
	}
	
	// method for getting the current xp
	public int getCurrentXp()
	{
		return XP[0];
	}
	
	public Texture getTexture()
	{
		return TEXTURE;
	}
	
	public Rectangle getBoundary()
	{
		return BOUNDARY;
	}
	
	public PolygonShape getPolyBoundary()
	{
		return POLY_BOUNDARY;
	}
	
	public CircleShape getCircleBoundary()
	{
		return CIRC_BOUNDARY;
	}
	
	public Fixture getFixture()
	{
		return FIXTURE;
	}
}
