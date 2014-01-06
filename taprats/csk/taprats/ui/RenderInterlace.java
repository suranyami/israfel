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
 * RenderInterlace.java
 *
 * Probably the most important rendering style from an historical point
 * of view.  RenderInterlace assigns an over-under rule to the edges
 * of the map and renders a weave the follows that assignment.  Getting
 * the over-under rule is conceptually simple but difficult in practice,
 * especially since we want to have some robust against degenerate maps
 * being produced by other parts of the program (*sigh*).
 *
 * Basically, if a diagram can be interlaced, you can just choose an
 * over-under relationship at one vertex and propagate it to all other
 * vertices using a depth-first search.
 *
 * Drawing the interlacing takes a bit of trig, but it's doable.  It's
 * just a pain when crossing edges don't cross in a perfect X.  I
 * might get this wrong.
 */

package csk.taprats.ui;

import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;

import csk.taprats.general.Signal;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.toolkit.Slider;
import csk.taprats.geometry.*;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;

class vertex_info
{
	boolean visited;
}

class edge_info
{
	boolean visited;
	boolean start_under;
}

public class RenderInterlace
	extends RenderStyle
{
	protected Map			map;
	protected Point[] 		pts;
	protected double 		width;
	protected double 		gap;

	private Slider			width_slide;
	private Slider			gap_slide;

	protected GridBagLayout	layout;

	public RenderInterlace()
	{
		super();
		this.pts = null;

		this.width = 0.03;
		this.gap = 0.008;

		this.width_slide = new Slider( "Width", this.width, 0.0, 0.1 );
		this.gap_slide = new Slider( "Gap Width", this.gap, 0.0, 0.06 );

		layout = new GridBagLayout();
		setLayout( layout );

		width_slide.insert( this, layout, 0, 0 );
		gap_slide.insert( this, layout, 0, 1 );

		Observer obs = (new Observer() {
			public void update( Observable obj, Object arg )
			{
				updateAll();
			}
		} );

		width_slide.value_changed.addObserver( obs );
		gap_slide.value_changed.addObserver( obs );
	}

	protected void updateAll()
	{
		width = width_slide.getValue();
		gap = gap_slide.getValue();

		setMap( this.map );
		style_changed.notify( null );
	}

	public static final Point[] getPoints( 
		Edge edge, Vertex from, Vertex to, double width, double gap )
	{
		boolean from_under = 
			edge.getV1().equals( from ) == getData( edge ).start_under;

		Point pfrom = from.getPosition();
		Point pto = to.getPosition();

		Point dir = pto.subtract( pfrom );
		dir.normalizeD();
		Point perp = dir.perp();

		// Four cases:
		// 	- cap
		//  - bend
		//  - interlace over
		//  - interlace under

		Point below = null;
		Point cen = null;
		Point above = null;

		int nn = to.numNeighbours();

		if( nn == 1 ) {
			// cap
			perp.scaleD( width );
			dir.scaleD( width );

			below = pto.subtract( perp );
			below.addD( dir );
			cen = pto.add( dir );
			above = pto.add( perp );
			above.addD( dir );
		} else if( nn == 2 ) {
			// bend
			Point[] jps = RenderOutline.getPoints( edge, from, to, width );
			below = jps[ 0 ];
			cen = pto;
			above = jps[ 1 ];
		} else {
			if( from_under ) {
				// interlace over
				Edge[] ns = new Edge[ nn ];
				int index = 0;
				int edge_idx = -1;
				for( Enumeration e = to.neighbours(); e.hasMoreElements(); ) {
					ns[ index ] = (Edge)( e.nextElement() );
					if( ns[ index ].equals( edge ) ) {
						edge_idx = index;
					}
					++index;
				}

				int nidx = (edge_idx + 2) % nn;

				Point op = ns[ nidx ].getOther( to ).getPosition();

				below = RenderOutline.getJoinPoint( pto, pfrom, op, width );

				if( below == null ) {
					below = pto.subtract( perp.scale( width ) );
				}

				cen = pto;

				above = below.convexSum( pto, 2.0 );
			} else {
				// interlace under

				// This is the hard case, fraught with pitfalls for 
				// the imprudent (i.e., me).  I think what I've got
				// now does a reasonable job on well-behaved maps
				// and doesn't dump core on badly-behaved ones.

				Edge[] ba = to.getBeforeAndAfter( edge );
				Point before_pt = ba[0].getOther( to ).getPosition();
				Point after_pt = ba[1].getOther( to ).getPosition();

				below = RenderOutline.getJoinPoint( 
					pto, pfrom, after_pt, width );
				above = RenderOutline.getJoinPoint(
					pto, before_pt, pfrom, width );

				cen = RenderOutline.getJoinPoint( 
					pto, before_pt, after_pt, width );

				if( below == null ) {
					below = pto.subtract( perp.scale( width ) );
				}
				if( above == null ) {
					above = pto.add( perp.scale( width ) );
				}
				if( cen == null ) {
					Point ab = after_pt.subtract( before_pt );
					ab.normalizeD();
					ab.perpD();
					ab.scaleD( width );
					cen = pto.subtract( ab );
				}

				// FIXME -- The gap size isn't consistent since
				// it's based on subtracting gap scaled unit vectors.
				// Scale gap by 1/sin(theta) to compensate for 
				// angle with neighbours and get a _perpendicular_ gap.

				below.subtractD( dir.scale( gap ) );
				above.subtractD( dir.scale( gap ) );
				cen.subtractD( dir.scale( gap ) );
			}
		}

		Point[] ret = { below, cen, above };
		return ret;
	}

	private static edge_info getData( Edge edge )
	{
		return (edge_info)( edge.getData() );
	}

	private static vertex_info getData( Vertex vert )
	{
		return (vertex_info)( vert.getData() );
	}

	private static boolean isVisited( Vertex vert )
	{
		return getData( vert ).visited;
	}

	/*
	 * Propagate the over-under relationship from a vertices to its
	 * adjacent edges.  The relationship is encapsulated in the 
	 * "edge_under_at_vert" variable, which says whether the 
	 * edge passed in is in the under state at this vertex.
	 * The whole trick is to manage how neighbours receive modifications
	 * of edge_under_at_vert.
	 *
	 * FIXME --
	 * If Java barfs on Taprats, complaining of a stack overflow, it'll
	 * be here.  The propagation algorithm is recursive and can get really
	 * deep.  The obvious solution is to use a java.util.Stack to manage
	 * propagation points and do the recursion on the heap instead.  But 
	 * the code would get much uglier in that case.  An easier solution 
	 * is to increase the maximum stack size in the java virtual machine.  
	 * For some dumb reason, the Sun 1.2.2 java VM hides the command line
	 * option to do this (can someone tell me why??).  So here it is.  To
	 * start Taprats with, say, a 10 megabyte stack, do
	 *
	 * 	java -Xss10M csk.taprats.Program
	 *
	 * I just hope that the browser VMs can handle the recursion.
	 * 
	 * The other solution is to put a try {} block around the propagation, 
	 * catch stack overflows, and put up a message block complaining to the
	 * user and telling them to increase their stack size.  Yech.
	 */

	public static void propagate(
		Vertex vertex, Edge edge, boolean edge_under_at_vert )
	{
		vertex_info vi = getData( vertex );
		vi.visited = true;

		int nn = vertex.numNeighbours();

		if( nn == 2 ) {
			Edge[] ba = vertex.getBeforeAndAfter( edge );
			Edge oe = ba[0];

			if( !getData( oe ).visited ) {
				// With a bend, we don't want to change the underness
				// of the edge we're propagating to.

				if( oe.getV1().equals( vertex ) ) {
					// The new edge starts at the current vertex.
					buildFrom( oe, !edge_under_at_vert );
				} else {
					// The new edge ends at the current vertex.
					buildFrom( oe, edge_under_at_vert );
				}
			}
		} else if( nn > 2 ) {
			Edge[] ns = new Edge[ nn ];
			int index = 0;
			int edge_idx = -1;
			for( Enumeration e = vertex.neighbours(); e.hasMoreElements(); ) {
				ns[ index ] = (Edge)( e.nextElement() );
				if( ns[ index ].equals( edge ) ) {
					edge_idx = index;
				}
				++index;
			}

			boolean cur_under = edge_under_at_vert;

			for( int idx = 1; idx < nn; ++idx ) {
				int cur = (edge_idx + idx) % nn;
				Edge oe = ns[ cur ];

				if( !getData( oe ).visited ) {
					if( oe.getV1().equals( vertex ) ) {
						buildFrom( oe, !cur_under );
					} else {
						buildFrom( oe, cur_under );
					}
				}

				cur_under = !cur_under;
			}
		}
	}

	// Propagate the over-under relation from an edge to its incident
	// vertices.

	public static void buildFrom( Edge edge, boolean start_under )
	{
		edge_info ei = getData( edge );

		ei.visited = true;
		ei.start_under = start_under;

		Vertex v1 = edge.getV1();
		Vertex v2 = edge.getV2();

		if( !isVisited( v1 ) ) {
			propagate( v1, edge, start_under );
		}
		if( !isVisited( v2 ) ) {
			propagate( v2, edge, !start_under );
		}
	}

	public static void initializeMap( Map map )
	{
		for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			edge_info ei = new edge_info();
			ei.visited = false;
			ei.start_under = false;
			edge.setData( ei );
		}

		for( Enumeration e = map.getVertices(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			vertex_info vi = new vertex_info();
			vi.visited = false;
			vert.setData( vi );
		}
	}

	public static void assignInterlacing( Map map )
	{
		initializeMap( map );

		for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			if( !getData( edge ).visited ) {
				buildFrom( edge, true );
			}
		}
	}

	public void setMap( Map map )
	{
		pts = null;
		this.map = null;

		if( map != null ) {
			this.map = (Map)( map.clone() );
			assignInterlacing( this.map );

			// Given the interlacing assignment created above, we can
			// use the beefy getPoints routine to extract the graphics
			// of the interlacing.

			pts = new Point[ this.map.numEdges() * 6 ];
			int index = 0;

			for( Enumeration e = this.map.getEdges(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );
				Vertex v1 = edge.getV1();
				Vertex v2 = edge.getV2();

				Point[] top = getPoints( edge, v1, v2, width, gap );
				Point[] fromp = getPoints( edge, v2, v1, width, gap );

				pts[ index ] = top[0];
				pts[ index + 1 ] = top[1];
				pts[ index + 2 ] = top[2];
				pts[ index + 3 ] = fromp[0];
				pts[ index + 4 ] = fromp[1];
				pts[ index + 5 ] = fromp[2];

				index += 6;
			}
		}
	}

	public void draw( GeoGraphics gg )
	{
		Color interior = new Color( 240, 240, 255 );

		gg.setColor( Color.black );
		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 6 ) {
				gg.setColor( interior );
				gg.drawPolygon( pts, idx, idx + 6, true );
			}

			for( int idx = 0; idx < pts.length; idx += 6 ) {
				gg.setColor( Color.black );
				gg.drawLine( pts[ idx + 2 ], pts[ idx + 3 ] );
				gg.drawLine( pts[ idx + 5 ], pts[ idx ] );
			}
		}
	}
}
