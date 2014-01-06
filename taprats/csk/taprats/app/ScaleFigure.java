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
 * ScaleFigure.java
 *
 */

package csk.taprats.app;

import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.geometry.*;

import csk.taprats.general.Loose;

public class ScaleFigure
	extends RadialFigure
	implements Cloneable
{
	protected RadialFigure 	child;
	protected double  		s;

	public ScaleFigure( RadialFigure child, double s )
	{
		super( child.getN() );
		
		this.child = child;
		this.s = s;
	}

	public Object clone()
	{
		return new ScaleFigure( (RadialFigure)( child.clone() ), getS() );
	}

	public double getS()
	{
		return s;
	}

	public void setS( double s )
	{
		this.s = s;
	}

	public Map buildUnit()
	{
		if( Loose.equals( s, 1.0 ) ) {
			return child.buildUnit();
		}

		Map cunit = child.buildUnit();

		Vertex tip = null;
		Point tip_pos = new Point( 1.0, 0.0 );

		// Find the tip, i.e. the vertex at (1,0)
		for( Enumeration e = cunit.getVertices(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			Point pos = vert.getPosition();
			if( Loose.equals( pos, tip_pos ) ) {
				tip = vert;
				break;
			}
		}

		// Scale the unit
		cunit.scale( s );

		tip_pos = tip.getPosition();

		// Build the clipping polygon
		Point[] border = new Point[ n ];
		for( int idx = 0; idx < n; ++idx ) {
			border[ idx ] = getArc( (double)idx * don );
		}

		// Locate the other vertex of the segment we're going to
		// extend.
		Vertex below_tip = null;

		for( Enumeration en = tip.neighbours(); en.hasMoreElements(); ) {
			Edge edge = (Edge)( en.nextElement() );
			Vertex ov = edge.getOther( tip );
			if( ov.getPosition().getY() < 0.0 ) {
				below_tip = ov;
				break;
			}
		}

		// Extend and clip.

		Point bpos = below_tip.getPosition();
		Point seg_end = tip_pos.add(
			tip_pos.subtract( bpos ).normalize().scale( 100.0 ) );

		Point endpoint = tip_pos;

		for( int idx = 0; idx < n; ++idx ) {
			Point poly_a = border[ idx ];
			Point poly_b = border[ (idx + 1) % n ];

			Point ep = Intersect.getIntersection( 
				tip_pos, seg_end, poly_a, poly_b );
			if( ep != null ) {
				endpoint = ep;
				break;
			}
		}

		// Now add the extended edge and its mirror image by first
		// intersecting against rotated versions.

		Point neg_start = Tr.apply( tip_pos );
		Point neg_end = 
			Tr.apply( new Point( endpoint.getX(), -endpoint.getY() ) );

		Vertex last_top = tip;
		Vertex last_bottom = tip;

		for( int idx = 0; idx < (n+1)/2; ++idx ) {
			Point isect = Intersect.getIntersection( 
				tip_pos, endpoint, neg_start, neg_end );
			if( isect == null ) {
				break;
			}

			Vertex iv = cunit.insertVertex( isect );
			cunit.insertEdge( last_top, iv );
			last_top = iv;
			iv = cunit.insertVertex( 
				new Point( isect.getX(), -isect.getY() ) );
			cunit.insertEdge( last_bottom, iv );
			last_bottom = iv;

			neg_start = Tr.apply( neg_start );
			neg_end = Tr.apply( neg_end );
		}

		Vertex iv = cunit.insertVertex( endpoint );
		if( !iv.equals( last_top ) ) {
			cunit.insertEdge( last_top, iv );
			iv = cunit.insertVertex( 
				new Point( endpoint.getX(), -endpoint.getY() ) );
			cunit.insertEdge( last_bottom, iv );
		}

		return cunit;
	}

	public static void main( String[] args )
	{
		int n = Integer.parseInt( args[0] );
		double q = (new Double(args[1])).doubleValue();
		int s = Integer.parseInt( args[2] );
		double sc = (new Double(args[3])).doubleValue();

		Rosette rosette = new Rosette( n, q, s );
		ScaleFigure sf = new ScaleFigure( rosette, sc );
		Map m = sf.getMap();

		csk.taprats.ui.MapViewer mv = 
			new csk.taprats.ui.MapViewer( -1.5, 1.5, 3.0, m, true );
		mv.setSize( 500, 500 );
		csk.taprats.toolkit.Util.openTestFrame( mv );
	}
}
