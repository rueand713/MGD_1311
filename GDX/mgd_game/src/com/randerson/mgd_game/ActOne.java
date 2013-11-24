package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.randerson.entities.Ambience;
import com.randerson.entities.Coin;
import com.randerson.entities.Heart;
import com.randerson.entities.Hero;
import com.randerson.entities.Item;

public class ActOne implements Screen {
	
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
	Texture PLATFORM;
	Fixture GROUND;
	Texture DOOR;
	Texture KEY;
	Texture PAUSE;
	Rectangle PauseButton;
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
	boolean DRAW_EXIT = false;
	boolean DRAW_KEY = false;
	Coin[] coins;
	Heart[] hearts;
	Ambience[] tiles;
	Ambience[] platforms;
	Rectangle key;
	Rectangle door;
	int[][] platformLocations = {{200, 300}, {628, 300}};
	int[][] heartLocations = {{20, 96}, {250, 64}};
	int[][] coinLocations = {{210, 64}, {320, 96}, {550, 64}, {700, 96}};
	int[][] tileLocations;
	int[] jumpTracker = {0, 0};
	int SCORE = 0;
	float X_STEP = 60;
	World WORLD;
	float STATE_TIME = 0f;
	ApplicationDefaults settings;
	AndroidGame GAME;
	boolean firstRun;
	boolean PAUSED = false;
	boolean TURBO = false;
	Box2DDebugRenderer debugger;
	
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
		
	// constructor for referencing parent and app context
	public ActOne(AndroidGame game)
	{
		GAME = game;
	}
	
	@Override
	public void show() {
		
		debugger = new Box2DDebugRenderer();
		
		// get the device properties for referencing
		DEVICE_WIDTH = GameManager.getWidth(true);
		DEVICE_HEIGHT = GameManager.getHeight(true);
				
		// setup the box2d world object
		WORLD = new World(new Vector2(0, -500), true);
		
		// create the game camera object
		CAMERA = GameManager.getCamera(DEVICE_WIDTH, DEVICE_HEIGHT);
		
		// *** INITIAL LOAD OF ASSETS *** 
		GRASS = GameManager.getTexture("Grass_1.png");
		KEY = GameManager.getTexture("Key.png");
		DOOR = GameManager.getTexture("Door.png");
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
		PLATFORM = GameManager.getTexture("Platform.png");
		PAUSE = GameManager.getTexture("PauseButton.png");
		
		// create the pause button rectangle for collision checking
		PauseButton = GameManager.getRect(10, DEVICE_HEIGHT - 120, 64, 64);
		
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
		BLOB.setPhysics(0, 0.3f, 0.0001f, 64.0f, true);
		BLOB.definePhysics(WORLD, true);
		BLOB.setAnimation(BLOB_MOVING_RIGHT);
		BLOB.getFixture().getBody().setLinearDamping(0.2f);
		BLOB.setHp(50);
		
		// set the initial touch position;
		TOUCH_POSITION = new Vector3(BLOB.getBoundary().x, BLOB.getBoundary().y, 0);
		
		// setup the exit items
		key = new Rectangle(250, 64, 32, 32);
		door = new Rectangle(400, 64, 64, 64);
		
		// setup the session prefs object
		settings = new ApplicationDefaults();
		
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
	public void render(float delta) {
		
		 // clear the screen
		 Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		 Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		 
		// grab the player positioning
		float playerX = BLOB.getFixture().getBody().getPosition().x;
    	float playerY = BLOB.getFixture().getBody().getPosition().y;
		
		// only if the game is not paused
		if (!PAUSED)
		{	 
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
		    		if (jumpTracker[0] == jumpType.notJumping.getValue() && BLOB.getBoundary().y == 64)
		    		{
		    			// set the jumping tracker to jumping
		    			jumpTracker[0] = jumpType.isJumping.getValue();
		    		}
		    	}
		    	else if (HERO_IS_MOVING == false)
		    	{
		    		// set the moving bool to true to update the player position
		    		HERO_IS_MOVING = true;
		    	}
		    	else if (HERO_IS_MOVING)
		    	{
		    		// set the running bool to true
		    		TURBO = true;
		    	}
		    	
		    	// set the change direction bool to true to update the player sprite direction
		    	HERO_CHANGED_DIRECTION = true;
		    	
		    }
		    
		   // method for handling the player position
		   updateHeroPosition();
		   
		  // method to check for collisions
		    detectCollision();
		    
		    if (DRAW_EXIT == false)
		    {
		    	// method for checking for exit conditions
		    	detectCanExit();
		    }
		    
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
		}
		else if (PAUSED && Gdx.input.isTouched())
		{
			// get the screen touch position
	    	TOUCH_POSITION = GameManager.getTouchVector();
	    	CAMERA.unproject(TOUCH_POSITION);
		}
		   	
