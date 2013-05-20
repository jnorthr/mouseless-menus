// class to build system title panel using one or more text panes of menu items to mimic two/three columns; generally shown in white
// see HeaderSupport.groovy to find out how menu items panel is built, as this appears in menus.groovy just below the panel generated by this classs
package org.jnorthr.menus.support;
import javax.swing.*
import java.awt.*
import groovy.swing.SwingBuilder

public class PanelSupport
{
	def panelsupportpanel
	def swing
	java.util.List<HeaderSupport> columns = []
	def static audit = false

	// default constructor to setup the JPanel using GridBagLayout
	public PanelSupport()
	{
		String propertyfile = 'resources/properties/menu.properties'  // non-OS specific parameters for business issues
	       	def config = new ConfigSlurper().parse(new File(propertyfile).toURL())	// get non-path related static values

		swing = new SwingBuilder()
		panelsupportpanel = swing.panel(background:Color.BLACK,layout:new GridLayout(rows:1,columns:3,hgap:14,vgap:1))		

		// now build 3 columns of menu item titles
		//def columns = []
		config.panelcolumns.times{columns <<  new HeaderSupport();}
		say "PanelSupport:"+config.itemtitles
		
		HeaderSupport.loadMenu(columns,config.itemtitles)

		// create a panel with these HeaderSupport jtextpane's - one per column; ps.getPanel() returns the JPanel with added components
		panelsupportpanel = loadPanel(columns)
		
	} // end of constructor


	// provide handle to constructed jpanel
	public getPanel()
	{
		return panelsupportpanel
	} // end of get


	// method to add one or more jtextpanes to this JPanel, number of columns held in menu.properties 
	public loadPanel(columns)
	{
		columns.each 
		{
			panelsupportpanel.add(it.getColumn())
		} // end of each

		return panelsupportpanel
	} // end of load


	// print debug text (maybe)
	public static void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of say

	// ===========================
	// test harness for this class
	public static void main(String[] args)
	{	
		say "... started"
		def ps = new PanelSupport()

		def frame = ps.swing.frame(title:"Header Example", pack:true, show:true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE, layout:new GridLayout(rows:2,columns:1,hgap:11,vgap:1)) 
		{
		   
		   container( ps.getPanel() )
		   panel(background:Color.RED)
		   {
			label "hi kids"
		   } // end of panel

		} // end of frame

		say "... ended"
	} // end of main

} // end of PanelSupport.class
