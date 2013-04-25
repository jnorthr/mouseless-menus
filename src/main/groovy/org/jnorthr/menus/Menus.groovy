package org.jnorthr.menus;
import java.awt.GridBagConstraints.*
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*
import java.awt.*
import groovy.swing.SwingBuilder
import groovy.text.Template
import groovy.text.SimpleTemplateEngine
// need this for property retrieval
import java.io.*;
import java.util.*
import java.awt.BorderLayout as BL
import javax.swing.BorderFactory; 
import javax.swing.border.*
import javax.swing.border.LineBorder
import javax.swing.JFrame
import javax.swing.JTextField
import java.awt.GridLayout
import java.awt.Color
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.awt.event.KeyListener;
import javax.swing.text.*
import java.awt.Graphics
import java.awt.event.WindowEvent;

// local imports
import org.jnorthr.menus.help.HelpWindow;
import org.jnorthr.menus.support.BottomBorder;
import org.jnorthr.menus.support.MenuColumnSupport;
import org.jnorthr.menus.support.Support;
import org.jnorthr.menus.support.PanelSupport;
import org.jnorthr.menus.CommandSet;


/* to do:
the # character
1. change title to include date/time/user/system name/pwd - done
2. include icons to setup initial position to a cornor of the screen - properties does this
3. make jextfield have an underline rather than a box - done
4. allow function keys - done
5. to run builtin commands, write a temp. script that's callable - not required; use 'command' prefix on mac os - what about xp ? see processbuilder.groovy
6. multi-column menu choices - done
7. non-numeric option kills menu - done
8. change caret to block - tough
9. use scroll pane jtextarea to display results - done using styled JTextPane; also cut&copy works but need to tab back to command line
10. try using command echo $GROOVY_HOME  to decode environmental variables - needs more work if > 1 $values
11. make text in jtextpane courier and green text and blue border - done
12. Run modal commands from options line - done
13. SBMJOB - maybe & at end of command does same ?
14. call qcmd & go menuname - go is done
15. F9 recall previous command - almost: does not do all commands like http:// or GO command
16. Reposition textpane to start of most recent command - see setCaret in Support - still off by number of cr/lf.s ? Only shows at bottom of jtp
17. decode ls -al hexcodes on display
18. include F key usage in panel - done
19. report no such option - done
20. use return  code to color code the text - done
21. find index of error=2 - done
22. save as ./data/logfile.txt
23. as general purpose tool, use File>Open, File>New and File>Save to preserve text files, notes, logs 
24. cannot balance top/bottom panels when expanded to full screen mode - done: used gridbagconstraints or horizontal split screen
25. finish timer - needs more logic to refresh menu title lines - done : may need placement on screen - done 
26. use main.txt in place of properties to build menu structure and refresh via F5 key - done
27. cannot change titles and refresh - may work now that titles are actually in the swing tree ?
28. cd .. from command line fails to change pwd, look into building environmental process
29. some commands like man ifconfig fail to return as the utility is waiting for an additional 'enter' keypress
30. allow www or http:// to signal an html URL for setPage or use 'open' command on mac but looses focus on textfield cos window switch to safari
31. remove loadMenus() from this source & use from MenuColumnSupport.() - done
32. allow debug text to go to jtp
33. allow https: - done
34. try 'go' menu as external command, does it exist? no=if not dot add .txt=does it exist? 
    no=did it say ./data folder? no=try in ./data folder - done
35. make frame title come from main.txt file - done

36. command line fails on ls -al *.groovy   - why ? because *.java is a shell expansion feature---> to get a list of files in a dir, try:
	assert new File('D:/Groovy/Scripts').list().toList() ==['Script.bat', 'File1.txt', 'File2.txt', 'Directory1', 'Directory2']
<or>
list= []
new File('D:\\Groovy\\Scripts').eachFileMatch(~/File.*?\.txt/){ list<< it.name }
    //a regular expression, or any caseable expression
assert list == ['File1.txt', 'File2.txt']

37. picking an option higher than possible after an F5 menu reload does not set max properly - fixed
38. if additional command line options follow a menu number choice, allow those extra parms to flow thru
39. can menu help text translate the various parms such as $JAVA_HOME into the current values on the current system, not a static system ?
40. menu title not refreshed when changing menu text file - fixed
41. menuItems count wrong when changing menu text file - fixed
42. finish logic to allow titles in menus without assigning a menu number to them
43. provide menu context -sensitive help system, if a menu xxx.txt has xxx.html as help else if not present use main.html 
44. url in *.html page will not find correct images
45. do not load menu file unless it has at least one  := and default title name = menu file name if missing - done
46. allow {} as command text for several command's or groovy shell call like : {cd /home/jim; play run} which should run 2 commands: command cd /home/jim then command play run
47. cannot update the time in header panel - tough - must rebuild headersupport each time !
48. finish menuitem class and integrate it
49. function key to toggle show of command text - done
50. show available menus from ./data at boj - done
51. F12 when already on main does not need refresh

//	lookAndFeel("metal")		
If you want to get a resource file, such as an icon, use:
InputStream in = MyClass.class.getResourceAsStream("MyIcon.gif");

p=['sh','-c','ls -l | sort'].execute()
println p.text

if it's winblows, I'd give up hope, of doing any meaningful command piping. But it does work

groovy> p=['cmd','/c','dir|sort'].execute()
groovy> println p.text
groovy> go

The following workaround works fine :
 - Clear the text area with 'setText(null)'
 - reinsert content with 'insert' method from 0 position

*/

