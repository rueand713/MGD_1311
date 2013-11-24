/*
 * project 		randerson-common-assets_lib
 * 
 * package 		libs
 * 
 * @author 		Rueben Anderson
 * 
 * date			Aug 15, 2013
 * 
 */
package com.randerson.mgd_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class ApplicationDefaults {
	
	Preferences preferences = null;
	
	// shared preferences constructor for single pref file
	public ApplicationDefaults() {
		
		preferences = Gdx.app.getPreferences("Defaults");
	}
	
	// methods for setting of the primitive data ***********
	public void setBool(String key, boolean value)
	{
		preferences.putBoolean(key, value);
		preferences.flush();
	}
	
	public void setInt(String key, int value)
	{
		preferences.putInteger(key, value);
		preferences.flush();
	}
	
	public void setFloat(String key, float value)
	{
		preferences.putFloat(key, value);
		preferences.flush();
	}
	
	public void setLong(String key, long value)
	{
		preferences.putLong(key, value);
		preferences.flush();
	}
	
	public void setString(String key, String value)
	{
		preferences.putString(key, value);
		preferences.flush();
	}
	
	// ****************************** end setting methods
	
	// method returns the preferences data for getting of the primitive types stored
	public Preferences getData()
	{
		return preferences;
	}
}
