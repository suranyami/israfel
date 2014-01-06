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
 * GeoView.java
 *
 * This class has been my Java mainstay for quite some time.  A GeoView
 * is a kind of Canvas that understands the notion of a 2D coordinate
 * system.  Subclasses of GeoView implement various interactive 2D
 * viewers.  It's sort of like having an OpenGL window with an Orthographic
 * 2D projection.
 *
 * GeoViews have all kinds of useful built-in interactions for changing
 * the viewport.  See the online documentation for more details.
 */

package csk.taprats.toolkit;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Transform;
import csk.taprats.geometry.Rect;

public class GeoView
	extends Canvas
{
	private double left;
	private double top;
	private double width;

	private double theta;

	private Transform transform;
	private Transform inverse;

	private Image backing_store;

	private boolean sink;

	protected int last_x, last_y;

	private boolean do_tracking;
	protected int track_x, track_y;

	private Dimension last_size;

	public GeoView( double left, double top, double width )
	{
		super();

		this.left = left;
		this.top = top;
		this.width = width;

		this.transform = null;
		this.backing_store = null;

		this.sink = false;

		this.last_size = new Dimension( 200, 200 );
		this.do_tracking = false;

		setBackground( Color.white );

		MouseENator men = new MouseENator();

		addMouseListener( men );
		addMouseMotionListener( men );

		forceRedraw();
	}

	public void update( Graphics g )
	{
		paint( g );
	}

	public void setSink( boolean sink )
	{
		this.sink = sink;
	}

	public void setTrack( boolean track )
	{
		this.do_tracking = track;
	}

	public void paint( Graphics g )
	{
		buildBackingStore();
		g.drawImage( backing_store, 0, 0, this );
	}
	
	private final void buildBackingStore()
	{
		if( backing_store == null ) {
			Dimension d = getSize();

			backing_store = createImage( d.width, d.height );

			// Since we now have a fresh backing store, redraw everything.
			redraw();
		}
	}

	// Force the whole View to redraw.  Useful when you change
	// the geometry.
	public void forceRedraw()
	{
		// HACK
		// The kosher way to do this would be something simple like
		// 	redraw();	<--- redraw the geometry
		// 	repaint();	<--- schedule a paint event
		// I'm not sure what the problem is, but the window doesn't
		// redraw fast enough when scaling, panning or rotating.  I
		// think maybe the redraw becomes so fast without blowing away
		// the backing store that the AWT thread never has a chance
		// to wake up and process the repaint request.  Since we want 
		// these viewport operations to be truly interactive, we'll 
		// hack the AWT and redraw the darn window manually.
		// I'm not 100% happy about this.  It's just the sort of thing
		// that'll break of some systems.  Test, test, test!!

		redraw();

		// Get the graphics object explicitly and draw the backing store.
		// Alternative: put a 'dirty' flag in GeoView.
		Graphics g = getGraphics();
		if( g != null ) {
			g.drawImage( backing_store, 0, 0, this );
		}
	}

	protected Graphics getBackGraphics()
	{
		return backing_store.getGraphics();
	}

	/*
	 * HACK
	 *
	 * I added this at the last second to fix a mysterious bug in the UI.
	 * Everything worked fine until I got to csk.taprats.ui.RenderPanel.
	 * For some reason, in that class, the GridBagLayout guy decides
	 * to resize the view object (of class csk.taprats.ui.RenderView) down
	 * to (0,0), creating exceptions and general badness.  Recording
	 * an explicit minimum size and preferred size works here -- it forces
	 * the GridBagLayout to accommodate this GeoView.  But it overrides
	 * default behaviours, which are usually cooperative.  I'd like to 
	 * know what I was doing wrong.
	 */

	public void setSize( int w, int h )
	{
		super.setSize( w, h );
		last_size = new Dimension( w, h );
	}

	public Dimension getPreferredSize()
	{
		return last_size;
	}

	public Dimension getMinimumSize()
	{
		return last_size;
	}

	/* END OF HACK */

	public void setBounds( double left, double top, double width )
	{
		this.left = left;
		this.top = top;
		this.width = width;
		this.transform = null;
		forceRedraw();
	}

	public void setTheta( double theta )
	{
		this.theta = theta;
		this.transform = null;
		forceRedraw();
	}

	public boolean redraw()
	{
		if( backing_store == null ) {
			return false;
		}

		computeTransform();

		Graphics g = getBackGraphics();
		Dimension d = getSize();
		g.clearRect( 0, 0, d.width, d.height );

		GeoGraphics g2d = new GeoGraphics( g, transform, this );

		redraw( g2d );

		if( sink ) {
			int w = d.width;
			int h = d.height;

			drawH( w, 0, bevel_h_0, g );
			drawH( w, 1, bevel_h_1, g );
			drawH( w, h - 2, bevel_h_2, g );
			drawH( w, h - 1, bevel_h_3, g );

			drawV( h, 0, bevel_v[0], g );
			drawV( h, 1, bevel_v[1], g );
			drawV( h, w - 2, bevel_v[1], g );
			drawV( h, w - 1, bevel_v[2], g );
		}

		return true;
	}

	protected void redraw( GeoGraphics g2d )
	{
		// Subclasses must override this method to do anything useful.
	}

	protected final void computeTransform()
	{
		if( transform == null ) {
			Dimension d = getSize();

			double wwidth = (double)( d.width );
			double wheight = (double)( d.height );
			double aspect = wwidth / wheight;

			double height = width / aspect;

			Point mid = new Point( wwidth / 2.0, wheight / 2.0 );

			// Hmmmm - because the aspect ratios are the same, I should
			// be able to simply rely on 'matchLineSegments'.

			Transform first = Transform.translate( 
				-left, -( top - height ) );
			Transform second = Transform.scale(
				wwidth / width, -( wheight / height ) );
			Transform third = Transform.translate(
				0.0, wheight );
			Transform fourth = Transform.rotateAroundPoint( mid, theta );

			transform = fourth.compose( 
				third.compose( second.compose( first ) ) );
			inverse = transform.invert();
		}
	}

	public void invalidate()
	{
		super.invalidate();
		backing_store = null;
		transform = null;
	}

	public final double getLeft()
	{
		return left;
	}

	public final double getTop()
	{
		return top;
	}

	public final double getViewWidth()
	{
		return width;
	}

	public final double getTheta()
	{
		return theta;
	}

	public final Polygon getBoundary()
	{
		Dimension d = getSize();

		double wwidth = (double)( d.width );
		double wheight = (double)( d.height );

		Polygon ret = new Polygon( 4 );

		ret.addVertex( inverse.apply( 0.0, 0.0 ) );
		ret.addVertex( inverse.apply( wwidth, 0.0 ) );
		ret.addVertex( inverse.apply( wwidth, wheight ) );
		ret.addVertex( inverse.apply( 0.0, wheight ) );

		return ret;
	}

	public void lookAt( Rect rect )
	{
		// Leave some breathing room around the rectangle to look at.
		// Having the region of interest bleed right to edge of the 
		// window doesn't look too good.

		rect = rect.centralScale( 1.25 );

		Dimension d = getSize();
		Transform Tc = rect.centerInside( new Rect(
			0.0, 0.0, (double)( d.width ), (double)( d.height ) ) );
		Transform inv = Tc.invert();

		Point pt = inv.apply( new Point( 0.0, 0.0 ) );
		Point tr = inv.apply( new Point( (double)( d.width ), 0.0 ) );

		left = pt.getX();
		top = pt.getY();
		width = tr.getX() - pt.getX();

		theta = 0.0;

		transform = null;
		forceRedraw();
	}

	public final Point worldToScreen( Point pt )
	{
		if( transform == null ) {
			computeTransform();
		}
			
		return transform.apply( pt );
	}

	public final Point screenToWorld( Point pt )
	{
		if( transform == null ) {
			computeTransform();
		}

		return inverse.apply( pt );
	}

	public final Point screenToWorld( int x, int y )
	{
		double xx = (double)( x );
		double yy = (double)( y );

		return inverse.apply( new Point( xx, yy ) );
	}

	public final Transform getTransform()
	{
		computeTransform();
		return transform;
	}

	public final Transform getInverseTransform()
	{
		computeTransform();
		return inverse;
	}

	private final void startMove( MouseEvent me )
	{
		last_x = me.getX();
		last_y = me.getY();
	}

	private final void startRotate( MouseEvent me )
	{
		last_x = me.getX();
		last_y = me.getY();
	}

	private final void startScale( MouseEvent me )
	{
		last_x = me.getX();
		last_y = me.getY();
	}

	private final void dragMove( MouseEvent me )
	{
		int x = me.getX();
		int y = me.getY();

		Point from = screenToWorld( last_x, last_y );
		Point to = screenToWorld( x, y );

		left -= (to.getX() - from.getX());
		top -= (to.getY() - from.getY());

		transform = null;
		forceRedraw();

		last_x = x;
		last_y = y;
	}

	private final void dragRotate( MouseEvent me )
	{
		int x = me.getX();
		int y = me.getY();

		Point from = screenToWorld( last_x, last_y );
		Point to = screenToWorld( x, y );

		Dimension d = getSize();
		Point mid = screenToWorld( d.width / 2, d.height / 2 );

		double theta = mid.sweep( to, from );

		this.theta += theta;
		transform = null;
		forceRedraw();

		last_x = x;
		last_y = y;
	}

	private final void dragScale( MouseEvent me )
	{
		int x = me.getX();
		int y = me.getY();

		Point from = screenToWorld( last_x, last_y );
		Point to = screenToWorld( x, y );

		Dimension d = getSize();

		double wwidth = (double)( d.width );
		double wheight = (double)( d.height );
		double aspect = wwidth / wheight;
		double height = width / aspect;

		Point c = new Point( left + width * 0.5, top - height * 0.5 );

		double dfrom = c.dist( from );
		double dto = c.dist( to );

		if( (width * dfrom) < (100.0 * dto) ) {
			double r = dfrom / dto;
			double nw = width * r;
			left = left - (nw-width) * 0.5;
			top = top + (height*r-height) * 0.5;
			width = width * r;

			transform = null;
			forceRedraw();
		}

		last_x = x;
		last_y = y;
	}

	private final void commitMove( MouseEvent me )
	{}

	private final void commitRotate( MouseEvent me )
	{}

	private final void commitScale( MouseEvent me )
	{}

	public static boolean isShift( MouseEvent me )
	{
		return me.isShiftDown();
	}

	public static boolean isButton( MouseEvent me, int i )
	{
		switch( i ) {
		case 2:
			return (me.getModifiers() & me.BUTTON2_MASK) != 0;
		case 3:
			return (me.getModifiers() & me.BUTTON3_MASK) != 0;
		case 1: default:
			// When I tried this under netscape, BUTTON1_MASK never
			// seemed to be set.  So obtain it by elimination instead.
			return 
				(me.getModifiers() & (me.BUTTON2_MASK | me.BUTTON3_MASK)) == 0;
		}
	}

	class MouseENator
		implements MouseListener, MouseMotionListener
	{
		public void mouseClicked( MouseEvent me ) 
		{
			track_x = me.getX();
			track_y = me.getY();
		}

		public void mouseEntered( MouseEvent me ) 
		{
			track_x = me.getX();
			track_y = me.getY();

			/*
			 * FIXME --
			 * When testing the applet on NT, I noticed weird behaviour.  Random
			 * Taprats windows would suddenly pop to the front of the screen as
			 * you move the mouse.  That's because of this call.  When the mouse
			 * enters a GeoView, we ask for focus so that the view can receive
			 * any keyboard events that occur while the mouse is inside it.
			 * But on NT (and some other window managers), requesting focus pops
			 * the window.  Quite unexpected for me since I'm used to the much more sane
			 * focus-follows-pointer.
			 *
			 * So turn off this behaviour by default and let subclasses that use
			 * key events turn it on.
			 */
			if( do_tracking ) {
				requestFocus();
			}
		}
	
		public void mouseExited( MouseEvent me ) 
		{
			track_x = me.getX();
			track_y = me.getY();
		}

		public void mousePressed( MouseEvent me ) 
		{
			track_x = me.getX();
			track_y = me.getY();

			if( isShift( me ) ) {
				if( isButton( me, 1 ) ) {
					startMove( me );
				} else if( isButton( me, 2 ) ) {
					startRotate( me );
				} else if( isButton( me, 3 ) ) {
					startScale( me );
				}
			}
		}

		public void mouseReleased( MouseEvent me ) 
		{
			track_x = me.getX();
			track_y = me.getY();

			if( isShift( me ) ) {
				if( isButton( me, 1 ) ) {
					commitMove( me );
				} else if( isButton( me, 2 ) ) {
					commitRotate( me );
				} else if( isButton( me, 3 ) ) {
					commitScale( me );
				}
			}
		}

		public void mouseMoved( MouseEvent me ) 
		{
			track_x = me.getX();
			track_y = me.getY();
		}

		public void mouseDragged( MouseEvent me )
		{
			track_x = me.getX();
			track_y = me.getY();

			if( isShift( me ) ) {
				if( isButton( me, 1 ) ) {
					dragMove( me );
				} else if( isButton( me, 2 ) ) {
					dragRotate( me );
				} else if( isButton( me, 3 ) ) {
					dragScale( me );
				}
			}
		}
	}

	/*
	 * Some helper data and functions for drawing the bevel around the outside
	 * of the view.
	 */

	private static java.awt.Color[] bevel_h_0 = {
		new Color( 1, 1, 1 ),
		new Color( 1, 1, 1 ),
		new Color( 1, 1, 1 ),
		new Color( 76, 76, 76 ),
		new Color( 103, 103, 103 ) };

	private static java.awt.Color[] bevel_h_1 = {
		new Color( 27, 27, 27 ),
		new Color( 53, 53, 53 ),
		new Color( 78, 78, 78 ),
		new Color( 153, 153, 153 ),
		new Color( 180, 180, 180 ) };

	private static java.awt.Color[] bevel_h_2 = {
		new Color( 127, 127, 127 ),
		new Color( 178, 178, 178 ),
		new Color( 228, 228, 228 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 255, 255 ) };

	private static java.awt.Color[] bevel_h_3 = {
		new Color( 154, 154, 154 ),
		new Color( 205, 205, 205 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 255, 255 ) };

	private static java.awt.Color[] bevel_v = {
		new Color( 52, 52, 52 ),
		new Color( 103, 103, 103 ),
		new Color( 228, 228, 228 ),
		new Color( 255, 255, 255 ) };

	private static final void drawH( int width, int y, java.awt.Color[] cs,
		Graphics g )
	{
		g.setColor( cs[0] );
		g.drawLine( 0, y, 0, y );
		g.setColor( cs[1] );
		g.drawLine( 1, y, 1, y );
		g.setColor( cs[2] );
		g.drawLine( 2, y, width - 2, y );
		g.setColor( cs[3] );
		g.drawLine( width - 2, y, width - 2, y );
		g.setColor( cs[4] );
		g.drawLine( width - 1, y, width - 1, y );
	}

	private static final void drawV( int height, int x, java.awt.Color c,
		Graphics g )
	{
		g.setColor( c );
		g.drawLine( x, 2, x, height - 2 );
	}
}
