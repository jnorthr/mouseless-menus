package org.jnorthr.menus;
import java.awt.Color;

public class MenuItem
{
	int menuKey		// sequential numbering key
	int menuBIC		// builtin command number; zero if not builtin;	
	int menuColumn		// which column to show this item if menuShow=true
	int menuLine		// which line of the above column to show this item on
	def menuColor		// particular color specification; eventually will hold hex values like 0xc00080
	boolean menuBold;	// from MenuEnty on old Duracell2 usbkey on iMac Media
	boolean menuItalics; // ditto
	int menuHexColor;	// ditto
	
	boolean menuShow	// true to show it
	String menuText		// the text of the menu item before the :=
	def menuCommand		// the full string after the :=
	String dialogTitle	// shows in title of dialog if BIC number says so
	String menuFileName	// when a BIC of 'go' says load and display another menu, this is the file name to load;
				// typically like ./data/menu.txt

	// Null Constructor
	public MenuItem()
	{
		menuKey = 0;
		menuBIC = 0;
		menuColumn = 0;
		menuLine = 0;
		menuColor = "green";
		menuBold = false;
		menuItalics = false;
		menuHexColor = Integer.parseInt("ff0000",16);
		menuShow = false;
		menuText = "";
		menuCommand = "";
		dialogTitle = "";
		menuFileName = "";
	} // end of constructor

	// Full Constructor
	// key as int 1..99
	// BIC - builtin command index as int
	// column int - which column should this menu item appear in ? 1..5 (missing col.s ok)
	// line int - which line should this menu item appear in ? (missing lines ok)
	// color name or hex code as def/object
	// bold the text on menu ?
	// italics the text on menu ?
	// menu hex color is int equivalent of color name decoded from hex
	// show/hide boolean: tru = show
	// menu text appearing on screen as String
	// command to execute if this menu item is triggered as String
	// title of ?? as String
	// name of file where this entry cam from as String
	public MenuItem(int key, int bic, int col, int line, def color, boolean bold, boolean italics, int hexColor, boolean show, String text, def command, String title, String filename)
	{
		menuKey = key;		
		menuBIC = bic;
		menuColumn = col;
		menuLine = line;
		menuColor = color;
		menuBold = bold;
		menuItalics = italics;
		menuHexColor = hexColor;
		menuShow = show
		menuText = text;
		menuCommand = command;
		dialogTitle = title;
		menuFileName = filename;
	} // end of constructor

	// needs work here !!!!!
	public applycolor()
	{
		// Hex to color
		int intValue = Integer.parseInt( "ff0000",16);
		Color aColor = new Color( intValue );
	} // apply color


	// make some methods to put values into this MenuItem
	// this one loads the key // 
	void putAt(int n, int c) throws RuntimeException 
	{ 
		if(menuKey == 0) 
		{
			menuKey = c;
		} 
		else 
		{
			throw RuntimeException("menu key already set, cannot set key again");
		} 
	}
	
	void putAt(int n, int c, int l){ if(menuKey == n) menuColumn = c; menuLine= l;  }
	void putAt(int n, String o){ if(menuKey == n) menuCommand= o;  }
	def getAt(int n){ if(menuKey == n) return menuColumn; }

	String toString() {return "menuKey=$menuKey  menuBIC=$menuBIC  menuColumn=$menuColumn   menuLine=$menuLine  menuColor=<$menuColor>  menuBold=<$menuBold>  menuItalics=<$menuItalics>  menuHexColor=<$menuHexColor>  menuShow=<$menuShow>  menuText=<$menuText>  menuCommand=<$menuCommand>  dialogTitle=<$dialogTitle>  menuFileName=<$menuFileName>"}

	// print debug text (maybe)
	public static void say(def text) 
	{
		println "$text" 
	} // end of say

	// test harness for this class
	public static void main(String[] args)
	{	
		say "... started"

		// menu array
		def ma = []

		MenuItem mi = new MenuItem()
		mi[0] = 7 // sets menu key
		assert mi[7]==0	// tests menu key 7 for a column number of zero
		ma << mi	// pump this new mi into our ma array
		mi[7] = "fred" // putAt with a string using menuKey of 7 to load menuCommand = "fred"
		assert mi.menuCommand.equals("fred")	// confirm it was set ok
		
		// now make a brand new menu item
		mi = new MenuItem(1,0,2,2,"Blue",true,true,128,true,"Fred","echo 'fred was here'","title","fn")
		println mi // what's it look like ?
		
		// was command set correctly in prior constructor ?
		assert mi.menuCommand.equals("echo 'fred was here'")   
		// can't get the syntax right for this, single,triple or double quotes won't work - ?? dunno - maybe ?
		
		// load menu item array with this item, so we should have 2 mi entries in ma
		ma << mi
		
		// show toString() value for each menu item
		say "\nma size is ${ma.size()} and has these menu items:"
		ma.each{ m -> println m;}
		
		say "== the end =="
	} // end of main

} // end of class
