package com.randerson.mgd_game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.randerson.entities.Coin;
import com.randerson.entities.Heart;
import com.randerson.entities.Hero;

public class Game implements ApplicationListener {
	
	// enum for tracking jump
	public enum jumpType
	{
		notJumping(0), isJumping(1);
		
		private int value;
		
		private jumpType(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return this.value;
		}
	};
	
	// class global objects - following all caps convention
	OrthographicCamera CAMERA;
	Texture BLOB_LEFT;
	Texture BLOB_RIGHT;
	Texture GRASS;
	Hero BLOB;
	Texture SCENE;
	Rectangle SCENE_BOX;
	Music bgm;
	Sound HEART_SFX;
	Sound COIN_SFX;
	Sound JUMP_SFX;
	Vector3 TOUCH_POSITION;
	boolean HERO_IS_MOVING = false;
	boolean HERO_CHANGED_DIRECTION = false;
	int DEVICE_WIDTH;
	int DEVICE_HEIGHT;
	boolean[] DRAW_HEARTS = {true, true};
	boolean[] DRAW_COINS = {true, true, true, true};
	Coin[] coins;
	Heart[] hearts;
	int[][] heartLocations = {{20, 32}, {250, 32}};
	int[][] coinLocations = {{210, 32}, {320, 32}, {550, 32}, {700, 32}};
	int[] jumpTracker = {0, 0};
	int SCORE = 0;
	float X_STEP;
	World WORLD;
	
	@Override
	public void create() {
		// setup the box2d world object
		WORLD = new World(new Vector2(0, -10), true); 
		
		// create the game camera object
		CAMERA = GameManager.getCamera(800, 480);
		
		// *** INITIAL LOAD OF ASSETS *** 
		GRASS = GameManager.getTexture("Grass_1.png");
		BLOB_LEFT = GameManager.getTexture("SlimeLeft.png");
		BLOB_RIGHT = GameManager.getTexture("SlimeRight.png");
		SCENE = GameManager.getTexture("Scene1_bg.png");
		bgm = GameManager.getMusic("forest_bgm.mp3");
		HEART_SFX = GameManager.getSound("heart_fx.mp3");
		COIN_SFX = GameManager.getSound("coin_fx.mp3");
		JUMP_SFX = GameManager.getSound("jump_fx.mp3");
		
		// verify that the background music object is created successfully
		if (bgm != null)
		{
			// set the music to loop and begin playing
			bgm.setLooping(true);
			bgm.play();;
		}
		
		// get the device properties for referencing
		DEVICE_WIDTH = GameManager.getWidth(true);
		DEVICE_HEIGHT = GameManager.getHeight(true);
		
		// create the hero and bg image bounding rectangle
		Rectangle heroFrame = GameManager.getRect(100, 0, 64, 64);
		SCENE_BOX = GameManager.getRect(0, 0, DEVICE_WIDTH, DEVICE_HEIGHT);
		
		// instantiate a hero object
		BLOB = new Hero(10, 5, 5, 5, 5, heroFrame, BLOB_RIGHT);
		
		// method for setting up the powerup objects
		setupPowerups();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		
		// clear the screen
		 Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		 Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// check if the user touched the screen
	    if (Gdx.input.isTouched())
	    {
	    	// get the screen touch position
	    	TOUCH_POSITION = GameManager.getTouchVector();
	    	CAMERA.unproject(TOUCH_POSITION);
	    	
	    	// set the moving bool to true to update the player position
	    	HERO_IS_MOVING = true;
	    	
	    	// set the change direction bool to true to update the player sprite direction
	    	HERO_CHANGED_DIRECTION = true;
	    	
	    	// get the x difference between the player and touch
	    	float x_difference = (TOUCH_POSITION.x - BLOB.getBoundary().x);
	    	
	    	// if negative absolute it to maintain positive difference value
	    	if (x_difference < 0)
	    	{
	    		x_difference *= -1;
	    	}
	    	
	    	// set the number of x steps for each frame
	    	if (x_difference > 300)
	    	{
	    		X_STEP = 60; // moves faster for further distances
	    	}
	    	else
	    	{
	    		X_STEP = 30; // move at the standard speed
	    	}
	    	
	    	
	    	// check for a hero touch event (jumping)
	    	if (TOUCH_POSITION.x >= BLOB.getBoundary().x && TOUCH_POSITION.x <= BLOB.getBoundary().x + 64)
	    	{
	    		// verify that the player is not still jumping
	    		if (jumpTracker[0] == jumpType.notJumping.getValue())
	    		{
	    			// play the jumping sfx
	    			JUMP_SFX.play();
	    			
	    			// set the jumping tracker to jumping
	    			jumpTracker[0] = jumpType.isJumping.getValue();
	    		}
	    	}
	    	
	    	// check if the player has jumped and the jump counter has reached its limiting value
	    	if (jumpTracker[0] == jumpType.isJumping.getValue() && jumpTracker[1] >= 30)
	    	{
	    		// reset the player's jump and jump counter
	    		jumpTracker[0] = jumpType.notJumping.getValue();
	    		jumpTracker[1] = 0;
	    	}
	    	else
	    	{
	    		// increment the jump counter
	    		jumpTracker[1]++;
	    	}
	    }
	    
	    // method for handling the player position
	   updateHeroPosition();
	   	
	   // create the batch file for the background image
	   SpriteBatch bgBatch = new SpriteBatch();
	    
		// add the game camera for use in the batch file
		// draw the sprites to screen
	    bgBatch.setProjectionMatrix(CAMERA.combined);
	    bgBatch.begin();
	    bgBatch.draw(SCENE, SCENE_BOX.x, SCENE_BOX.y);
	    
		// draw the score onto the screen
	   GameManager.drawFont(bgBatch, "SCORE: " + SCORE, 0, GameManager.getHeight(false) - 35);
	    
	    // draw the sprite to the screen
	    bgBatch.draw(BLOB.getTexture(), BLOB.getBoundary().x, BLOB.getBoundary().y);
	    
		 // method for handling powerup positions
	    updatePowerups(bgBatch);
	    
	    // end the sprite batch
	    bgBatch.end();
	    
	   // draw the hp meter
	   GameManager.drawRectangle(new ShapeRenderer(), CAMERA, 0, GameManager.getHeight(false) - 30, BLOB.getHp(), 30, Color.GREEN);
		    
		 // update the camera
	    CAMERA.update();
	    
	    // method to check for collisions
	    detectCollision();
	    
	    // step the box2d world 
	    WORLD.step(1/60f, 6, 2);
	}

