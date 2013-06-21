package org.jnorthr.menus.support;
import javax.swing.JOptionPane;
import java.util.Properties;
/*
    Class to resolve the path to the ./resources folder when the
    menu system is run from a .jar, as this may have a different path to ./resources

	from some directory, use groovy to run directly like
	groovy /Volumes/Media1/Software/menus/src/main/groovy/org/jnorthr/menus/support/PathFinder.groovy
	
	The constructor will take a string pointing to a folder holding the menus, documents and properties for this app, or will fail
	with an option panel.
	
	Alternatively, run the default constructor with no parms/args to have this module find the menu folder named 'resources'
*/

public class PathFinder
{
	// print audit messages ?
    def audit = false


	// OS-specific parms; cannot be in same config file as config.rewrite looses some path info
	def os = System.getProperty('os.name')
    def osid = ""; // like 'Mac OS X' becomes 'macosx'

	// here are the two property files we need to run this app
    String menupropertiesname = "resources/properties/menu.properties";
    String pathpropertiesname = "resources/properties/path.properties";


    // does path exist ? - assume no
    def found = false
    
    // what is the path ?
	// if resolved, it's the absolute path name of the menus folder like: /Volumes/Media1/Software/menus but w/o /resources
    def resourcePath = "";
    
    // give full path name to menu props: the folder, resource and property folders plus the property file name
    def menuPropertiesPath = "";

    // give full path name to path props
    def pathPropertiesPath = "";
    

    // folder where this class currently sits - if getClass was called, but might not be if found earlier in search seq.
    String location = "undiscovered";

    // where is the path ? task eventually fills this string w/description of how the menu folder was located
    def resourcePathDiscovery = "not found";
	String task = "unknown";

    // give canonical file name of located file from locate() method
    def locatedFileCanonicalName= "";
	boolean located = false;

	// holds the loaded map from both property files
	def menuMap
	def pathMap

	String cmdprefix="";
	String editor="";
	
	// full hit of system properties
	Properties systemProperties
	Enumeration enums;
	
    // cook your own class toString
    public String toString()
    {
        String tx = "\nResource found ? ${(found)?"yes":"no";}\nPath to Resource : ${resourcePath}\nPath to menu properties : ${menuPropertiesPath}\nPath to path properties : ${pathPropertiesPath}\nresourcePathDiscovery : ${resourcePathDiscovery}\nClass location : ${location}  locatedFileCanonicalName : ${locatedFileCanonicalName}"
        return tx;    
    } // end of toString()


    // get the flag from menu.properties to decide if we print an audit trail   
    public getAudit()
    {
        return audit;
    } // end of method
   
    

    // does a resources path exist ?    
    public exists()
    {
        return found;
    } // end of method
    
    
    // get the resources path - folder name   
    public getResourcePath()
    {
        return resourcePath;
    } // end of method
    

    // get how resources path was found ?    
    public getResourcePathLocation()
    {
        return resourcePathDiscovery;
    } // end of method
    
    
    // show audit trail ?    
    public say(tx)
    {
        if (audit) println tx;
    }


    // get MENU_RESOURCES Environment Variable
    public getEnvironmentVariable()
    {
        task = "MENU_RESOURCES Environment Variable"
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
        task = "Current Working Directory (pwd)"    
        String pwd = System.getProperty("user.dir");
        say "getPWD() returns "+pwd
        return pwd;
    } // end of
        
        
    // get user's home directory
    public getHOME()
    {
        task = "User's Home Directory"    
        String home = System.getProperty("user.home");
        say "getHOME() returns "+home
        return home;
    } // end of


    // get directory of executing class, or failing that, the location of the class loader
    public getCLASS()
    {
        URL loc = this.class.getProtectionDomain().getCodeSource()?.getLocation();
        say "getCLASS() PathFinder loaded from :"+loc.toString();

        task = "Class Loader Path"    
        ClassLoader loader = this.class.getClassLoader();
        location = loader.getResource("org/jnorthr/menus/support/PathFinder.class").toString();
        say "getCLASS() ClassLoader location :"+location;
        
        if ( location.equals("null") ) 
        {
        	location=loc;
	        task = "Class Location"    
        } // end of if
        
        say "getCLASS() returns "+location;
        return location;
    } // end of

	// http://mdsaputra.wordpress.com/2011/11/18/java-system-getproperties/
	private void showAllSystemGetPropertiesParam()
	{
    	systemProperties = System.getProperties();
    	enums = systemProperties.propertyNames();
    	for (; enums.hasMoreElements();) 
		{
        	String key = (String) enums.nextElement();
        	String value = systemProperties.getProperty(key);
        	say(key + " = " + value);
    	} // end of for
	} // end of method

