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
 * Ahhh, the Waitbox.  A masterpiece of UI trickery.
 *
 * When the user clicks the "build" button in the Preview window,
 * the system goes off and builds the map in a separate thread.  But
 * there's no way for the user to feel like the map is being assembled.
 * Normally, a progress bar is shown when a long operation is taking
 * place; the progress bar is modal and tells you to wait while stuff
 * happens.  Here, progress is in the background.  The user can continue
 * working.  How do we indicate progress?
 *
 * This class acts like a progress bar.  It doesn't indicate percentage
 * of completion, it's more like an animated hourglass cursor -- it just
 * ticks to give the user the feeling that something is happening 
 * when that something is invisible.  It's modeless -- the user can keep
 * working on other stuff while the Waitbox is open.  Several Waitboxes
 * can be open simultaneously.
 *
 * The ironic thing is that the Waitbox isn't tied to the map construction
 * at all!  It just ticks away on its own timer.  If map construction went
 * into an infinite loop or got stalled somehow, it would just keep ticking.
 *
 * It's kind of silly, but it makes a huge difference in the user experience.
 * Ideally, the Waitbox would have a status line showing what stage the
 * build process is at.  That's for another time.
 *
 * And of course, making the ticker be a little Islamic design is a nice
 * touch.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;

import csk.taprats.toolkit.*;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;

class waitbox_view
	extends GeoView
{
	int count;

	public waitbox_view()
	{
		super( -1.2, 1.2, 2.4 );
		setSink( true );
		this.count = 0;

		setBackground( new Color( 0.9f, 0.9f, 0.9f ) );
	}

	public void step()
	{
		++count;
		if( count == 9 ) {
			count = 0;
		}
		forceRedraw();
	}

	public void redraw( GeoGraphics gg )
	{
		Transform T = Transform.rotate( -Math.PI * 0.25 );
		Transform Tr = Transform.rotate( Math.PI * 0.5 );

		for( int idx = 0; idx < 8; ++idx ) {
			gg.pushAndCompose( Tr );
			if( idx < count ) {
				gg.setColor( new Color( 0.0f, 0.0f, 0.5f ) );
				gg.drawPolygon( pts, true );
				gg.setColor( Color.black );
			}
			gg.drawPolygon( pts, false );
			gg.pop();
			Tr.composeD( T );
		}
	}

	private static Point[] pts = {
		new Point( 0.36161567304292225, 0 ),
		new Point( 0.6173165676349103, -0.25570089459198786 ),
		new Point( 0.8940852215490653, -0.25570089459198797 ),
		new Point( 1.0, 0.0  ),
		new Point( 0.8940852215490653, 0.25570089459198797 ),
		new Point( 0.6173165676349103, 0.25570089459198786 )
	};
}

public class Waitbox
	extends Panel
	implements Runnable
{
	private Thread			thread;
	private boolean			done;
	private waitbox_view 	view;

	public Waitbox()
	{
		super();

		this.done = false;

		this.thread = new Thread( this, "waitbox thread" );
		this.view = new waitbox_view();
		this.view.setSize( 128, 128 );

		Label l1 = new Label( "Building your design.", Label.LEFT );
		Label l2 = new Label( "Please be patient...", Label.LEFT );

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 6, 6, 6, 6 );
		gbc.weightx = 10;
		gbc.weighty = 10;

		layout.setConstraints( this.view, gbc );
		add( this.view );

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1;
		gbc.weighty = 1;

		layout.setConstraints( l1, gbc );
		add( l1 );

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		layout.setConstraints( l2, gbc );
		add( l2 );

		thread.start();
	}

	public void run()
	{
		while( !done ) {
			try {
				thread.sleep( 500 );
			} catch( InterruptedException ie ) {}
			view.step();
		}
		// System.err.println( "done." );
	}

	public void terminate()
	{
		// thread.stop();
		// stopping the thread becomes deprecated in jdk 1.2.  They 
		// recommend setting a flag that the thread can read to know 
		// when to stop.

		done = true;
	}

	public static final void main( String[] args )
	{
		Waitbox wb = new Waitbox();
		csk.taprats.toolkit.Util.openTestFrame( wb, false );
	}
}
