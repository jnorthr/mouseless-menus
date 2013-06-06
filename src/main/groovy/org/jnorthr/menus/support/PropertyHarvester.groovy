package org.jnorthr.menus.support;
import org.jnorthr.menus.support.PathFinder

public class PropertyHarvester
{
	def audit = false;
	
	// just the properties needed for Menus
    def map =[:]

	// keeps PathFinder.menuMap
	def config; 
	
	// load System properties
	Properties props;

    public PropertyHarvester()
    {
	    // keeps PathFinder.menuMap
    	PathFinder resourcePath = new PathFinder(); 
    	config = resourcePath.menuMap;
    	props = System.getProperties()
    	config.propertykeys.each 
    	{ k ->  
        	    def tx = (props.get(k)) ?  (String)props.get(k) : "unknown"  
            	map.put(k,tx)
    	} // end of each
    
	    setDate();
                
	    map.put("application", config.application)
    	map.put("version",config.version)
    
	    // load o/s specific command prefix
    	// "command set" -> iMac PPC version <or> "cmd /c " -> RedApple
    	getSpecificOS(config.cmd)
    	getNetwork("ifconfig")
    } // end of constructor
    
    // see map
	public void show()
	{
    	map.each{mk,mv ->
        	println "map key:"+mk+"="+mv
    	} // end of each

		println "\nPathFinder map follows :"
    	config.each{ck,cv -> println "PathFinder key=$ck and value=$cv"}

		println "\n\nSystem properties follow:"
		props.each{k,v -> println "k=$k and v=$v"}

	} // end of method


	// audit trail reporting
	public void say(msg)
	{
		if (audit) println msg;
	} // end of method


	// find local matching values for our menu.properties requirements   
	public void getSpecificOS(cmd)
	{
		def word1
		def txt = cmd.execute().text
        txt.eachLine
        {
          line -> 
            if (line.trim().size() > 0)
            {
                def words = line.split(/=/)
                say "<"+line+"> has "+words.size()+" words";
                if (words.size()>1)
                {
                        word1=words[0].toLowerCase()
                        if (config.systemkeys.contains(word1)) {map.put(word1,words[1])}   
                } // end of if
            } // end of if

        } // end of eachLine
	}   // end of method


    // fill map with date and time
    def setDate()
    {
        def words = new Date().toString().split(' ')
        def dat = words[0]+' '+words[2]+' '+words[1]+' '+words[5]        // like  Mon 21 Dec 2009
        def tim = words[3]+' '+words[4]                            // like 16:37:40 CET
        map.put("date",dat)
        map.put("time",tim)
    } // end of setDate
    
	public void getNetwork(cmd)
	{
        say "\nnow running $cmd"
        def txt = cmd.execute().text
        txt.eachLine
        { line -> 
        	if (line.trim().size() > 0)
        	{
            	def tokens = line.trim().split()
            	say tokens[0]+" "+tokens[1]
            	if (tokens[0].toLowerCase()=="inet") {map.put(tokens[0],tokens[1])}
            	if (tokens[0].toLowerCase()=="ipv4") {map.put("inet",tokens[tokens.size()-1])}
        	} // end of if
    	} // end of eachLine
	} // end of method   

    // get a map entry, if found, then return map.value else return 'not declared'
    def getMap(k)
    {
		say "CommandSet getmap($k)"
		if (k.equals("blank")) return "";
		if (k.equals("blank2")) return "";
		return (this.map[k]==null) ? "not declared" : this.map[k] 
    } // end of method

    // ============================
    // test harness for this class
    public static void main(String[] args)
    {    
        println "---> starting"
        PropertyHarvester ph = new PropertyHarvester()
        println "-------"
        ph.show()
        println "-------"
        ph.getMap("path")
        println "---> end"
    } // end of main
    
} // end of class