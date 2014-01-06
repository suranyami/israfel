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
 * Loose.java
 *
 * A bunch of fuzzy comparisons that have a margin of error.
 * Useful whenever you're doing computational geometry.
 */

package csk.taprats.general;

import csk.taprats.geometry.Point;

public class Loose
{
	public static final double TOL = 1e-7;
	public static final double TOL2 = 1e-10;

	public static final boolean equals( double a, double b )
	{
		return Math.abs( a - b ) < TOL;
	}

	public static final boolean zero( double a )
	{
		return Math.abs( a ) < TOL;
	}

	public static final boolean lessThan( double a, double b )
	{
		return a < (b + TOL);
	}

	public static final boolean greaterThan( double a, double b )
	{
		return a > (b - TOL);
	}

	public static final boolean equals( Point a, Point b )
	{
		return a.dist2( b ) < TOL2;
	}

	public static final boolean zero( Point a )
	{
		return a.mag2() < TOL2;
	}
}
