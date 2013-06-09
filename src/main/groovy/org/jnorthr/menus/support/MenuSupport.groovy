package org.jnorthr.menus.support;
import javax.swing.*
import java.awt.*
import javax.swing.text.*;
import java.awt.BorderLayout
import javax.swing.BorderFactory; 
import javax.swing.border.*
import javax.swing.border.LineBorder
import java.awt.GridLayout
import org.jnorthr.menus.support.PathFinder

public class Support
{
	def static audit = false
	def static framefixedtitle = "MENU"

	def ls = System.getProperty('line.separator')
	def fs = System.getProperty('file.separator')
	def us = System.getProperty('user.name')
	def os = System.getProperty('os.name')
	Properties props
	def pwd=""

	def static int wide
	def static int high
	def static int top = 0
	def static int left = 0
	def frame

	PathFinder pathFinder
	def menuMap
	def pathMap
	def OSN

	JTextPane jtp;			
	StyledDocument doc;
	SimpleAttributeSet as0
	SimpleAttributeSet as1 // yellow
	SimpleAttributeSet as2 // red
	SimpleAttributeSet as3 // green
	SimpleAttributeSet as4 // white
	SimpleAttributeSet attr = null
	//CommandSet comset = new CommandSet()	

	def env = [:]
	def stack = []
	int stackMax = 0
	def lookback = -1 // really just stack size

	def title1 = "0        1         2         3         4         5         6         7         8         9         0         1         2         3 2"
	def title2 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012"
	def title3 = "..."
	def title4 = "- - - - - - "

	def t1
	def t2
	def t3
	def t4

	def mono
	def commandPrefix = ""
	def builtincommand = ["go","stop"]
	def tokens			// used in resolveBuiltin
	int at    			// points to zero/none or which builtin command
	String co 			// temp. matcher field

	def hex = [
	0:'0',
	1:'1',
	2:'2',
	3:'3',
	4:'4',
	5:'5',
	6:'6',
	7:'7',
	8:'8',
	9:'9',
	10:'a',
	11:'b',
	12:'c',
	13:'d',
	14:'e',
	15:'f'
	] // end of hex 

	// test harness for this class
	public static void main(String[] args)
	{	
		println "... started"
		Support su = new Support()
		println "... ended"
	} // end of main



	// class constructor - loads configuration, get system environmental variables; gets hardware window size;
	public Support()
	{
        pathFinder = new PathFinder();	
		menuMap = pathFinder.menuMap;
		pathMap  = pathFinder.pathMap;

		// Get all system properties
  		props = System.getProperties()
		OSN = System.getProperty('os.name')
		pwd = System.getProperty('user.dir')

		commandPrefix = pathMap.commandPrefix		// something like 'open ' on mac os
		
       	env = System.getenv()
		getWindowSize()
		mono = new Font("Monospaced", Font.PLAIN, 10)
		say("... Support() ready..\nCommand prefix:<$commandPrefix>\nOSN=<$OSN>\npwd=<$pwd>")
	} // end of constructor

	// return a handle to the menuMap file
	public getMenuMap()
	{
		return menuMap
	}

	// return a handle to the name of the operating system name
	public getOSN()
	{
		return OSN
	}

	// find out what is the command prefix for this platform
	public getCommandPrefix() 
	{
		return commandPrefix
	} // end of 


	// update frame handle
	public setFrame(def fr) 
	{
		frame = fr
	} // end of 


	// find and replace environmental variables
	public findEnv(def txt)
	{
		int ix = txt.indexOf('$');
		if (ix < 0) return txt;
		int ix2 = txt.indexOf(fs); 	// find file separator after $
		if (ix2<0) ix2 = txt.size() 	// didnt find a file sep so use size of text as last ix
		if (ix2 < ix) return txt;
		String substr = txt.substring(ix+1, ix2);
		say substr
		def evar = getEnv(substr)
		substr = txt.substring(ix, ix2);
		say("environmental variable $substr=${evar}") // need logic to xlate $GROOVY_HOME into text plus file.separator
		def replacedText = replace(txt,substr,evar)
		say("${replacedText}")
		return replacedText

	} // end of findEnv


