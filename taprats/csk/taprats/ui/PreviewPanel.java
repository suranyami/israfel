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
 * PreviewPanel.java
 *
 * Shows a DesignPreview and provides a button to turn the preview into
 * a finished map, which can be drawn with different rendering styles.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;

import csk.taprats.app.*;
import csk.taprats.tile.*;
import csk.taprats.geometry.*;

import csk.taprats.toolkit.ClosePanel;
import csk.taprats.toolkit.WindowCloser;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;

public class PreviewPanel
	extends ClosePanel
{
	private Prototype			proto;
	private DesignPreview 		design;
	private Button				build;

	public PreviewPanel( Prototype proto )
	{
		super();

		this.proto = proto;

		this.design = new DesignPreview( proto );
		this.design.setSink( true );
		this.design.setSize( 400, 400 );
		this.build = new Button( "Build" );

		this.build.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) 
			{
				createFinalMap();
			}
		} );

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 4;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 6, 6, 6, 6 );

		layout.setConstraints( design, gbc );
		add( design );

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 6, 6, 6, 6 );

		layout.setConstraints( build, gbc );
		add( build );

		gbc.gridx = 1;

		layout.setConstraints( close, gbc );
		add( close );
	}

	/*
	 * Yay threads! 
	 *
	 * Encapsulate the function to build the map into a thread so that 
	 * building the map can run in the background while you're editing the
	 * design (or a completely different design).
	 *
	 * This class holds a reference to a Waitbox (see Waitbox.java) which
	 * acts like a ticker to show that progress is being made.  When 
	 * the building is finished, it kills the Waitbox.
	 */

	class map_constructor
		implements Runnable
	{
		Frame		msg;
		Waitbox 	wb;
		
		map_constructor( Frame msg, Waitbox wb )
		{
			this.msg = msg;
			this.wb = wb;
		}

		private void reportFailure( Throwable thr )
		{
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter( sw );
			thr.printStackTrace( pw );

			String trace = sw.toString();

			String apology = 
				"Sorry, but the attempt to build this pattern failed.\n" +
				"Of course, bug reports are appreciated.  If you want\n" +
				"to send me a bug report, send the information below to\n" +
				"me at csk@cs.washington.edu.  Thanks.";
			
			Frame f = new Frame( "Build failed" );
			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			f.setLayout( layout );

			TextArea ta = new TextArea( 
				apology, 4, 60, TextArea.SCROLLBARS_NONE );
			ta.setEditable( false );

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets( 6, 6, 6, 6 );

			layout.setConstraints( ta, gbc );
			f.add( ta );

			ta = new TextArea(
				trace, 4, 60, TextArea.SCROLLBARS_BOTH );
			ta.setEditable( false );
			ta.setBackground( Color.white );

			gbc.gridy = 1;

			layout.setConstraints( ta, gbc );
			f.add( ta );

			f.addWindowListener( 
				new csk.taprats.toolkit.WindowCloser( f, false ) );

			f.pack();
			f.show();
		}

		public void run()
		{
			// In this method, I resort to wrapping the whole dang thing
			// inside a big try {} block.  I just don't want the whole program
			// to go to pot if some map building code is broken.  So 
			// I catch everything and abandon the process of map building
			// if there's a problem.  Really, the exception handlers should
			// throw up apologetic message boxes.

			try {
				Polygon pgon = design.getBoundary();
				Map m = proto.construct( pgon );

				RenderPanel render = new RenderPanel( m );
				render.getView().setTheta( design.getTheta() );
				render.getView().setBounds( 
					design.getLeft(), design.getTop(), design.getViewWidth() );
				render.setSize( 500, 500 );

				Frame f = new Frame( "Rendered Design of " + 
					proto.getTiling().getName() );
				render.setParentFrame( f );
				f.addWindowListener( new WindowCloser( f, false ) );
				f.setLayout( new BorderLayout() );
				f.add( "Center", render );
				f.pack();
				f.show();
			} catch( Exception e ) {
				reportFailure( e );
				/*
				// FIXME -- turn these into message boxes.
				System.err.println( 
					"Caught an exception during map building; aborting." );
				System.err.println( e );
				e.printStackTrace();
				*/
			} catch( InternalError e ) {
				reportFailure( e );
				/*
				System.err.println( 
					"Caught an internal error during map building; aborting." );
				System.err.println( e );
				e.printStackTrace();
				*/
			}

			// Close the Waitbox.
			wb.terminate();
			msg.setVisible( false );
			msg.dispose();
		}
	}

	private void createFinalMap()
	{
		Waitbox wb = new Waitbox();

		Frame f = new Frame();
		f.setLayout( new BorderLayout() );
		f.add( "Center", wb );
		f.pack();
		f.show();

		// Make the thread run at a low priority.  If the user sits and
		// waits, this won't make it take any longer.  If the user interacts
		// with another design, it'll run slower in the background.  Tough
		// luck!

		Thread th = new Thread( new map_constructor( f, wb ) );
		th.setPriority( Thread.MIN_PRIORITY );
		th.start();
	}
}
