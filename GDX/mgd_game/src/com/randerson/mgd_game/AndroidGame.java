package com.randerson.mgd_game;

import com.badlogic.gdx.Game;

public class AndroidGame extends Game {
	
	// create the screen objects for accessing outside the class
	ActOne actOne;
	MainMenu mainMenu;
	Tutorial tutorial;
	Splash splash;
	Credits credits;
	
	@Override
	public void create() {
		
		// setup the game screens passing in a reference to this
		// AndroidGame class
		actOne = new ActOne(this);
		mainMenu = new MainMenu(this);
		tutorial = new Tutorial(this);
		splash = new Splash(this);
		credits = new Credits(this);
		
		// set the first screen to show
		setScreen(mainMenu);
	}
}
