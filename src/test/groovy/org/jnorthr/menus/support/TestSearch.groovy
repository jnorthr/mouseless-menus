package org.jnorthr.menus.support;

import static org.hamcrest.CoreMatchers.*; // hamcrest let's me use 'assertThat'
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.*;
import java.awt.Color;
import org.jnorthr.menus.support.Search;
import java.util.logging.Logger;
import java.util.logging.*;

public class TestSearch {
	//private final static Logger LOGGER = Logger.getLogger("TestSearch");

	public TestBottomBorder() throws java.io.IOException
	{
		//Handler ch = new FileHandler("TestSearch.log");
		//LOGGER.getLogger("TestSearch").addHandler(ch); 
	}
	
	@Before 
	public void setUp() 
	{ 
		//LOGGER.info("É started TestSearch"); 
    	def path = "/Volumes/Media1/Backups/DuracellUSBKey2/Menus/data/";
		def tmp = new File(path+"/.menulist.txt")
		if (tmp.exists()) tmp.delete()
	} // end of setup

    @Test
    public void test1()
    {
    	def path = "/Volumes/Media1/Backups/DuracellUSBKey2/Menus/data/";
        Search fs = new Search(path);
        assertThat(fs, notNullValue() );

		//LOGGER.info("É test1 ok - Search not null"); 
    } // end of method

    @Test
    public void test2()
    {
    	def path = "/Volumes/Media1/Backups/DuracellUSBKey2/Menus/data/";
        Search fs = new Search(path);

		def tmp = new File(path+"/.menulist.txt")
		def flag2 = (tmp.exists()) ? true : false;
        assertThat(flag2, is(true) );

		//LOGGER.info("É test2 ok - Search created list of menus"); 
    } // end of method
 
} // end of class