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
 * Edge.java
 *
 * The edge component of the planar map abstraction.
 */

package csk.taprats.geometry;

public class Edge
{
	Map 		map;

	Vertex 		v1;
	Vertex 		v2;

	Object 		data;

	Edge( Map map, Vertex v1, Vertex v2 )
	{
		this.map = map;

		this.v1 = v1;
		this.v2 = v2;
	}

	public final Vertex getV1()
	{
		return v1;
	}

	public final Vertex getV2()
	{
		return v2;
	}

	public final Object getData()
	{
		return data;
	}

	public final void setData( Object data )
	{
		this.data = data;
	}

	public final Map getMap()
	{
		return map;
	}

	public final Vertex getOther( Vertex v )
	{
		if( v.equals( v1 ) ) {
			return v2;
		} else {
			return v1;
		}
	}

	/*
	 * Used to sort the edges in the map.
	 */
	public final double getMinX()
	{
		return Math.min( v1.getPosition().getX(), v2.getPosition().getX() );
	}
}
