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
 * ExplicitFigure.java
 *
 * A variety of Figure which contains an explicit map, which is
 * simple returned when the figure is asked for its map.
 */

package csk.taprats.app;

import csk.taprats.geometry.*;

public class ExplicitFigure 
	extends Figure
{
	private Map map;

	public ExplicitFigure( Map map )
	{
		this.map = map;
	}

	public Object clone()
	{
		return new ExplicitFigure( (Map)( map.clone() ) );
	}

	public Map getMap()
	{
		// FIXME -- does this need to be cloned?
		return (Map)( map.clone() );
	}
}
