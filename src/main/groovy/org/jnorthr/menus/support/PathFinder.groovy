package org.jnorthr.menus.support;
/*
    Class to resolve the path to the ./resources folder when the
    menu system is run from a .jar, as this may have a different path to ./resources
*/
import javax.swing.JOptionPane;

public class PathFinder
{
    def audit = false
    def osid = new StringBuffer();
    
    String menuproperties = "resources/properties/menu.properties";
    String pathproperties = "resources/properties/path.properties";

    // does path exist ? - assume no
    def found = false
    
    // what is the path ?
    def resourcePath = "";
    
    // give full path name to menu props
    def menuPropertiesPath = "";

    // give full path name to path props
    def pathPropertiesPath = "";
    
    // folder where this class currently sits - if getClass was called, but might not be if found earlier in search seq.
    String location = "";

    // where is the path ?
    def resourcePathDiscovery = "not found";


    // cook your own class toString
    public String toString()
    {
        String tx = "Resource found ? ${(found)?"yes":"no";}\nPath to Resource : ${resourcePath}\nPath to menu properties : ${menuPropertiesPath}\nPath to path properties : ${pathPropertiesPath}"
        return tx;    
    } // end of toString()
    

    // does a resources path exist ?    
    public exists()
    {
        return found;
    } // end of method
    
    
    // does a resources path exist ?    
    public getResourcePath()
    {
        return resourcePath;
    } // end of method
    
    // does a resources path exist ?    
    public getResourcePathLocation()
    {
        return resourcePathDiscovery;
    } // end of method
    
    
    // show audit trail ?    
    public say(tx)
    {
        if (audit) println tx;
    }


    // get MENU_RESOURCES
    public getEnvironmentVariable()
    {
        resourcePathDiscovery = "MENU_RESOURCES Environment Variable"
        String env = System.getenv("MENU_RESOURCES");
        if (env==null) env="";
        int j = env.lastIndexOf('/');
        if (j>-1) j+=1;
        int k = env.size();
        if (j<k && j > -1)
        {
        	env+="/"
        } // end of if
        
        say "getEnvironmentVariable() returns "+env
        return env;
    } // end of
    

    // get current directory
    public getPWD()
    {
        resourcePathDiscovery = "Current Working Directory (pwd)"    
        String pwd = System.getProperty("user.dir");
        say "getPWD() returns "+pwd
        return pwd;
    } // end of
        
        
    // get home directory
    public getHOME()
    {
        resourcePathDiscovery = "User's Home Directory"    
        String home = System.getProperty("user.home");
        say "getHOME() returns "+home
        return home;
    } // end of


    // get directory of executing class
    public getCLASS()
    {
        URL loc = this.class.getProtectionDomain().getCodeSource()?.getLocation();
        say "getCLASS() PathFinder loaded from :"+loc.toString();

        resourcePathDiscovery = "Class Loader Path"    
        ClassLoader loader = this.class.getClassLoader();
        location = loader.getResource("org/jnorthr/menus/support/PathFinder.class").toString();
        say "getCLASS() ClassLoader location :"+location;
        
        if ( location.equals("null") ) 
        {
        	location=loc;
	        resourcePathDiscovery = "Class Location"    
        } // end of if
        
        say "getCLASS() returns "+location;
        return location;
    } // end of


    // default class constructor -
    // searches for our properties in this sequence : 
    // 1.current working directory  2.user's home directory  3.location of this class  4.MENU_RESOURCES env. variable
    public PathFinder()
    {
    	say "PathFinder() default constructor"
        if (!exists())
        {
            discovery(getPWD());
        } // end of if 
        

        if (!exists())
        {
            discovery(getHOME());
        } // end of if 
        
                
        if (!exists())
        {
            String path_to_resources = getEnvironmentVariable();
            if (path_to_resources!=null)
            {
                discovery(path_to_resources);
            } // end of if    
        } // end of if 


        if (!exists())
        {
            discovery(getCLASS());
        } // end of if         
        

        if (!exists())
        {
        	resourcePathDiscovery = "not found"
            JOptionPane.showMessageDialog(null, "Cannot find ${menuproperties} property files\nset MENU_RESOURCES environment var.", 
            "Bad News !", JOptionPane.ERROR_MESSAGE); 
            //System.exit(1);
        } // end of if 

    } // end of constructor


    // class constructor - 
    // your choice, but if no properties found, say so
    public PathFinder(String path)
    {        
    	say "PathFinder(${path}) constructor"
        discovery(path);

        if (!exists())
        {
        	resourcePathDiscovery = "not found"
            JOptionPane.showMessageDialog(null, "Cannot find ${menuproperties} property files\nset MENU_RESOURCES environment var.", 
            "Bad News !", JOptionPane.ERROR_MESSAGE); 
            //System.exit(1);
        } // end of if 

    } // end of constructor


