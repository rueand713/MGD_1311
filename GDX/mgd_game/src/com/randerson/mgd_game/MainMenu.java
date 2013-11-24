package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MainMenu implements Screen {

	AndroidGame GAME;
	Texture PLAY_BUTTON;
	Texture TUTORIAL_BUTTON;
	Texture CREDITS_BUTTON;
	Texture TITLE;
	Music THEME;
	
	int DEVICE_WIDTH;
	int DEVICE_HEIGHT;
	OrthographicCamera CAMERA;
	Rectangle TITLE_BOX;
	Rectangle PLAY_BOX;
	Rectangle HELP_BOX;
	Rectangle CREDS_BOX;
	
	Vector3 TOUCH_POSITION;
	
	public MainMenu(AndroidGame game)
	{
		GAME = game;
	}
	
	@Override
	public void render(float delta) {
		
		// clear the screen
		 Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		 Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		 
		 if (Gdx.input.isTouched())
		 {
			 TOUCH_POSITION = GameManager.getTouchVector();
			 CAMERA.unproject(TOUCH_POSITION);
			 
			 buttonCheck();
		 }
		 
		// create the batch file for the background image
	   SpriteBatch bgBatch = new SpriteBatch();
		    
		// add the game camera for use in the batch file
		// draw the sprites to screen
	    bgBatch.setProjectionMatrix(CAMERA.combined);
	    bgBatch.begin();
	    bgBatch.draw(TITLE, TITLE_BOX.x, TITLE_BOX.y);
	    GameManager.drawFont(bgBatch, "(Alpha Build)", DEVICE_WIDTH - 200, DEVICE_HEIGHT - 15, 1.25f, Color.WHITE);
	    bgBatch.draw(PLAY_BUTTON, PLAY_BOX.x, PLAY_BOX.y);
	    bgBatch.draw(TUTORIAL_BUTTON, HELP_BOX.x, HELP_BOX.y);
	    bgBatch.draw(CREDITS_BUTTON, CREDS_BOX.x, CREDS_BOX.y);
	    
	    bgBatch.end();
	    
	    CAMERA.update();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		// *** Load up the assets ***
		PLAY_BUTTON = GameManager.getTexture("Play_Button.png");
		TUTORIAL_BUTTON = GameManager.getTexture("Help_Button.png");
		CREDITS_BUTTON = GameManager.getTexture("Creds_Button.png");
		TITLE = GameManager.getTexture("Title.png");
		THEME = GameManager.getMusic("Theme.mp3");
		
		// get the device properties for referencing
		DEVICE_WIDTH = GameManager.getWidth(false);
		DEVICE_HEIGHT = GameManager.getHeight(false);
		
		// set the title elements
		TITLE_BOX = GameManager.getRect(0, 0, DEVICE_WIDTH, DEVICE_HEIGHT);
		PLAY_BOX = GameManager.getRect(DEVICE_WIDTH / 10, 150, 640, 50);
		CREDS_BOX = GameManager.getRect(DEVICE_WIDTH / 10, 90, 640, 50);
		HELP_BOX = GameManager.getRect(DEVICE_WIDTH / 10, 30, 640, 50);
		
		TOUCH_POSITION = new Vector3();
		
		// create the game camera object
		CAMERA = GameManager.getCamera(DEVICE_WIDTH, DEVICE_HEIGHT);
		
		// verify that the music file has been created 
		if (THEME != null)
		{
			// play the music
			THEME.setLooping(true);
			THEME.play();
		}
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		
		// verify that the music file is valid
		if (THEME != null)
		{
			// pause the music
			THEME.pause();
		}
		
	}

	@Override
	public void resume() {
		
		// verify that the music file is valid
		if (THEME != null)
		{
			// resume playing the music
			THEME.play();
		}
		
	}

	@Override
	public void dispose() {
		
		// release the assets
		PLAY_BUTTON.dispose();
		CREDITS_BUTTON.dispose();
		TUTORIAL_BUTTON.dispose();
		THEME.dispose();
	}
	
	// method for determining which button was selected
	public void buttonCheck()
	{
		Vector2 touch = new Vector2(TOUCH_POSITION.x, TOUCH_POSITION.y);
		
		// check if the touch position overlaps with any of the buttons
		if (GameManager.overlaps(touch, PLAY_BOX, false))
		{
			GAME.setScreen(GAME.actOne);
			THEME.pause();
		}
		else if (GameManager.overlaps(touch, HELP_BOX, false))
		{
			GAME.setScreen(GAME.tutorial);
			THEME.pause();
		}
		else if (GameManager.overlaps(touch, CREDS_BOX, false))
		{
			GAME.setScreen(GAME.credits);
			THEME.pause();
		}
	}

}