// class wrapper uses keystroke listener logic
class Menus implements KeyListener 
{

	def static audit = true
	//String propertyfile = './data/menu.properties'  		// non-OS specific parameters for business issues
	def support
	java.util.List<MenuColumnSupport> cs = []

	def ps = new PanelSupport()

	def static swing
	def static frame	
	def frametitle 
	JTextPane jtp;
	def helpfilename = "./resources/help.html"
	Border cyanline = new LineBorder(Color.red,1);
	def mono = new Font("Monospaced", Font.PLAIN, 10)

	def bb = new BottomBorder(Color.green)

	GridBagConstraints c = new GridBagConstraints();
	def p1 
	def footer1 = "F1=Help   F2=Allow   F3=Exit   F5=Refresh   F9=Recall   F10/F12=Cancel   F15=All Menus   F17=Cmds"
	def footer2 = " "
	def l1 
	def l2 
	def l3 
	def foc = false
	SimpleAttributeSet as3

	// e.isAltDown()	true if the ALT key was down when this event happened.
	// e.isControlDown()	true if the CTRL key was down when this event happened.
	// e.isShiftDown()	true if the SHIFT key was down when this event happened.
	// e.isMetaDown()	true if the META key was down when this event happened.
	// e.isAltGraphDown()	true if the ALT-GRAPH key was down when this event happened.
	public void keyReleased(KeyEvent ke){}
	public void keyTyped(KeyEvent e)   {}
	public void keyPressed(KeyEvent ke) 
	{
		boolean f = false

		if (ke.isShiftDown()) 
		{
			f = true
		} // end of if

		switch (ke.getKeyCode()) 
		{
			case KeyEvent.VK_F3:  // move x coordinate left
			if (f)
			{
				//println "F15 key pressed"
				String menu = "./data/.menulist.txt"; 
				MenuColumnSupport.loadMenu(cs,menu)    
				frame.setTitle(MenuColumnSupport.getFrameTitle())
				support.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
			} // end of shift

			else
			{
				//println "F3 key pressed"
				ender()
			} // end of 
			break;

			case KeyEvent.VK_ENTER:  
			if (f)
			{
				//println "Shift+ENTER key pressed"
				swing.tf.text=""
			}
			else
			{
				//println "ENTER key pressed"
			} // end of 
			break;

			case KeyEvent.VK_F2: // allow focus in joblog
			foc = (foc) ? false : true;
			jtp.setFocusable(foc)
			break;


			case KeyEvent.VK_F1: // ask for help
			helpme(); 
			break;

			// F5 --------------------------------------
			case KeyEvent.VK_F5: // reload menu commands

			String menu = MenuColumnSupport.getStorage().getCurrentMenu(); 

			// use F17 to toggle show/hide of menu items
			if (f) 
			{
				MenuColumnSupport.loadMenu(cs,menu,true)    
			}	// end of if
			else
			{
				MenuColumnSupport.loadMenu(cs,menu)    // menuitemsfilename)
			} // end of else

			frame.setTitle(MenuColumnSupport.getFrameTitle())
			support.resetStack()
			swing.tf.text=""
			swing.tf.requestFocusInWindow()
			swing.tf.grabFocus();
			break;


			// F9 --------------------------------------
			case KeyEvent.VK_F9: // recall prior command
			swing.tf.text = support.getStackEntry(true)
			break;

			// Up Arrow --------------------------------------
			case KeyEvent.VK_UP: // recall prior command - moving backward thru commands from most recent to oldest, then wrap after blank line
			swing.tf.text = support.getStackEntry(true)
			break;


			// Down Arrow --------------------------------------
			case KeyEvent.VK_DOWN: // recall next command
			swing.tf.text = support.getStackEntry(false)
			break;

			// Backstep thru previous menus using either F10 or F12 function keys or the escape key --------------------------------------
			case KeyEvent.VK_ESCAPE: 
			case KeyEvent.VK_F12: // mimic F12 for short keyboards
			case KeyEvent.VK_F10: // mimic F12 for short keyboards
			String priormenu = MenuColumnSupport.getStorage().getPriorMenu()
			String cm = MenuColumnSupport.getStorage().getCurrentMenu()
			if (!priormenu.equals(cm))
			{
				MenuColumnSupport.getStorage().pop()
				MenuColumnSupport.loadMenu(cs,priormenu)    // menuitemsfilename)
				frame.setTitle(MenuColumnSupport.getFrameTitle())
			} // end of if

			// reset pointer to command stack, then re-focus
			support.resetStack()
			swing.tf.text=""
			swing.tf.requestFocusInWindow()
			swing.tf.grabFocus();
			break;

		} // end of switch

    	} // end of keyPressed