		   // create the batch file for the background image
		   SpriteBatch bgBatch = new SpriteBatch();
		    
			// add the game camera for use in the batch file
			// draw the sprites to screen
		    bgBatch.setProjectionMatrix(CAMERA.combined);
		    bgBatch.begin();
		    bgBatch.draw(SCENE, SCENE_BOX.x, SCENE_BOX.y, DEVICE_WIDTH, DEVICE_HEIGHT);
		    
		    // method for handling the tiles
		    updateTiles(bgBatch);
		    
			// draw the score onto the screen
		   GameManager.drawFont(bgBatch, "SCORE: " + SCORE, DEVICE_WIDTH - 500, DEVICE_HEIGHT, 2.0f, Color.WHITE);
		    
		   // method for handling platform position
		   //movePlatforms();
		   
		    // draw the sprite to the screen
		   if (HERO_IS_MOVING && !PAUSED)
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
		   bgBatch.draw(HUD_back, 0, DEVICE_HEIGHT - 50);
		   
		   // end the sprite batch
		   bgBatch.end();
		   
		   // draw the hp meter
		   GameManager.drawRectangle(new ShapeRenderer(), CAMERA, 11, DEVICE_HEIGHT - 43, BLOB.getHpMeterLevel(475f), 30, Color.GREEN);
		   SpriteBatch batch = new SpriteBatch();
		   batch.setProjectionMatrix(CAMERA.combined);
		   batch.begin();
		   
		  // draw the pause button
		   batch.draw(PAUSE, PauseButton.x, PauseButton.y);
		   
		   // draw the hp meter covering
		   batch.draw(HUD_front, 0, DEVICE_HEIGHT - 50);
		   
		   if (PAUSED)
		   {
			   // draw the pause message onto the screen
				GameManager.drawFont(batch, "PAUSED", (DEVICE_WIDTH / 2) - 50, DEVICE_HEIGHT / 2, 2.0f, Color.WHITE);   
		   }
		   
		   batch.end();
		
		if (GameManager.overlaps(new Vector2(TOUCH_POSITION.x, TOUCH_POSITION.y), PauseButton, true))
		{
			// set the paused bool
			if (PAUSED)
			{
				PAUSED = false;
			}
			else if (!PAUSED)
			{
				PAUSED = true;
			}
			
			// reset the touch
			TOUCH_POSITION = new Vector3(0, 0, 0);
		}
		
		// update the camera
		CAMERA.update();
		
		// render physics boundaries for debugging
		debugger.render(WORLD, CAMERA.combined);
		     
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
			Vector2 position = tile.getPosition();
			Texture mesh = tile.getTexture();
			
