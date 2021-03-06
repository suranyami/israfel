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
 * Figure.java
 *
 * Making the user interface operate directly on maps would be 
 * a hassle.  Maps are a very low level geometry and topological 
 * structure.  Not so good for interacting with users.  So I
 * define a Figure class, which is a higher level structure -- 
 * an object that knows how to build maps.  Subclasses of Feature
 * understand different ways of bulding maps, but have the advantage
 * of being parameterizable at a high level.
 */

package csk.taprats.app;

import csk.taprats.geometry.Map;

public abstract class Figure
	implements Cloneable
{
	abstract public Object clone();
	abstract public Map getMap();
}
