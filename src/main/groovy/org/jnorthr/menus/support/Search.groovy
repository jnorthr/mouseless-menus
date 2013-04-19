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
	def searchClos
	private menus =[:]

    // =========================================
    // class constructor where tx = path to menu folder
	// and default is to write map of menu filenames to output text file
	public Search(String tx)
	{			
		def text= tx.trim();

		// Apply closure
		searchClos( new File( text ) )

		writeResults(path,menus);	
		
	}	// end of method



    // =========================================
    // class constructor where tx = path to menu folder and flag to say if o rnot write results
	// and default is to write map of menu filenames to output text file
	public Search(String tx, boolean flag)
	{			
		def text= tx.trim();

		// Apply closure
		searchClos( new File( text ) )

		if (flag)
		{
			writeResults(path,menus);	
		}
		
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
	public searchClos(File folder) 
	{ 
	    println "Dir ${folder.getCanonicalPath()}";
	    def path = folder.getCanonicalPath()
		def lines
		
        folder.eachDir( searchClos );
        folder.eachFileMatch(~/.*?\.txt/) 
        {
                String fn = it.getCanonicalFile();
				//println "... folder.eachFileMatch:<"+fn+">"
				
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
	def path = "/Volumes/Media/Backups/DuracellUSBKey2/Menus/data/";
	
	if (args.length>0) path = args[0]
	println path;

	Search mf = new Search(path);

	mf = new Search(path, false);
	def menus = mf.getMenuFileNames()
	
	menus.each{fn -> println "... "+fn}

	println "... the end "
	println "--------------------------------------------------\n"
	}	// end of main
} // end of class