    	// turn on auditlog listing
	public static void setAudit() {audit=true}
	
	public void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of say

	// not quite sure which method is used, so declared text as String
	public static void say(String text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	// ==========================
	// setup up gui actions
	// ==========================

	// help
	def helpme =  
	{
		def help = helpfilename
		String menu = MenuColumnSupport.getStorage().getCurrentMenu(); 
		int k = menu.lastIndexOf("/")
		int dot = menu.lastIndexOf(".")
		def menu2 = menu.substring(0,dot) + ".html"
		def name = menu.substring(k+1,k+2).toUpperCase()+menu.substring(k+2,dot).toLowerCase()
                def fi = new File(menu2);
                if (fi.exists()) help = menu2;
		say "helpme menu is <${menu2}> help file will be $help and name is $name"
		File hf = new File(help)
		URL url=null;
		url=hf.toURL();

		HelpWindow hw = new HelpWindow("$name Help Text", url);    
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();

	} // end of help


	// define saver closure
	def saver =
	{ event ->
		def nano1 = System.nanoTime()
		def cmd = swing.tf.text

 		// ignore empty requests
		if (cmd==null || cmd.size()<1 || cmd.trim().equals("") )
		{
			support.resetStack()
			swing.tf.text=""
			swing.tf.requestFocusInWindow()
			swing.tf.grabFocus();
			return;
		} // end of if

		// remove leading/trailing blanks
		cmd = swing.tf.text.trim()		


		int num = 0
		boolean f = true		
		
		def xlate = ""

			if (cmd.size() < 3) 
			{ 	
				try
				{ 
					num = cmd.toInteger()
				} 
				catch(NumberFormatException x) 
				{
					f=false;
				} // end of catch
			} // end of if

			else
			{
				def words = cmd.tokenize()
				try
				{ 
					num = words[0].toInteger()	
				} 
				catch(NumberFormatException x) 
				{
					f=false;
				} // end of catch

			} // end of else
			
			if (f && ( num< 1 || num > MenuColumnSupport.getMenuOptionCount() ) ) 
			{
				support.appendText("* No such choice: $num", support.as2);
				support.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				return;
			} // end of if

			// translate a menu number choice to the eqivalent text
			if ( num > 0 )
			{
				xlate = MenuColumnSupport.getMenuCommand(--num)
				cmd = ( cmd.size() > 2 ) ? xlate +  cmd.substring( cmd.indexOf(" ") ) : xlate
			} // end of if

 		// ignore empty requests from internal menu commands
		if (cmd==null || cmd.size()<1 || cmd.trim().equals("") )
		{
			support.appendText("* No command for choice: ${num+1}", support.as2);
			support.resetStack()
			swing.tf.text=""
			swing.tf.requestFocusInWindow()
			swing.tf.grabFocus();
			return;
		} // end of if


		// solve http requests, PDF requests
		cmd = support.resolveCommand(cmd)

		// Echo the String
		support.appendText("\n> ${cmd}", as3)

		// crude attempt to translate environmental variables with $ prefix
		cmd = support.findEnv(cmd)	
		//say("env returned $cmd")

		// remember this command				
		support.putStack(cmd)	

		def tokens = cmd.tokenize()
		def c = tokens[0].trim().toLowerCase()
		switch ( c )
		{
			case "cd" : 
				support.setPWD( tokens[1].trim() ) 
				cmd=null;
				break;

			case "pwd" :
				support.WriteOutput(0, "", support.getPWD() );
				cmd=null;	
				break;

			default :
				cmd = support.resolveBuiltinCommand(cmd)		// convert any builtin commands 
		} // end of else

		if (!(cmd==null))					// a bad way to do this - refactor later
		{
			if (cmd.substring(0,1)=='¤')			// only known existing menu filenames come back here with ¤ prefix
			{
				def fn = cmd.substring(1)
				MenuColumnSupport.loadMenu( cs, fn)    // menuitemsfilename
				frame.setTitle(MenuColumnSupport.getFrameTitle())
			} // end of if
			else
			{
				support.runCommand(cmd)
			} // end of else
		} // end of else

		// repaint header
		//support.
		swing.hd.validate()

		// compute timing, clear text field and refocus				
		swing.f2.text = Support.computeNano(nano1)
		swing.tf.text=""
		// does not reclaim focus if URL starts firefox/safari,etc.
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
		return
	} // end of saver


	// setup up gui action to end job
	def ender =
	{ event ->
		System.exit(0)
	}

	// ====================
	// build a swing panel
	public void getPanel(String fn)
	{
		MenuColumnSupport.loadMenu( cs, fn)

   		say("There are ${MenuColumnSupport.getMenuItemCount()} menu items")
		support.appendText("O/S=${support.getOSN()} and ${MenuColumnSupport.getStorage().getCurrentMenu()}");
		support.appendText(" has ${MenuColumnSupport.getMenuItemCount()} menu choices");

		c = new GridBagConstraints();	
		c.insets = new Insets(0, 1, 0, 1);
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.gridx = 0; // column number where zero is first column
		c.gridy = 0; // row number where zero is first row
		c.anchor = GridBagConstraints.WEST; // direction of drift 4 this component when smaller than window 
		c.fill = GridBagConstraints.NONE; // rule to let a component expand both ways when more space is available on resize

 
		JFrame.setDefaultLookAndFeelDecorated(true);
		swing = new SwingBuilder()
		frame = swing.frame(title:"${frametitle}", background:Color.black, pack:false, show:true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE,size:[800, 500]) 
		{   
		   panel(layout:new GridBagLayout(), background:Color.BLACK)
		   {
			c.fill = GridBagConstraints.BOTH; // HORIZONTAL
			c.weighty = 0.0;
			c.weightx = 0.0;

			c.gridy = 0; 
			c.gridx = 0;

			// layout headings
			hbox(constraints: c, id:'hd' ) { widget(ps.getPanel()) }
			c.gridy += 1;

			c.weighty = 0.4;
			c.gridx  = 0

			// layout menu item columns
			hbox(constraints: c)
 		        {			
 		          sp1 = scrollPane(id:'sp1',border:null,minimumSize:[160,110],preferredSize:[180,140]) {widget(cs[0].getColumn()) }   
			   label "    "
 		          sp2 = scrollPane(id:'sp2',border:null,minimumSize:[160,110],preferredSize:[180,140]) {widget(cs[1].getColumn()) }
			   label "    "
 		          sp3 = scrollPane(id:'sp3',border:null,minimumSize:[160,110],preferredSize:[180,140]) {widget(cs[2].getColumn()) }
		        } 					// end of hbox


		        c.fill = GridBagConstraints.NONE; 	// rule to let a component expand both ways when more space is available on resize
			c.gridwidth = GridBagConstraints.RELATIVE
			c.gridy += 1;  				// row three and first column
			c.gridx = 0;  				// row four and first column
			c.weighty = 0.0;
			c.weightx = 0.0;

			// command entry field
			hbox(constraints: c)
			{
				def t3 = label(id:'t3','Enter menu # or command : ', font:mono, foreground:Color.GREEN)  

				tf = textField(id:'tf', foreground:Color.GREEN, columns: 100, border:bb,  font:mono, actionPerformed: { event -> saver()}, minimumSize:[550, 12], opaque:true, background:Color.BLACK)
				tf.addKeyListener(this);
				tf.setCaretColor(Color.YELLOW)
				tf.getCaret().setBlinkRate(400);
				t3.setHorizontalTextPosition(JLabel.LEFT);
			} // end of hbox


			c.gridy += 1;  				// row four and first column
			c.gridx = 0;  				// first column
			c.weighty = 0.0;
			c.gridwidth = GridBagConstraints.REMAINDER; // makes this component span all columns if last in row

			hbox(constraints: c)
			{
				label(id:'f1',font:mono,foreground:Color.GREEN, text:"${footer1}",constraints: c)   
				label "    "
				label(id:'f2',font:mono,foreground:Color.YELLOW, text:"${footer2}",constraints: c)   
			} // end of hbox
		   
			c.gridx = 0;  				// first column
			c.weighty = 1.0;
			c.weightx = 1.0;
			c.gridy += 1;  				// row five and first column	
			c.fill = GridBagConstraints.BOTH;
			sp = scrollPane(id:'sp',border:cyanline,constraints: c) {widget(jtp) }

		   } // end of JPanel

		} // end of frame

		
		// store handle to frame, then position frame in center of display
		support.setFrame(frame)
		frame.setTitle(support.getFrameTitle())
		def loc = support.getConfig().location
		support.moveWindow(loc) 			// move this frame to center of display 
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
	} // end getPanel



	// ===============
	// constructor
	// ===============
	public Menus() 
	{ 
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		support = new Support()
   		frametitle = support.getFrameTitle()
/*
   		worker = new SwingWorker()    
   		{  public Object construct() 
   		   {
   		   	   return new expensiveDialogComponent();
   		   } // end of construct
   		};
   		worker.start();
*/

		as3 = new SimpleAttributeSet();
		StyleConstants.setForeground(as3, Color.green);

		3.times{ cs.leftShift ( new MenuColumnSupport() ) }

   		// build text pane for joblog text
		jtp = support.getTextPane()
		p1 = support.getHeaders() //support.getTitles()

		jtp.setFocusable(false)				// false will dis-allow copy/paste from joblog view but tab key not needed
	} // end of default constructor


	// test harness for this class
	public static void main(String[] args)
	{	
		//println "... started"
		setAudit()
		Menus ivs = new Menus()
		ivs.getPanel("./resources/main.txt")
		ivs.frame.show()
		//ivs.say("... done ===")

	} // end of main

};    // end of class 


/* ==============================================
* spare logic that works to show info dialog

// also see Main.java code example on background task work
// see http://java.sun.com/products/jfc/tsc/articles/threads/threads1.html  for code fragment
public class Worked extends SwingWorker
{
	SwingWorker worker = new SwingWorker()    
   		{  public Object construct() 
   		   {
   		   	   return new expensiveDialogComponent();
   		   } // end of construct
   		};
} // end of class

def todo = Menus.class.getResourceAsStream(helpfilename).text
int messageType = JOptionPane.INFORMATION_MESSAGE; // no standard icon
JOptionPane.showMessageDialog(null, "$todo", "Menu Help", messageType);
 */