package org.jnorthr.menus.runtime;
import javax.swing.JOptionPane;
import org.jnorthr.menus.support.PathFinder;
import org.jnorthr.menus.runtime.CommandResponse;
import org.jnorthr.menus.runtime.CommandResolver;

public class CommandProcessor
{
	def audit = true
	PathFinder pathfinder = new PathFinder();
	CommandResponse cr = new CommandResponse();
	CommandResolver resolver = new CommandResolver(cr);    

	def commandPrefix = "open ";
	final int as0 = 0;
	final int as1 = 1;
	final int as2 = 2;
	

	// --------------------------------------------------------
	// class constructor - loads configuration, 
	// get system environmental variables; 
	// gets hardware window size;
	public CommandProcessor()
	{
		//commandPrefix = "open "; //pathMap.commandPrefix		// something like 'open ' on mac os
		say("\n... CommandProcessor() ready..")
	} // end of constructor


	// ----------------------------------------------------------
	// find out what is the command prefix for this platform: "sh -c" "cmd /c" "open " ??
	public getCommandPrefix() 
	{
		return commandPrefix
	} // end of 


	// --------------------------------------------
	// wrning dialog
	public void showDialog(String title, String tx)
	{
		int messageType = JOptionPane.WARNING_MESSAGE; // no standard icon
		JOptionPane.showMessageDialog(null, "$tx", "${title}", messageType);
	} // end of showDialog


	// =========================================================
	// print audit trail - maybe
	public void say(String text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	// -------------------------------------------
	// main entry point to execute user request 
	public process(String tx)
	{
		// load cmd into storage
		// see if storage has any content
		// if content, make a list of tokens
		// make first token trim and lowercase
		// see if first token is a number > 0
        def ready = decodeCommand(tx);
        if (ready)
        {
        	ready = hasMenuNumber(); 
        } // end of if
        
        // still ok to proceed ?
        if (ready)
        {
        	if (cr.nan)
        	{
				cr.bic = resolver.resolveCommand(); 
				say "... <${cr.word1LC}> = task=${cr.bic}"          		
        	
        		switch(cr.bic)
        		{
        			case 1:		resolveMenu();
        						break;
        		} // end of switch
        		
        	} // end of if
        	
        } // end of if

	    println cr;
        
	} // end of decodeCommand









	// -------------------------------------------
	// look for first token
	public decodeCommand(String tx)
	{
		cr.reset();
        cr.load(tx);
		decodeCommand();
	} // end of decodeCommand


	// -------------------------------------------
	// look for first token
	public decodeCommand()
	{
		cr.processed = true;
		if (cr.commandlinetext.trim().size() < 1 ) return false;
		
		cr.words = cr.commandlinetext.trim().tokenize()
		cr.word1 = cr.words[0].trim();
		cr.word1LC = cr.words[0].trim().toLowerCase();

		return true; 
	} // end of decodeCommand


	// see if first token is a real integer value > zero
	public hasMenuNumber()
	{
		// not a number
		cr.nan = true;
		try
		{	
			cr.menunumber = Integer.parseInt(cr.word1);
			cr.nan = ( cr.menunumber > 0 ) ? false : true; // if > 0, it is a number
		}
		catch(Exception e) { cr.nan = true; }	
		
		return cr.nan;
	} // end of hasMenuNumber





	// 
	def appendText(String tx,int code)
	{
		say tx;	
	} // end 
	
	
	// -------------------------------------------
	// look for built-in command like: go <menuname>
	public resolveMenu()
	{
		def tx2;
		
		// fail if not enough parms for a proper 'go' command
		if (cr.words.size() < 2 ) 
		{
			tx2 = "Menu file name not specified\n${cr.commandlinetext}"
			showDialog("GO Command Parameters",tx2)
			appendText("${tx2}", as2);
			cr.failed = true;
			cr.reason = 2;
			return false;		
		}
		
		// ok, so are assumed to be processing a 'go' command with token 2 as the menu file name
		// if it's good return true
	 	cr.located = pathfinder.locate( cr.words[1] )		
		if (cr.located)
		{
			cr.menufilename = pathfinder.getFullName();
			tx2 = "CommandProcessor found go request for\n${cr.menufilename}"
			showDialog("GO Command Parameters",tx2)
			return true;
		} // end of good filename		
		
		
		// report bad news	
		def tx = "Menu file not found for this request\n${cr.commandlinetext}"
		showDialog("GO Command Parameters",tx)
		appendText("${tx}", as2);
		cr.failed = true;
		cr.reason = 1;
		return false;
	} // end of resolveMenu


	// --------------------------------------
	// test harness for this class
	public static void main(String[] args)
	{	
		println "... started"
		CommandProcessor cp = new CommandProcessor()
		println "getCommandPrefix():"+cp.getCommandPrefix();
		cp.showDialog("Main Methods Test", "This is the text for a warning dialog")
		cp.say "if audit flag is on, then print this"
		cp.process("go fred")
		cp.process("go fred.txt")
		cp.process("go fred.pdf")
		cp.process("go ted")
		cp.process("go ./fred")
		cp.process("go resources/fred")
		cp.process("go ./resources/fred")
		cp.process("go ")
		cp.process(" ")
		cp.process("12 'hi kids' said the boy | ")
		cp.process("groovy -v ")

		cp.process(" 0")
		cp.process(" -12 ")
		cp.process(" - 1 2")
		cp.process(" 1")
		cp.process(" 234")
		cp.process(" +66 ")

		cp.process("www.ibm.com")
		cp.process("ftp://gosmer.net")
		cp.process(" https://marker.com/network")
		cp.process(" sftp://marker.com/network")
		cp.process(" echo 'hi kids' ")

		cp.process("fred.jpg ")
		cp.process("open mark.png")
		cp.process("open mark.pdf")
		cp.process("mark.doc")


		println "... ended"
	} // end of main

} // end of CommandProcessor.class
