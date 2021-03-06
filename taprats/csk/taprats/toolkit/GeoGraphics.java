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
 * GeoGraphics.java
 *
 * A GeoGraphics instance acts like the 2D geometry version of
 * java.awt.Graphics (or, more recently, like java2d's Graphics2D class).
 * It understands how to draw a bunch of ordinary geometric primitives
 * by doing the appropriate coordinate transforms and drawing the AWT
 * versions.
 *
 * Note that circles are drawn in screen space -- circles drawn
 * from a sheared coordinate system won't show up as ellipses.  Darn.
 *
 * GeoGraphics maintains a stack of coordinate transforms, meant to
 * behave like OpenGL's matrix stack.  Makes it easy to execute a
 * bunch of primitives in some pushed graphics state.
 */

package csk.taprats.toolkit;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Component;
import java.util.Stack;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;
import csk.taprats.geometry.Rect;
import csk.taprats.geometry.Polygon;

public class GeoGraphics
{
	private Graphics 		graphics;
	private Transform		transform;
	private Stack			pushed;
	private Component 		component;

	public GeoGraphics( Graphics graphics, Transform transform,Component comp )
	{
		this.graphics = graphics;
		this.transform = transform;
		this.pushed = null;
		this.component = comp;
	}

	public GeoGraphics( Graphics graphics, Transform transform )
	{
		this( graphics, transform, null );
	}

	void dispose()
	{
		graphics.dispose();
	}

	public final Graphics getDirectGraphics()
	{
		return graphics;
	}
	
	public final Transform getTransform()
	{
		return transform;
	}

	public final void drawLine( double x1, double y1, double x2, double y2 ) {
		double v1x = transform.applyX( x1, y1 );
		double v1y = transform.applyY( x1, y1 );
		double v2x = transform.applyX( x2, y2 );
		double v2y = transform.applyY( x2, y2 );

		graphics.drawLine( (int)v1x, (int)v1y, (int)v2x, (int)v2y );
	}

	public final void drawLine( Point v1, Point v2 )
	{
		drawLine( v1.getX(), v1.getY(), v2.getX(), v2.getY() );
		/*
		Point v1p = transform.apply( v1 );
		Point v2p = transform.apply( v2 );

		graphics.drawLine( 
			(int)( v1p.getX() ), (int)( v1p.getY() ),
			(int)( v2p.getX() ), (int)( v2p.getY() ) );
		*/
	}

	// Draw a thick like as a rectangle.
	public final void drawThickLine( 
		double x1, double y1, double x2, double y2, double width )
	{
		drawThickLine( new Point( x1, y1 ), new Point( x2, y2 ), width );
	}

	public final void drawThickLine( Point v1, Point v2, double width )
	{
		Point p = (v2.subtract( v1 )).perp();

		Point[] pts = { 
			v1.add( p.scale( width ) ), v1.subtract( p.scale( width ) ),
			v2.subtract( p.scale( width ) ), v2.add( p.scale( width ) ) };

		drawPolygon( pts, true );

		drawCircle( v1, width / 2.0, true );
		drawCircle( v2, width / 2.0, true );
	}

	public final void drawRect( Point topleft, double width, double height,
		boolean filled )
	{
		double x = topleft.getX();
		double y = topleft.getY();

		Point[] pts = {
			topleft, 
			new Point( x + width, y ),
			new Point( x + width, y + height ),
			new Point( x, y + height ) };
		
		drawPolygon( pts, filled );
	}

	public final void drawPolygon( Point[] pts, boolean filled )
	{
		drawPolygon( pts, 0, pts.length, filled );
	}

	public final void drawPolygon( Point[] pts, int start, int end, 
		boolean filled )
	{
		int len = end - start;
		int[] xpts = new int[ len ];
		int[] ypts = new int[ len ];

		int index = 0;
		for( int i = start; i < end; ++i ) {
			double x = pts[i].getX();
			double y = pts[i].getY();

			xpts[ index ] = (int)( transform.applyX( x, y ) );
			ypts[ index ] = (int)( transform.applyY( x, y ) );
			++index;
		}

		if( filled ) {
			graphics.fillPolygon( xpts, ypts, len );
		} else {
			graphics.drawPolygon( xpts, ypts, len );
		}
	}

	public final void drawPolygon( csk.taprats.geometry.Polygon pgon, 
		boolean filled )
	{
		int len = pgon.numVertices();
		int[] xpts = new int[ len ];
		int[] ypts = new int[ len ];

		for( int i = 0; i < len; ++i ) {
			Point v = pgon.getVertex( i );
			double x = v.getX();
			double y = v.getY();

			xpts[ i ] = (int)( transform.applyX( x, y ) );
			ypts[ i ] = (int)( transform.applyY( x, y ) );
		}

		if( filled ) {
			graphics.fillPolygon( xpts, ypts, len );
		} else {
			graphics.drawPolygon( xpts, ypts, len );
		}
	}

	public final void drawCircle( Point origin, double radius, 
		boolean filled )
	{
		Point rad = transform.apply( new Point( radius, 0.0 ) );
		Point orig = transform.apply( new Point( 0.0, 0.0 ) );
		double true_radius = rad.dist( orig );

		Point new_origin = transform.apply( origin );
		Point new_topleft = new_origin.subtract( 
			new Point( true_radius, true_radius ) );

		int r2 = (int)( true_radius * 2.0 );

		if( filled ) {
			graphics.fillOval( 
				(int)( new_topleft.getX() ), 
				(int)( new_topleft.getY() ),
				r2, r2 );
		} else {
			graphics.drawOval( 
				(int)( new_topleft.getX() ), 
				(int)( new_topleft.getY() ),
				r2, r2 );
		}
	}

	public final void drawCircle( Point origin, double radius )
	{
		drawCircle( origin, radius, false );
	}

	// Again, no fancy warping is done.  The image is drawn at the
	// transformed origin point.
	public final void drawImage( Image image, Point pt )
	{
		if( component != null ) {
			Point rad = transform.apply( pt );
			graphics.drawImage( 
				image, (int)( rad.getX() ), (int)( rad.getY() ), component );
		}
	}

	public final void drawImage( Image image )
	{
		if( component != null ) {
			graphics.drawImage( image, 0, 0, component );
		}
	}

	public final Color getColor()
	{
		return graphics.getColor();
	}

	public final void setColor( Color c )
	{
		graphics.setColor( c );
	}

	public final void pushAndCompose( Transform T )
	{
		if( pushed == null ) {
			pushed = new Stack();
		}

		pushed.push( transform );
		transform = transform.compose( T );
	}

	public final Transform pop()
	{
		Transform it = transform;
		transform = (Transform)( pushed.pop() );
		return it;
	}
}
