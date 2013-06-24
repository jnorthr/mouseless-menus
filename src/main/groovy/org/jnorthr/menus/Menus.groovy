package org.jnorthr.menus;
//import net.miginfocom.swing.MigLayout;
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
//import org.jnorthr.menus.support.ClassLoaderManager;
//import org.jnorthr.menus.CommandSet;

import org.jnorthr.menus.support.PathFinder;
import org.jnorthr.menus.support.Storage;
import org.jnorthr.menus.support.CommandStorage;


// class wrapper uses keystroke listener logic
class Menus implements KeyListener 
{
	def static audit = true
	Support support = new Support()

	CommandStorage cmds = new CommandStorage();
	java.util.List<MenuColumnSupport> cs = []

    def classpathEntries = [] // used in F19 to see jars in this loader
	PathFinder pathfinder;

	def ps = new PanelSupport()

	// keep stack of menu file names - how deep is menu layer that F12 key can use to retrace prior menu choices 
	Storage storage = new Storage()

	def static swing
	def static frame	
	def frametitle 
	JTextPane jtp;
	def helpfilename = "resources/documents/help.html"
	def todofilename = "resources/documents/todo.html"
	Border cyanline = new LineBorder(Color.red,1);
	def mono = new Font("Monospaced", Font.PLAIN, 10)

	def bb = new BottomBorder(Color.green)