	// find environmental variable value from key
	public getEnv(def ky)
	{
		String va = env[ky]
		if (va==null) return ky
		return va
	} // end of getEnv


        // copied from http://www.exampledepot.com/egs/java.lang/ReplaceString.html?l=rel
        static String replace(String str, String pattern, String replace) 
        {
        	int s = 0;
        	int e = 0;
        	StringBuffer result = new StringBuffer();
    
        	while ((e = str.indexOf(pattern, s)) >= 0) 
        	{
        		result.append(str.substring(s, e));
        		result.append(replace);
        		s = e+pattern.length();
        	} // end of while
        	result.append(str.substring(s));
        	return result.toString();
        } // end of method


        // F9&UP arrow lookback thru commands
	public getStackEntry(boolean dir)
	{
		def thisStack = ""
		if (stackMax<1) return thisStack	;	// stack.size is zero if no entries in collection

		if (dir)					// scroll up (backwards)
		{	
			if (lookback<1) 
			{ 
				resetStack()
			} // end of if
			else
			{
				thisStack = stack[--lookback]
			} // end of else

		} // end of if
		else
		{
			if (!(lookback<stackMax)) lookback = -1; 
			thisStack = stack[++lookback]
		} // end of else

		return thisStack
	} // end of getStack

	// get current working directory
	public getPWD()
	{
		return pwd 
	} // end of get

	// set current working directory
	public setPWD(def newpwd)
	{
		pwd = newpwd
	} // end of set


	// set lookback pointer to last command in the stack
	public resetStack()
	{
		lookback=stackMax 
	} // end of reset

        // F9&UP arrow lookback thru commands
	public putStack(def entry)
	{
		stack << entry
		stackMax+=1					// stackMax counts how many entries in stack
		resetStack()

	} // end of getStack


	// =========================================================
	// print msg text
	public static void say(String text) 
	{
		if (audit) {println "$text";} 
	} // end of say

	public static void say(String text, boolean f) 
	{
		if (audit) {print "$text";} 
	} // end of say


	// general logic used to position window on this hardware
	public static getWindowSize()
	{
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		wide = dim.width
		high = dim.height
	} // end of getWindowSize

	// method to place window at TOPLEFT,BOTTOMLEFT,TOPRIGHT,BOTTOMRIGHT,or CENTER by default
	public moveWindow(String at)
	{
		Dimension f = frame.getSize();
		//println "moveWindow($at) : w=${f.width} h=${f.height}"
		switch(at)
		{
			case "TL" :
				left = 0
				top = 0
				break
			case "BL" :
				left = 0
				top = high - f.height
				break
			case "TR" :
				left = wide - f.width
				top = 0
				break
			case "BR" :
				left = wide - f.width
				top = high - f.height
				break
			default :
				left = (wide - f.width) / 2
				top = (high - f.height) / 2
		} // end of switch

		if (left<0) left=0;
		if (top<0) top =0;
		frame.setLocation(left,top)
	} // end of MoveWindow


