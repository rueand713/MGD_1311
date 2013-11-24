package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Credits implements Screen {

	AndroidGame GAME;
	SpriteBatch batch;
	OrthographicCamera CAMERA;
	String titleText = "CREDITS";
	String musicText = "Music by www.SoundRangers.com";
	String coinSfxText = "Coin sound by 'Dr. Minky' www.freesound.org";
	String jumpSfxText = "Jumping sound by 'Cman634' www.freesound.org";
	String heartSfxText = "Heart sound taken from Apple GarageBand";
	String myCreditsText = "Programming & Art by Rueben Anderson";
	Texture bgImage;
	int DEVICE_WIDTH;
	int DEVICE_HEIGHT;
	
	public Credits(AndroidGame game)
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
		
		batch.setProjectionMatrix(CAMERA.combined);
		batch.begin();
		batch.draw(bgImage, 0, 0);
		GameManager.drawFont(batch, titleText, DEVICE_WIDTH / 3, DEVICE_HEIGHT - 50, 2, Color.GREEN);
		GameManager.drawFont(batch, musicText, 10, DEVICE_HEIGHT - 125, 1.25f, Color.WHITE);
		GameManager.drawFont(batch, coinSfxText, 10, DEVICE_HEIGHT - 175, 1.25f, Color.WHITE);
		GameManager.drawFont(batch, jumpSfxText, 10, DEVICE_HEIGHT - 225, 1.25f, Color.WHITE);
		GameManager.drawFont(batch, heartSfxText, 10, DEVICE_HEIGHT - 275, 1.25f, Color.WHITE);
		GameManager.drawFont(batch, myCreditsText, 10, DEVICE_HEIGHT - 325, 1.25f, Color.WHITE);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		DEVICE_WIDTH = GameManager.getWidth(false);
		DEVICE_HEIGHT = GameManager.getHeight(false);
		
		// create the sprite batch object
		batch = new SpriteBatch();
		
		// setup the camera
		CAMERA = GameManager.getCamera(DEVICE_WIDTH, DEVICE_HEIGHT);
		
		// load up the background
		bgImage = GameManager.getTexture("Credits.png");
		
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
