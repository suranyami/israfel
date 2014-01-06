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
 * Star.java
 * 
 * The classic [n/d]s star construction.  See the paper for more
 * details.
 */

package csk.taprats.app;

import csk.taprats.geometry.*;

import csk.taprats.general.Loose;

public class Star
	extends RadialFigure
	implements Cloneable
{
	private double 	d;
	private int		s;

	public Star( int n, double d, int s )
	{
		super( n );

		this.d = d;
		this.s = s;
	}

	public Star()
	{
		this( 8, 3.0, 2 );
	}

	public Object clone()
	{
		return new Star( getN(), getD(), getS() );
	}
	
	public double getD()
	{
		return d;
	}

	public void setD( double d )
	{
		this.d = d;
	}

	public int getS()
	{
		return s;
	}

	public void setS( int s )
	{
		this.s = s;
	}

	public Map buildUnit()
	{
		double clamp_d = Math.max( 1.0, Math.min( d, 0.5 * dn - 0.01 ) );

		double did = Math.floor( clamp_d );
		double dfrac = clamp_d - did;
		int di = (int)( did );
		boolean is_int = false;

		int clamp_s = Math.min( s, di );
		int outer_s = Math.min( s, di - 1 );

		if( dfrac < Loose.TOL ) {
			dfrac = 0.0;
			is_int = true;
		} else if( (1.0 - dfrac) < Loose.TOL ) {
			dfrac = 0.0;
			di = (int)( did ) + 1;
			is_int = true;
		}

		Map from = new Map();

		Point[] points = new Point[ clamp_s + 1 ];
		int index = 0;

		Point a = new Point( 1.0, 0.0 );
		Point b = getArc( clamp_d * don );

		for( int idx = 1; idx <= outer_s; ++idx ) {
			Point ar = getArc( (double)idx * don );
			Point br = getArc( ((double)idx - clamp_d) * don );

			Point mid = Intersect.getIntersection( a, b, ar, br );

			points[index] = mid;
			++index;
		}

		Vertex vt = from.insertVertex( a );
		Vertex top_prev = vt;
		Vertex bot_prev = vt;

		for( int idx = 0; idx < index; ++idx ) {
			Vertex top = from.insertVertex( points[ idx ] );
			Vertex bot = from.insertVertex(
				new Point( points[ idx ].getX(), -points[ idx ].getY() ) );
			
			from.insertEdge( top_prev, top );
			from.insertEdge( bot_prev, bot );

			top_prev = top;
			bot_prev = bot;
		}

		if( clamp_s == di ) {
			Point midr = Tr.apply( top_prev.getPosition() );
			Vertex v4 = from.insertVertex( midr );

			if( is_int ) {
				from.insertEdge( top_prev, v4 );
			} else {
				Point ar = getArc( did * don );
				Point br = getArc( -dfrac * don );

				Point c = getArc( d * don );

				Point cent = Intersect.getIntersection( ar, br, a, c );
				Vertex vcent = from.insertVertex( cent );
				from.insertEdge( top_prev, vcent );
				from.insertEdge( vcent, v4 );
			}
		}

		return from;
	}

	public static final void main( String[] args )
	{
		int n = Integer.parseInt( args[0] );
		double d = (new Double(args[1])).doubleValue();
		int s = Integer.parseInt( args[2] );

		Star star = new Star( n, d, s );
		Map m = star.getMap();

		csk.taprats.ui.MapViewer mv = 
			new csk.taprats.ui.MapViewer( -1.5, 1.5, 3.0, m, true );
		mv.setSize( 500, 500 );
		csk.taprats.toolkit.Util.openTestFrame( mv );
	}
}
