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
 * MapTest.java
 *
 * Yet another test application (this project's full of 'em).  This app
 * tests that maps are working correctly.  You get a simple window where
 * you can draw a polygonal curve.  The segments of the curve are combined
 * into a planar map in a _horribly_ inefficient way that exercises the
 * Map code.  Then you get a MapViewer that lets you play around with
 * the resulting Map.
 *
 * This code is totally not used in the applet.
 */

package csk.taprats.ui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

import csk.taprats.geometry.*;
import csk.taprats.toolkit.*;

public class MapTest
	extends GeoView
{
	private Vector pts;

	public MapTest()
	{
		super( -1.0, 1.0, 2.0 );
		pts = new Vector();

		MapTestMouse mouse = new MapTestMouse();
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
	}

	public void redraw( GeoGraphics gg )
	{
		gg.setColor( Color.black );
		for( int idx = 0; idx < pts.size() - 1; ++idx ) {
			Point a = (Point)( pts.elementAt( idx ) );
			Point b = (Point)( pts.elementAt( idx + 1 ) );
			gg.drawLine( a, b );
		}
	}

	void openMapViewer()
	{
		Vector maps = new Vector();

		for( int idx = 0; idx < pts.size() - 1; ++idx ) {
			Point p1 = (Point)( pts.elementAt( idx ) );
			Point p2 = (Point)( pts.elementAt( idx + 1 ) );

			Map m = new Map();
			Vertex v1 = m.insertVertex( p1 );
			Vertex v2 = m.insertVertex( p2 );
			m.insertEdge( v1, v2 );

			maps.addElement( m );
		}

		while( maps.size() > 1 ) {
			System.err.println( "maps left: " + maps.size() );
			Vector nmaps = new Vector();

			int idx = 0;
			while( true ) {
				if( idx == maps.size() ) {
					break;
				} else if( idx == (maps.size() - 1) ) {
					nmaps.addElement( maps.elementAt( idx ) );
					break;
				} else {
					Map m1 = (Map)( maps.elementAt( idx ) );
					Map m2 = (Map)( maps.elementAt( idx + 1 ) );
					// System.err.println( "merging." );
					m1.mergeMap( m2 );
					nmaps.addElement( m1 );
					idx += 2;
				}
			}

			maps = nmaps;
		}

		MapViewer mv = new MapViewer( 
			-1.0, 1.0, 2.0, (Map)( maps.elementAt( 0 ) ), true );
		mv.setSize( 400, 400 );
		Frame f = new Frame( "Map View Test" );
		f.setLayout( new BorderLayout() );
		f.add( "Center", mv );
		f.pack();
		f.show();

		pts = new Vector();
		forceRedraw();
	}

	class MapTestMouse
		extends MouseAdapter
		implements MouseMotionListener
	{
		public void mousePressed( MouseEvent me )
		{
			// Left mouse button adds a point.
			// Right mouse button opens the map viewer.

			if( isButton( me, 1 ) ) {
				pts.addElement( screenToWorld( me.getX(), me.getY() ) ); 
				forceRedraw();
			} else if( isButton( me, 3 ) ) {
				openMapViewer();
			}
		}

		public void mouseDragged( MouseEvent me )
		{}

		public void mouseMoved( MouseEvent me )
		{}

		public void mouseReleased( MouseEvent me )
		{}
	}

	public static final void main( String[] args )
	{
		MapTest mt = new MapTest();
		mt.setSize( 400, 400 );
		Frame f = new Frame( "Map Test" );
		f.setLayout( new BorderLayout() );
		f.add( "Center", mt );
		f.pack();
		f.show();
	}
}
