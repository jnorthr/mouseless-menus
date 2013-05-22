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
		String at = loader.getResource("org/jnorthr/menus/support/PathFinder.class").toString();
		say "-->"+at;
		discovery(at);
    } // end of constructor


    // class constructor - 
    public PathFinder(String path)
    {        
		discovery(path);
    } // end of constructor


    // discovery method - 
    public discovery(String path)
    {        
        resourcePath = path;
        def tokens = path.trim().split("/").toList()
        tokens.each{tok -> say tok;}
        int ct = tokens.size()
        say "\nthere are "+ct+" tokens\n"
        int at = -1;

        while(ct-- > 0)
        {
            say "ct="+ct+" and tokens["+ct+"] ="+tokens[ct]
            if (tokens[ct].toLowerCase().equals("lib"))
            {
                at  = ct;
            } // end of if
        } // end of while

        at = ( tokens[0].toLowerCase().startsWith("jar:") ) ? at : -1;
    
        tokens.eachWithIndex{tok, ix -> 
                say "ix="+ix+" : "+tok;
                if (ix > 0 && ix < at)
                {
                    osid +="/"+tok.trim()
                } // end of if
    
        } // end of each


        // do we have a /lib entry from above search ? at > -1 if yes
        if (at > -1)
        {
            flag = new File(osid).exists()
            say "does osid=${osid} path exist? "+flag

            // ok, if that path exists, does our /resources folder exist too ?
            if (flag)
            {                
                flag = new File(osid+"/"+menuproperties).exists()
                if (flag) { resourcePath = osid; }
            } // end of if
    
        } // end of if
        
        // no, there is no /lib
          
        say "\n... found at=${at} and osid=${osid} and does it exist? ${flag}\n--- the end ---"
    } // end of constructor


    // test harness for this class
    public static void main(String[] args)
    {    
        println "... started"
        def path="jar:file:/Volumes/Media1/TestData/menus-1.0/lib/menus-1.0.jar!/org/jnorthr/menus/support/PathFinder.class"
        PathFinder resourcePath;
 
        if (args.size() > 0)
        {
            path = args[0];        
        	resourcePath = new PathFinder(path)
        } // end of if
		else
		{
        	resourcePath = new PathFinder();		
		} // end of else

        if (resourcePath.exists())
       	{
           	println "... path to:"+path
           	println "    has resource path at :"+resourcePath.getResourcePath();        
       	}
       	else
       	{
           	println "... path to:"+path
           	println "    exists ?"+resourcePath.exists();
   		} // end of else

        println "... ended"
    } // end of main


} // end of class