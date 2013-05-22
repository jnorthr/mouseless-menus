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
        tokens.each{tok -> say tok;}

        int ct = tokens.size()
        say "\nthere are "+ct+" tokens\n"


		// at will point to the token with a lib or libs value
        int at = -1;

		def walking = true;

		// walk backward thru the list until < 0 or lib/libs found
        while(walking)
        {
			ct-=1;
            say "ct="+ct+" and tokens["+ct+"] ="+tokens[ct]
            if (tokens[ct].toLowerCase().equals("lib"))
            {
                at  = ct;
				walking = false;
            } // end of if

            if (tokens[ct].toLowerCase().equals("libs"))
            {
                at  = ct;
				walking = false;
            } // end of if

			if (ct<1) walking = false;

        } // end of while


        def jar = ( tokens[0].toLowerCase().startsWith("jar:") ) ? true : false;
    
		say "\nis this a jar ? "+jar+" and lib is at ${at}\n"
		
		// walk forward for each token
        tokens.eachWithIndex
		{ tok, ix -> 
                print "ix="+ix+" : "+tok;
                if (ix > 0 && ix < at)
                {
                    osid +="/"+tok.trim();
					print " osid = "+osid;
                	flag = chkobj(osid+"/"+menuproperties);
                	if (flag) 
					{ 
						resourcePath = osid; 
						print "  --> ok, we're setting resourcePath="+osid;
					} // end of if
										
                } // end of if
    			println ""
        } // end of each


        // do we have a /lib entry from above search ? at > -1 if yes
        if (at > -1)
        {
            flag = new File(osid).exists()
            say "\ndoes osid=${osid} path exist? "+flag

            // ok, if that path exists, does our /resources folder exist too ?
            if (flag)
            {                
                flag = chkobj(osid+"/"+menuproperties);
                if (flag) { resourcePath = osid; }
            } // end of if
    
        } // end of if
        
        // no, there is no /lib
          
        say "\n... found at=${at} and osid=${osid} and does it exist? ${flag}\n--- the end ---"
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