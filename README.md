mouseless-menus
===============

IBM iSeries Menu System - a mouse-free alternative

Modules as of wed. 29 may, 2013

// main modules
MenuArray.txt - early attempt to make an array of cells, each with a single menu item

MenuCommandSet.groovy -  an earlier version of the logic to build a system header set as a maximum width of 132 chars; see HeaderSupport for a later way to do this worked ok but columns would not move when window was resized.

MenuFile.groovy - a module to confirm a text file exists with given name
 -  if exists, parse out menu title with :=*MENUTITLE 
	and if found set boolean menuFileExists as true
 -  if true, can then use accessor methods to gain values
 -  also keeps all valid non-remark lines in a list named menuLines
 -  it's the responsibility of the calling module to provide a menu file name that points to a real file

MenuItem.groovy - not sure if in use, yet one more try to make a nice menu line object like Validator became

MenuItem.v2.txt - previous effort to above

Menus.groovy - app wrapper with keyboard handling & swing builder

Menus.groovy.old - previous effort to above


//  help window modules
BareBonesBrowserLaunch.java - low-level java code to call o/s specific code to launch a browser

HelpWindow.java - makes window and calls  BareBonesBrowserLaunch.openURL(xxx); 


// splasher modules - used to show a splash window while menus starts (which can be slow!)
Splasher.groovy.bak - prior version of below

Splasher.java - Shows the splash screen, launches the application and then disposes of
     * the splash screen.
     * @param optional args the command line arguments
     * 		args 1	-	groovy script file name
     * 		args 2	-	image file name to display as splash screen image
     * 		args 3	-	groovy script file name 

SplashWindow.java - low level awt call to put up an image 
// remember that this approach will NOT work unless the image is on the classpath !
// added gradle logic to include splash image as follows using += syntax

// samples
MathOp.java - simple arithmtic

ShowProp.java - get System properties and printout

Simple.java - System.out.println(System.getProperty("simple.message") + args[0] + " from Simple.");

test51.groovy - RSS feed consumer example and several process.execute() tests with diff. configs for the output streams


// support
//  the big bunch of helper logic
BottomBorder.java - 

ColorManager.groovy - 

ColumnSupport.groovy - 

ConsumeFirefoxBookmarks.groovy - 

ConsumeSafariBookmarks.groovy - 

FontSupport.groovy -

JavaFileLogger.java - 

MenuHeaderSupport.groovy - 

MenuItem.groovy.old - 

MenuItem.groovy.old2 - 

MenuPanelSupport.groovy - 

MenuStorage.groovy - 

MenuSupport.groovy - 

PathFinder.groovy - 

Search.groovy - 

Tracer.groovy - 

URLClass.java - 

Validator.groovy - 