	@Override
	public void pause() {
		
		// check that the music object is valid
		if (bgm != null)
		{
			// pause the music
			bgm.pause();
		}
	}

	@Override
	public void resume() {
		
		// check if the music object is valid and not playing
		if (bgm != null && bgm.isPlaying() == false)
		{
			// begin playing the music again
			bgm.play();;
		}
	}

	@Override
	public void dispose() {
		
		// nullify the java arrays
		DRAW_HEARTS = null;
		DRAW_COINS = null;
		coins = null;
		hearts = null;
		heartLocations = null;
		coinLocations = null;
		BLOB = null;
		
		// dispose of the Gdx resources
		BLOB_LEFT.dispose();
		BLOB_RIGHT.dispose();
		SCENE.dispose();
		bgm.dispose();
		COIN_SFX.dispose();
		HEART_SFX.dispose();
		JUMP_SFX.dispose();
	}
	
	// method for updating the powerups on screen
	public void updatePowerups(SpriteBatch batch)
	{
		// iterate over the bool array for drawing hearts
		for (int i = 0; i < DRAW_HEARTS.length; i++)
		{
			// check if it is ok to draw the currently index heart object
			if (DRAW_HEARTS[i] == true)
			{
				// create a reference to the heart at index in the hearts array
				Heart heart = hearts[i];
				
				// gets the rectangle and texture for the heart in the current index
				Rectangle item = heart.getBoundary();
				Texture mesh = heart.getTexture();
				
				// draw the sprite to the screen
			    batch.draw(mesh, item.x, item.y);
			}
		}
		
		// iterate over the bool array for drawing coins
		for (int n = 0; n < DRAW_COINS.length; n++)
		{	
			// check if it is ok to draw the currently index coin object
			if (DRAW_COINS[n] == true)
			{
				// create a reference to the coin at index in the coins array
				Coin coin = coins[n];
				
				// gets the rectangle and texture for the coin in the current index
				Rectangle item = coin.getBoundary();
				Texture mesh = coin.getTexture();
				
				// draw the sprite to the screen
			    batch.draw(mesh, item.x, item.y);
			}
		}
	}
	