	GridBagConstraints c = new GridBagConstraints();
	def p1 
	def footer1 = "F1=Help   F3=Exit                    F5=Refresh   F7=Selfedit   F8=Allow   F9=Recall   F10/F12=Cancel"
	def footer2 = "F13=ToDo  F15=All Menus   F16=Find   F17=Cmds     F19=Classpath"
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
		boolean f = (ke.isShiftDown()) ? true : false;
		switch (ke.getKeyCode()) 
		{
			case KeyEvent.VK_ENTER:  
				if (f)
				{
				 	say"Shift+ENTER key pressed"
					swing.tf.text=""
				}
				else
				{
					say "ENTER key pressed"
				} // end of
				 
				break;


			// ============================================		
			// Up Arrow --------------------------------------
			// recall prior command - moving backward thru commands from most recent to oldest, 
			// then wrap after blank line
			case KeyEvent.VK_UP: 
				swing.tf.text = cmds.getPriorCommand();   //support.getStackEntry(true)
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				break;


			// ============================================		
			// Down Arrow --------------------------------------
			// recall next command
			case KeyEvent.VK_DOWN: 
				swing.tf.text = cmds.getNextCommand();  //support.getStackEntry(false)
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				swing.tf.setCaretPosition(0);
				break;


			// ============================================		
			// ask for help	
			case KeyEvent.VK_F1: 
				if (!f)
				{
					helpme(); 
				} // end of shift

				else
				{
					say "F13 key pressed"
					todo();
				} // end of 
				break;


			// ============================================		
			// menu exit command
			case KeyEvent.VK_F3:  // move x coordinate left
			
				// if F15 key ?  F3+shift key - used to see all the menus 
				if (f)
				{
					say "F15 key pressed"

					String menu = ".menulist"; 
					def path = "resources"

					org.jnorthr.menus.support.Search sea = new org.jnorthr.menus.support.Search(path);
					def pr = sea.parseResults("*ALLMENUS");
					sea.writeResults(path, pr, "Available Menus", menu)				

					MenuColumnSupport.loadMenu(cs,menu)
					storage.leftShift(menu)    
					frame.setTitle(MenuColumnSupport.getFrameTitle())
					support.appendText("${pr.size} available menus", support.as4);
					cmds.resetStack()
					swing.tf.text=""
					swing.tf.requestFocusInWindow()
					swing.tf.grabFocus();
				} // end of shift

				else
				{
					say "F3 key pressed"
					ender()
				} // end of 
				break;


			// ============================================		
			// menu find command
			case KeyEvent.VK_F4:  // move x coordinate left
				// if F16 find key ? if no text, then it's a 'find all' search 
				if (f)
				{
					say "F16 key pressed"

					def hasText = (swing.tf.text.trim().size() > 0) ? true : false
					def searchText = (hasText) ? swing.tf.text.trim() : "*ALL" ;

					def path = "resources"
					org.jnorthr.menus.support.Search mf = new org.jnorthr.menus.support.Search(path);
					def re = mf.parseResults(searchText);
					re.each{say "--->"+it;}

					searchText = (hasText) ? "'"+swing.tf.text.trim()+"'" : "*ALL" ;
					mf.writeResults(path,re, """Your Search for $searchText""")				
					String menu = ".searchlist"; 
					MenuColumnSupport.loadMenu(cs,menu)
					storage.leftShift(menu)    
					frame.setTitle(MenuColumnSupport.getFrameTitle())
					def ms = (re.size > 0) ? re.size.toString() : "no" 
					support.appendText("found ${ms} menu items for ${searchText}", support.as4);
					cmds.resetStack()
					swing.tf.text=""
					swing.tf.requestFocusInWindow()
					swing.tf.grabFocus();
				} // end of shift

				else
				{
					say "F4 key pressed"
			    } // end of 
				break;
				

			// ============================================		
			// F5 - reload menu commands
			case KeyEvent.VK_F5:
				String menu = storage.getCurrentMenu(); 

				// use F17 to toggle show/hide of menu items
				if (f) 
				{
					MenuColumnSupport.loadMenu(cs,menu,true)    
				}	// end of if
				
				// F5 - just reload menu normally
				else
				{
					MenuColumnSupport.loadMenu(cs,menu) 
					//storage.leftShift(menu)
				} // end of else

				frame.setTitle(MenuColumnSupport.getFrameTitle())
				cmds.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				break;


			// ============================================		
			// ask to edit this current menu
			case KeyEvent.VK_F7:
				// use F19 to show classpaths from classloader
				if (f) 
				{  
					printClassPath this.class.classLoader;
					classpathEntries.each
					{ e -> 
						support.appendText(e.toString(), support.as0);
					} // end of each

					cmds.resetStack()
					swing.tf.text=""
					swing.tf.requestFocusInWindow()
					swing.tf.grabFocus();
				}	// end of if
				
				// F7 - edit this current menu
				else
				{
					selfedit(); 
				} // end of else			 
				break;

			// ============================================		
			// allow focus in joblog, the lower pane of the splitpane group	
			case KeyEvent.VK_F8: 
				foc = (foc) ? false : true;
				jtp.setFocusable(foc)
				break;


			// ============================================		
			// F9 --------------------------------------
			// recall prior command
			case KeyEvent.VK_F9: 
				swing.tf.text = cmds.getPriorCommand();  //support.getStackEntry(true)
				break;


			// ============================================		
			// Backstep thru previous menus using either F10 or F12 function keys or the escape key --------------------------------------
			case KeyEvent.VK_ESCAPE: 
			case KeyEvent.VK_F12: // F12 for long keyboards
			case KeyEvent.VK_F10: // mimic F12 for short keyboards

				// geet prior menu name
				String priormenu = storage.getPriorMenu()

				// get current menu name
				String cm = storage.getCurrentMenu()
				
				// if they are not the same, reload the previous menu file
				if (!priormenu.equals(cm))
				{
					storage.pop()
					MenuColumnSupport.loadMenu(cs,priormenu)    
					frame.setTitle(MenuColumnSupport.getFrameTitle())
				} // end of if

				// reset pointer to command stack, then re-focus
				cmds.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				break;

		} // end of switch

    } // end of keyPressed method


	// nice bit o code to show jars in this class loader
    public printEntries()
    {
        classpathEntries.each{e -> println e; }
    } // end of method
    
	// gather list of jars in this class loader
    public printClassPath(classLoader) 
    {
          classpathEntries += "classLoader.parent: $classLoader"
          classLoader.getURLs().each 
          { url->
             classpathEntries += "- ${url.toString()}"
          } // end of each
          if (classLoader.parent) 
          {
              printClassPath(classLoader.parent)
          } // end of if
          
    } // end of method





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

	// F1 help
	def helpme =  
	{	
		def help = support.getMenuMap().helpfilename 
		def menu2;
		String menu = storage.getCurrentMenu(); 
		int dot = menu.lastIndexOf(".")
		
		if (dot < 0)
		{
			menu += ".html";
		}
		else
		{
		 	menu = menu.substring(0,dot) + ".html"
		} // end of else

		dot = menu.lastIndexOf("/")
		menu2 = (dot < 0) ? menu : menu.substring(dot+1); 		
		say "helpme menu is <$help>  menu=<$menu>  menu2=<$menu2>"

		// check if context vesion of .html found
        if ( new File(menu2).exists() ) 
        {
        	help = menu2;
        	say "... context help $menu2 exists, so use it rather than $help"
        }
        
		//File hf = new File(help)
		//URL url=null;
		//url=hf.toURL();

		HelpWindow hw = new HelpWindow("Help Text", help);    
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();

	} // end of help


	// F7 selfedit
	def selfedit =  
	{
		String menu = storage.getCurrentMenu(); 
		say "  Menus.groovy has menu="+menu
		boolean located = pathfinder.locate(menu);
		if (located)
		{
			menu = pathfinder.getFullName();
			def dothis = pathfinder.editor+menu 
			say "  Menus.groovy will self-edit menu "+menu+" so we <${dothis}>"
			support.runCommand(dothis);
		} // end of if
		  
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
	} // end of selfedit


	// F13 todo list
	def todo =  
	{	
		def help = support.getMenuMap().todofilename 
		say "todo menu is $help"

		HelpWindow hw = new HelpWindow("To Do List", help);    
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
	} // end of todo



	// define saver closure
	def saver =
	{ event ->
		def nano1 = System.nanoTime()
		def cmd = swing.tf.text

 		// ignore empty requests
		if (cmd==null || cmd.size()<1 || cmd.trim().equals("") )
		{
			cmds.resetStack()
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
				cmds.resetStack()
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
			cmds.resetStack()
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
		say("env returned $cmd")

		// remember this command				
		cmds.putStack(cmd)	

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
				MenuColumnSupport.loadMenu( cs, fn)    
				storage.leftShift(fn)
				frame.setTitle(MenuColumnSupport.getFrameTitle())
			} // end of if
			else
			{
				support.runCommand(cmd)
			} // end of else
		} // end of else

		// repaint header
		//support.
		//swing.hd.validate()

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
		MenuColumnSupport.loadMenu( cs, fn )
		storage.leftShift(fn)
		
   		say("There are ${MenuColumnSupport.getMenuItemCount()} menu items")
		support.appendText("O/S=${support.getOSN()} and ${storage.getCurrentMenu()}");
		support.appendText(" has ${MenuColumnSupport.getMenuItemCount()} menu choices");

 
		JFrame.setDefaultLookAndFeelDecorated(true);
		swing = new SwingBuilder()
		frame = swing.frame(title:"${frametitle}", background:Color.black, pack:true, show:true, 
			defaultCloseOperation:JFrame.EXIT_ON_CLOSE, preferredSize:[800, 760]) 
		{   
		  splitPane(orientation:JSplitPane.VERTICAL_SPLIT)   //, dividerLocation:200, dividerSize:10) 
		  {		
		    panel(background:Color.PINK) 
		    {
				boxLayout(axis:BoxLayout.Y_AXIS)
				widget(ps.getPanel())  
	
		   		panel(background:Color.BLACK) 
		   		{
					boxLayout(axis:BoxLayout.X_AXIS)
					Box.createHorizontalGlue()
					widget(cs[0].getColumn())
					Box.createHorizontalGlue()
					widget(cs[1].getColumn()) 
					Box.createHorizontalGlue()
					widget(cs[2].getColumn()) 
					Box.createHorizontalGlue()
				} // end of panel
				
		   		panel(background:Color.BLACK) 
		   		{
				   		boxLayout(axis:BoxLayout.Y_AXIS)
				   		panel()
				   		{
					   		boxLayout(axis:BoxLayout.X_AXIS)
							Box.createVerticalGlue()
							def t3 = label(id:'t3','Enter menu # or command : ', font:mono, foreground:Color.GREEN)  
							t3.setHorizontalTextPosition(JLabel.LEFT);

							tf = textField(id:'tf', foreground:Color.GREEN, columns: 90, border:bb,  font:mono, actionPerformed: { event -> saver()}, minimumSize:[550, 12], opaque:true, background:Color.BLACK)
							tf.addKeyListener(this);
							tf.setCaretColor(Color.YELLOW)
							tf.getCaret().setBlinkRate(200);
						} // end of container	

						panel()
						{
							boxLayout(axis:BoxLayout.X_AXIS)
							//Box.createHorizontalGlue()
							def f1 = label(id:'f1',font:mono,foreground:Color.GREEN, text:"${footer1.trim().padRight(160)}")
							Box.createHorizontalGlue()
						}
						panel()
						{
					   		boxLayout(axis:BoxLayout.X_AXIS)
							Box.createHorizontalGlue()
							def f2 = label(id:'f2',font:mono,foreground:Color.GREEN, text:"${footer2.padRight(160)}")   
							//Box.createHorizontalGlue()
						}
							//Box.createVerticalGlue()
		   		} // end of JPanel

		   } // end of JPanel


			log = scrollPane(id:'log',border:cyanline,minimumSize:[600, 400],constraints: "left") {widget(jtp) } // ,constraints: "bottom"

		} // end of splitPane

		} // end of frame

		
		// store handle to frame, then position frame in display
		support.setFrame(frame)
		//frame.setTitle("System Menu");  //support.getFrameTitle())
		def loc = support.getMenuMap().location
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
        //println "... Menus started"
   		frametitle = "System Menu"; //support.getFrameTitle();
   		
   		pathfinder = new PathFinder();
   		
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
		//p1 = support.getHeaders() //support.getTitles()

		// false will dis-allow copy/paste from joblog view but tab key not needed
		jtp.setFocusable(false)				


	} // end of default constructor


	// test harness for this class
	public static void main(String[] args)
	{	
		//println "... started"
		setAudit()
		Menus ivs = new Menus()
		ivs.getPanel("resources/main.txt")
		//ivs.frame.show()
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