	// -------------------------------------------------------------------------------
    // discovery method - 
    public discovery(String path)
    {        
        say "... discovery underway for "+path
        
        if (path==null || ( path.size() < 1 ))
            return;
            
        // here we split the path into tokens using /
        def tokens = path.trim().split("/").toList()
        tokens.each{tok -> say "...."+tok;}

        int ct = tokens.size()
        say "there are "+ct+" tokens\n----------------------------"


		// no words in path ? bail out...
		if (ct<1) return;


        def type = 0;
        type += ( tokens[0].toLowerCase().startsWith("jar:") ) ? 1 : 0 ;
        type += ( tokens[0].toLowerCase().startsWith("file:") ) ? 2 : 0 ;
        type += ( tokens[0].size() < 1 ) ? 4 : 0 ;  // root declaration like /Volumes/Users/jim
    
        // set lowest starting token
        int i = (type>0) ? 0 : -1;
        say "\nwhat is this ? "+type+" and i=${i}\n"
                  
		// set loop flag
        def walking = true;
        def pn="";
        
        while (walking)
        {
            if (ct > i)
            {
                say "\n... "+ct+" = "+tokens[ct]+"; ";
                pn="";
                ct.times{a -> 
                        if ( a > i  && ( tokens[a].size() > 0 ) ) 
                        {
                        	if (a>0) { pn += "/"; }
                        	pn += tokens[a];
                        } // end of if                 
                } // end of times
                
                def fg = chkobj(pn+"/"+menuproperties)
                say pn+" exists ? "+fg;
                
                if (fg)
                {

                    if ( chkobj(pn+"/"+menuproperties) && chkobj(pn+"/"+pathproperties) )
                    {
                        walking = false;
                        found = true;
                        resourcePath = pn;
                        menuPropertiesPath = pn+"/"+menuproperties;
                        pathPropertiesPath = pn+"/"+pathproperties;
                    }
                    else
                    {
                        found = false;
                        resourcePath = "";
                        menuPropertiesPath = "";
                        pathPropertiesPath = "";
                        JOptionPane.showMessageDialog(null, "Path ${pn} does not have both property files", "Bad News !", JOptionPane.ERROR_MESSAGE); 
                    } // end of else
                    
                } // end of if                
            } // end of if
        
            if ( --ct < 0 ) walking = false;

        } // end of while
        
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
        println "\n==============================/n... started\n"
        def path="jar:file:/Volumes/Media1/Software/menus/build/libs/menus-1.0.jar!/org/jnorthr/menus/support/PathFinder.class"
        PathFinder resourcePath;

        println "Search path with existing properties files:"+path
        resourcePath = new PathFinder(path)
        println resourcePath;
        if (resourcePath.exists())
        {
              println "... path to:"+path
               println "    has resource path at :"+resourcePath.getResourcePath();      
               println "    found resource path at :"+resourcePath.getResourcePathLocation();      
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
            println "Search for path using command line argument:"+path
            resourcePath = new PathFinder(path)
            println resourcePath;
            if (resourcePath.exists())
               {
                   println "... path to:"+path
                   println "    has resource path at :"+resourcePath.getResourcePath(); 
                   println "    found resource path at :"+resourcePath.getResourcePathLocation();             
               }
               else
               {
                   println "... path to:"+path
                   println "    exists ? "+resourcePath.exists();
               } // end of else

        } // end of if


        println "\n----------------------------------------\n"
        println "Search for path using default search sequence:"
        resourcePath = new PathFinder();   
        println resourcePath;
     
           if (resourcePath.exists())
           {
               println "... path to:"+resourcePath.location
               println "    has resource path at :"+resourcePath.getResourcePath(); 
               println "    found resource path at :"+resourcePath.getResourcePathLocation();                            
           }
           else
          {
             println "... path to:"+resourcePath.location
               println "    exists ? "+resourcePath.exists();
        } // end of else

        println "\n----------------------------------------\n"
        path = "./resources/properties/menu.properties"
        println "Search for path using relative local directory argument:"+path
        resourcePath = new PathFinder(path);
        println resourcePath;        
           if (resourcePath.exists())
           {
               println "... path to:"+resourcePath.menuproperties
               println "    has resource path at :"+resourcePath.getResourcePath();
               println "    found resource path at :"+resourcePath.getResourcePathLocation();                             
           }
           else
          {
             println "... path to:"+resourcePath.menuproperties
               println "    exists ? "+resourcePath.exists();
        } // end of else


        println "... ended\n===================================\n"
    } // end of main


} // end of class