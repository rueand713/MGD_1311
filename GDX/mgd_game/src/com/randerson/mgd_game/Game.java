package com.randerson.mgd_game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.randerson.entities.Coin;
import com.randerson.entities.Heart;

public class Game implements ApplicationListener {
	
	public enum jumpType {
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
	Texture BLOB;
	Texture SCENE;
	Rectangle BLOB_BOX;
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
	int[][] heartLocations = {{20, 32}, {150, 32}};
	int[][] coinLocations = {{210, 32}, {320, 32}, {550, 32}, {700, 32}};
	int[] jumpTracker = {0, 0};
	int SCORE = 0;
	
	@Override
	public void create() {
		
		// create the game camera object
		CAMERA = GameManager.getCamera(800, 480);
		
		// create the hero texture and bg image texture
		BLOB = GameManager.getTexture("SlimeLeft.png");
		SCENE = GameManager.getTexture("Scene1_bg.png");
		
		// create the background music and sound objects
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
		DEVICE_WIDTH = Gdx.graphics.getWidth();
		DEVICE_HEIGHT = Gdx.graphics.getHeight();
		
		// create the hero and bg image bounding rectangle
		BLOB_BOX = GameManager.getRect(100, 0, 64, 64);
		SCENE_BOX = GameManager.getRect(0, 0, DEVICE_WIDTH, DEVICE_HEIGHT);
		
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
	    	
	    	// check for a hero touch event (jumping)
	    	if (TOUCH_POSITION.x >= BLOB_BOX.x && TOUCH_POSITION.x <= BLOB_BOX.x + 64)
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
	   GameManager.drawFont(bgBatch, "SCORE: " + SCORE, 0, 475);
	    
	    // draw the sprite to the screen
	    bgBatch.draw(BLOB, BLOB_BOX.x, BLOB_BOX.y);
	    
		 // method for handling powerup positions
	    updatePowerups(bgBatch);
	    
	    // end the sprite batch
	    bgBatch.end();
		    
		 // update the camera
	    CAMERA.update();
	    
	    // method to check for collisions
	    detectCollision();
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
		
		// dispose of the Gdx resources
		BLOB.dispose();
		SCENE.dispose();
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
	    	if (BLOB_BOX.x != TOUCH_POSITION.x)
	    	{
	    		// check if the player touched behind or in front of the character
	    		if (TOUCH_POSITION.x < BLOB_BOX.x)
	    		{
	    			// verify if the character sprite should be changed
	    			if (HERO_CHANGED_DIRECTION)
	    			{
	    				// update the character sprite to face left
	    				BLOB = GameManager.getTexture("SlimeLeft.png");
	    				
	    				// set the change direction bool to false
	    				HERO_CHANGED_DIRECTION = false;
	    			}
	    			
	    			// move the player one pixel left each render
	    			BLOB_BOX.x -=1;
	    		}
	    		else if (TOUCH_POSITION.x > BLOB_BOX.x)
	    		{
	    			// verify if the character sprite should be changed
	    			if (HERO_CHANGED_DIRECTION)
	    			{
	    				// update the character sprite to face right
	    				BLOB = GameManager.getTexture("SlimeRight.png");
	    				
	    				// set the change direction bool to false
	    				HERO_CHANGED_DIRECTION = false;
	    			}
	    			
	    			// move the player one pixel right each render
	    			BLOB_BOX.x += 1;
	    		}	
	    	}		// check if the character has reached the touch position and set to move
	    	else if (BLOB_BOX.x == TOUCH_POSITION.x && HERO_IS_MOVING != false)
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
			if (coin.getBoundary().overlaps(BLOB_BOX) && DRAW_COINS[i] == true)
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
			if (heart.getBoundary().overlaps(BLOB_BOX) && DRAW_HEARTS[n] == true)
			{
				// play the heart sound effect
				HEART_SFX.play();
				
				// set the heart to no longer be drawn
				DRAW_HEARTS[heart.getIndex()] = false;
			}
		}
	}

}
