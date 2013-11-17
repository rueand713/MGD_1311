package com.randerson.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.randerson.mgd_game.GameManager;

public class Actors {

	// Player class objects
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
	
	// Sprite related objects
	private Texture TEXTURE;
	private Rectangle BOUNDARY;
	private PolygonShape POLY_BOUNDARY;
	private CircleShape CIRC_BOUNDARY;
	private ArrayList<Item> INVENTORY;
	private Fixture FIXTURE;
	private Animation ANIMATION;
	
	// Physics related objects
	protected float FRICTION;
	protected float DENSITY;
	protected float RESTITUTION;
	
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
	
	// method for setting the physics attributes associated with the world
	public void setPhysics(float restitution, float friction, float density, float shapeRadius, boolean usePolygon)
	{
		RESTITUTION = restitution;
		FRICTION = friction;
		DENSITY = density;
		
		if (usePolygon)
		{
			POLY_BOUNDARY = Box2D.getPolygon(shapeRadius);
		}
		else
		{
			CIRC_BOUNDARY = Box2D.getCircle(shapeRadius);
		}
	}
	
	// method for defining the physics set in the previous method
	public void definePhysics(World world, boolean usePolygon)
	{
		Shape shape;
		
		if (usePolygon)
		{
			shape = POLY_BOUNDARY;
		}
		else
		{
			shape = CIRC_BOUNDARY;
		}
		
		// create the body definition with the libgdx rect data
		BodyDef bodydef = Box2D.getBodyDef(Box2D.DYNAMIC_BODY, BOUNDARY.x, BOUNDARY.y);
		
		// create the body from the definition
		Body body = Box2D.getBody(world, bodydef);
		
		// create the fixture definition from the physics objects set during construction
		FixtureDef fxDef = Box2D.getFixtureDef(shape, DENSITY, FRICTION, RESTITUTION);
		
		// create a fixture from the body
		FIXTURE = Box2D.getFixture(body, fxDef);
	}
	
	// method for checking collision between two entities using their fixtures
	public boolean overlaps(Fixture fixture)
	{
		// get the vector objects associated with this actor and the other supplied
		Vector2 actorPosition = FIXTURE.getBody().getPosition();
		Vector2 otherPosition = fixture.getBody().getPosition();
		
		// set the default collision return value
		boolean isColliding = false;
		
		// set the x and y collision ranges for 64x64 sprites
		float xRangeMax = actorPosition.x + 64;
		float xRangeMin = actorPosition.x - 64;
		float yRangeMax = actorPosition.y + 64;
		float yRangeMin = actorPosition.y - 64;
		
		// check if the two fixtures occupy the same space x & y
		// if they do then a collision is set to return true
		if (otherPosition.x >= xRangeMin && otherPosition.x <= xRangeMax)
		{
			if (otherPosition.y >= yRangeMin && otherPosition.y <= yRangeMax)
			{
				isColliding = true;
			}
		}
		
		return isColliding;
	}
	
	// method for checking collision between two entities using a fixture and rectangle
	public boolean overlaps(Rectangle rectangle)
	{
		// get the vector objects associated with this actor and the other supplied
		Vector2 actorPosition = FIXTURE.getBody().getPosition();
		Vector2 otherPosition = new Vector2(rectangle.getX(), rectangle.getY());
		
		// set the default collision return value
		boolean isColliding = false;
		
		// set the x and y collision ranges for 64x64 sprites
		float xRangeMax = actorPosition.x + 64;
		float xRangeMin = actorPosition.x - 64;
		float yRangeMax = actorPosition.y + 64;
		float yRangeMin = actorPosition.y - 64;
		
		// check if the two fixtures occupy the same space x & y
		// if they do then a collision is set to return true
		if (otherPosition.x >= xRangeMin && otherPosition.x <= xRangeMax)
		{
			if (otherPosition.y >= yRangeMin && otherPosition.y <= yRangeMax)
			{
				isColliding = true;
			}
		}
		
		return isColliding;
	}
	
	// method for checking collision between two entities using a fixture and rectangle
	public boolean overlaps(Vector2 otherPosition)
	{
		// get the vector objects associated with this actor
		Vector2 actorPosition = FIXTURE.getBody().getPosition();
		
		// set the default collision return value
		boolean isColliding = false;
		
		// set the x and y collision ranges for 64x64 sprites
		float xRangeMax = actorPosition.x + 64;
		float xRangeMin = actorPosition.x - 64;
		float yRangeMax = actorPosition.y + 64;
		float yRangeMin = actorPosition.y - 64;
		
		// check if the two fixtures occupy the same space x & y
		// if they do then a collision is set to return true
		if (otherPosition.x >= xRangeMin && otherPosition.x <= xRangeMax)
		{
			if (otherPosition.y >= yRangeMin && otherPosition.y <= yRangeMax)
			{
				isColliding = true;
			}
		}
		
		return isColliding;
	}
	
	// ***** SETTER METHODS *****
	
	// method for adding item to inventory
	public void addItem(Item item)
	{
		INVENTORY.add(item);
	}
	
	public void setLevel(int level)
	{
		LEVEL = level;
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
		HP = hp;
	}
	
	public void setMaxHp(int hp)
	{
		MAX_HP = hp;
	}
	
	public void setupAnimation(int rows, int columns)
	{
		ANIMATION = GameManager.animate(TEXTURE, rows, columns, 0);
	}
	
	public void setAnimation(Animation animation)
	{
		ANIMATION = animation;
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
	
	public Fixture getFixture()
	{
		return FIXTURE;
	}
	
	public TextureRegion getAnimatedFrame(float stateTime, boolean isLooping)
	{
		return ANIMATION.getKeyFrame(stateTime, isLooping);
	}
}
