package com.randerson.entities;

public class Item {

	int CONDITION;
	int VALUE;
	int INDEX = -1;
	String NAME;
	String DESCRIPTION;
	String OWNER;
	
	public Item(String name, String description, String owner, int condition, int value, int index)
	{
		// set the item properties
		NAME = name;
		DESCRIPTION = description;
		OWNER = owner;
		CONDITION = condition;
		VALUE = value;
		INDEX = index;
	}
	
}
