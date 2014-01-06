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
 * RenderOutline.java
 *
 * The simplest non-trivial rendering style.  RenderOutline just uses
 * some trig to fatten up the map's edges, also drawing a line-based
 * outline for the resulting fat figure.
 *
 * The same code that computes the draw elements for RenderOutline can
 * be used by other "fat" styles, such as RenderEmboss.
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

public class RenderOutline
	extends RenderStyle
{
	Map			map;
	Point[] 	pts;
	double 		width;

	private Slider	width_slide;

	protected GridBagLayout	layout;

	public RenderOutline()
	{
		super();
		this.pts = null;

		this.width = 0.03;

		this.width_slide = new Slider( "Width", this.width, 0.0, 0.1 );
		
		// Put the layout object into the instance so that subclasses can
		// add their own controls.

		layout = new GridBagLayout();
		setLayout( layout );
		width_slide.insert( this, layout, 0, 0 );

		width_slide.value_changed.addObserver( new Observer() {
			public void update( Observable obj, Object arg )
			{
				updateWidth();
			}
		} ); 
	}

	protected void updateWidth()
	{
		width = width_slide.getValue();

		setMap( this.map );
		style_changed.notify( null );
	}

	protected double getLineWidth()
	{
		return this.width;
	}

	/*
	 * Do a mitered join of the two fat lines (a la postscript, for example).
	 * The join point on the other side of the joint can be computed by
	 * reflecting the point returned by this function through the joint.
	 */
	static final Point getJoinPoint( 
		Point joint, Point a, Point b, double width )
	{
		double th = joint.sweep( a, b );

		if( Math.abs( th - Math.PI ) < 1e-7 ) {
			return null;
		} else {
			Point d1 = joint.subtract( a );
			d1.normalizeD();
			Point d2 = joint.subtract( b );
			d2.normalizeD();

			double l = width / Math.sin( th );
			double isx = joint.getX() - (d1.getX() + d2.getX()) * l;
			double isy = joint.getY() - (d1.getY() + d2.getY()) * l;
			return new Point( isx, isy );
		}
	}

	/*
	 * Look at a given edge and construct a plausible set of points
	 * to draw at the edge's 'to' vertex.  Call this twice to get the
	 * complete outline of the hexagon to draw for this edge.
	 */
	static final Point[] getPoints( 
		Edge edge, Vertex from, Vertex to, double width )
	{
		Point pfrom = from.getPosition();
		Point pto = to.getPosition();

		Point dir = pto.subtract( pfrom );
		dir.normalizeD();
		Point perp = dir.perp();

		int nn = to.numNeighbours();

		Point above = null;
		Point below = null;

		if( nn == 1 ) {
			below = pto.subtract( perp.scale( width ) );
			above = pto.add( perp.scale( width ) );
		} else if( nn == 2 ) {
			Edge[] ba = to.getBeforeAndAfter( edge );
			Vertex ov = ba[0].getOther( to );
			Point pov = ov.getPosition();

			Point jp = getJoinPoint( pto, pfrom, pov, width );

			if( jp == null ) {
				below = pto.subtract( perp.scale( width ) );
				above = pto.add( perp.scale( width ) );
			} else {
				below = jp;
				above = jp.convexSum( pto, 2.0 );
			}
		} else {
			Edge[] ba = to.getBeforeAndAfter( edge );
			Point before_pt = ba[0].getOther( to ).getPosition();
			Point after_pt = ba[1].getOther( to ).getPosition();

			below = getJoinPoint( pto, pfrom, after_pt, width );
			above = getJoinPoint( pto, before_pt, pfrom, width );
		}

		Point[] ret = { below, above };
		return ret;
	}

	public void setMap( Map map )
	{
		pts = null;

		this.map = map;

		if( this.map != null ) {
			double width = getLineWidth();

			pts = new Point[ map.numEdges() * 6 ];
			int index = 0;

			for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );
				Vertex v1 = edge.getV1();
				Vertex v2 = edge.getV2();

				Point[] top = getPoints( edge, v1, v2, width );
				Point[] fromp = getPoints( edge, v2, v1, width );

				pts[ index ] = top[0];
				pts[ index + 1 ] = v2.getPosition();
				pts[ index + 2 ] = top[1];
				pts[ index + 3 ] = fromp[0];
				pts[ index + 4 ] = v1.getPosition();
				pts[ index + 5 ] = fromp[1];

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
				gg.setColor( Color.black );
				gg.drawLine( pts[ idx + 2 ], pts[ idx + 3 ] );
				gg.drawLine( pts[ idx + 5 ], pts[ idx ] );
			}
		}
	}
}
