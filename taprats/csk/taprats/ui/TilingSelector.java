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
 * TilingSelector.java
 *
 * The UI that provides an entry point to the entire system.  TilingSelector
 * it a UI that allows the user to navigate between all the tilings defined
 * in the system and choose one to work with.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.tile.*;
import csk.taprats.toolkit.WindowCloser;

public class TilingSelector
	extends Panel
{
	private Tiling[] 	tilings;
	private TilingCard	card;

	private int			current;

	private Button		open;
	private Button		prev;
	private Button		next;
	
	/*
	 * Start with an explicit set of tilings.
	 */
	public TilingSelector( Tiling[] tilings )
	{
		this.tilings = new Tiling[ tilings.length ];
		System.arraycopy( tilings, 0, this.tilings, 0, tilings.length );
		init();
	}

	/*
	 * Start with the builtin tilings.
	 */
	public TilingSelector()
	{
		this.tilings = new Tiling[ Tiling.numTilings() ];

		int index = 0;
		for( Enumeration e = Tiling.getTilings(); e.hasMoreElements(); ) {
			Tiling t = (Tiling)( e.nextElement() );
			this.tilings[ index ] = t;
			++index;
		}
		init();
	}
	
	private void init()
	{
		this.card = new TilingCard( tilings[ 0 ] );
		this.current = 0;

		this.open = new Button( "Open" );
		this.open.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) 
			{
				openCurrent();
			}
		} );

		/*
		 * In an ideal world, prev and next would be image buttons with
		 * icons of proper VCR controls.  But AWT doesn't support anything
		 * other than text buttons (this is silly -- see Gtk+ for the 
		 * proper way to design a button, as a container that can be pressed).
		 * I believe Swing does support buttons with both images and text.
		 * In the meantime, I'll be happy with the simple "<<" and ">>".
		 * The meaning should still be obvious.
		 */

		this.prev = new Button( "<<" );
		this.prev.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) 
			{
				setPrevious();
			}
		} );
		this.next = new Button( ">>" );
		this.next.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) 
			{
				setNext();
			}
		} );

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 3;

		layout.setConstraints( card, gbc );
		add( card );

		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.insets = new Insets( 3, 3, 3, 3 );

		gbc.gridx = 0;
		gbc.weightx = 1;
		layout.setConstraints( prev, gbc );
		add( prev );

		gbc.gridx = 1;
		gbc.weightx = 2;
		layout.setConstraints( open, gbc );
		add( open );

		gbc.gridx = 2;
		gbc.weightx = 1;
		layout.setConstraints( next, gbc );
		add( next );
	}

	public void setPrevious()
	{
		--current;
		if( current < 0 ) {
			current = tilings.length - 1;
		}
		card.setTiling( tilings[ current ] );
	}

	public void setNext()
	{
		++current;
		if( current >= tilings.length ) {
			current = 0;
		}
		card.setTiling( tilings[ current ] );
	}

	public void openCurrent()
	{
		DesignEditor editor = new DesignEditor( tilings[ current ] );
		Frame f = new Frame( "Design editor: " + tilings[ current ].getName() );
		editor.setParentFrame( f );
		f.setLayout( new BorderLayout() );
		f.add( "Center", editor );
		f.addWindowListener( new WindowCloser( f, false ) );
		f.pack();
		f.show();
	}

	public void addNotify()
	{
		super.addNotify();

		Font current = getFont();
		Font f = new Font( "SansSerif", Font.BOLD, current.getSize() + 2 );
		prev.setFont( f );
		next.setFont( f );
	}

	public static final void main( String[] args )
	{
		csk.taprats.toolkit.Util.openTestFrame( new TilingSelector() );
	}
}
