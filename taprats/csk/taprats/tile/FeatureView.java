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
 * FeatureView.java
 *
 * It's unlikely this file will get used in the applet.  A FeatureView
 * is a special kind of GeoView that assumes a subclass will maintain
 * a collection of Features.  It knows how to draw Features quickly,
 * and provides a bunch of services to subclasses for mouse-based
 * interaction with features.
 */

package csk.taprats.tile;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.geometry.*;

import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Point;
import csk.taprats.toolkit.GeoView;
import csk.taprats.toolkit.GeoGraphics;

public class FeatureView
	extends GeoView
{
	// The default colour of the interior of a polygon.
	protected static Color pcol;

	FeatureView( double l, double t, double w )
	{
		super( l, t, w );
	}

	static void drawFeature( GeoGraphics g2d, PlacedFeature pf, boolean draw_c,
		Color icol )
	{
		Feature f = pf.getFeature();
		Graphics g = g2d.getDirectGraphics();
		Transform T = g2d.getTransform().compose( pf.getTransform() );

		// Polygon pgon = f.getPolygon();
		Point[] pts = f.getPoints();

		int sz = pts.length;

		int[] xpts = new int[ sz ];
		int[] ypts = new int[ sz ];

		Point v;
		Point cent = new Point( 0.0, 0.0 );

		for( int i = 0; i < sz; ++i ) {
			v = T.apply( pts[ i ] );
			xpts[ i ] = (int)( v.getX() );
			ypts[ i ] = (int)( v.getY() );

			// Also build the center.
			cent.setX( cent.getX() + v.getX() );
			cent.setY( cent.getY() + v.getY() );
		}

		g.setColor( icol );
		g.fillPolygon( xpts, ypts, sz );
		g.setColor( Color.black );
		g.drawPolygon( xpts, ypts, sz );

		if( draw_c ) {
			cent.scaleD( 1.0 / (double)sz );
			g.setColor( Color.red );
			g.drawOval( (int)cent.getX() - 3, (int)cent.getY() - 3, 6, 6 );
		}
	}

	static void drawFeature( GeoGraphics g2d, PlacedFeature pf, boolean b )
	{
		drawFeature( g2d, pf, b, pcol );
	}

	static void drawFeature( GeoGraphics g2d, PlacedFeature pf )
	{
		drawFeature( g2d, pf, false, pcol );
	}

	/*
	 * Subclasses are expected to override the following two methods
	 * to point to their own Feature collections.  Really, these should
	 * probably be abstract methods.  I guess I wanted the class to
	 * stand on its own for some unknown reason.
	 */
	public int numFeatures()
	{
		return 0;
	}
	public PlacedFeature getFeature( int idx ) 
	{
		return null;
	}

	static Point featureCenter( Feature f )
	{
		Polygon pgon = f.getPolygon();

		double x = 0.0;
		double y = 0.0;

		for( int v = 0; v < pgon.numVertices(); ++v ) {
			Point a = pgon.getVertex( v );

			x += a.getX();
			y += a.getY();
		}

		double nv = (double)pgon.numVertices();
		return new Point( x / nv, y / nv );
	}

	Selection findFeature( Point spt )
	{
		Point wpt = screenToWorld( spt );

		// Of course, find receivers in reverse order from draw order.
		// Much more intuitive this way.
		for( int idx = numFeatures() - 1; idx >= 0; --idx ) {
			PlacedFeature pf = getFeature( idx );
			Transform T = pf.getTransform();
			Feature f = pf.getFeature();

			Polygon pgon = f.getPolygon();
			pgon.applyTransform( T );

			/*
			Point sc = worldToScreen( T.apply( featureCenter( f ) ) );

			if( spt.dist2( sc ) < 49.0 ) {
				return new Selection( idx, 0 );
			}
			*/

			if( pgon.containsPoint( wpt ) ) {
				return new Selection( idx, 0 );
			}
		}

		return null;
	}

	Selection findVertex( Point spt )
	{
		for( int idx = numFeatures() - 1; idx >= 0; --idx ) {
			PlacedFeature pf = getFeature( idx );
			Transform T = pf.getTransform();
			Feature f = pf.getFeature();
			Polygon pgon = f.getPolygon();

			for( int v = 0; v < pgon.numVertices(); ++v ) {
				Point a = T.apply( pgon.getVertex( v ) );
				Point sa = worldToScreen( a );

				if( spt.dist2( sa ) < 49.0 ) {
					return new Selection( idx, v );
				}
			}
		}

		return null;
	}

	Selection findEdge( Point spt )
	{
		for( int idx = numFeatures() - 1; idx >= 0; --idx ) {
			PlacedFeature pf = getFeature( idx );
			Transform T = pf.getTransform();
			Feature f = pf.getFeature();
			Polygon pgon = f.getPolygon();

			for( int v = 0; v < pgon.numVertices(); ++v ) {
				Point a = T.apply( pgon.getVertex( v ) );
				Point b = T.apply( 
					pgon.getVertex( (v+1) % pgon.numVertices() ) );

				Point sa = worldToScreen( a );
				Point sb = worldToScreen( b );

				if( spt.distToLine( sa, sb ) < 7.0 ) {
					return new Selection( idx, v );
				}
			}
		}

		return null;
	}

	static {
		pcol = new Color( 0.85f, 0.85f, 1.0f );
	}
}
