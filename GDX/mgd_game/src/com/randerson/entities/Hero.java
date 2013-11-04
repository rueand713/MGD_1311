package com.randerson.entities;

public class Hero extends Actors {

	private int XP_TO_LEVEL;
	
	public Hero(int hp_modifier, int strength, int endurance, int constitution, int level)
	{
		super(hp_modifier, strength, endurance, constitution, level);
		
		// determine the xp required for this entity to level
		XP_TO_LEVEL = (strength + endurance + constitution) * (100 - level);
	}
	
	// ***** SETTER METHODS *****
	
	public void updateStats()
	{
		// increment the level
		this.incrementLevel();
		
		// add the current xp to the total xp
		// and reset the current xp
		this.setTotalXp(this.getCurrentXp());
		this.setCurrentXp(0);
		
		// determine the hitpoint level
		int hp = (int) (this.getStr() * (this.getLvl() * 0.5)) + (this.getEnd() * this.getMod()) + (int) (this.getCon() * (this.getLvl() * 0.25));
		
		// set the hp
		this.setHp(hp);
		
		// determine physical damage level
		 this.setPdam((int) (this.getStr() * 0.25));
		
		// determine the special damage level
		this.setSdam((int) (this.getCon() * 0.5));
	}
	
	// **** Getter Methods ****
	
	public int getNextXp()
	{
		return XP_TO_LEVEL;
	}
}
