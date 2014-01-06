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
 * WindowCloser.java
 *
 * A WindowCloser is a simple event handler that can be reused in many
 * contexts.  Its job is to hook into the "window closing" events and
 * make sure the window goes away.  It also has an extra flag to optionally
 * exit the program when this happens.  
 */

package csk.taprats.toolkit;

public class WindowCloser
	extends java.awt.event.WindowAdapter
	implements java.awt.event.ActionListener
{
	private java.awt.Window window;
	private boolean quit;

	public WindowCloser( java.awt.Window window, boolean quit )
	{
		this.window = window;
		this.quit = quit;
	}

	public WindowCloser( java.awt.Window window )
	{
		this( window, false );
	}

	public void windowClosing( java.awt.event.WindowEvent we )
	{
		window.setVisible( false );
		window.dispose();

		if( quit ) {
			System.exit( 0 );
		}
	}

	public void actionPerformed( java.awt.event.ActionEvent ae )
	{
		window.setVisible( false );
		window.dispose();

		if( quit ) {
			System.exit( 0 );
		}
	}
}
