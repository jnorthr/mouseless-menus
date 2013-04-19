package org.jnorthr.menus;

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
import org.jnorthr.menus.support.BottomBorder;
import java.util.logging.Logger;
import java.util.logging.*;

public class TestBottomBorder {
	private final static Logger LOGGER = Logger.getLogger("TestBottomBorder");

	public TestBottomBorder() throws java.io.IOException
	{
		Handler ch = new FileHandler("TestBottomBorder.log");
		LOGGER.getLogger("TestBottomBorder").addHandler(ch); 
	}
	
	@Before 
	public void setUp() 
	{ 
		LOGGER.info("... started TestBottomBorder"); 
	}

    @Test
    public void test1()
    {
		java.awt.Color c = new java.awt.Color(128);
        BottomBorder b = new BottomBorder(c);
        assertThat(b, notNullValue() );
        assertThat(b.isBorderOpaque(), is(false) );
		LOGGER.info("... test1 ok - not null and opaque=false"); 
    } // end of method

    @Test
    public void test2()
    {
		java.awt.Color c = new java.awt.Color(255);
        BottomBorder b = new BottomBorder(c, 2);
	    assertThat(b, notNullValue() );
	    assertThat(b.getThickness(), is(2) );
	    assertThat(b.getGap(), is(2) );
	    LOGGER.info("... test2 ok - not null, thickness=2, gap=2"); 
	} // end of method

    @Test
    public void test3()
    {
		java.awt.Color c = new java.awt.Color(128);
        BottomBorder b = new BottomBorder(c, 3, 7);
  	    assertThat(b, notNullValue() );
	    assertThat(b.getThickness(), is(3) );
	    assertThat(b.getGap(), is(7) );
	    LOGGER.info("... test3 ok - not null, thickness=3, gap=7"); 
	} // end of method

    @Test
    public void test4()
    {
		java.awt.Color c = new java.awt.Color(255);
        BottomBorder b = new BottomBorder(c, 4, 9);
		String d = b.getLineColor().toString();
  	    assertEquals(d,  "java.awt.Color[r=0,g=0,b=255]");
	    LOGGER.info("... test4 ok - color:java.awt.Color[r=0,g=0,b=255]"); 
	} // end of method

 
} // end of class