package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Splash implements Screen {

	AndroidGame GAME;
	SpriteBatch batch;
	Texture splashScreen;
	int SCORE;
	String titleText = "Level Complete";
	String continueText = "Tap Screen To Continue";
	int DEVICE_WIDTH;
	int DEVICE_HEIGHT;
	OrthographicCamera CAMERA;
	
	public Splash(AndroidGame game)
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
		batch.draw(splashScreen, 0, 0, DEVICE_WIDTH, DEVICE_HEIGHT);
		GameManager.drawFont(batch, titleText, 50, DEVICE_HEIGHT - 50, 2, Color.GREEN);
		GameManager.drawFont(batch, "SCORE: " + SCORE, 70, DEVICE_HEIGHT - 125, 1.25f, Color.WHITE);
		GameManager.drawFont(batch, continueText, 50, 100, 1, Color.WHITE);
		batch.end();
		
		// update camera view
		CAMERA.update();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		// create the splash sprite batch
		batch = new SpriteBatch();
		
		// load up the splash background texture
		splashScreen = GameManager.getTexture("SplashBg.png");
		
		// setup the screen width and height variables
		DEVICE_WIDTH = GameManager.getWidth(false);
		DEVICE_HEIGHT = GameManager.getHeight(false);
		
		// create the game camera object
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
		
		splashScreen.dispose();
	}

}
