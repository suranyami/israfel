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
 * TilingViewer.java
 *
 * TilingViewer gets a Tiling instance and displays the tiling in a 
 * view window.  It always draws as much of the tiling as necessary to
 * fill the whole view area, using the modified polygon-filling algorithm
 * in csk.taprats.geometry.FillRegion.
 *
 * TilingViewer also has a test application that accepts the name of
 * a built-in tiling on the command line or the specification of a tiling
 * on System.in and displays that tiling.  Access it using
 * 		java csk.taprats.tile.TilingViewer
 */

package csk.taprats.tile;

import java.util.Vector;
import java.io.*;

import java.awt.Frame;
import java.awt.BorderLayout;

import csk.taprats.toolkit.*;
import csk.taprats.geometry.*;

public class TilingViewer
	extends GeoView
{
	Tiling 			tiling;
	FillRegion		fill;
	DrawIt			callback;

	public TilingViewer( Tiling tiling )
	{
		super( -4.9, 4.9, 9.8 );
		this.tiling = tiling;

		this.fill = new FillRegion();
		this.callback = new DrawIt();
	}

	public void setTiling( Tiling tiling )
	{
		this.tiling = tiling;
		forceRedraw();
	}

	// The FillRegion algorithm relies on a callback to send information
	// about which translational units to draw.  Here, we get T1 and T2,
	// build a transform and draw the PlacedFeatures in the translational
	// unit at that location.
	class DrawIt
		implements UnitCallback
	{
		GeoGraphics gg;

		DrawIt( GeoGraphics gg )
		{
			this.gg = gg;
		}

		DrawIt()
		{
			this( null );
		}

		void setGraphics( GeoGraphics gg )
		{
			this.gg = gg;
		}

		public void receive( int a1, int a2 )
		{
			Point t1 = tiling.getTrans1();
			Point t2 = tiling.getTrans2();

			Transform T = Transform.translate( 
				t1.scale( (double)a1 ).add( t2.scale( (double)a2 ) ) );

			gg.pushAndCompose( T );

			for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
				PlacedFeature pf = tiling.getFeature( idx );
				FeatureView.drawFeature( gg, pf );
			}

			gg.pop();
		}
	}

	public void redraw( GeoGraphics g2d )
	{
		Point t1 = tiling.getTrans1();
		Point t2 = tiling.getTrans2();

		callback.setGraphics( g2d );
		Polygon bds = getBoundary();
		Polygon nbds = new Polygon( 4 );
		Point c = bds.getVertex( 0 ).convexSum( bds.getVertex( 2 ), 0.5 );

		// To help ensure the tiling covers the viewport, we scale
		// the viewport up a bit before filling.  This gives us some
		// leeway in case the fill algorithm is a bit sloppy.

		nbds.addVertex( c.convexSum( bds.getVertex( 0 ), 1.5 ) );
		nbds.addVertex( c.convexSum( bds.getVertex( 1 ), 1.5 ) );
		nbds.addVertex( c.convexSum( bds.getVertex( 2 ), 1.5 ) );
		nbds.addVertex( c.convexSum( bds.getVertex( 3 ), 1.5 ) );

		fill.fill( nbds, t1, t2, callback );
	}

	public static final void main( String[] args )
	{
		Tiling tiling = null;

		if( args.length == 1 ) {
			tiling = Tiling.find( args[ 0 ] );
			if( tiling == null ) {
				System.err.println( "No such tiling." );
				System.exit( -1 );
			}
		} else {
			System.out.println( "Awaiting user input of a tiling." );

			try {
				tiling = Tiling.readTiling( 
					new InputStreamReader( System.in ) );
			} catch( IOException ioe ) {
				System.err.println( "Parse error." );
			}
		}

		TilingViewer tt = new TilingViewer( tiling );
		tt.setSize( 500, 500 );

		Frame frame = new Frame( "Tiling Test" );
		frame.setLayout( new BorderLayout() );
		frame.add( "Center", tt );
		frame.pack();
		frame.show();
	}
}