			// draw the sprite to the screen
			batch.draw(mesh, position.x, position.y);
		}
		
		// iterate over the locations array of the game platforms
		for (int j = 0; j < platformLocations.length; j++)
		{
			Ambience platform = platforms[j];
			
			// extract the tile vector position and texture
			Vector2 position = platform.getPosition();
			Texture mesh = platform.getTexture();
			
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
		
		// check if the key should be drawn to screen
		if (DRAW_KEY)
		{
			Texture mesh = KEY;
			batch.draw(mesh, key.x, key.y);
		}	// check if the door should be drawn
		else if (DRAW_EXIT)
		{
			Texture mesh = DOOR;
			batch.draw(mesh, door.x, door.y);
		}
	}
	
	/*
	public void updateHero()
	{
		// get the player coordinates
		int playerX = (int) BLOB.getFixture().getBody().getPosition().x;
		int playerY = (int) BLOB.getFixture().getBody().getPosition().y;
		
		if (HERO_IS_MOVING == true)
		{
			// get the touch position
	    	int touchX = (int) TOUCH_POSITION.x;
	    			
	    	// get the delta time
	    	float delta = GameManager.getDelta(false);
	    	float jump = 0;
	    	
	    	if (jumpTracker[0] == jumpType.isJumping.getValue())
			{
				if (jumpTracker[1] < 1)
				{
					jump = BLOB.getFixture().getBody().getMass() * 10000;
					
					// play the jumping sfx
	    			JUMP_SFX.play();
				}
			}
		    	
			// check if the player touched behind or in front of the character
			if (touchX <= playerX)
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
				delta *= -300;
				
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
				
				// move the player to the right with interpolation
				delta *= 300;
			
			}		// check if the character has reached the touch position and set to move
			
			BLOB.getFixture().getBody().applyLinearImpulse(delta, jump, playerX, playerY, true);
			
			// get the player coordinates
			playerX = (int) BLOB.getFixture().getBody().getPosition().x;
			playerY = (int) BLOB.getFixture().getBody().getPosition().y;
		    
		    // apply the coords from the lib boundary to the Box2D boundary
	    	BLOB.getFixture().getBody().setTransform(playerX, playerY, 0);
	    	BLOB.getBoundary().setPosition(playerX, playerY);
	    	
	    	if (BLOB.getBoundary().x >= touchX && BLOB.getBoundary().x <= touchX + 64)
			{
	    		if (HERO_IS_MOVING == true)
	    		{
					// set the moving bool to skip updating character position
					HERO_IS_MOVING = false;
					delta = 0;
	    		}
			}
		}
	}*/
	
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
					playerY += 360f * GameManager.getDelta(false);
					
					// play the jumping sfx
	    			JUMP_SFX.play();
				}
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
	    			
	    			if (TURBO == true)
	    	    	{
	    	    		// set the player to move faster
	    	    		X_STEP = 120;
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
	    			
	    			if (TURBO == true)
	    	    	{
	    	    		// set the player to move faster
	    	    		X_STEP = 120;
	    	    	}
	    			
    				// move the player to the left with interpolation
	    			playerX += deltaMove;
	    		}	
	    		
	    	}		// check if the character has reached the touch position and set to move
	    	else if (playerX == touchX && HERO_IS_MOVING == true)
	    	{
	    		// set the moving bool to skip updating character position
	    		HERO_IS_MOVING = false;
	    		
	    		// reset the turbo
	    		TURBO = false;
	    		
	    		// reset the movement speed
	    		X_STEP = 60;
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
		
		// check if there is a collision between the hero and the key
		if (BLOB.overlaps(key) && DRAW_KEY == true)
		{
			// set the key to no longer be drawn
			DRAW_KEY = false;
			
			// set the door to be drawn
			DRAW_EXIT = true;
			
			// add the key to inventory
			BLOB.addItem(new Item("Key", "Level 1 Exit Key", "World", 100, 1, 0));
			
			// play sound
			COIN_SFX.play();
		}
		
		// check if there is a collision between the hero and the door
		if (BLOB.overlaps(door) && DRAW_EXIT == true)
		{
			// play sound
			HEART_SFX.play();
			
			// pass the score into the splash screen
			GAME.splash.SCORE = SCORE;
			
			// stop playing the music
			bgm.stop();
			
			// display the end of level splash screen
			GAME.setScreen(GAME.splash);
		}
	}
	
	public void setupTiles()
	{
		// determine how many tiles it takes to stretch across the device floor
		int numTiles = (DEVICE_WIDTH / 64) + 1;
		
		// allocate the tiles in the tilesLocation integer array
		tileLocations = new int[numTiles][2];
		
		// iterate over the number of tiles assigning the x, y coords for each
		for (int j = 0; j < numTiles; j++)
		{
			int x = 64 * j;
			int y = 0;
			
			tileLocations[j][0] = x;
			tileLocations[j][1] = y;
		}
		
		// setup the array of tiles
		tiles = new Ambience[tileLocations.length];
		
		// create a single solid entity for the game floor
		BodyDef bodydef = Box2D.getBodyDef(Box2D.STATIC_BODY, 0, 0);
		Body body = Box2D.getBody(WORLD, bodydef);
		Shape shape = Box2D.getPolygon(DEVICE_WIDTH * 2, 64, new Vector2(32, 32));
		FixtureDef fxDef = Box2D.getFixtureDef(shape, 0.1f, 1.0f, 0);
		GROUND = Box2D.getFixture(body, fxDef);
		shape.dispose();
		
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
				//grassTile.definePhysics(WORLD, x, y, 32.0f, 32.0f);
				
				// set a static position for the tile
				grassTile.setStaticPosition(x, y);
				
				// add the tile to the tiles array
				tiles[i] = grassTile;
			}
		}
		
		// setup the array of platforms
		platforms = new Ambience[platformLocations.length];
		
		// iterate over the platforms and creating a platform for each location
		for (int n = 0; n < platformLocations.length; n++)
		{
			// create a platform object
			Ambience platform = new Ambience(PLATFORM, "Platform", false);
			
			// verify that the platform is valid
			if (platform != null)
			{
				// get the tile coordinates for the array
				int x = platformLocations[n][0];
				int y = platformLocations[n][1];
				
				// define the physics for the platform object
				platform.definePhysics(WORLD, Box2D.KINEMATIC_BODY, x, y, 128, 32);
				platform.setStaticPosition(x, y);
				
				// add the platform to the platform array
				platforms[n] = platform;
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
		SCORE = settings.getData().getInteger("score", SCORE);
		maxHp = settings.getData().getInteger("maxHp", maxHp);
		hp = settings.getData().getInteger("hp", hp);
		totXp = settings.getData().getInteger("totXp", totXp);
		xp = settings.getData().getInteger("xp", xp);
		nextXp = settings.getData().getInteger("nextXp", nextXp);
		lv = settings.getData().getInteger("lv", lv);
		
		// set the retrieved values
		BLOB.setCurrentXp(xp);
		BLOB.setTotalXp(totXp);
		BLOB.setHp(hp);
		BLOB.setMaxHp(maxHp);
		BLOB.setNextXp(nextXp);
		BLOB.setLevel(lv);
		BLOB.getFixture().getBody().setTransform(new Vector2(x, y), 0);
	}
	
	// method for moving the platforms around the screen
	public void movePlatforms()
	{
		// the min and max positions for the platform checking for evaulating their movements
		// two arrays of coordinates that represent the min x and y values
		Vector2[] horizontalRange = {new Vector2(200, 300), new Vector2(500, 300)};
		Vector2[] verticalRange = {new Vector2(628, 50), new Vector2(628, 300)};
		
		for (int i = 0; i < platformLocations.length; i++)
		{
			// get the platform at the current index
			Ambience platform = platforms[i];
			
			// verify the object is valid
			if (platform != null)
			{
				// get the platform vector position
				Vector2 pos = platform.getFixture().getBody().getPosition();
				
				// get the platform positions
				float platX = 0;
				float platY = 0;
				
				if (i == 0)
				{
					// check if the x is within the positional range
					// set the impulse value to make the platform move in the given direction
					if (pos.x < horizontalRange[0].x)
					{
						platX = pos.x + (30.0f * GameManager.getDelta(false));
					}
					else if (pos.x >= horizontalRange[1].x)
					{
						platX = pos.x - (30.0f * GameManager.getDelta(false));
					}
					
					// move the platform
					platform.getFixture().getBody().setTransform(platX, 300, 0);
					platform.setStaticPosition(platX, 300);
				}
				else if (i == 1)
				{
					// check if the y is within the positional range
					// set the impulse value to make the platform move in the given direction
					if (pos.y < verticalRange[0].y)
					{
						platY = pos.y + (30 * GameManager.getDelta(false));
					}
					else if (pos.y >= verticalRange[1].y)
					{
						platY = pos.y - (30 * GameManager.getDelta(false));
					}
					
					// move the platform
					platform.getFixture().getBody().setTransform(628, platY, 0);
					platform.setStaticPosition(628, platY);
				}
			}
		}
		
	}
	
	public void detectCanExit()
	{
		boolean coinsTaken = true;
		
		// iterate over the coins tracking array
		for (int i = 0; i < DRAW_COINS.length; i++)
		{	
			// check if any coins have not been collected
			if (DRAW_COINS[i] == true)
			{
				// coin has not been collected set the coins taken to false
				coinsTaken = false;
			}
		}
		
		// check if the player has collected all coins and the key
		// to activate the exit conditions
		if (coinsTaken == true && DRAW_KEY == false && DRAW_EXIT == true)
		{
			DRAW_EXIT = true;
		}
		else if (coinsTaken == true && DRAW_KEY == false && DRAW_EXIT == false)
		{
			DRAW_KEY = true;
		}

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
}
