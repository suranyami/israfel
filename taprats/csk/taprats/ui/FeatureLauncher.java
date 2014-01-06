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
 * FeatureLauncher.java
 *
 * A repository for a collection of FeatureButtons, kind of like a
 * radio group.  Manages the currently active button and enforces
 * mutual exclusion.  Exports a signal for telling other objects when
 * the active selection changes (DesignEditor uses this to change what's
 * being edited).
 *
 * This class also contains some code to automatically decide what the
 * initial figure should be for each feature in a tiling.  This is probably
 * a bad idea -- the tiling (or some outside client) should tell you what
 * it wants to have by default.  But since I'm controlling the possible
 * tilings for now, it's not a big deal, and I can always add the
 * functionality later.
 */

package csk.taprats.ui;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.awt.*;
import java.awt.event.*;

import csk.taprats.tile.*;
import csk.taprats.app.*;
import csk.taprats.general.*;

public class FeatureLauncher
	extends Panel
{
	private Tiling 			tiling;
	private FeatureButton[]	buttons;
	private int				current;

	public Signal 			selection_changed;

	public FeatureLauncher( Tiling tiling )
	{
		this.tiling = tiling;

		// The tiling keep track of PlacedFeatures, but (right now) it
		// doesn't know what _Feature_s those PlacedFeatures refer to.
		// So we have to uniquify the list of Features in the following
		// manner.  
		//
		// FIXME -- this sort of code should be moved to the Tiling class.
		// Even better, Tiling should maintain the list of Features all
		// the time.

		Hashtable fs = new Hashtable();
		int nf = tiling.numFeatures();
		for( int idx = 0; idx < nf; ++idx ) {
			Feature f = tiling.getFeature( idx ).getFeature();
			fs.put( f, f );
		}

		this.buttons = new FeatureButton[ fs.size() ];
		Enumeration e = fs.keys();
		for( int idx = 0; idx < fs.size(); ++idx ) {
			Feature f = (Feature)( e.nextElement() );
			buttons[ idx ] = new FeatureButton( new DesignElement( f ) );
			buttons[ idx ].setSize( 130, 130 );
			buttons[ idx ].action.addObserver( new FBObserver( idx ) );
		}

		this.current = 0;
		buttons[ 0 ].setSelected( true );

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets( 6, 3, 6, 3 );
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTH;

		for( int idx = 0; idx < buttons.length; ++idx ) {
			layout.setConstraints( buttons[ idx ], gbc );
			add( buttons[ idx ] );
			++gbc.gridy;
		}

		this.selection_changed = new Signal();
	}

	public int numFeatureButtons()
	{
		return buttons.length;
	}

	public FeatureButton getFeatureButton( int n )
	{
		return buttons[ n ];
	}

	public FeatureButton getCurrent()
	{
		if( current < 0 ) {
			return null;
		} else {
			return buttons[ current ];
		}
	}

	public void setCurrent( int id )
	{
		if( id != current ) {
			if( current != -1 ) {
				buttons[ current ].setSelected( false );
			}
			current = id;
			if( current != -1 ) {
				buttons[ id ].setSelected( true );
			}
			selection_changed.notify( new Integer( current ) );
		}
	}

	class FBObserver
		implements Observer
	{
		int id;

		FBObserver( int id )
		{
			this.id = id;
		}

		public void update( Observable obs, Object arg )
		{
			setCurrent( id );
		}
	}

	public static final void main( String[] args ) 
	{
		Tiling t = Tiling.find( args[0] );
		FeatureLauncher fl = new FeatureLauncher( t );
		csk.taprats.toolkit.Util.openTestFrame( fl );
	}
}
