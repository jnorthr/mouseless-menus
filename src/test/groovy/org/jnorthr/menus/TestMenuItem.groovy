package org.jnorthr.menus;
import static org.hamcrest.CoreMatchers.*; // let's me use 'assertThat'
import org.jnorthr.menus.MenuItem;
// see: http://java-x.blogspot.fr/2007/01/unit-testing-with-junit-40.html
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.logging.Logger;
import java.util.logging.*;
// tried to get logging to work but ran out of time - needs more research
public class TestMenuItem
{
	private ArrayList<MenuItem> ma; 
	//private final static Logger LOGGER = Logger.getLogger("TestMenuItem");
	
	public TestMenuItem()
	{
		//Handler ch = new FileHandler("fred.log");
		//LOGGER.getLogger("TestMenuItem").addHandler(ch); 
	}
	
	@Before 
	public void setUp() 
	{ 
		ma = new ArrayList<MenuItem>();
		//LOGGER.info("É started a test"); 
	}
	
	// test harness for this class
	@Test
	public void test1()
	{	
		MenuItem mi = new MenuItem();
		//assertThat(mi, nullValue());
		assertNotNull("MenuItem constructor failed",mi);
		//LOGGER.info("É test1 ok"); 
	}

	@Test
	public void test2()
	{	
		MenuItem mi = new MenuItem();
		mi[0] = 7
		assert mi.menuKey==7
		//LOGGER.info("É test2 ok"); 
	}
	
	@Test
	public void test3()
	{	
		MenuItem mi = new MenuItem();
		mi[0] = 6
		ma << mi
		assertThat(ma.size(), is(1)); 
		assertThat(ma[0].menuKey, is(6))
		//LOGGER.info("É test3 ok"); 
	}


	@Test
	public void test4()
	{	
		MenuItem mi = new MenuItem();
		mi[0] = "fred"
		assert mi.menuCommand.equals("fred")	// fails on == 
		//LOGGER.info("É test4 ok"); 
	}


	@Test
	public void test5()
	{	
		MenuItem mi = new MenuItem();

		// try full 10 property constructor as:
		// key as int 1..99
		// BIC - builtin command index as int
		// column int - which column should this menu item appear in ? 1..5 (missing col.s ok)
		// line int - which line should this menu item appear in ? (missing lines ok)
		// color name or hex code as def/object
		// show/hide boolean: tru = show
		// menu text appearing on screen as String
		// command to execute if this menu item is triggered as String
		// title of ?? as String
		// name of file where this entry cam from as String
		mi = new MenuItem(2,0,2,2,'Red',true,"Fred","echo 'fred was here'","this is a title","filename")
		assert mi.menuCommand.equals("echo 'fred was here'")    
		//LOGGER.info( mi.toString() );

		ma << mi
		assertThat(ma.size(), is(1));

		//LOGGER.info( "ma size is ${ma.size()} and has these menu items:")
		ma.each{ m -> println m;}
		println "== the end =="
	} // end of main

} // end of class