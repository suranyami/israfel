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
 * RenderEmboss.java
 *
 * A rendering style for maps that pretends that the map is carves out
 * of wood with a diamond cross section and shines a directional light 
 * on the wood from some angle.  The map is drawn as a collection of 
 * trapezoids, and the darkness of each trapezoid is controlled by the
 * angle the trapezoid makes with the light source.  The result is a 
 * simple but highly effective 3D effect, similar to flat-shaded 3D
 * rendering.
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

/*
 * In practice, we can make this a subclass of RenderOutline -- it uses the
 * same pre-computed point array, and just add one controls and overloads
 * the draw function.
 */

public class RenderEmboss
	extends RenderOutline
{
	private double 		angle;
	private double 		light_x;
	private double 		light_y;

	private Slider		angle_slide;

	public RenderEmboss()
	{
		super();

		setAngle( Math.PI * 0.25 );

		this.angle_slide = new Slider( "Azimuth", 45.0, 0.0, 360.0 );
		
		angle_slide.insert( this, layout, 0, 1 );

		angle_slide.value_changed.addObserver( new Observer() {
			public void update( Observable Obj, Object arg )
			{
				updateAngle();
			}
		} ); 
	}

	private void setAngle( double angle )
	{
		this.angle = angle;
		this.light_x = Math.cos( angle );
		this.light_y = Math.sin( angle );
	}

	protected void updateAngle()
	{
		double aval = angle_slide.getValue();
		setAngle( ((double)aval * Math.PI) / 180.0 );

		style_changed.notify( null );
	}

	private final void drawTrap( GeoGraphics gg, 
		Point a, Point b, Point c, Point d )
	{
		Point N = a.subtract( d );
		N.perpD();
		N.normalizeD();

		// dd is a normalized floating point value corresponding to 
		// the brightness to use.
		double dd = 0.5 * ( N.getX() * light_x + N.getY() * light_y + 1.0 );

		// Quantize to sixteen grey values.
		int bb = (int)( 16.0 * dd );
		gg.setColor( greys[ bb ] );

		Point[] trap_pts = { a, b, c, d };
		gg.drawPolygon( trap_pts, true );
	}

	public void draw( GeoGraphics gg )
	{
		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 6 ) {
				drawTrap( gg, pts[idx+1], pts[idx+2], pts[idx+3], pts[idx+4] );
				drawTrap( gg, pts[idx+4], pts[idx+5], pts[idx+0], pts[idx+1] );
				
				gg.setColor( Color.black );
				gg.drawPolygon( pts, idx, idx + 6, false );
				gg.drawLine( pts[ idx + 1 ], pts[ idx + 4 ] );
			}
		}
	}

	private static Color[] greys;

	static {
		float frac = 240.0f / 255.0f;

		greys = new Color[ 17 ];

		for( int idx = 0; idx < 17; ++idx ) {
			float t = (float)idx / 16.0f;
			float g = (1.0f-t)*0.4f + t*0.99f;

			greys[ idx ] = new Color( g * frac, g * frac, g );
		}
	}
}
