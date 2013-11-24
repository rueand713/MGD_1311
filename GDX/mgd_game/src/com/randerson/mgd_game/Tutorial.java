package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tutorial implements Screen{

	AndroidGame GAME;
	SpriteBatch batch;
	Texture helpScreen;
	OrthographicCamera CAMERA;
	int DEVICE_WIDTH;
	int DEVICE_HEIGHT;
	
	public Tutorial(AndroidGame game)
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
			 GAME.setScreen(GAME.mainMenu);
		 }
		 
		 // draw the sprites
		 batch.setProjectionMatrix(CAMERA.combined);
		 batch.begin();
		 batch.draw(helpScreen, 0, 0, DEVICE_WIDTH, DEVICE_HEIGHT);
		 GameManager.drawFont(batch, "Tap Screen to Exit", 50, DEVICE_HEIGHT / 2, 1.25f, Color.WHITE);
		 batch.end();
		 
		 // update the camera
		 CAMERA.update();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		// setup the sprite batch
		batch = new SpriteBatch();
		
		// load the background image
		helpScreen = GameManager.getTexture("Help.png");
		
		// set the screen width and height to use
		DEVICE_WIDTH = GameManager.getWidth(false);
		DEVICE_HEIGHT = GameManager.getHeight(false);
		
		// set the camera up
		CAMERA = GameManager.getCamera(DEVICE_WIDTH, DEVICE_HEIGHT);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
