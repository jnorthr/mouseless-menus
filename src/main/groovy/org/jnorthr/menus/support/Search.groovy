package org.jnorthr.menus.support;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/* 
 * A support utility to find menu files in a menu directory. 
 * Write filenames to <menu directory>+"/.menulist.txt"
 * Given the title of "Available Menus:=*MENUTITLE"
 */ 
public class Search
{
	// show/hide audit trail msgs
	boolean audit = false;

	// Create a ref for closure
	def searchLogic

	// collect a map of files that are our menu files
	// key is canonical file name, hopefully unique, value is the title for that file 
	// declared as in this example :  /User/jim/Desktop/menus/fred.txt := Some Groovy Stuff
	private menus =[:]


	// keep all matching menu lines in a collection - loaded from parseResults
	def imports = []

	// folder to hold newly created list of menus as a .txt file
	def path
	boolean hasbeenset = false;
	def pwd
	
	DateFormat dateFormat = new SimpleDateFormat("EEE. dd MMM yyyy HH:mm:ss");
    
	//get current date time with Date()
    	Date date = new Date();
    	def now = dateFormat.format(date);


    	// =========================================
    	// class constructor where tx = path to menu folder
	// and default is to write map of menu filenames to output text file
	public Search(String tx)
	{			
		this(tx, true);
	}	// end of method


	// =========================================
	// get menu list from search logic
	// a map of .txt menu files with at least one menu line := like this
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
	
	
	// get the directory name as a string and return it
	def getCanonical(String fn)
	{
		def cp = new File(fn.trim()).getCanonicalPath().toString();
		say "getCanonical($fn) ="+cp
		return cp;
	} // end of def


	// =========================================
    	// class constructor where tx = path to menu folder and flag to say if or not write results
	// to write map of menu filenames to output text file
	public Search(String tx, boolean flag)
	{			
		say "Search($tx) = "

		// confirm file/folder exists
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
		
	} // end of method



	// =========================================
    	// pull out map of all text files that were idenified as menu files
    	public getMenuFileNames()
    	{
	    return menus;
    	} // end of method


    	// =========================================
    	// show audit trail if allowed
    	public say(tx)
    	{
    		if (audit) 
		{
			println tx;
		} // end of if
   	 } // end of method


	// =========================================
	// Define closure
	public searchLogic(File fh) 
	{ 		
	    print "Dir ${fh.getCanonicalPath()} ";

	    pwd = fh.getCanonicalPath();

	    if (!hasbeenset)
	    {
		hasbeenset = true;
		path = pwd;
	    } // end of if

	    File fh2 = new File(pwd);
	    def lines
	    say " gave <$pwd> ";
            fh2.eachDir
	    { 
		f -> 	say "fh2.eachDir= ${ f.toString() }"; 
			searchLogic(f) 
	    };

            fh2.eachFileMatch(~/.*?\.txt/) 
            {
		// give an audit of the file name that matches the .txt spec
		say "fh2.eachFileMatch($it)"
                String fn = it.getCanonicalFile();
		say "\n.. fh2.eachFileMatch:<"+fn+">"
				
		// ignore our own internal .txt file
		def ok = (fn.toLowerCase().endsWith(".menulist.txt") ) ? false : true;
				
		// build new file handle and get all text from that file 
                def fi = new File(fn);
                if (fi.exists() && ok)
                {
	            println "   File ${fn}  ------------";
		    lines = fi.readLines()
		    outer: for (line in lines) 
		    {	
			if (!line.trim()) continue
    			def words = line.split(/\:=/).toList()

			// find a menu title that identifies this .txt file as a menu file for us
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
	// Parse menu map to identify menu lines within each text file that match the search criteria
	public parseResults(String findthis) 
	{ 
		return parseResults( findthis, menus )
	} // end of method


    	// =========================================
	// Parse menu map to identify menu lines within each text file that match the search criteria
	// returns a list of each and every menu item line having the target text string regardless of case
	public parseResults(String findthis, Map menus) 
	{ 
		say "parseResults(${findthis})"
		menus.each{say "... ->"+it}

		// keep all matching menu lines in a collection
		imports = []

		menus.each
		{ men ->
			say "---> ready for menu :"+men.key
			File me = new File(men.key);
			def lines = me.readLines()
			say "   ${men.key} had ${lines.size()} lines"
			
    			lines.each
			{
        		    ln -> if ( ln =~ findthis || convertCase(ln) =~ findthis  ) 
				  {
            				imports << ln;
					say "... stored "+ln;
        			  } // end of if
    			} // end of eachLine

			//say "\nso we now have our target lines for "+findthis
			//imports.each{ say it; }
		} // end of each

		say "--- the end of parse ---"
		return imports;

	} // end of method


	// logic to convert input to lowercase and strip out all blanks, even when within a word, so
	// A S 4 0 0  I B M :=
	// becomes as400ibm
	def convertCase(tx)
	{	
		def txlc = tx.trim().toLowerCase();
		def word = ""
		txlc.each{letter-> if (letter!=' ') word+=letter;}
		return word;
	} // end of convert
	
    	// =========================================
	// Write menu list to a permanent file whose path identifies the folder location
	public writeResults(String path,Map menus) 
	{ 
		def tmp = new File(path+"/.menulist.txt")
		println "\n... writing list of menus to "+tmp.getCanonicalFile();

		def reorder=[]
		menus.each
		{ 	
			String entry = it.value.trim() + "|"+ "${it.value.trim()} :=go ${it.key.trim()}\n"
			reorder << entry;
		} // end of each

		reorder.sort();

		tmp.write("Available Menus:=*MENUTITLE\n");
		tmp.append "// This menu was created on : ${now}\n"

		int count = 0
		reorder.each
		{ 
			int i = it.indexOf("|");
			tmp.append it.substring(i+1);   //("${it.value} :=go ${it.key}\n"); 
			count+=1; 
		} // end of each

		println "$count menus exist. Press F15 to review."
	} // end of method


	// -----------------------------------------------------
	// test harness for this class
	public static void main(String[] args)
	{	
		println "--------------------------------------------------"
		print "... started in folder "
		def path = "./resources";
	
		if (args.length>0) 
		{
			path = args[0];
			println "--> setting path from args[0] to "+path
		}
		println "--> path now "+path;

		def fi = new File(path);
		if (fi.exists())
		{
			Search mf = new Search(path);
			println "\n... doing pass 2 -> from path="+path
			mf = new Search(path, false);
			def menus = mf.getMenuFileNames()
			//menus.each{fn -> println "... "+fn}
		
			println "\n now find a series of menu items that match our search criteria"
			def git = mf.parseResults("as/400",menus);    // "^GitHub*"
			println "\n\n-------------------------\n  Searched for git and found:"
			git.each{key ->println "---> key:"+key;}

			def re = mf.parseResults("cherokee");
			println "\n ---> search for 'cherokee' found :"
			re.each{println "--->"+it;}
			println "-------------------------"
		} // end of if

		println "... the end "
		println "--------------------------------------------------\n"
	} // end of main

} // end of class