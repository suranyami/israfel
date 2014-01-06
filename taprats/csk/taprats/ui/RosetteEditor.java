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
 * RosetteEditor.java
 *
 * The controls for editing a Rosette.  This is just some glue code for
 * creating some sliders and transferring the settings between the sliders
 * and a Rosette object.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;

import csk.taprats.toolkit.Slider;
import csk.taprats.app.*;

public class RosetteEditor
	extends FigureEditor
{
	private Rosette			rosette;

	private Slider			q;
	private Slider			s;

	public RosetteEditor( DesignEditor editor )
	{
		this( editor, null );
	}

	public RosetteEditor( DesignEditor editor, Rosette rosette )
	{
		super( editor );

		Observer obs = (new Observer() {
			public void update( Observable obs, Object arg )
			{
				updateGeometry();
			}
		}); 

		q = new Slider( "Q", 0.0, 0.0, 1.0 );
		s = new Slider( "S", 0.0, 0.0, 1.0 );
		s.setIntegral( true );

		GridBagLayout layout = new GridBagLayout();
		setLayout( layout );
		q.insert( this, layout, 0, 0 );
		s.insert( this, layout, 0, 1 );

		q.value_changed.addObserver( obs );
		s.value_changed.addObserver( obs );
		
		setRosette( rosette );
	}

	public Figure getFigure()
	{
		return rosette;
	}

	public void setRosette( Rosette rosette )
	{
		this.rosette = rosette;

		if( this.rosette != null ) {
			int n = rosette.getN();
			double q = rosette.getQ();
			int s = rosette.getS();

			this.q.setValues( q, -1.0, 1.0, false );
			this.s.setValues( s, 1.0, (double)((n-1)/2-1), false );
		}
	}

	public void resetWithFigure( Figure figure )
	{
		setRosette( (Rosette)figure );
	}

	private void updateGeometry()
	{
		if( rosette != null ) {
			double qval = q.getValue();
			int sval = (int)( s.getValue() );

			rosette.setQ( qval );
			rosette.setS( sval );

			changed();
		}
	}
}
