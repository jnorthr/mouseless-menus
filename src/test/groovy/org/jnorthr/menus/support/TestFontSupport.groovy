package org.jnorthr.menus.support;

import static org.hamcrest.CoreMatchers.*; // let's me use 'assertThat'
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
import org.jnorthr.menus.support.FontSupport;
import java.util.logging.Logger;
import java.util.logging.*;

public class TestFontSupport {
	//private final static Logger LOGGER = Logger.getLogger("TestFontSupport");

	public TestBottomBorder() throws java.io.IOException
	{
		//Handler ch = new FileHandler("TestFontSupport.log");
		//LOGGER.getLogger("TestFontSupport").addHandler(ch); 
	}
	
	@Before 
	public void setUp() 
	{ 
		//LOGGER.info("É started TestFontSupport"); 
	}

    @Test
    public void test1()
    {
        FontSupport fs = new FontSupport("mac");
        assertThat(fs, notNullValue() );
		//LOGGER.info("É test1 ok - fs not null"); 
    } // end of method


    @Test
    public void test2()
    {
    	FontSupport fs = new FontSupport("mac");
        assertThat(fs, notNullValue() );
	    assertThat(fs.getPaths(), is("/Library/Fonts/") );
	    assertThat(fs.getFontPath(), is("/Library/Fonts/") );
	    assertThat(fs.getMonoFontFilename(), is("/Library/Fonts/Courier New.ttf") );
	    //LOGGER.info("É test2 ok - not null, mono font file name is ${fs.getMonoFontFilename()}"); 
	} // end of method
 
} // end of class