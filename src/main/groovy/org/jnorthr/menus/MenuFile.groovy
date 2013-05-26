// a module to confirm a text file exists with given name
// if exists, parse out menu title with :=*MENUTITLE and if found set boolean menuFileExists as true
// if true, can then use accessor methods to gain values
package org.jnorthr.menus;
import org.jnorthr.menus.support.Validator;

public class MenuFile
{
	private String dialogTitle	// shows in title of dialog from getTitle()
	private String menuFileName	// when a BIC of 'go' says load and display another menu, this is the file name to load; typically like ../menudata/menu.txt
    private boolean menuFileExists // true if go menufile confirmed to exist         
    Validator val;
 
	// accessor for dialog title
	public getTitle()
	{
		return (menuFileExists) ? dialogTitle : ""	
	} // end of method

	// accessor for full canonical file name
	public getFullFileName()
	{
		return (menuFileExists) ? menuFileName : ""		
	} // end of method

	// accessor to confirm file exists - true or false
	public isMenuFile()
	{
		return menuFileExists;	
	} // end of method

 
	// make a valid menu item text string like:  abc:=./resources/fred.txt
	public crtMenuEntry()
	{
		return (menuFileExists) ? dialogTitle + ":=go " + menuFileName : ""	
	} // end of method



	// no args constructor
	public MenuFile()
	{
		dialogTitle="";
		menuFileName = "";  // filled with canonical name in chkobj if menuFileExists
		menuFileExists = false;
	}	// end of method


	// one args constructor
	public MenuFile(def fn)
	{
		this();
		menuFileName = fn;
		menuFileExists = chkobj(fn);
		if ( menuFileExists )
		{ 
			rtvobjnam(fn);
			loader(fn); 
		}
	}	// end of method


    // ==================================================================
    // look up filename if it exists; print message showing results, if audit flag is set 
    public chkobj(def filename)
    {
        return new File(filename).exists();
    } // end of method


    // ==================================================================
    // look up filename if it exists; print message showing results, if audit flag is set 
    public rtvobjnam(def filename)
    {
		menuFileName = new File(filename).getCanonicalFile().toString().trim();
        return menuFileName
    } // end of method




	// ===============================================================
	// class output debug / print internals
	// print text (maybe)
	public void say(def text) 
	{
		println "$text" 
	} // end of say


	// ==================================================================
	// logic to find a line in this menu text file like abc:=*MENUTITLE
	private loader(def fn)
	{
		new File(fn).eachLine{  ln ->
			println ln;
		    val = new Validator(ln);
			if (!val.remarks && val.valid)
			{		
				println "->"+val.colorComponent+" : "+val.textComponent+" : "+val.commandComponent+"<-"
			} // end of if 
		} // end of each 
	
	
        def fi = new File(fn);
		def lines = fi.readLines();
		lines.each{ln ->
				def words = ln.split(":=");
				if (words.size() > 1)	
				{
					//say "ln has two words: $ln"
					def w1 = words[1].toLowerCase() 
					if (w1.equals("*menutitle"))
					{
						dialogTitle = words[0].trim();
						menuFileExists = true; 	
					}	// end of if
				} // end of if
			}	// end of each
	}	// end of method


	// standard override method
	String toString() 
	{ 
		def ms = "menuFileName=$menuFileName & menuFileExists=$menuFileExists title=<$dialogTitle>"
	}	// end of method


	// -----------------------------------------------------
	// test harness for this class
	public static void main(String[] args)
	{	
		println "--------------------------------------------------"
		println "... started"
		def mfn = "max2.txt"
		if (args.size()<1) 
		{
			println "... no menu filename provided - using internal names"
		}
		else
		{
			mfn = args[0]
		}
		
		println "... running MenuFile with $mfn as argv to module"
		
		MenuFile mf = new MenuFile(mfn);
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();

		println " "

		println "... test using file located at gradle project root "
		mf = new MenuFile("./resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();

		println " "

		println "... test using file located at gradle project root - relative ./ "
		mf = new MenuFile("./src/main/resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();

		println " "
		
		println "... test using file located at gradle project root - not relative - just folder name"
		mf = new MenuFile("src/test/resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();

		println " "
		
		println "... test using file located at actual full directory location "
		mf = new MenuFile("/Volumes/Media1/Software/menus/resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println " "
		
		println "... test with no file name at all"
		mf = new MenuFile();
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println " "
		
		println "... the end "
		println "--------------------------------------------------"

	}	// end of main

 } 	// end of class