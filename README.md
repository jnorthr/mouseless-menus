mouseless-menus
===============

IBM iSeries Menu System - a mouse-free alternative

Modules as of wed. 29 may, 2013
__________________________________


// main modules
MenuArray.txt - early attempt to make an array of cells, each with a single menu item

MenuCommandSet.groovy -  an earlier version of the logic to build a system header set as a maximum width of 132 chars; see HeaderSupport for a later way to do this worked ok but columns would not move when window was resized.
- called from MenuHeaderSupport.groovy
- deleted from MenuSupport.groovy

MenuFile.groovy - a module to confirm a text file exists with given name
 -  if exists, parse out menu title with :=*MENUTITLE 
	and if found set boolean menuFileExists as true
 -  if true, can then use accessor methods to gain values
 -  also keeps all valid non-remark lines in a list named menuLines
 -  it's the responsibility of the calling module to provide a menu file name that points to a real file
- called from ColumnSupport.groovy
- called from Search.groovy

MenuItem.groovy - not sure if in use, yet one more try to make a nice menu line object like Validator became
- unused

MenuItem.v2.txt - previous effort to above

Menus.groovy - app wrapper with keyboard handling & swing builder
- called from build.gradle and Splasher

Menus.groovy.old - previous effort to above


//  help window modules
BareBonesBrowserLaunch.java - low-level java code to call o/s specific code to launch a browser
- called from HelpWindow

HelpWindow.java - makes window and calls  BareBonesBrowserLaunch.openURL(xxx) for hyperlinks; 
- called from Menus.groovy


// splasher modules - used to show a splash window while menus starts (which can be slow!)
Splasher.groovy.bak - prior version of below

Splasher.java - Shows the splash screen, launches the application and then disposes of
     * the splash screen.
     * @param optional args the command line arguments
     * 		args 1	-	groovy script file name
     * 		args 2	-	image file name to display as splash screen image
     * 		args 3	-	groovy script file name 
- called from build.gradle


SplashWindow.java - low level awt call to put up an image 
// remember that this approach will NOT work unless the image is on the classpath !
// added gradle logic to include splash image as follows using += syntax
- called from Splasher


// samples
MathOp.java - simple arithmtic

ShowProp.java - get System properties and printout

Simple.java - System.out.println(System.getProperty("simple.message") + args[0] + " from Simple.");

test51.groovy - RSS feed consumer example and several process.execute() tests with diff. configs for the output streams


// support
//  the big bunch of helper logic
BottomBorder.java - 
- called from Menus.groovy

ColorManager.groovy - 
- called from Validator.groovy

ColumnSupport.groovy - 
- called from Search.groovy
- called from Menus.groovy

ConsumeFirefoxBookmarks.groovy - 
- called from build.gradle

ConsumeSafariBookmarks.groovy - 
- called from build.gradle

FontSupport.groovy -
- called from build.gradle

JavaFileLogger.java - 
- called : unknown maybe in /test suite ?

MenuHeaderSupport.groovy - 
- called from MenuPanelSupport.groovy

MenuItem.groovy.old - 
- no reference

MenuItem.groovy.old2 - 
- no reference

MenuPanelSupport.groovy - 
- called from Menus.groovy


MenuStorage.groovy - 
- called from ColumnSupport.groovy

MenuSupport.groovy - 
- called from Menus.groovy

PathFinder.groovy - 
- MenuCommandSet.groovy
- ColumnSupport.groovy
- FontSupport.groovy
- MenuPanelSupport.groovy
- MenuStorage.groovy
- MenuSupport.groovy

Search.groovy - 
- called from Menus.groovy

Tracer.groovy - not used yet
- taken from MenuSupport.groovy

URLClass.java - 
- unused

Validator.groovy - 
- MenuFile.groovy

