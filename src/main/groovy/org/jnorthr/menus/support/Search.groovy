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
	boolean audit = true;

	// Create a ref for closure
	def searchLogic

	// collect a map of files that are our menu files
	// key is canonical file name, hopefully unique, value is the title for that file 
	// declared as in this example :  /User/jim/Desktop/menus/fred.txt := Some Groovy Stuff
	private menus =[:]


	// keep all matching menu lines in a collection - loaded from parseResults
	def matchingMenuLines = []

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
			matchingMenuLines = searchLogic( new File( fn ) )

			// ok, discovered all menu files available in this folder, so create and write an 'all' menus file
			if (flag)
			{
			    writeResults(path,matchingMenuLines);	
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
	// Logic to find all menu files in one directory
	public searchLogic(File fh) 
	{ 		
	    say "\nDir ${fh.getCanonicalPath()} ";

	    pwd = fh.getCanonicalPath();

	    if (!hasbeenset)
	    {
			hasbeenset = true;
			path = pwd;
			menus =[:];
			matchingMenuLines = [];
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
		//say "fh2.eachFileMatch($it)"
            	String fn = it.getCanonicalFile();
		//say "\n.. fh2.eachFileMatch:<"+fn+">"
				
		// ignore our own internal .txt file
		def ok = (fn.toLowerCase().endsWith(".menulist.txt") ) ? false : true;
				
		// build new file handle and get all text from that file 
            def fi = new File(fn);
            if (fi.exists() && ok)
            {
	            //say "   File ${fn}  ------------";
		    	lines = fi.readLines()
		    	outer: for (line in lines) 
		    	{	
				if (!line.trim()) continue
    				def words = line.split(/\:=/).toList()

				// find a menu title that identifies this .txt file as a menu file for us
        			if (words.size() > 1 && words[1].toLowerCase().equals("*menutitle")) 
				{ 
					// plug a map with menu filename as key and value= menu title declared in :=*menufile  line
					menus[fn] = words[0]
					def temp = words[0].trim()+":=go "+fn.trim(); //+"\n"		
					matchingMenuLines << temp;					
					break outer;
				} // end of if
	    	    } // end of for

              } // end of if

            }  // end of eachFile
		
			return matchingMenuLines;
	} // end of closure
	

    // =========================================
	// Parse menu map to identify menu lines within each text file that match the search criteria
	public parseResults(String findthis) 
	{ 
		return parseResults( findthis, menus )
	} // end of method



	// split search string into one or more words
	def getParseTokens(String findthis)
	{
		def tokens = findthis.trim().toLowerCase().split(' ').toList()
		def finder =[]
		tokens.each{e->
			String ee = e.trim() 
			if (ee.size()>0) 
			{
				finder += ee.toLowerCase()
			} // end of if			
		} // end of each
			
		say "... ok, split <$findthis> into ${finder.size()} tokens"
		return finder;
	} // end of splitting


	// compare search string in one or more words to chosen line
	def compareTokens(def words, String line)
	{
		int matched = 0;
		
		// words were trimmed() and lowercased above
		words.each{ e->  
			if ( line =~ e ) {matched += 1; say "... e of <${e}> matched <${line}>"  }
		} // end of each
		
		def ok = ( words.size()==matched ) ? true : false;
		return ok;
	} // end of splitting


	// see if this is a menu title line and not a real link
	def isNotTitleLine(String ln)
	{
		int g = ln.toLowerCase().indexOf("*menutitle")
		return (g < 0) ? true : false;
	} // end of isTitleLine

    // =========================================
	// Parse map of menu files to identify menu lines within each text file that match the search criteria
	// returns a list of each and every menu item line having the target text string regardless of case
	public parseResults(String findthis, Map menus) 
	{ 
		say "parseResults(${findthis})"
		menus.each{say "... ->"+it}


		boolean findAllMenus = (findthis.equals("*ALLMENUS")) ? true : false;
		boolean findAll = (findthis.equals("*ALL")) ? true : false;


		// split search term into several words
		def words = getParseTokens(findthis);
		say "parseResults(${findthis}) found ${words.size()} words; findAllMenus=$findAllMenus  findAll=$findAll"
		
		// keep all matching menu lines in a collection
		 matchingMenuLines = []

		//walk thru each menu file
		menus.each
		{ men ->
			say "---> ready for menu :"+men.key
			File me = new File(men.key);
			def lines = me.readLines()
			
			say "   ${men.key} had ${lines.size()} lines"
			
			
			// *ALLMENUS comes here
			if (findAllMenus) 
			{
				def tx2 = men.value.trim() + ":=go " + men.key
				matchingMenuLines << tx2
			} // end of if
			
			// not *ALLMENUS
			else
			{
				// *ALL to pick each and every menu line that's not a *menutitle
				if (findAll)
				{
		    		lines.each
					{	li -> 
						if (isNotTitleLine(li))
						{
							matchingMenuLines << li;
							say "... stored "+li;
						} // end of if				
		    		} // end of eachLine
										
				} // end of if findAll
				
				else
				{			
		    		lines.each
					{	ln -> 
						if ( convertCase(ln) =~ findthis || compareTokens( words, ln ) )
						{
							if (isNotTitleLine(ln))
							{
								matchingMenuLines << ln;
								say "... stored "+ln;
							} // end of if
        				} // end of if
		    		} // end of eachLine
	
				} // end of else

			} // end of else

						
			say "\nso we now have our target lines for "+findthis
			matchingMenuLines.each{ say it; }
		} // end of each

		say "--- the end of parse ---"
		return matchingMenuLines;

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
	public writeResults(String path, def matchingMenuLines ) 
	{ 
		writeResults(path, matchingMenuLines, "Available Menus");
	} // end of method
	
	
    // =========================================
	// Write menu list to a permanent file whose path identifies the folder location
	// with menu title as specified

	public writeResults(String path, def menus, String tl) 
	{ 
		def tmp = new File(path+"/.menulist.txt")
		say "\n... writing list of menus to "+tmp.getCanonicalFile();

		def reorder=[]

		menus.each
		{ entry ->
			if (menus instanceof Map)
			{
				String xx = entry.value.trim() + "|"+ "${entry.value.trim()} :=go ${entry.key.trim()}\n"		
				reorder << xx;
			} // end of
			else
			{
				int ii = entry.indexOf(":=");
				if (ii > -1)
				{
					String k = entry.substring(0,ii) + "|" + entry + "\n";
					reorder << k;
				} // end of if
			}
		} // end of each

		reorder.sort();


		tmp.write("${tl} As Of ${now}:=*MENUTITLE\n");
		tmp.append "// This menu was created on : ${now} with ${reorder.size()} menu items\n"

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
		def path = "resources";
	
		if (args.length>0) 
		{
			path = args[0];
			println "--> setting path from args[0] to "+path
		}
		println "--> path now "+path;

		def fi = new File(path);
		if (fi.exists())
		{
			println "--> running";

			Search mf = new Search(path);
			mf.audit=false;
			println "\n... doing pass 2 -> from path="+path
			mf = new Search(path, false); // false means dont write results file
			def menus = mf.getMenuFileNames()
			menus.each{fn -> println "... "+fn}
			mf.audit=true;

			println "\n now find a series of menu items that match our search criteria"
			def github = mf.parseResults("^GitHub*",menus);    
			println "\n\n-------------------------\n  Searched for github and found:"
			github.each{key ->println "---> key:"+key;}
			mf.writeResults(path,github, """Your Search for 'GitHub' As Of ${mf.now}""")

			println "-------------------------\nfind items with two terms in search sequence"
			println "\n ---> search for '  groovy  grep' found :"

			def re = mf.parseResults("  groovy  grep");
			re.each{println "--->"+it;}
			mf.writeResults(path,re, """Your Search for '  groovy  grep' As Of ${mf.now}""")

			re = mf.parseResults("*ALLMENUS");
			re.each{println "--->"+it;}
			mf.writeResults(path,re, """Your Search for All Menus As Of ${mf.now}""")


			println "-------------------------\nok\n"
		} // end of if

		println "... the end "
		println "--------------------------------------------------\n"
	} // end of main

} // end of class

/*
* logic for menus keyboard access
			case KeyEvent.VK_F4:  // move x coordinate left
			if (f)
			{
				println "F16 key pressed"
				def path = "./resources"
				org.jnorthr.menus.support.Search mf = new org.jnorthr.menus.support.Search(path);
				def re = mf.parseResults(swing.tf.text);
				re.each{println "--->"+it;}
				boolean hasText = (swing.tf.text.trim().size() > 0)  ? true : false;
				def searchWas = (hasText) ? "'"+swing.tf.text.trim()+"'" : "Everything"
				mf.writeResults(path,re, """Your Search for ${searchWas} """)				
				String menu = "./resources/.menulist.txt"; 
				MenuColumnSupport.loadMenu(cs,menu)    
				frame.setTitle(MenuColumnSupport.getFrameTitle())
				support.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
			} // end of shift

			else
			{
				println "F4 key pressed"
				//ender()
			} // end of 
			break;
*/
