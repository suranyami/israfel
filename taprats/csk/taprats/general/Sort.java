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
 * Sort.java
 *
 * An algorithm for sorting vectors of objects.  Taken directly from the 
 * quicksort pseudocode on page 154 of CLR (The White Book).  Comes complete
 * with a little test program that sorts vectors of integers.
 *
 */

package csk.taprats.general;

import java.util.Vector;
import java.io.*;

public class Sort
{
	public static void quickSort( Vector v, int i1, int i2, Comparison comp )
	{
		if( i1 < i2 ) {
			int q = partition( v, i1, i2, comp );
			quickSort( v, i1, q, comp );
			quickSort( v, q + 1, i2, comp );
		}
	}

	private static int partition( Vector v, int i1, int i2, Comparison comp )
	{
		Object x = v.elementAt( i1 );
		int i = i1 - 1;
		int j = i2 + 1;

		while( true ) {
			do --j; while( comp.compare( v.elementAt( j ), x ) > 0 );
			do ++i; while( comp.compare( v.elementAt( i ), x ) < 0 );

			if( i < j ) {
				Object t1 = v.elementAt( i );
				Object t2 = v.elementAt( j );
				v.setElementAt( t1, j );
				v.setElementAt( t2, i );
			} else {
				return j;
			}
		}
	}

	public static final void main( String[] args )
		throws java.io.IOException
	{
		Vector v = new Vector();
		Comparison c = new Comparison() {
			public int compare( Object a, Object b )
			{
				return ((Integer)a).intValue() - ((Integer)b).intValue();
			}
		};

		StreamTokenizer st = new StreamTokenizer( 
			new InputStreamReader( System.in ) );
		while( true ) {
			int tt = st.nextToken();
			if( tt == st.TT_EOF ) {
				break;
			}
			v.addElement( new Integer( (int)( st.nval ) ) );
		}

		quickSort( v, 0, v.size() - 1, c );

		for( int idx = 0; idx < v.size(); ++idx ) {
			System.out.println( v.elementAt( idx ) );
		}
	}
}