	// look for built-in commands: go <menuname>
	public String resolveBuiltinCommand(String cmd)
	{
		tokens = cmd.tokenize()
		int ts = tokens.size()
		say "cmd '$cmd' has $ts tokens"
		at = 0
		if (ts<1) return cmd;

		// at least one token available (ts>0), so examine it for evidence of builtin command
		co = tokens[0].toLowerCase()			// co holds possible builtin command
		int i = 0;
		builtincommand.each		// match first token against each bic 
		{ bi -> i+=1; 
				if (bi.equals(co)) 	// found builtin command where at points to that command
				{
					at=i;		// which BIC ?  AT points to it
				} // end of if
		}  // end of each

		say("builtin command is $at with $ts parameters")
		// if it was a builtin command - which one ?
		switch (at)
		{
			case 1: 				// 'GO' builtin command plus one parameter
				if (ts<2 || ts > 2) 
				{
						def tx = "This command requires a single filename parameter"
						showDialog("GO Command Parameters",tx)
						//appendText("> ${cmd}", as3);
						appendText("${tx}", as2);
						cmd=null
				} // end of token count not equal 2: 'go' + <menu name>
				else
				{		// go fred	- load the menu 'fred' with it's options
						def fn = tokens[1].trim()	// get the menu filename
						if (!fn.toLowerCase().endsWith(".txt")) 
						{
							fn += ".txt";		// make sure it ends with .txt
							say "fn did not have a .txt extension, so now="+fn
						} // end of if
						
						int i8 = fn.indexOf("/") 
						int i9 = fn.indexOf("\\")
						if (i8 < 0 && i9 < 0)
						{
							say "fn <$fn> had no folder information, i8 ="+i8+" i9="+i9
							fn = pathFinder.getResourcePath()+"/resources/"+fn;   //getMenuMap().menuFolder + fn; 
							say "fn had no folder information, so now ="+fn
						} // end of if
						else
						{
							say "fn now has name="+fn
						}

						def f1 = new File(fn)
						cmd = (f1.exists()) ? "�${fn}" : null 

						say("builtin command resolves to <$cmd>")
						if (cmd==null)
						{
							def tx = "Menu file not found for $fn"
							showDialog("GO Command Parameters",tx)
							appendText("${tx}", as2);
						} // end of if
						else
						{
							appendText("return code 0", as1);
						} // end of else
				} // end of else
				break;

			default:
				break;
		} // end of switch

		return cmd
	} // end of resolveBuiltinCommand



	// describe dialog
	public void showDialog(String ti, String tx)
	{
		int messageType = JOptionPane.WARNING_MESSAGE; // no standard icon
		JOptionPane.showMessageDialog(null, "$tx", "${ti}", messageType);
	} // end of showDialog


	// xlate command text with http:// or www. prefix
	public resolveCommand(String co)
	{
		String cmd = co.trim()+"     ";
		int csize = cmd.size()

		if (csize < 2) return co;

		def tokens = cmd.split()

		switch ( tokens[0].trim().toLowerCase() )
		{
			case "open" :	return cmd;
					break;

			case "cd" :	setPWD(tokens[1])
					break;

			case "pwd" :
			case "command": return cmd;
					break;

			case "sh" :	return cmd;
					break;

			case "echo" :
			case "ls" :	return "command "+cmd.trim()
					break;

			case "set" :	return "sh -c '${cmd.trim()}' "
					break;

			default :
					break;
		}


		// if it's a groovy or groovyc request then add & to spawn as a sub-task
		if (cmd.substring(0,6).toLowerCase().equals("groovy")) return cmd +" & ";


		// look for internet naming conventions, if so call the apple 'open ' prefix to run a browser with this dns name
		if (cmd.substring(0,3).toLowerCase()=="www" ) 
		{
				cmd = getCommandPrefix() + "http://" + cmd
				//println " - no, changed to $cmd"
				return cmd
		} // end of if

		if (cmd.substring(0,4).toLowerCase()=="http" ) 
		{
				cmd = getCommandPrefix() + cmd
				//println " - no, changed to $cmd"
				return cmd
		} // end of if

		if (cmd.substring(0,5).toLowerCase()=="https" ) 
		{
				cmd = getCommandPrefix() + cmd
				//println " - no, changed to $cmd"
				return cmd
		} // end of if

		// call 'open ' to read any pdf files
		if (cmd.toLowerCase().endsWith("pdf") ) 
		{
				cmd = getCommandPrefix() + cmd
				//println " - no, changed to $cmd"
		} // end of if

		// could add more choices here to handle other senarios or file types like images, etc.

		return cmd
	} // end of resolveCommand


	// method to execute immediate command based on number of this menu option; 
	// not used since menu options come from main.txt file now
	// public void runCommand(int option) 
	// {
	//		runCommand(config.menus.commands[option])
	// } // end of runCommand using menu number

