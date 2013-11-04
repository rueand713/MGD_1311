package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameManager {

	public static Texture getTexture(String path)
	{
		// create a texture object from asset with supplied path
		Texture texture = new Texture(Gdx.files.internal(path));
		
		return texture;
	}
	
	public static Sound getSound(String path)
	{
		// create a sound object from asset with supplied path
		Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
		
		return sound;
	}
	
	public static Music getMusic(String path)
	{
		// create a music object from asset with supplied path
		Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
		
		return music;
	}
	
	public static OrthographicCamera getCamera(int width, int height)
	{
		// create a new orthographic camera with supplied width and height
		OrthographicCamera camera = new OrthographicCamera();
		 camera.setToOrtho(false, width, height);
		 
		 return camera;
	}
	
	// Note: The x/y coordinates of the bucket define the bottom left corner of the bucket, 
	// the origin for drawing is located in the bottom left corner of the screen.
	public static Rectangle getRect(float x, float y, int width, int height)
	{
		// create a new rectangle object
		Rectangle rect = new Rectangle();
		
		// set the rectangle params
		rect.x = x;
		rect.y = y;
		rect.width = width;
		rect.height = height;
		
		return rect;
	}
	
	public static void drawFont(SpriteBatch batch, String string, int x, int y)
	{
		BitmapFont font = new BitmapFont();
		
		font.draw(batch, string, x, y);
	}
	
	public static Vector3 getTouchVector()
	{
		Vector3 vectPos = new Vector3();
	    vectPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	    
	    return vectPos;
	}
	
}
