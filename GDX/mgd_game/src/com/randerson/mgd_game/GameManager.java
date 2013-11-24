package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
	
	// method for drawing text to screen
	public static void drawFont(SpriteBatch batch, String string, int x, int y, float scale, Color color)
	{
		BitmapFont font = new BitmapFont();
		font.scale(scale);
		font.setColor(color);
		
		font.draw(batch, string, x, y);
	}
	
	// method for getting the touch vector
	public static Vector3 getTouchVector()
	{
		Vector3 vectPos = new Vector3();
	    vectPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	    
	    return vectPos;
	}
	
	// method for returning the deltatime either raw or smooth
	public static float getDelta(boolean getRawTime)
	{
		float dTime = Gdx.graphics.getDeltaTime();
		
		if (getRawTime)
		{
			dTime = Gdx.graphics.getRawDeltaTime();
		}
		
		return dTime;
	}
	
	// method for drawing a rectangle to the screen
	public static void drawRectangle(ShapeRenderer shape, OrthographicCamera camera, float x, float y, float width, float height, Color color)
	{
		shape.setProjectionMatrix(camera.combined);
		shape.begin(ShapeType.Filled);
		shape.setColor(color);
		shape.rect(x, y, width, height);
		shape.end();
	}
	
	// method for getting the device height or view height relative to the device height
	public static int getHeight(boolean deviceHeight)
	{
		// get the device height
		float height = Gdx.graphics.getHeight();
		
		// check if the method should return the device height or view height
		if (deviceHeight == false)
		{
			// find the view percentage of device height
			float heightPercent = 480 / height;
			
			// multiply to get the game view height
			height = height * heightPercent;
		}
		
		return (int) height;
	}
	
	// method for getting the device width or view width relative to the device width
	public static int getWidth(boolean deviceWidth)
	{
		// get the device width
		float width = Gdx.graphics.getWidth();
		
		// check if the method should return the device width or view width
		if (deviceWidth == false)
		{
			// find the view percentage of the device width
			float widthPercent = 800 / width;
			
			// multiply to get the game view width
			width = width * widthPercent; 
		}
		
		return (int) width;
	}
	
	// method for creating and returning an animation object for a full sheet or for a particular row in the sheet
	public static Animation animate(Texture spriteSheet, int rows, int columns, int rowToReturn)
	{
		// create the animation object
		Animation animation;
		
		// get the sprite width and height based on the sheet size and number of rows and columns
		int height = spriteSheet.getHeight() / rows;
		int width = spriteSheet.getWidth() / columns;
		
		// create the texture region arrays for extracting the individual frames
		TextureRegion[][] region = TextureRegion.split(spriteSheet, width, height);
		TextureRegion[] sheetFrames = new TextureRegion[columns * rows];
		
		// sprite sheet frame index
		int index = 0;
		
		// iterate over the rows and columsn present in the spritesheet and extract each region as a frame
		for (int i = 0; i < rows; i++)
		{
			for (int n = 0; n < columns; n++)
			{
				// set the frame at current index to the n-th index at index i of the region array
				sheetFrames[index++] = region[i][n];
			}
		}
		
		// check if the method should return an animation of only a single row or not
		if (rowToReturn > 0)
		{
			// create a temporary region object for extracting the sprite row
			TextureRegion[] tempFrame = new TextureRegion[columns];
			
			// the frame index of the current sheetFrames region object to begin extraction at
			int frame = (rowToReturn - 1) * columns;
			
			// iteroate over the temporary frame object assigning the frame at the current 
			// index of the sheetFrames object to the current index in the temp frame object
			for (int x = 0; x < tempFrame.length; x++)
			{
				tempFrame[x] = sheetFrames[frame + x];
			}
			
			// assign the new frames
			sheetFrames = tempFrame;
		}
		
		// initialize the animation with the keyframes set in the sheetFrames textureRegion
		animation = new Animation(0.25f, sheetFrames);
		
		return animation;
	}
	
	public static Vector2 getVect2(Vector3 vector)
	{
		return new Vector2(vector.x, vector.y);
	}
	
	// method for checking collision between two entities using a fixture and rectangle
	public static boolean overlaps(Vector2 touch, Rectangle rectangle, boolean addPadding)
	{
		// get the vector objects associated with this actor and the other supplied
		Vector2 button = new Vector2(rectangle.x, rectangle.y);
		
		int padding = 0;
		
		if (addPadding)
		{
			padding = 10;
		}
		
		// set the default collision return value
		boolean isColliding = false;
		
		// set the x and y collision ranges for sprites
		float xRangeMax = button.x + (rectangle.getWidth() + padding);
		float xRangeMin = button.x - (10 + padding);
		float yRangeMax = button.y + (rectangle.getHeight() + padding);
		float yRangeMin = button.y - (10 + padding);
		
		// check if the two fixtures occupy the same space x & y
		// if they do then a collision is set to return true
		if (touch.x >= xRangeMin && touch.x <= xRangeMax)
		{
			if (touch.y >= yRangeMin && touch.y <= yRangeMax)
			{
				isColliding = true;
			}
		}
		
		return isColliding;
	}
	
}
