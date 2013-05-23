package org.jnorthr.menus.support;
/*
	Class to resolve the path to the ./resources folder when the
	menu system is run from a .jar, as this may have a different path to ./resources
*/

public class PathFinder
{
    def audit = true
    def osid = new StringBuffer();
    
    String menuproperties = "resources/properties/menu.properties";

    // does path exist ? - assume no
    def flag = false
    def resourcePath = "";
    String location = "";


    // does a resources path exist ?    
    public exists()
    {
        return flag;
    } // end of method
    
    
    // does a resources path exist ?    
    public getResourcePath()
    {
        return resourcePath;
    } // end of method
    
    
    // show audit trail ?    
    public say(tx)
    {
        if (audit) println tx;
    }

        
    // default class constructor - 
    public PathFinder()
    {
        URL loc = this.class.getProtectionDomain().getCodeSource()?.getLocation();
        say "PathFinder() loaded from :"+loc.toString();

        ClassLoader loader = this.class.getClassLoader();
		location = loader.getResource("org/jnorthr/menus/support/PathFinder.class").toString();
		say "-->"+location;
		
		if (location==null) location=loc;
		
		// ok, let's do it
		discovery(location);
    } // end of constructor


    // class constructor - 
    public PathFinder(String path)
    {        
		discovery(path);
    } // end of constructor


    // discovery method - 
    public discovery(String path)
    {        
		say "... discovery underway for "+path
        resourcePath = path;

		// here we split the path into tokens using /
        def tokens = path.trim().split("/").toList()
        tokens.each{tok -> say "...."+tok;}

        int ct = tokens.size()
        say "\nthere are "+ct+" tokens\n----------------------------\n"

		def type = 0;
        type += ( tokens[0].toLowerCase().startsWith("jar:") ) ? 1 : 0 ;
        type += ( tokens[0].toLowerCase().startsWith("file:") ) ? 2 : 0 ;
    
    	// set lowest starting token
    	int i = (type>0) ? 0 : -1;
		say "\nwhat is this ? "+type+" and i=${i}\n"
		          
		// if no tokens, dont walk else walk
		def walking = ( ct > 0 ) ? true : false;
		def pn="";
		
		while (walking)
		{
			if (--ct > i)
			{
				println "\n... "+ct+" = "+tokens[ct]+"; ";
				pn="";
				ct.times{a -> print " a="+a+"; " 
						if (a>i) pn += "/"+tokens[a]				 
				} // end of times
				
				def fg = chkobj(pn+"/"+menuproperties)
				println pn+" exists ? "+fg;
				
				
			}
		
			//ct-=1;
			if (ct<1) walking = false;
		} // end of while
		
        //say "\n... found at=${at} and osid=${osid} and does it exist? ${flag}\n--- the end ---"
    } // end of constructor


	// does this filename point to a file that is present ?
	public boolean chkobj(String f)
	{
		return new File(f).exists();
	}

	// =======================================================================
    // test harness for this class
    public static void main(String[] args)
    {    
        println "\n==============================/n... started"
        def path="jar:file:/Volumes/Media1/Software/menus/build/libs/menus-1.0.jar!/org/jnorthr/menus/support/PathFinder.class"
        PathFinder resourcePath;

        println "... path input:"+path
        resourcePath = new PathFinder(path)
       	if (resourcePath.exists())
      	{
          	println "... path to:"+path
           	println "    has resource path at :"+resourcePath.getResourcePath();        
       	}
       	else
       	{
           	println "... path to:"+path
           	println "    exists ? "+resourcePath.exists();
   		} // end of else

 		println "\n----------------------------------------\n"

        if (args.size() > 0)
        {
            path = args[0];        
        	resourcePath = new PathFinder(path)
            println "... path input:"+path
        	if (resourcePath.exists())
       		{
           		println "... path to:"+path
           		println "    has resource path at :"+resourcePath.getResourcePath();        
       		}
       		else
       		{
           		println "... path to:"+path
           		println "    exists ? "+resourcePath.exists();
   			} // end of else

        } // end of if


 		println "\n----------------------------------------\n"
        resourcePath = new PathFinder();		
       	if (resourcePath.exists())
       	{
           	println "... path to:"+resourcePath.location
           	println "    has resource path at :"+resourcePath.getResourcePath();        
       	}
       	else
      	{
         	println "... path to:"+resourcePath.location
           	println "    exists ? "+resourcePath.exists();
		} // end of else

		println "\n----------------------------------------\n"
        resourcePath = new PathFinder(resourcePath.menuproperties);		
       	if (resourcePath.exists())
       	{
           	println "... path to:"+resourcePath.menuproperties
           	println "    has resource path at :"+resourcePath.getResourcePath();        
       	}
       	else
      	{
         	println "... path to:"+resourcePath.menuproperties
           	println "    exists ? "+resourcePath.exists();
		} // end of else


        println "... ended\n===================================\n"
    } // end of main


} // end of class