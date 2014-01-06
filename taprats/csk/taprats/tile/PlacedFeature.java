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
 * PlacedFeature.java
 *
 * A PlacedFeature is a Feature together with a transform matrix.
 * It allows us to share an underlying feature while putting several
 * copies together into a tiling.  A tiling is represented as a 
 * collection of PlacedFeatures (that may share Features) that together
 * make up a translational unit.
 */

package csk.taprats.tile;

import csk.taprats.geometry.Transform;

public class PlacedFeature
{
	private Feature 		feature;
	private Transform		T;

	PlacedFeature( Feature feature, Transform T )
	{
		this.feature = feature;
		this.T = T;
	}

	public final Feature getFeature()
	{
		return feature;
	}

	public final Transform getTransform()
	{
		return T;
	}

	public final void setTransform( Transform T )
	{
		this.T = T;
	}
}
