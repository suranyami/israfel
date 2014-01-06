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
 * MapViewer.java
 *
 * A GeoView that gives you a default geometric view of a planar map.
 * There's the ability to jitter when drawing (see the 'why jitter?' note
 * in the source).  There's also a simple facility for moving vertices
 * around and rebuilding the graph (new intersections can emerge while 
 * editing, so this is nontrivial).
 *
 * The editing system is a beast -- it stomps on the map code, and does
 * so deliberately.  It shouldn't be used for production code.  It's more
 * of a stress test for the Map class to make sure it can merge maps
 * quickly and without bugs.
 */

package csk.taprats.ui;

import java.awt.Color;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Random;

import csk.taprats.geometry.*;
import csk.taprats.toolkit.*;

public class MapViewer
	extends GeoView
{
	private Map 		map;

	/* 
	 * Used when the map is editable.
	 */
	private Map			temp_map;
	private Vertex		temp_vert;

	/*
	 * Used when jittering.
	 */
	private Random 		r_jitter;

	public MapViewer( double left, double top, double width, 
		Map map, boolean editable )
	{
		super( left, top, width );

		this.map = map;
		this.r_jitter = null;

		if( editable ) {
			Mouse mouse = new Mouse();
			addMouseListener( mouse );
			addMouseMotionListener( mouse );
		}

		// A silent UI feature to toggle jittering.

		addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent ke )
			{
				if( ke.getKeyChar() == 'j' ) {
					if( r_jitter != null ) {
						setJitter( false );
					} else {
						setJitter( true );
					}
				}
			}
		} );
	}

	public MapViewer( double left, double top, double width, Map map )
	{
		this( left, top, width, map, false );
	}

	public void setJitter( boolean b )
	{
		if( r_jitter == null ) {
			if( b ) {
				r_jitter = new Random();
				forceRedraw();
			}
		} else {
			if( !b ) {
				r_jitter = null;
				forceRedraw();
			}
		}
	}

	public void redraw( GeoGraphics gg )
	{
		Map dmap = map;

		java.awt.Graphics g = gg.getDirectGraphics();

		// If we're editing the map, merge in the sub-map containing the
		// vertex that's being edited.  Hooboy, this hammers on the map
		// code.  Many merges every second.

		if( temp_map != null ) {
			Map t = (Map)( map.clone() );
			t.mergeMap( temp_map );
			dmap = t;
		}

		gg.setColor( Color.black );
		for( Enumeration e = dmap.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			Point a = worldToScreen( edge.getV1().getPosition() );
			Point b = worldToScreen( edge.getV2().getPosition() );

			// Why jitter?
			//
			// As you can see, when jittering is turned on, every 
			// vertex and edge is jittered from its position by a random
			// distance (in screen space).  This is a useful debugging 
			// feature for maps.  Sometimes, map code doesn't work
			// right and duplicate vertices or edges are inserted into
			// a map.  Jittering everything allows you to see duplicates.
			// It has helped in the past.  Now that the map code is
			// 100% bug free, I guess it doesn't matter that much anymore :^)

			if( r_jitter != null ) {
				Point pl = new Point( 
					(int)(r_jitter.nextDouble() * 5)-2, 
					(int)(r_jitter.nextDouble()*5)-2 );

				a = a.add( pl );
				b = b.add( pl );
			}

			g.drawLine( 
				(int)( a.getX() ), (int)( a.getY() ), 
				(int)( b.getX() ), (int)( b.getY() ) );
		}

		gg.setColor( Color.red );
		for( Enumeration e = dmap.getVertices(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			Point a = worldToScreen( vert.getPosition() );
			
			int ax = (int)( a.getX() );
			int ay = (int)( a.getY() );

			if( r_jitter != null ) {
				ax += (int)(r_jitter.nextDouble() * 5) - 2;
				ay += (int)(r_jitter.nextDouble() * 5) - 2;
			}
			
			g.drawOval( ax - 2, ay - 2, 5, 5 );
		}

		if( temp_vert != null ) {
			Point a = worldToScreen( temp_vert.getPosition() );
			
			int ax = (int)( a.getX() );
			int ay = (int)( a.getY() );
			
			g.fillOval( ax - 2, ay - 2, 5, 5 );
			gg.setColor( Color.blue );
			g.drawOval( ax - 2, ay - 2, 5, 5 );
		}
	}

	class Mouse
		extends MouseAdapter
		implements MouseMotionListener
	{
		Point last;

		public void mousePressed( MouseEvent me )
		{
			if( isShift( me ) ) {
				return;
			}

			temp_map = null;
			temp_vert = null;

			// Another hidden UI element to dump the current map
			// as text.  Useful for debugging.

			if( isButton( me, 2 ) ) {
				map.dump( System.err );
				return;
			}

			Point sp = new Point( (double)me.getX(), (double)me.getY() );
			Point p = screenToWorld( sp );
			last = p;

			for( Enumeration e = map.getVertices(); e.hasMoreElements(); ) {
				Vertex v = (Vertex)( e.nextElement() );
				Point vp = v.getPosition();
				Point sv = worldToScreen( vp );

				// If we click on a vertex, edit that vertex.  Remove it
				// from the map and create a new map consisting of the star
				// subgraph containing the vertex and its neighbours.
				// As the vertex is moved around, re-merge the star graph
				// with the main map.

				if( sp.dist2( sv ) < 49.0 ) {
					temp_map = new Map();
					temp_vert = temp_map.insertVertex( vp );

					for( Enumeration n = v.neighbours(); n.hasMoreElements(); ){
						Edge edge = (Edge)( n.nextElement() );
						Point op = edge.getOther( v ).getPosition();
						Vertex ov = temp_map.insertVertex( op );
						temp_map.insertEdge( temp_vert, ov );
					}

					map.removeVertex( v );
					forceRedraw();
					return;
				}
			}
		}

		public void mouseDragged( MouseEvent me )
		{
			Point sp = new Point( (double)me.getX(), (double)me.getY() );
			Point p = screenToWorld( sp );

			if( temp_map != null ) {
				temp_map.transformVertex( temp_vert, Transform.translate( 
					p.subtract( last ) ) );
				forceRedraw();
			}

			last = p;
		}

		public void mouseReleased( MouseEvent me )
		{
			if( temp_map != null ) {
				map.mergeMap( temp_map );
				temp_map = null;
				temp_vert = null;
				forceRedraw();
			}
		}

		public void mouseMoved( MouseEvent me )
		{}
	}
}
