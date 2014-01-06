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
 * ClosePanel.java
 *
 * A simple extension of the Panel class that includes a close button.
 * The button waits for a client to set the parent frame so that when
 * it's clicked, it knows which frame to dispose of.  Subclasses can
 * then add the close button in their layouts at initialization time.
 */

package csk.taprats.toolkit;

import java.awt.*;
import java.awt.event.*;

public class ClosePanel
	extends Panel
{
	protected Button 	close;
	private Frame		parent;

	public ClosePanel()
	{
		super();
		this.close = new Button( "Close" );
		this.parent = null;

		this.close.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae )
			{
				if( parent != null ) {
					parent.setVisible( false );
					parent.dispose();
				}
			}
		} );
	}

	public void setParentFrame( Frame frame )
	{
		this.parent = frame;
	}
}
