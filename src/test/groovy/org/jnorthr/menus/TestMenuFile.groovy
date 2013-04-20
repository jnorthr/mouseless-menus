package org.jnorthr.menus;
import static org.hamcrest.CoreMatchers.*; // let's me use 'assertThat'
import org.jnorthr.menus.MenuFile;
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
public class TestMenuFile
{
	private ArrayList<MenuItem> ma; 
	private final static Logger LOGGER = Logger.getLogger("TestMenuFile");
	
	public TestMenuFile()
	{
		Handler ch = new FileHandler("TestMenuFile.log");
		LOGGER.getLogger("TestMenuFile").addHandler(ch); 
	}
	
	@Before 
	public void setUp() 
	{ 
		ma = new ArrayList<MenuFile>();
		LOGGER.info("... started a test of TestMenuFile"); 
	}
	
	// test harness for this class
	@Test
	public void test1()
	{	
		MenuFile mi = new MenuFile();
		assertThat(mi, notNullValue() );
		assertNotNull("MenuFile constructor failed",mi);
		LOGGER.info("... MenuFile test1 ok"); 
	}

	
	// test harness for MenuFile with string menu file name
	@Test
	public void test2()
	{	
		MenuFile mi = new MenuFile("jim.txt");
		assertThat(mi, notNullValue() );
		assertNotNull("MenuFile constructor failed",mi);
		LOGGER.info("... MenuFile test2 ok"); 
	}

	// test harness for MenuFile with string menu file name
	@Test
	public void test3()
	{	
		MenuFile mi = new MenuFile("jim.txt");
		assertThat(mi, notNullValue() );
		assertNotNull("MenuFile constructor failed",mi);
		assertThat(mi.menuFileExists, is(true) );
		LOGGER.info("... MenuFile test3 ok"); 
	}

	// test harness for MenuFile with string menu file name
	// confirm absolute canonical file name is as expected
	@Test
	public void test4()
	{	
		MenuFile mi = new MenuFile("jim.txt");
		assertThat(mi.menuFileName, is("/Volumes/Media1/Software/menus/jim.txt") );
		LOGGER.info("... MenuFile test4 ok"); 
	}

	// test harness for MenuFile with string menu file name
	// confirm absolute canonical file name is as expected
	@Test
	public void test5()
	{	
		MenuFile mi = new MenuFile("./jim.txt");
		assertThat(mi.menuFileName, is("/Volumes/Media1/Software/menus/jim.txt") );
		LOGGER.info("... MenuFile test5 ok"); 
	}


} // end of class