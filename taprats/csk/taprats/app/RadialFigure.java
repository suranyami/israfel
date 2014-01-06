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
 * RadialFigure.java
 *
 * A RadialFigure is a special kind of Figure that has d_n symmetry.  That
 * means that it can be rotated by 360/n degrees and flipped across certain
 * lines through the origin and it looks the same.
 *
 * We take advantage of this by only making subclasses produce a basic
 * unit, i.e. a smaller map that generates the complete figure through the
 * action of c_n (just the rotations; the reflections are factored in
 * by subclasses).
 */

package csk.taprats.app;

import java.util.Vector;

import csk.taprats.geometry.*;

public abstract class RadialFigure
	extends Figure
	implements Cloneable
{
	protected int 			n;
	protected double		dn;
	protected double 		don;
	protected Transform 	Tr;

	protected double 		time;

	protected RadialFigure( int n )
	{
		this.n = n;
		this.dn = (double)n;
		this.don = 1.0 / this.dn;
		this.Tr = Transform.rotate( 2.0 * Math.PI * don );

		this.time = 0.0;
	}

	abstract public Object clone();

	// Get the point frac of the way around the unit circle.
	protected static Point getArc( double frac )
	{
		double ang = frac * 2.0 * Math.PI;
		return new Point( Math.cos( ang ), Math.sin( ang ) );
	}

	// Subclasses provide a method for getting the basic unit.
	abstract public Map buildUnit();

	// Apply c_n to get a complete map from unit.
	protected Map getMap( Map unit )
	{
		Map ret = new Map();

		Vector transforms = new Vector( n );
		Transform base = (Transform)Tr.clone();
		for( int idx = 0; idx < n; ++idx ) {
			transforms.addElement( base );
			base = base.compose( Tr );
		}

		ret.mergeSimpleMany( unit, transforms );

		return ret;
	}

	public Map getMap()
	{
		Map unit = buildUnit();
		return getMap( unit );
	}

	public int getN()
	{
		return n;
	}
}
