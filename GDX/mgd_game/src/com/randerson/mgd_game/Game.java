package com.randerson.mgd_game;

import android.content.Context;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.randerson.entities.Ambience;
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
	Animation BLOB_MOVING_LEFT;
	Animation BLOB_MOVING_RIGHT;
	Texture BLOB_IDLE_LEFT;
	Texture BLOB_IDLE_RIGHT;
	Texture BLOB_SPRITES;
	Texture GRASS;
	Texture HUD_back;
	Texture HUD_front;
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
	Ambience[] tiles;
	int[][] heartLocations = {{20, 96}, {250, 64}};
	int[][] coinLocations = {{210, 64}, {320, 96}, {550, 64}, {700, 96}};
	int[][] tileLocations = {{0, 0}, {64, 0}, {128, 0}, {192, 0}, {256, 0}, {320, 0}, {384, 0}, {448, 0}, {512, 0}, {576, 0}, {640, 0}, {704, 0}, {768, 0}};
	int[] jumpTracker = {0, 0};
	int SCORE = 0;
	float X_STEP;
	World WORLD;
	float STATE_TIME = 0f;
	ApplicationDefaults settings;
	Context CONTEXT;
	boolean firstRun;
	boolean PAUSED = false;
	boolean isVictorious = false;
	
	@Override
	public void create() {
	
		// get the device properties for referencing
		DEVICE_WIDTH = GameManager.getWidth(false);
		DEVICE_HEIGHT = GameManager.getHeight(false);
				
		// setup the box2d world object
		WORLD = new World(new Vector2(0, -128), true);
		
		// create the game camera object
		CAMERA = GameManager.getCamera(DEVICE_WIDTH, DEVICE_HEIGHT);
		
		// *** INITIAL LOAD OF ASSETS *** 
		GRASS = GameManager.getTexture("Grass_1.png");
		BLOB_IDLE_LEFT = GameManager.getTexture("SlimeLeft.png");
		BLOB_IDLE_RIGHT = GameManager.getTexture("SlimeRight.png");
		BLOB_SPRITES = GameManager.getTexture("SlimeAtlas.png");
		SCENE = GameManager.getTexture("Scene1_bg.png");
		bgm = GameManager.getMusic("forest_bgm.mp3");
		HEART_SFX = GameManager.getSound("heart_fx.mp3");
		COIN_SFX = GameManager.getSound("coin_fx.mp3");
		JUMP_SFX = GameManager.getSound("jump_fx.mp3");
		HUD_front = GameManager.getTexture("HUD.png");
		HUD_back = GameManager.getTexture("HUD_Hp_bg.png");
		
		// setup the blob animation objects for quick reference setting
		BLOB_MOVING_RIGHT = GameManager.animate(BLOB_SPRITES, 2, 3, 2);
		BLOB_MOVING_LEFT = GameManager.animate(BLOB_SPRITES, 2, 3, 1);
		
		// verify that the background music object is created successfully
		if (bgm != null)
		{
			// set the music to loop and begin playing
			bgm.setLooping(true);
			bgm.play();
		}
		
		// method for setting up the powerup objects
		setupPowerups();
		
		// method for setting up tiles
		setupTiles();
		
		// create the hero and bg image bounding rectangle
		Rectangle heroFrame = GameManager.getRect(100, 64, 64, 64);
		SCENE_BOX = GameManager.getRect(0, 0, DEVICE_WIDTH, DEVICE_HEIGHT);
		
		// instantiate a hero object
		BLOB = new Hero(10, 5, 5, 5, 5, heroFrame, BLOB_IDLE_RIGHT);
		BLOB.setPhysics(0, 1.0f, 10f, 32.0f, true);
		BLOB.definePhysics(WORLD, true);
		BLOB.setAnimation(BLOB_MOVING_RIGHT);
		
		// setup the session prefs object
		settings = new ApplicationDefaults(CONTEXT);
		
		// verify that the settings object is valid
		if (settings != null)
		{
			// set the firstRun object bool
			firstRun = settings.getData().getBoolean("first-run", true);
			
			// check if this is the first app run
			if (firstRun)
			{
				// first run save the initial data
				saveData();
			}
			else
			{
				// otherwise load up the previous session data
				loadData();
			}
			
		}
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		
		// clear the screen
		 Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		 Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		 // grab the player positioning
		float playerX = BLOB.getFixture().getBody().getPosition().x;
    	float playerY = BLOB.getFixture().getBody().getPosition().y;
		 
		// check if the user touched the screen
	    if (Gdx.input.isTouched())
	    {
	    	// get the screen touch position
	    	TOUCH_POSITION = GameManager.getTouchVector();
	    	CAMERA.unproject(TOUCH_POSITION);
	    	
	    	// check for a hero touch event (jumping)
	    	if (BLOB.overlaps(new Vector2(TOUCH_POSITION.x, TOUCH_POSITION.y)))
	    	{
	    		// verify that the player is not still jumping
	    		if (jumpTracker[0] == jumpType.notJumping.getValue())
	    		{
	    			// set the jumping tracker to jumping
	    			jumpTracker[0] = jumpType.isJumping.getValue();
	    		}
	    	}
	    	
	    	// set the moving bool to true to update the player position
	    	HERO_IS_MOVING = true;
	    	
	    	// set the change direction bool to true to update the player sprite direction
	    	HERO_CHANGED_DIRECTION = true;
	    	
	    	// get the x difference between the player and touch
	    	float x_difference = (TOUCH_POSITION.x - playerX);
	    	
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
	    }
	   	
	   // create the batch file for the background image
	   SpriteBatch bgBatch = new SpriteBatch();
	    
		// add the game camera for use in the batch file
		// draw the sprites to screen
	    bgBatch.setProjectionMatrix(CAMERA.combined);
	    bgBatch.begin();
	    bgBatch.draw(SCENE, SCENE_BOX.x, SCENE_BOX.y);
	    
	    // method for handling the tiles
	    updateTiles(bgBatch);
	    
		// draw the score onto the screen
	   GameManager.drawFont(bgBatch, "SCORE: " + SCORE, 0, GameManager.getHeight(false) - 55);
	    
	   // method for handling the player position
	   updateHeroPosition();
	   
	    // draw the sprite to the screen
	   if (HERO_IS_MOVING)
	   {
		   bgBatch.draw(BLOB.getAnimatedFrame(STATE_TIME, HERO_IS_MOVING), playerX, playerY);
	   }
	   else
	   {
		   bgBatch.draw(BLOB.getTexture(), playerX, playerY);
	   }
	    
	   // method for handling powerup positions
	   updatePowerups(bgBatch);
	    
	   // draw the hp meter blank background
	   bgBatch.draw(HUD_back, 0, GameManager.getHeight(false) - 50);
	   
	   // end the sprite batch
	   bgBatch.end();
	   
	   // draw the hp meter
	   GameManager.drawRectangle(new ShapeRenderer(), CAMERA, 11, GameManager.getHeight(false) - 43, BLOB.getHp(), 30, Color.GREEN);
	   
	   SpriteBatch batch = new SpriteBatch();
	   batch.setProjectionMatrix(CAMERA.combined);
	   batch.begin();
	   
	   // draw the hp meter covering
	   batch.draw(HUD_front, 0, GameManager.getHeight(false) - 50);
	   batch.end();
		 
	   // update the camera
	   CAMERA.update();
		   
	    
	    // method to check for collisions
	    detectCollision();
	    
	    // check if the player has jumped and the jump counter has reached its limiting value
    	if (jumpTracker[0] == jumpType.isJumping.getValue() && jumpTracker[1] >= 10)
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
    	
    	// time manager for animation frames
	    STATE_TIME += GameManager.getDelta(false);
	    
	    // winning conditional tracker
	    if (isVictorious)
	    {
	    	// call the game ending method
	    	gameOver();
	    }
	    
	    // step the box2d world 
	    WORLD.step(1/60f, 6, 2);
	}

	@Override
	public void pause() {
		
		// set the pause bool to true for session delaying
		PAUSED = true;
		
		// check that the music object is valid
		if (bgm != null)
		{
			// pause the music
			bgm.pause();
		}
		
		// verify that the settings is setup
		if (settings != null)
		{
			// first run save the initial data
			saveData();
		}
	}

	@Override
	public void resume() {
		
		// set the pause bool to false for session returning
		PAUSED = false;
		
		// check if the music object is valid and not playing
		if (bgm != null && bgm.isPlaying() == false)
		{
			// begin playing the music again
			bgm.play();
		}
		
		// verify that the settings is setup
		if (settings != null)
		{
			// otherwise load up the previous session data
			loadData();
		}
	}

	@Override
	public void dispose() {
		
		// nullify the java arrays
		BLOB_IDLE_LEFT.dispose();
		BLOB_IDLE_RIGHT.dispose();
		BLOB_SPRITES.dispose();
		DRAW_HEARTS = null;
		DRAW_COINS = null;
		coins = null;
		hearts = null;
		heartLocations = null;
		coinLocations = null;
		BLOB = null;
		
		// dispose of the Gdx resources
		SCENE.dispose();
		bgm.dispose();
		COIN_SFX.dispose();
		HEART_SFX.dispose();
		JUMP_SFX.dispose();
	}
	
	// method for updating the tiles on screen
	public void updateTiles(SpriteBatch batch)
	{
		// iterate over the locations array of the game tiles
		for (int i = 0; i < tileLocations.length; i++)
		{
			// get the tile at the current index in the tiles array
			Ambience tile = tiles[i];
			
			// extract the tile vector position and texture
			Vector2 position = tile.getFixture().getBody().getPosition();
			Texture mesh = tile.getTexture();
			
			// draw the sprite to the screen
			batch.draw(mesh, position.x, position.y);
		}
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
		// get the player coordinates
		int playerX = (int) BLOB.getFixture().getBody().getPosition().x;
		int playerY = (int) BLOB.getFixture().getBody().getPosition().y;
		
		 // check if the object is moving
	    if (HERO_IS_MOVING == true)
	    {
	    	// get the touch position
	    	int touchX = (int) TOUCH_POSITION.x;
	    			
	    	// get the delta time
	    	float deltaMove = (X_STEP * GameManager.getDelta(false));
	    	
	    	if (jumpTracker[0] == jumpType.isJumping.getValue())
			{
				if (jumpTracker[1] < 1)
				{
					playerY += 256.0f * GameManager.getDelta(false);
					
					// play the jumping sfx
	    			JUMP_SFX.play();
				}
			}
	    	else
	    	{
	    		playerY = 64;
	    	}
	    	
	    	// check if the character has reached the x touch point
	    	if (playerX != touchX)
	    	{
	    		// check if the player touched behind or in front of the character
	    		if (touchX < playerX)
	    		{
	    			// verify if the character sprite should be changed
	    			if (HERO_CHANGED_DIRECTION)
	    			{
	    				// update the character sprite to face left
	    				BLOB.setAnimation(BLOB_MOVING_LEFT);
	    				BLOB.setTexture(BLOB_IDLE_LEFT);
	    				
	    				// set the change direction bool to false
	    				HERO_CHANGED_DIRECTION = false;
	    			}
	    			
    				// move the player to the left with interpolation
	    			playerX -= deltaMove;
	    			
	    		}
	    		else if (touchX > playerX)
	    		{
	    			// verify if the character sprite should be changed
	    			if (HERO_CHANGED_DIRECTION)
	    			{
	    				// update the character sprite to face right
	    				BLOB.setAnimation(BLOB_MOVING_RIGHT);
	    				BLOB.setTexture(BLOB_IDLE_RIGHT);
	    				
	    				// set the change direction bool to false
	    				HERO_CHANGED_DIRECTION = false;
	    			}
	    			
    				// move the player to the left with interpolation
	    			playerX += deltaMove;
	    		}	
	    		
	    	}		// check if the character has reached the touch position and set to move
	    	else if (playerX == touchX && HERO_IS_MOVING == true)
	    	{
	    		// set the moving bool to skip updating character position
	    		HERO_IS_MOVING = false;
	    	}
	    }
	    
	    // apply the coords from the lib boundary to the Box2D boundary
    	BLOB.getFixture().getBody().setTransform(playerX, playerY, 0);
    	BLOB.getBoundary().setPosition(playerX, playerY);
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
			if (n % 5 == 0)
			{
				 texturePath = "Coin_Gold.png";
				 name = "Gold Coin";
				 value = 10;
			}
			else if (n % 3 == 0)
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
			if (BLOB.overlaps(coin.getBoundary()) && DRAW_COINS[i] == true)
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
			if (BLOB.overlaps(heart.getBoundary()) && DRAW_HEARTS[n] == true)
			{
				// play the heart sound effect
				HEART_SFX.play();
				
				BLOB.setHp(BLOB.getHp() + heart.getRestoration());
				
				// set the heart to no longer be drawn
				DRAW_HEARTS[heart.getIndex()] = false;
			}
		}
	}
	
	public void setupTiles()
	{
		// setup the array of tiles
		tiles = new Ambience[tileLocations.length];
		
		// iterate over the tile locations creating tiles for each coordinate
		for (int i = 0; i < tileLocations.length; i++)
		{
			// create a grass tile object
			Ambience grassTile = new Ambience(GRASS, "Grass", false);
			
			// verify that the object if valid
			if (grassTile != null)
			{
				// get the tile coordinates from the array
				int x = tileLocations[i][0];
				int y = tileLocations[i][1];
				
				// define the physics for the grass tile object
				// width and height take half of the sprite sizes as arguments
				grassTile.definePhysics(WORLD, x, y, 32.0f, 32.0f);
				
				// add the tile to the tiles array
				tiles[i] = grassTile;
			}
		}
	}

	// method for saving session data
	public void saveData()
	{
		// load the current game data
		float x = BLOB.getFixture().getBody().getPosition().x;
		float y = BLOB.getFixture().getBody().getPosition().y;
		int maxHp = BLOB.getMaxHp();
		int hp = BLOB.getHp();
		int xp = BLOB.getCurrentXp();
		int totXp = BLOB.getTotalXp();
		int nextXp = BLOB.getNextXp();
		int lv = BLOB.getLvl();
		
		// save the data into the application prefs
		settings.setFloat("xPos", x);
		settings.setFloat("yPos", y);
		settings.setInt("score", SCORE);
		settings.setInt("maxHp", maxHp);
		settings.setInt("hp", hp);
		settings.setInt("totXp", totXp);
		settings.setInt("xp", xp);
		settings.setInt("nextXp", nextXp);
		settings.setInt("lv", lv);
		
	}
	
	// method for restoring session data
	public void loadData()
	{
		// retrieve the default values
		float x = BLOB.getFixture().getBody().getPosition().x;
		float y = BLOB.getFixture().getBody().getPosition().y;
		int maxHp = BLOB.getMaxHp();
		int hp = BLOB.getHp();
		int xp = BLOB.getCurrentXp();
		int totXp = BLOB.getTotalXp();
		int nextXp = BLOB.getNextXp();
		int lv = BLOB.getLvl();
		
		// try to load the saved data values supplying the defaults on null returns
		x = settings.getData().getFloat("xPos", x);
		y = settings.getData().getFloat("yPos", y);
		SCORE = settings.getData().getInt("score", SCORE);
		maxHp = settings.getData().getInt("maxHp", maxHp);
		hp = settings.getData().getInt("hp", hp);
		totXp = settings.getData().getInt("totXp", totXp);
		xp = settings.getData().getInt("xp", xp);
		nextXp = settings.getData().getInt("nextXp", nextXp);
		lv = settings.getData().getInt("lv", lv);
		
		// set the retrieved values
		BLOB.setCurrentXp(xp);
		BLOB.setTotalXp(totXp);
		BLOB.setHp(hp);
		BLOB.setMaxHp(maxHp);
		BLOB.setNextXp(nextXp);
		BLOB.setLevel(lv);
		BLOB.getFixture().getBody().setTransform(new Vector2(x, y), 0);
	}
	
	public void gameOver()
	{
		
	}
	
	public void levelOver()
	{
		
	}
	
}