	public void updateHeroPosition()
	{
		 // check if the object is moving
	    if (HERO_IS_MOVING == true)
	    {
	    	// check if the character has reached the x touch point
	    	if (BLOB.getBoundary().x != TOUCH_POSITION.x)
	    	{
	    		// check if the player touched behind or in front of the character
	    		if (TOUCH_POSITION.x < BLOB.getBoundary().x)
	    		{
	    			// verify if the character sprite should be changed
	    			if (HERO_CHANGED_DIRECTION)
	    			{
	    				// update the character sprite to face left
	    				BLOB.setTexture(BLOB_LEFT);
	    				
	    				// set the change direction bool to false
	    				HERO_CHANGED_DIRECTION = false;
	    			}
	    			
	    			float deltaMove = (X_STEP * GameManager.getDelta(false));
	    			
    				// move the player to the left with interpolation
	    			BLOB.getBoundary().x -= deltaMove;
	    			
	    		}
	    		else if (TOUCH_POSITION.x > BLOB.getBoundary().x)
	    		{
	    			// verify if the character sprite should be changed
	    			if (HERO_CHANGED_DIRECTION)
	    			{
	    				// update the character sprite to face right
	    				BLOB.setTexture(BLOB_RIGHT);
	    				
	    				// set the change direction bool to false
	    				HERO_CHANGED_DIRECTION = false;
	    			}
	    			
					float deltaMove = (X_STEP * GameManager.getDelta(false));
	    			
    				// move the player to the left with interpolation
	    			BLOB.getBoundary().x += deltaMove;
	    		}	
	    	}		// check if the character has reached the touch position and set to move
	    	else if (BLOB.getBoundary().x == TOUCH_POSITION.x && HERO_IS_MOVING != false)
	    	{
	    		// set the moving bool to skip updating character position
	    		HERO_IS_MOVING = false;
	    	}
	    }
	}
	
	public void setupPowerups()
	{
		// COIN SETUP
		
		// initialize the coins array
		coins = new Coin[coinLocations.length];
		
		// iterate over the coin locations array creating a coin and adding it to the array
		for (int n = 0; n < coinLocations.length; n++)
		{
			// set the default coin values
			String texturePath = "Coin_Bronze.png";
			String name = "Bronze Coin";
			int value = 1;
			
			// check if n meets modulus conditions for higher valued coin
			if (n % 3 == 0)
			{
				 texturePath = "Coin_Gold.png";
				 name = "Gold Coin";
				 value = 10;
			}
			else if (n % 5 == 0)
			{
				texturePath = "Coin_Silver.png";
				name = "Silver Coin";
				value = 5;
			}
			
			// create the new coin object with the generated parameters
			Coin coin = new Coin(coinLocations[n][0], coinLocations[n][1], texturePath, name, value);
			
			// add the index id
			coin.setIndex(n);
			
			// add the coin to the array
			coins[n] = coin;
		}
		
		// HEART SETUP
		
		hearts = new Heart[heartLocations.length];
		
		// iterate over the coin locations array creating a coin and adding it to the array
		for (int j = 0; j < heartLocations.length; j++)
		{
			// set the default coin values
			String texturePath = "Heart.png";
			String name = "Heart";
			int restoration = 10;
			
			// create the new coin object with the generated parameters
			Heart heart = new Heart(heartLocations[j][0], heartLocations[j][1], texturePath, name, restoration);
			
			// add the index id
			heart.setIndex(j);
			
			// add the coin to the array
			hearts[j] = heart;
		}
	}
	
	public void detectCollision()
	{
		// iterate over the coins array and check for a collision with each object
		for (int i = 0; i < coins.length; i++)
		{
			// get the coin at the current index in the array
			Coin coin = coins[i];
			
			// check if there is a collision between this coin and the hero
			if (coin.getBoundary().overlaps(BLOB.getBoundary()) && DRAW_COINS[i] == true)
			{
				// play the coin sound fx
				COIN_SFX.play();
				
				// add the coins to the score
				SCORE += coin.getValue();
				
				// set the coin to no longer be drawn
				DRAW_COINS[coin.getIndex()] = false;
			}
		}
		
		// iterate over the hearts array and check for a collision with each object
		for (int n = 0; n < hearts.length; n++)
		{
			// get the coin at the current index in the array
			Heart heart = hearts[n];
			
			// check if there is a collision between this coin and the hero
			if (heart.getBoundary().overlaps(BLOB.getBoundary()) && DRAW_HEARTS[n] == true)
			{
				// play the heart sound effect
				HEART_SFX.play();
				
				BLOB.setHp(BLOB.getHp() + heart.getRestoration());
				
				// set the heart to no longer be drawn
				DRAW_HEARTS[heart.getIndex()] = false;
			}
		}
	}

}
