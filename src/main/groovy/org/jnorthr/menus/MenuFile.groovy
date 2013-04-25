package org.jnorthr.menus;
public class MenuFile
{
	private String dialogTitle	// shows in title of dialog if BIC number says so
	private String menuFileName	// when a BIC of 'go' says load and display another menu, this is the file name to load; typically like ../menudata/menu.txt
    private boolean menuFileExists // true if go menufile confirmed to exist         

 
	// no args constructor
	public MenuFile()
	{
		dialogTitle="";
		menuFileName = "";
		menuFileExists = false;
	}	// end of method

	// one args constructor
	public MenuFile(def fn)
	{
		this();
		menuFileName = fn;
		menuFileExists = chkobj(fn);
		if (menuFileExists)	{ getTitle(fn); }
	}	// end of method

    // ==================================================================
    // look up filename if it exists; print message showing results, if audit flag is set 
    public chkobj(def filename)
    {
        def fi = new File(filename);
        def flag2 = (fi.exists()) ? true : false
        def tx = (flag2) ? "file $filename exists" : "file $filename does not exist";
		say tx;
		if (flag2)
		{
			menuFileName = fi.getCanonicalFile();
		}
        return flag2
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
	public getTitle(def fn)
	{
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
						dialogTitle = words[0] 	
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
		println " "

		println "... test using file located at gradle project root "
		mf = new MenuFile("./jim.txt");
		println mf
		println " "

		println "... test using file located at gradle project root - relative ./ "
		mf = new MenuFile("./src/main/resources/jim.txt");
		println mf
		println " "
		
		println "... test using file located at gradle project root - not relative - just folder name"
		mf = new MenuFile("src/test/resources/jim.txt");
		println mf
		println " "
		
		println "... test using file located at actual full directory location "
		mf = new MenuFile("/Volumes/Media/Software/groovysamples/src/main/groovy/jim.txt");
		println mf
		println " "
		
		println "... the end "
		println "--------------------------------------------------"

	}	// end of main

 } 	// end of class