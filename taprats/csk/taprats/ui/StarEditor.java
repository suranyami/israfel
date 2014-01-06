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
 * StarEditor.java
 *
 * The controls for editing a Star.  Glue code, just like RosetteEditor.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;

import csk.taprats.toolkit.Slider;
import csk.taprats.app.*;

public class StarEditor
	extends FigureEditor
{
	private Star			star;

	private Slider			d;
	private Slider			s;

	public StarEditor( DesignEditor editor )
	{
		this( editor, null );
	}

	public StarEditor( DesignEditor editor, Star star )
	{
		super( editor );

		Observer obs = (new Observer() {
			public void update( Observable obj, Object arg )
			{
				updateGeometry();
			}
		} );

		d = new Slider( "D", 0.0, 0.0, 1.0 );
		s = new Slider( "S", 0.0, 0.0, 1.0 );
		s.setIntegral( true );

		GridBagLayout layout = new GridBagLayout();
		setLayout( layout );

		d.insert( this, layout, 0, 0 );
		s.insert( this, layout, 0, 1 );

		d.value_changed.addObserver( obs );
		s.value_changed.addObserver( obs );

		setStar( this.star );
	}

	public Figure getFigure()
	{
		return star;
	}

	public void setStar( Star star )
	{
		this.star = star;

		if( this.star != null ) {
			int n = star.getN();
			double d = star.getD();
			int s = star.getS();
			double dmax = 0.5 * (double)n;

			this.d.setValues( d, 1.0, dmax, false );
			this.s.setValues( (double)s, 1.0, dmax, false );
		}
	}

	public void resetWithFigure( Figure figure )
	{
		setStar( (Star)figure );
	}

	private void updateGeometry()
	{
		if( star != null ) {
			double dval = d.getValue();
			int sval = (int)( s.getValue() );

			star.setD( dval );
			star.setS( sval );

			changed();
		}
	}
}
