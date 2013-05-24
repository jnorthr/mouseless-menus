package org.jnorthr.menus.support;

// module to find a true-type monospaced font filename; uses 1st word of the o/s name as key into a paths property file
// does NOT define the font size or special features like BOLD - just the font filename - then confirms it exists
import groovy.text.Template
import groovy.text.SimpleTemplateEngine
// need this for property retrieval
import java.io.*;
import java.util.*
import java.awt.*
import javax.swing.*
import java.awt.Font
import org.jnorthr.menus.support.PathFinder

/*
For maximum portability, use the generic font names, but you can use any font installed in the system. It is suggested to use a font family name, and create the font from that, but you can also use the fonts directly. You can get an array of all available font family names or all fonts. 

The path.properties config file uses the 'environments' aspect of groovy ConfigSlurper(), so you pass in an
args. of the name of an o/s within this environment like 'linux'

// Font info is obtained from the current graphics environment.
GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

//--- Get an array of font names (smaller than the number of fonts)
String[] fontNames = ge.getAvailableFontFamilyNames();

//--- Get an array of fonts.  It's preferable to use the names above.
Font[] allFonts = ge.getAllFonts();
*/


class FontSupport
{
	// OS-specific parms; cannot be in same config file as config.rewrite looses some path info
	def os = System.getProperty('os.name')	// like: Mac OS X

	// handle on specific environment subset of the config file
	def paths
	def fontpath
	def monofontfilename

	def static audit = true
	public void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of echo

	public getMonoFontFilename()
	{
		return monofontfilename
	}

	public getFontPath()
	{
		return fontpath
	}

	public getPaths()
	{
		return paths.basepath
	}


	// ===============
	// constructor with operating system id, see path.properties file for matching choices
	// avoids asking the local operating system to choose a collection of font attributes
	// ===============
	public FontSupport(String osid) 
	{ 
		// Use operating system property first word
		say "os name is $osid"
		process(osid);
	} // end of constructor


	// find font path and mono font name based on the operating system id
	public process(String osid)
	{
		say("... FontSupport() set to use paths for $osid")

		PathFinder resourcePath = new PathFinder(); 
		paths = resourcePath.pathMap;	
		say("paths is set to use '$osid' paths of '${paths.fontpath}'")

		fontpath = paths.fontpath
		monofontfilename = paths.monofont
	
		say("the fontpath is '$fontpath'")
		say("the monofontfilename is '$monofontfilename'")

		// build full path+filename like c:\windows\font\cour.ttf
		def fn = fontpath+monofontfilename
		monofontfilename = fn
		say("the full font name is '$fn'")

		// confirm font file exists or fail if not
		def fi = new File(fn)
		if(fi.exists()) {say "$fn was found"}
		else
		{
		    say "$fn was NOT found !"
			def todo = "$fn was not found\nChange path.properties to point to a valid font"
			int messageType = JOptionPane.INFORMATION_MESSAGE; // no standard icon
			JOptionPane.showMessageDialog(null, "$todo", "Font File Missing", messageType);
		} // end of else

		say("... FontSupport() end of constructor ...")
	
	} // end of method


	// ===============
	// constructor
	// ===============
	public FontSupport() 
	{ 
		// Use operating system property first word
		say "os name is $os"
  		def tokens = os.toLowerCase().split(' ').toList()
  		def osid = tokens[0]
		process(osid);
    } // end of default constructor


    // test harness for this class
    public static void main(String[] args)
    {	
        println "\n==============================\n... started\n"
		FontSupport ivs = new FontSupport()
		println "font file name is ${ivs.getMonoFontFilename()}"
		println "access path is "+ivs.getPaths();
		println "font path is "+ivs.getFontPath();
		println "font name is "+ivs.getMonoFontFilename()
		
        println "\n=============================="

		ivs = new FontSupport("usbkey");
		println "font file name is ${ivs.getMonoFontFilename()}"
		println "access path is "+ivs.getPaths();
		println "font path is "+ivs.getFontPath();
		println "font name is "+ivs.getMonoFontFilename()
        println "\n==============================\n... ended\n"
    } // end of main
};    // end of class 


