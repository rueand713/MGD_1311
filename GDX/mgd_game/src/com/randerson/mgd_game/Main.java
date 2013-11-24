package com.randerson.mgd_game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class Main extends AndroidApplication {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// creat a new application configuration object
		AndroidApplicationConfiguration aac = new AndroidApplicationConfiguration();
		
		// configure the app settings
		aac.useAccelerometer = false;
		aac.useCompass = false;
		aac.useGL20 = true;
		
		// initialize an instance of the android game
		AndroidGame game = new AndroidGame();
		initialize(game, aac);
	}
	
	
}