	def decode(b)
	{
	    def va = (b<0) ? b + 256 : b
	    int ud = (va<16) ? 0 : va / 16
	    int ld = va - (ud * 16)
	    def r = hex[ud]+hex[ld]
	    return r
	} // end of decode

	def fix(x)
	{
		def s = x.substring(5)
		return s
	} // end of fix





	// =================================================================================================================
	// method to execute immediate command
	// =================================================================================================================
	public void runCommand(def command) 
	{
		int j = jtp.getText().length() + 3; 
		def proc4

		def sbmjob = (command.endsWith("&")) ? true : false

		// do the business wrapped with try/catch block
		try
		{ 	
			def initialSize = 16384
			def outStream = new ByteArrayOutputStream(initialSize)
			def errStream = new ByteArrayOutputStream(initialSize)
			//def out = new StringBuilder()
			//def err = new StringBuilder()
			//Appendable outStream;
			//Appendable errStream;

			// since * is a shell expansion feature, you cannot pass 'ls menus*' as a command; try sh -c 'ls *.groovy' 
			proc4 = command.execute(null, new File(pwd))                     // Call *execute* on the string
			//proc4 = command.execute()


			// bailout if command ends with & which forces process to background
			if (sbmjob) 
			{   
				say " - not waiting for process to finish"
				return;
			} // end of if	


			proc4.consumeProcessOutput(outStream, errStream)
			//proc4.waitFor()

			// extra try-catch pair for process execution
			try {
				proc4.waitForProcessOutput(outStream, errStream)
				//proc4.waitForOrKill(5000)                     // Wait for the command to finish
			}	// end of try
			catch (Exception e)
			{
				say("timeout: ${e.getMessage()}");
			} // end of catch

 
			// Obtain status and output
			int ev = proc4.exitValue()
			def et = errStream 		// proc4.err.text.trim()
			def so = outStream 		// proc4.in.text.trim()
			attr = null

			// crude attempt to identify failed task requests, and if so, adjust text color display with text
			def pattern = ~"^.*(error).*"	// look for pattern evidence of error while running this command; crude and often misses it
			if (ev==0) attr = as0		// set the text color depending on exit value of command
			if (ev==1) attr = as2
			if (ev==2) attr = as2

			// audit trail print if audit set
			say("return code: ${ ev}")	// auditlog reporting
			say("stderr: ${et}")
			say("stdout: ${so}") 		// *out* from the external program is *in* for groovy


			def matcher = et =~ pattern;
			def matcher2 = so =~ pattern;

			// if command execution response was an error, then return the error text
			if (matcher.find() || matcher2.find() ) 
			{
			   attr = as2
			} // end of if 


			//call output
			WriteOutput(ev,et,so);

		}	// end of try

		catch (Exception e)
		{
			say("${ e.getMessage() }")
			jtp.setEditable(true)
			jtp.grabFocus();
			appendText("${e.getMessage()}", as2)
			displayStackTraceInformation(e,false)
			int j4 = jtp.getText().length(); 
			jtp.setCaretPosition(j4);
			jtp.setEditable(false)
		} // end of catch


		// This did not work as there was no wait for the process to complete, so need a blocking method
		// to hold here til it does; also need solution for commands like man which require several keypresses to cpmplete
		//def cmd = menuMap.menus.commands[option]
		//def result = cmd.execute()
		//println result.text

	} // end of runCommand


	// set up frame title
	public void WriteOutput(ev,et,so)
	{
		// set the text color depending on exit value of command
		switch(ev)
		{
			case 0 : attr = as0;
				break;

			case 1 : attr = as2
				break;

			case 2 : attr = as2
				break;

			default: attr = null;
		} // end of switch

		// write output to jtextpane
		// clearText() - could also use setText(null)
		jtp.setEditable(true)
		jtp.grabFocus();
		appendText("return code: ${ev}", as1);
		if (et.size()>0) appendText("${et}", attr);
		if (so.size()>0) appendText("${so}", attr);
		int j = jtp.getText().length();
		jtp.setCaretPosition(j);
		jtp.setEditable(false)
	} // end of writeOutput

