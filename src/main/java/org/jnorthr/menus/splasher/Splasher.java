// #! /bin/bash
// export CLASSPATH=.:/Volumes/Data/dev/groovy/groovy-2.0.0/embeddable/groovy-all-2.0.0-beta-1.jar:$CLASSPATH
// cd /Volumes/Data/dev/groovy/Menus
// java  Splasher 
package org.jnorthr.menus.splasher;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.io.File;
import javax.swing.SwingWorker;
import groovy.lang.GroovyShell; // need groovy-all-x.x.x.jar in your CLASPATH env.variable to compile this java source
import org.jnorthr.menus.splasher.SplashWindow;

public class Splasher 
{
    /**
     * Shows the splash screen, launches the application and then disposes
     * the splash screen.
     * @param optional args the command line arguments
     * 		args 1	-	groovy script file name
     * 		args 2	-	image file name to display as splash screen image
     * 		args 3	-	groovy script file name     
     */
    public static void main(String[] args)  throws Throwable 
    {
    	System.out.println("... starting");
    	String fn = "Menus.groovy";
    	String imagepath = "../../../../resources/images/loading.gif";
		String[] path = {"/Volumes/Data/dev/groovy/Menus"};		
    	
    	if (args.length>0) fn=args[0];
    	if (args.length>1) imagepath=args[1];
    	if (args.length > 2) 
    	{
    		//path.clear();
    		path = new String[1];
    		path[0] = args[2];
    	} // end of if
    	
        SplashWindow.splash(Splasher.class.getResource(imagepath));
        
		GroovyShell shell = new GroovyShell();
		shell.run(new File(fn), path);
  
	/*	implement worker threads later
   		SwingWorker worker = new SwingWorker()    
   		{  
   			public Object construct() 
   	   		{   		   	 
 	   		}; // end of construct
   		};

	   	worker.start();
	*/


	//    	shell.run(new File(fn), path);

    //SplashWindow.invokeMain("MyApplication", args);
	//System.out.println("   dispose");
	
    System.out.println("    dispose");
    SplashWindow.disposeSplash();
    System.out.println("... Splasher ending");
	//System.exit(0);
    } // end of main
    
} // end of splasher class
