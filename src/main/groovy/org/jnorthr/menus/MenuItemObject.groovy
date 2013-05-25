/*
Place holder class
keeps two components of each valid menu line 1) the text seen by user as menu text and 2) command to execute when this menu item is picked, plus optionally a text color for the menu text
*/
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

    // logic to yield an Integer from a hex code
    public setColor(String hexcode)
    {
        try 
        {
            this.fontcolor = Integer.parseInt(hexcode,16);
			menuColor = new Color(this.fontcolor);
			return true;
        }
        catch (NumberFormatException e) 
		{ 
			println "Cannot convert '${hexcode}' as hex code to an integer \n"+e.getMessage();
			return false; 
		} // end of catch
    }    // end of method


	public setColor(int color)
	{
		this.fontcolor = color;
		menuColor = new Color(color);		
	} // end of method



	// test harness for this class
	public static void main(String[] args)
	{	
		println "\n\n... started"
		
		// empty constructor
		MenuItemObject mio = new MenuItemObject();
		mio.setColor(12345);
		println mio;

		// text, command constructor
		mio = new MenuItemObject("Hi Kids","echo \$CLASSPATH");
		println mio;

		
		// text, command, integer color constructor
		mio = new MenuItemObject("Fred Said","echo 'Fred Said'", 123);
		println mio;

		// text constructor, then set text color
		mio.menuText = "Groovy Codehaus";
		def result = mio.setColor("ccaadd");
		println "did ccaadd convert to a color? "+result;
		println mio

		println "... the end ...\n\n"
	} // end of main

} // end of class