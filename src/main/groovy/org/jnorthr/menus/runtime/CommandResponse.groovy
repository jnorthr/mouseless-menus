package org.jnorthr.menus.runtime;

public class CommandResponse
{
    // text coming from the jtp jtextfield as either 1)blank 2) a menu number 3) 'go' to a menu 4) o/s command for the local o/s
    String commandlinetext = "";
    
    // set when command line text has been decoded
    boolean processed = false
    boolean failed = false
    int reason = 0;
    
    // assumed to be 'not a number' for first token
	boolean nan = true;
    
    // number of menu option chosen
    int menunumber = 0;
    // this command is copied out of the menu array list for the above menunumber, when > zero
    def menunumbercommandtext="";
        
        
    // --------------------------------------------
    // these values set after command text decoded
    boolean internalcommand = false;
    int bic = 0;

    // 'go' command menu; 'located' when the declared menu file has been found
    boolean located = false;    
    // the full canonical filename of this menu
    def menufilename=""
    
    // true if a local command o/s command has been actioned
    boolean executed = false
    
    // the o/s local exit code
    int exitValue = 0;
    
    // the o/s local error text - if any
    def errorStream="";
    // the o/s local output text - if any
    def outStream = "";    
    
    
    // take apart the command
	def words //     
    def word1	// the first token - trimmed
    def word1LC	// the first token - trimmed and made lower case
    
    
    // put all variable values back to original state
    public reset()
    {
        commandlinetext = "";    
        processed = false;
        failed = false
        reason = 0;  
        menunumber = 0;  
        menunumbercommandtext="";
        internalcommand = false;
        bic = 0;        
        located = false;
        menufilename="";
        executed = false;
        exitValue = 0;
        errorStream="";
        outStream = "";  
        words=null;
        word1="";
        word1LC = ""; 
    } // end of method

    // --------------------------------------------------------
    // class constructor - loads text from menu jtp field, 
    public CommandResponse()
    {
        println("\n... CommandResponse() ready..")
    } // end of constructor
    

    // --------------------------------------------------------
    // class constructor - 
    public CommandResponse(String clt)
    {
        println("\n... CommandResponse() ready..");
        this.commandlinetext = clt;
    } // end of constructor


    // --------------------------------------------------------
    // class constructor - 
    public load(String clt)
    {
        println("\n... CommandResponse() loading <${clt}>");
        this.commandlinetext = clt;
    } // end of constructor

    
    
    // standard override method
    String toString() 
    { 
        def ms = """commandlinetext=<${commandlinetext}> & processed=$processed failed=$failed reason=$reason nan=$nan menunumber=$menunumber menunumbercommandtext=<${menunumbercommandtext}> internalcommand=$internalcommand bic=$bic located=$located menufilename=<$menufilename> executed=$executed exitValue=$exitValue errorStream=<${errorStream}> outStream=<${outStream}>"""
    }    // end of method
    


    // --------------------------------------
    // test harness for this class
    public static void main(String[] args)
    {    
        println "... started"
        CommandResponse cr = new CommandResponse();
        println cr;    
        cr.load("echo 'hi kids'");
        println cr;    

        cr = new CommandResponse(" go main")
        println cr;    

        cr.reset();
        println cr;
        
        println "\n... ended"
    } // end of main

} // end of CommandProcessor.class