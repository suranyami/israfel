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
 * TestView.java
 *
 * A simple application to test the GeoView and GeoGraphics classes.
 */

package csk.taprats.toolkit;

import java.util.Vector;

import java.awt.Color;
import java.awt.Frame;
import java.awt.BorderLayout;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Transform;

public class TestView
	extends GeoView
{
	private Vector 	quads;
	private Color	c1;
	private Color	c2;
	private Color	s;

	public TestView()
	{
		super( -5.0, 5.0, 10.0 );
		setSize( 400, 400 );

		setSink( true );

		this.quads = new Vector( 128 );

		for( int idx = 0; idx < 8; ++idx ) {
			for( int jdx = 0; jdx < 8; ++jdx ) {
				double xm = -4 + (double)idx;
				double ym = -4 + (double)jdx;

				Point[] spts = {
					new Point( xm + 0.25, ym - 0.2 ),
					new Point( xm + 0.95, ym - 0.2 ),
					new Point( xm + 0.95, ym + 0.5 ),
					new Point( xm + 0.25, ym + 0.5 ) };
				Point[] pts = {
					new Point( xm, ym ),
					new Point( xm + 0.7, ym ),
					new Point( xm + 0.7, ym + 0.7 ),
					new Point( xm, ym + 0.7 ) };
				
				quads.addElement( pts );
				quads.addElement( spts );
			}
		}

		s = new Color( 0.1f, 0.1f, 0.1f );
		c1 = new Color( 0.8f, 0.7f, 0.6f );
		c2 = new Color( 0.7f, 0.6f, 0.8f );
	}

	protected void redraw( GeoGraphics gg )
	{
		boolean b = false;

		for( int idx = 0; idx < quads.size(); idx += 2 ) {
			if( (idx % 16) == 0 ) {
				b = !b;
			}

			Point[] pts = (Point[])( quads.elementAt( idx ) );
			Point[] spts = (Point[])( quads.elementAt( idx + 1 ) );
			
			gg.setColor( s );
			gg.drawPolygon( spts, true );

			if( b ) {
				gg.setColor( c1 );
			} else {
				gg.setColor( c2 );
			}

			gg.drawPolygon( pts, true );
			b = !b;
		}
	}

	public static final void main( String[] args )
	{
		Frame f = new Frame( "GeoView test" );
		f.setLayout( new BorderLayout() );

		GeoView v = new TestView();

		f.add( "Center", v );
		f.pack();
		f.show();
	}
}
