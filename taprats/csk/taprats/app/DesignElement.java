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
 * DesignElement.java
 *
 * A DesignElement is the core of the process of building a finished design.
 * It's a Feature together with a Figure.  The Feature comes from the
 * tile library and will be used to determine where to place copies of the
 * Figure, which is designed by the user.
 */

package csk.taprats.app;

import csk.taprats.tile.*;
import csk.taprats.geometry.*;

public class DesignElement
	implements Cloneable
{
	private Feature 	feature;
	private Figure		figure;

	public DesignElement( Feature feature, Figure figure )
	{
		this.feature = feature;
		this.figure = figure;
	}

	public DesignElement( Feature feature )
	{
		this.feature = feature;
		if( feature.isRegular() && (feature.numPoints() > 4) ) {
			this.figure = new Rosette( feature.numPoints(), 0.0, 3 );
		} else {
			this.figure = new ExplicitFigure( new Map() );
		}
	}

	public Object clone()
	{
		return new DesignElement( feature, (Figure)( figure.clone() ) );
	}

	public Feature getFeature()
	{
		return feature;
	}

	public Figure getFigure()
	{
		return figure;
	}

	public void setFigure( Figure figure )
	{
		this.figure = figure;
	}
}