	// set up frame title
	public String getFrameTitle()
	{
		return getMenuMap().menutitle.trim() + " " + framefixedtitle
	} // end of label 1

	// set up frame title
	public String getFrameTitle(String t)
	{
		return t.trim() + " " + framefixedtitle
	} // end of label 1





	// added this new method to support the new HeaderSupport class which should build a panel of	
	// 3 columns of system menu item names
	// construct a title panel with labels 
	public JPanel getHeaders()
	{	
		def p = new JPanel()
		p.setBackground(Color.red);
		p.setOpaque(true)
		p.add(new JLabel("Hi kids"))
		return p
	} // end of 



	// one-time setup of a text pane and styles with colors to be used as the joblog
	public JTextPane getTextPane()
	{
		jtp = new JTextPane();
		//jtp.setMaximumSize(new Dimension(1024, 768)) 
		//jtp.setPreferredSize(new Dimension(780, 510))
		jtp.setFont(new Font("Monospaced", Font.PLAIN, 12));
		jtp.setForeground(Color.green);
		jtp.setBackground(Color.black);
		jtp.setEditable(false);

		Border redline = new LineBorder(Color.cyan,1,true);
		jtp.setBorder(null)

		// zero return code
		as0 = new SimpleAttributeSet();
		StyleConstants.setForeground(as0, Color.white);
		//StyleConstants.setfBold(as0, true);
		//StyleConstants.setItalic(as0, true);

		// normal response text
		as1 = new SimpleAttributeSet();
		StyleConstants.setForeground(as1, Color.yellow);
		//StyleConstants.setBold(as1, true);
		
		// echo terminal command bad news in red
		as2 = new SimpleAttributeSet();
		StyleConstants.setForeground(as2, Color.red);

		// echo terminal command
		as3 = new SimpleAttributeSet();
		StyleConstants.setForeground(as3, Color.green);

		// echo terminal command
		as4 = new SimpleAttributeSet();
		StyleConstants.setForeground(as4, Color.orange);

		return jtp;
	} // end of getText


	// This is logic to populate the joblog panel =========================
        // Clear out current document
	private void clearText() 
	{
		jtp.setStyledDocument (doc = new DefaultStyledDocument());
	} // end of clearText

	// method to add text to pane with specific display attributes
	private void appendText(String s, AttributeSet attributes) 
	{
		String sb = '\n'+s
		doc = jtp.getDocument();
		try 
		{
			doc.insertString(doc.getLength(), sb, attributes);
		}
		catch (BadLocationException e) {}
	} // end of appendText

	private void appendText(String s, boolean f) 
	{
		doc = jtp.getDocument();
		try 
		{
			doc.insertString(doc.getLength(), s, null);
		}
		catch (BadLocationException e) {}
	} // end of appendText

	private void appendText(String s) 
	{
		doc = jtp.getDocument();
		try 
		{
			doc.insertString(doc.getLength(), s, null);
		}
		catch (BadLocationException e) {}
	} // end of appendText

	// figure out elapsed time of most recent command
	public static computeNano(def nano1)
	{
		def nano2 = System.nanoTime()
		def el = 0.000
		el = (nano2 - nano1) / 1000000000
		//def sf1 = String.format('nanoseconds:%<tN', el)
		def t = "elapsed=${el.toString()} sec.s"
		return t
	} // end of nano compute