	// ===================================================================
	// now that we have an actual path to our menu property file, slurp it
	public loadProperties(String environment)
	{
		// get non-path related static values
        menuMap = new ConfigSlurper().parse(new File(menuPropertiesPath).toURL())	

		// is the audit flag set in the menu.properties ?	
		this.audit = menuMap["audit"]
		this.editor = menuMap["editor"]
		this.cmdprefix = menuMap["cmdprefix"];
		
		showAllSystemGetPropertiesParam();
		
		def p = new Properties(System.getProperties());
		p.each 
		{ pk, pv ->  
			say "pk="+pk+" pv="+pv
		} // end of each

		menuMap.each 
		{ k, v ->  
			say "k="+k+" v="+v
		} // end of each

		try
		{
			// get path related static values
        	pathMap = new ConfigSlurper(environment).parse(new File(pathPropertiesPath).toURL())	
			pathMap.each 
			{ k ->  
				say "k="+k
			} // end of each
					
			say "\n -- end of pathMap.each ---"
		}
		catch(Exception x) {println x.printStackTrace();}

    } // end of method


    // default class constructor - ( when no user folder was named )
    // searches for our properties in this sequence : 
    // 1.current working directory  
	// 2.user's home directory  
	// 3.MENU_RESOURCES env. variable  
	// 4.location of this class
    public PathFinder()
    {
    	say "PathFinder() default constructor"
		resetClass();

        discovery(getPWD());

		say "... after getPWD() exists:"+exists()
		
		
		// when menu folder was not found in the current working directory
        if (!exists())
        {
            discovery(getHOME());
        } // end of if 
		say "... after getHOME() exists:"+exists()
        
                
		// when menu folder was not found in the user's home directory
        if (!exists())
        {
            String path_to_resources = getEnvironmentVariable();
            if (path_to_resources!=null)
            {
                discovery(path_to_resources);
            } // end of if    
        } // end of if 
		say "... after MENU_RESOURCES exists:"+exists()


		// when menu folder was not found due to missing environment variable MENU_RESOURCES
        if (!exists())
        {
            discovery(getCLASS());
        } // end of if         
		say "... after getCLASS() exists:"+exists()
        

		// ------------------------------------------------------
		// bad news, no folder found, so die
        if (!exists())
        {
        	resourcePathDiscovery = "not found"
            JOptionPane.showMessageDialog(null, "Cannot find ${menupropertiesname} property files\nset MENU_RESOURCES environment var.", 
            "Bad News !", JOptionPane.ERROR_MESSAGE); 
            System.exit(1);
        } // end of if 
		else
		{
			loadProperties(osid);
		}

    } // end of constructor


    // class constructor - 
    // your choice, but if no properties found, say so
    public PathFinder(String path)
    {        
    	say "PathFinder(${path}) constructor"
		resetClass();
		
		task = "External path provided "
        discovery(path);

        if (!exists())
        {
            JOptionPane.showMessageDialog(null, "Cannot find ${menupropertiesname} property files\nset MENU_RESOURCES environment var.", 
            "Bad News !", JOptionPane.ERROR_MESSAGE); 
            System.exit(1);
        } // end of if 
		else
		{
			task = "External path was given"
			loadProperties(osid);
		}
    } // end of constructor


	// what was the full canonical filename of that file we just located ?
	public getFullName()
	{
		return locatedFileCanonicalName
	} // end of method

	// -------------------------------------------------------------------------------
    // locate method -
 	// try to find the absolute location of a file, either menu.txt, a document.html, loading.gif or xxx.properties file
    public locate(String file)
    {        
        say "... locate underway to find "+file
		boolean dot = ( file.lastIndexOf('.') > -1 ) ? true : false;
		if (!dot) 
		{
			file+=".txt";
			say "file had no suffix, so gave it a .txt and now it's :"+file
		} // end of if

		int k = file.lastIndexOf('.')
		def suffix = file.substring(k+1)
		def suffixLC = suffix.toLowerCase()

		say "... k=$k suffix=<$suffix> suffixLC=<${suffixLC}>"
		
		// decide which folder to use
		def prop = ( suffixLC.equals("properties") ) ? true : false ;
		def doc = ( suffixLC.equals("html") || suffixLC.equals("htm")  || suffixLC.equals("pdf") ) ? true : false ;
		def menu = ( suffixLC.equals("text") || suffixLC.equals("txt") ) ? true : false ;
		def img  = ( suffixLC.equals("png") || suffixLC.equals("gif")  || suffixLC.equals("jpg")  || suffixLC.equals("jpeg") ) ? true : false ;
		
		// this is something we cannot handle, so bail out
		if (!prop && !doc && !menu && !img) 
		{
			say "... cannot handle this name:"+file;
			return false;
		} // end of if
		
		def folder = resourcePath + "/resources"
		folder += (prop) ? "/properties" : "" ;
		folder += (doc) ? "/documents" : "" ;
		folder += (img) ? "/images" : "" ;
		folder += "/"+file;
		say "... went looking for this folder:"+folder
		def fi = new File(folder);
		located = fi.exists()
		say "    found ? "+located

		if (located) 
		{
     		locatedFileCanonicalName = fi.canonicalFile.toString()
			println locatedFileCanonicalName
		} // end of if
		return located
    } // end of method



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
        type += ( tokens[0].size() < 1 ) ? 4 : 0 ;  // root declaration like /Volumes/Users/jim has blank 1st token
    
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
                
