/*
 * taprats -- an interactive design tool for computer-generated 
 *            Islamic patterns.
 *
 * Copyright (C) 2000 Craig S. Kaplan, all rights reserved
 *
 * email: csk@cs.washington.edu
 * www:   http://www.cs.washington.edu/homes/csk/taprats/
 *
 * You may not copy, redistribute or reuse this source code at
 * this time.  It is likely that in the future I will make the
 * source more freely available.  In the meantime, please be
 * patient, and contact me if you have questions about the use
 * of this source code or the compiled applet.
 *
 */

/*
 * Util.java
 *
 * Contains the handy "openTestFrame" function that takes any component
 * and opens it up in its own window, optionally setting the window to
 * quit on closing.  Useful for writing quick main() tests in every UI class.
 */

package csk.taprats.toolkit;

public class Util
{
	public static final void openTestFrame( java.awt.Component c, boolean quit )
	{
		java.awt.Frame frame = new java.awt.Frame( "Test" );
		frame.setLayout( new java.awt.BorderLayout() );
		frame.add( "Center", c );

		frame.addWindowListener( new WindowCloser( frame, quit ) );

		frame.pack();
		frame.show();
	}

	public static final void openTestFrame( java.awt.Component c )
	{
		openTestFrame( c, true );
	}
}
