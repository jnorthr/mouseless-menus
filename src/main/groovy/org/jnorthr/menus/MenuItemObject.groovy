package org.jnorthr.menus;
import java.awt.Color;

public class MenuItemObject
{
	String menuText	= "";	// the text of the menu item before the :=
	def menuCommand	= "";	// the full string after the :=

	int fontcolor = 0;		// text color - defaults to black	
	java.awt.Color menuColor = new java.awt.Color(0);	// internally maintained in synch with fontcolor; 

	// Null Constructor
	public MenuItemObject()
	{
	} // end of constructor


	public setColor(int color)
	{
		this.fontcolor = color;
		menuColor = new Color(color);		
	} // end of method

	// Non-Null Constructor - defaults to black text
	public MenuItemObject(String text, String command)
	{
		this.menuText = text;
		this.menuCommand = command;
	} // end of constructor


	// Non-Null Constructor with colored text
	public MenuItemObject(String text, String command, int color)
	{
		this.menuText = text;
		this.menuCommand = command;
		this.fontcolor = color;
		menuColor = new Color(color);
	} // end of constructor


	// class toString() method
    	String toString() 
    	{ 
        	return "text=<${menuText}> command=<${menuCommand}>  color as int=<${fontcolor}> awt.color=<${menuColor}>"
    	}  // end of method


	// test harness for this class
	public static void main(String[] args)
	{	
		println "... started"
		MenuItemObject mio = new MenuItemObject();
		mio.setColor(12345);
		println mio;

		mio = new MenuItemObject("Hi Kids","echo \$CLASSPATH");
		println mio;

		mio = new MenuItemObject("Fred Said","echo 'Fred Said'", 123);
		println mio;

		mio.menuText = "Groovy Codehaus";
		println mio;

		println "... the end ..."
	} // end of main

} // end of class