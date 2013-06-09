// a module to confirm a text file exists with given name
// if exists, parse out menu title with :=*MENUTITLE and if found set boolean menuFileExists as true
// if true, can then use accessor methods to gain values

// also keeps all valid non-remark lines in a list<Validator>

// it's the responsibility of the calling module to provide a menu file name that points to a real file
package org.jnorthr.menus;
import org.jnorthr.menus.support.Validator;

public class MenuFile
{
    // show/hide audit trail msgs
    boolean audit = false;

	// shows in title of dialog from getTitle(); loaded from *menutitle line
	private String dialogTitle = ""

	// this is the file name to load; typically like ../resources/menu.txt
	// when a BIC of 'go' says load and display another menu, 
	private String menuFileName=""

	// true if menuFileName confirmed to exist
	private boolean menuFileExists = false          

	// handle to validator for a single line
    Validator val;
    	
	// List of lines that would make good menu entries, excludes remarks lines // and *menutitle lines 
	List<Validator> MenuWrapper() = []   	
    

	// accessor for dialog title
	public getTitle()
	{
		return (menuFileExists) ? dialogTitle : ""	
	} // end of method


	// accessor for full canonical file name of this menu
	public getFullFileName()
	{
		return (menuFileExists) ? menuFileName : ""		
	} // end of method


	// accessor to confirm file exists - true or false
	public isMenuFile()
	{
		return menuFileExists;	
	} // end of method


 	// accessor to find number of valid lines in the menu table
	public int getMenuLineCount()
	{
		return menuLines.size();	
	} // end of method


	// make a valid menu item text string like:  abc:=go ./resources/fred.txt
	public crtMenuEntry()
	{
		return (menuFileExists) ? dialogTitle + ":=go " + menuFileName : ""	
	} // end of method


	// no args constructor
	public MenuFile()
	{
		menuFileName = "";  // filled with canonical name in chkobj if menuFileExists
		dialogTitle = "";
		menuFileExists = false;
	}	// end of method


	// one args constructor for name of a file, that might be our menu file, as a string
	public MenuFile(def fn)
	{
		this();
		menuFileName = fn;
		menuFileExists = chkobj(fn);
		if ( menuFileExists )
		{ 
			rtvobjnam(fn);

			// now read each line, validate it and store in menuLines
			loader(fn); 
		} // end of if
		
	}	// end of method


    // ==================================================================
    // look up filename if it exists; print message showing results, if audit flag is set 
    public chkobj(def filename)
    {
    	def f = new File(filename)
    	boolean dir = f.isDirectory() ? true : false;
        return f.exists()  && !dir;
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
		if (audit) println "$text" 
	} // end of say


	// ==================================================================
	// logic to find a line in this menu text file like abc:=*MENUTITLE
	private loader(def fn)
	{
		menuLines = [];	
		new File(fn).eachLine{ ln ->
			if ( ln.size() > 1 ) processor(ln);
		} // end of each 
	
	}	// end of method


	// ==================================================================
	// logic to find a line in this menu text file like abc:=*MENUTITLE
	private processor(ln)
	{
		say ln;
	    val = new Validator(ln);
		if (!val.remarks && val.valid)
		{		
			say "->"+val.colorComponent+" : "+val.textComponent+" : "+val.commandComponent+"<-"
			def w1 = val.commandComponent.toLowerCase() 
			if (w1.equals("*menutitle"))
			{
				dialogTitle = val.textComponent;
			}	// end of if
			else
			{
				menuLines += val;
			} // end of else
			
		} // end of if 
		else
		{
			if (!val.remarks)
			{
				menuLines += val;
			} // end of if
					
		} // end of else
		

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
		println "getMenuLineCount() :"+mf.getMenuLineCount();

		println " "

		println "... test using file located at gradle project root "
		mf = new MenuFile("./resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println "getMenuLineCount() :"+mf.getMenuLineCount();

		println " "

		println "... test using file located at gradle project root - relative ./ "
		mf = new MenuFile("./src/main/resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println "getMenuLineCount() :"+mf.getMenuLineCount();

		println " "
		
		println "... test using file located at gradle project root - not relative - just folder name"
		mf = new MenuFile("src/test/resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println "getMenuLineCount() :"+mf.getMenuLineCount();

		println " "
		
		println "... test using file located at actual full directory location "
		mf = new MenuFile("/Volumes/Media1/Software/menus/resources/jim.txt");
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println "getMenuLineCount() :"+mf.getMenuLineCount();
		
		println " "
		
		println "... test with no file name at all"
		mf = new MenuFile();
		println mf
		println "isMenuFile() ? :"+mf.isMenuFile();
		println "getTitle() :"+mf.getTitle();
		println "crtMenuEntry() :"+mf.crtMenuEntry()
		println "getFullFileName() :"+mf.getFullFileName();
		println "getMenuLineCount() :"+mf.getMenuLineCount();

		println " "
		
		println "... the end "
		println "--------------------------------------------------"

	}	// end of main

 } 	// end of class