	// ====================================================
	// method to print failure stack trace in a nice formt
	public boolean displayStackTraceInformation (Throwable ex,
                                                        boolean displayAll)
        {
        	if (null == ex)
        	{
        		appendText("Null stack trace reference! Bailing...", as0);
        		return false;
        	}
        	appendText("The stack according to printStackTrace():\n", as0);
        	//ex.printStackTrace();
        	//say ("");
        	StackTraceElement[] stackElements = ex.getStackTrace();
        	if (displayAll)
        	{
        		appendText("The " + stackElements.length +
                                " element" +
                                ((stackElements.length == 1) ? "": "s") +
                                " of the stack trace:\n", as0);
               }
               else
               {
               	       appendText("The top element of a " +
                                stackElements.length +
                                " element stack trace:\n", as0);
               }
        
               for (int lcv = 0; lcv < stackElements.length; lcv++)
               {
               	       appendText("Filename: " +
                                stackElements[lcv].getFileName(), as0);
                      appendText("Line number: " +
                                stackElements[lcv].getLineNumber(), as0);
                      String className = stackElements[lcv].getClassName();
                      String packageName = extractPackageName (className);
                      String simpleClassName = extractSimpleClassName (className);
                      say ("Package name: " +  ("".equals (packageName) ?  "[default package]" : packageName));
                      say ("Full class name: " + className);
                      say ("Simple class name: " + simpleClassName);
                      //say ("Unmunged class name: " +  unmungeSimpleClassName (simpleClassName));
                      say ("Direct class name: " +  extractDirectClassName (simpleClassName));
                      say ("Method name: " + stackElements[lcv].getMethodName());
                      say ("Native method?: " +   stackElements[lcv].isNativeMethod());
                      say ("toString(): " +  stackElements[lcv].toString());
                      say ("");
                      if (!displayAll)
                      	      return true;
               		}
               		say ("");
               		return true;
        }       // End of displayStackTraceInformation().

        // discover PackageName
        public static String extractPackageName (String fullClassName)
        {
        	if ((null == fullClassName) || ("".equals (fullClassName)))
        		return "";
        	int lastDot = fullClassName.lastIndexOf ('.');
        	if (0 >= lastDot)
        		return "";
        
        	return fullClassName.substring (0, lastDot);
        } // end of extractPackageName

        // discover ClassName
        public static String extractSimpleClassName (String fullClassName)
        {
        	if ((null == fullClassName) || ("".equals (fullClassName)))
        		return "";
        	int lastDot = fullClassName.lastIndexOf ('.');
        	if (0 > lastDot)
        		return fullClassName;
        
        	return fullClassName.substring (++lastDot);
        } // end of extractSimpleClassName

        // discover ClassName
        public static String extractDirectClassName (String simpleClassName)
        {
        	if ((null == simpleClassName) || ("".equals (simpleClassName)))
        		return "";
        	int lastSign = simpleClassName.lastIndexOf ('$');
        	if (0 > lastSign)
        		return simpleClassName;
        	return simpleClassName.substring (++lastSign);
        } // end of extractSimpleClassName

} // end of Support.class

// ================================================================================

/*
== spare logic bits here
 key listener logic here:
    KeyListener keyListener = new KeyListener() {
      public void keyPressed(KeyEvent keyEvent) {
        printIt("Pressed", keyEvent);
      }
      public void keyReleased(KeyEvent keyEvent) {
        printIt("Released", keyEvent);
      }
      public void keyTyped(KeyEvent keyEvent) {
        printIt("Typed", keyEvent);
      }
      private void printIt(String title, KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        String keyText = KeyEvent.getKeyText(keyCode);
        System.out.println(title + " : " + keyText);
      }
    };

	//borderLayout()
			//vbox(constraints: BorderLayout.CENTER)
			vbox()
			{	label "hi kids"
				//menuLines.times{ml -> label(id:'l${ml}',"${ml+1}. ${menuMap.menus.names[ml]}")}
				//vstrut(8)
			} // end of vbox
			panel   //(constraints: BorderLayout.SOUTH)
			{
				tf = textField(id:'choiceTextField', columns: 2, maximumSize: [40, 20],  actionPerformed: { event -> saver() })
				tf.setHorizontalAlignment(JTextField.RIGHT)
			} // end of panel

			'l${ml}'.setHorizontalAlignment(JLabel.LEFT)
			'l${ml}'.setFont(new Font("Courier New", Font.ITALIC, 12));
			'l${ml}'.setForeground(Color.GREEN))
			//def mono = new Font("Courier New", Font.ITALIC, 12)

*/