                def fg = chkobj(pn+"/"+menupropertiesname)
                say pn+" exists ? "+fg;
                
                if (fg)
                {

                    if ( chkobj(pn+"/"+menupropertiesname) && chkobj(pn+"/"+pathpropertiesname) )
                    {
                        walking = false;
                        found = true;
                        resourcePath = pn;
                        menuPropertiesPath = pn+"/"+menupropertiesname;
                        pathPropertiesPath = pn+"/"+pathpropertiesname;
						resourcePathDiscovery = task;
						say "---> fg at "+task
                    }
                    else
                    {
    					resetClass();
                    	JOptionPane.showMessageDialog(null, "Path ${pn} does not have both property files", "Bad News !", JOptionPane.ERROR_MESSAGE); 
                    } // end of else
                    
                } // end of if                
            } // end of if
        
            if ( --ct < 0 ) walking = false;

        } // end of while
        
    } // end of method


	// logic to reset class
	private resetClass()
	{
        found = false;
        resourcePath = "";
        menuPropertiesPath = "";
        pathPropertiesPath = "";
		task="unknown";
		resourcePathDiscovery="not found";
		def tokens = os.split(' ').toList()
		tokens.each{e-> osid+=e;}
		osid = osid.toLowerCase()
		say "-----> os="+os+" giving osid="+osid
	}

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
		println "";
        if (resourcePath.exists())
        {
            println "... path to:"+path
            println "    has resource path at :"+resourcePath.getResourcePath();      
            println "    found resource path at :"+resourcePath.getResourcePathLocation();
            println "    find main :"+ resourcePath.locate("main") 
            if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName
    
           }
           else
           {
               println "... path to:"+path
               println "    exists ? "+resourcePath.exists();
               if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName
           } // end of else



         println "\n----------------------------------------\n"

        if (args.size() > 0)
        {
            path = args[0];        
            println "Search for path using command line argument:"+path
            resourcePath = new PathFinder(path)
            println resourcePath;
        	println ""
            if (resourcePath.exists())
               {
                   println "... path to:"+path
                   println "    has resource path at :"+resourcePath.getResourcePath(); 
                   println "    found resource path at :"+resourcePath.getResourcePathLocation();
            	   println "    find main :"+ resourcePath.locate("main")                    
                   if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName             
               }
               else
               {
                   println "... path to:"+path
                   println "    exists ? "+resourcePath.exists();
                   if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName
               } // end of else

        } // end of if


        println "\n----------------------------------------\n"
        println "Search for path using default search sequence:"
        resourcePath = new PathFinder();   
        println resourcePath
        println ""
           if (resourcePath.exists())
           {
               println "... path to:"+resourcePath.location
               println "    has resource path at :"+resourcePath.getResourcePath(); 
               println "    found resource path at :"+resourcePath.getResourcePathLocation();   
               println "    find main :"+ resourcePath.locate("main") 
               if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         
           }
           else
          {
               println "... path to:"+resourcePath.location
               println "    exists ? "+resourcePath.exists();
               println "    find main :"+ resourcePath.locate("main") 
               if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         
        } // end of else

		String name = "main";
		boolean found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

		name = "main.txt";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

		name = "main.html";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

		name = "FairChild.pdf";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName          


		name = "menu.properties";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         


        println "\n----------------------------------------\n"


        path = "./resources/properties/menu.properties"
        println "Search for path using relative local directory argument:"+path
        resourcePath = new PathFinder(path);
        println resourcePath
        println "";
        if (resourcePath.exists())
        {
            println "... path to:"+resourcePath.menupropertiesname
            println "    has resource path at :"+resourcePath.getResourcePath();
            println "    found resource path at :"+resourcePath.getResourcePathLocation();
            println "    find main :"+ resourcePath.locate("main")                              
            if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         
        }
        else
        {
            println "... path to:"+resourcePath.menupropertiesname
            println "    exists ? "+resourcePath.exists();
            println "    find main :"+ resourcePath.locate("main") 
            if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         
        } // end of else


        name = "loading3.gif"
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         


		name = "main";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

		name = "main.txt";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

		name = "main.html";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

		name = "path.properties";
		found = resourcePath.locate(name);
		println "    went looking for $name; did we find it ?"+found
		println "                                 full name ?"+resourcePath.getFullName()
		println "    find main :"+ resourcePath.locate("main") 
        if (resourcePath.located) println "located:"+resourcePath.locatedFileCanonicalName                         

        println "... ended\n===================================\n"
    } // end of main


} // end of class