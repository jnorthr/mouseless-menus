package org.jnorthr.menus.support;
import java.util.Date;
/* 
 * A support utility to find menu files in a menu directory. 
 */ 
public class Search
{
	// show/hide audit trail msgs
	boolean audit = false;

	// Create a ref for closure
	def searchLogic
	private menus =[:]

	// folder to hold newly created list of menus as a .txt file
	def path
	
    // =========================================
    // class constructor where tx = path to menu folder
	// and default is to write map of menu filenames to output text file
	public Search(String tx)
	{			
		this(tx, true);
	}	// end of method


    // =========================================
	// get menu list from search logic
	// these will be .txt menu files with at least one menu line := like this
	public getMenus() 
	{ 
		return menus
	} // end of 



	// confirm string points to an existing file or path
	def chkobj(String fn)
	{
		File fi = new File(fn.trim());
		return ( fi.exists() ) ? true : false;
	} // end of chkobj
	
	
	// get the directory
	def getCanonical(String fn)
	{
		def cp = new File(fn.trim()).getCanonicalPath().toString();
		say "getCanonical($fn) ="+cp
		return cp;
	} // end of def

    // =========================================
    // class constructor where tx = path to menu folder and flag to say if or not write results
	// and default is to write map of menu filenames to output text file
	public Search(String tx, boolean flag)
	{			
		say "Search($tx) = "

		if ( chkobj(tx) )
		{
			say " ... chkobj ok ...   "
			def fn = getCanonical(tx)
			
			// Apply closure
			searchLogic( new File( fn ) )

			if (flag)
			{
				writeResults(path,menus);	
			} // end of if		
		} // end of if
		
	}	// end of method



    // =========================================
    // show audit trail if allowed
    public getMenuFileNames()
    {
		return menus;
    }	// end of method


    // =========================================
    // show audit trail if allowed
    public say(tx)
    {
    	if (audit) 
		{
			println tx;
		} // end of if
    }	// end of method

    // =========================================
	// Define closure
	public searchLogic(File fh) 
	{ 		
	    print "Dir ${fh.getCanonicalPath()} ";
	    path = fh.getCanonicalPath()
	
		File fh2 = new File(path);
		def lines
		println " gave <$path> ";
        fh2.eachDir{ f -> say "fh2.eachDir= ${ f.toString() }"; searchLogic(f) };
        fh2.eachFileMatch(~/.*?\.txt/) 
        {
				say "fh2.eachFileMatch($it)"
                String fn = it.getCanonicalFile();
				println "\n.. fh2.eachFileMatch:<"+fn+">"
				
				def ok = (fn.toLowerCase().endsWith(".menulist.txt") ) ? false : true; 
                def fi = new File(fn);
                if (fi.exists() && ok)
                {
	            	println "   File ${fn}  ------------";
			    	lines = fi.readLines()
			    	outer: for (line in lines) 
		    	    {	
			 			if (!line.trim()) continue
    					def words = line.split(/\:=/).toList()
        				if (words.size() > 1 && words[1].toLowerCase().equals("*menutitle")) 
						{ 
							menus[fn] = words[0]
							break outer;
						} // end of if
		    	    } // end of for

                } // end of if

            }  // end of eachFile

	} // end of closure
	
	
    // =========================================
	// Write menu list to a permanent file whose path identifies the folder location
	public writeResults(String path,Map menus) 
	{ 
		def tmp = new File(path+"/.menulist.txt")
		println "\n... writing list of menus to "+tmp.getCanonicalFile();
		
		// Instantiate a Date object
        Date date = new Date();

		tmp.write("Available Menus:=*MENUTITLE\n");
		tmp.append ("// Dated ${date.toString()}\n");
		int count = 0
		menus.each{ tmp.append ("${it.value} :=go ${it.key}\n"); count+=1; }
		println "$count menus exist. Press F15 to review."
	} // end of method


// -----------------------------------------------------
// test harness for this class
public static void main(String[] args)
{	
	println "--------------------------------------------------"
	print "... started in folder "
	def path = "./resources/";
	
	if (args.length>0) path = args[0]
	println path;
	def fi = new File(path);
	if (fi.exists())
	{
		Search mf = new Search(path);
		println "\n... doing pass 2 ->"
		mf = new Search(path, false);
		def menus = mf.getMenuFileNames()
		menus.each{fn -> println "... "+fn}
	} // end of if

	println "... the end "
	println "--------------------------------------------------\n"
	}	// end of main
} // end of class