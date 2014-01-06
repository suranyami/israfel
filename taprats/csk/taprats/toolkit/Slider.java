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
 * Slider.java
 *
 * Implement the abstraction of a data slider, which has a label,
 * the slider itself, and a text box.  The text box displays the 
 * value of the slider, and new values can be typed directly into
 * the box.
 *
 * You don't want to pack the whole slider into a panel because 
 * you might want to stack several sliders vertically and have them
 * align without setting widths explicitly.  The solution is 
 * to provide a method to add the three slider components to a
 * container equipped with a GridBagLayout.  You specify the
 * x,y origin of the slider and it adds it in.
 */

package csk.taprats.toolkit;

import java.awt.*;
import java.awt.event.*;

import csk.taprats.general.Signal;

public class Slider
{
	private	Label			name;
	private Scrollbar 		scroll;
	private TextField		field;

	private double 			min;
	private double			max;
	private double 			val;

	private boolean 		integral;

	public Signal			value_changed;

	public Slider( String name, double init, double min, double max )
	{
		this.val = init;
		this.min = min;
		this.max = max;

		this.integral = false;

		this.name = new Label( name );
		this.field = new TextField( ftoa( init ), 7 );
		this.scroll = new Scrollbar( Scrollbar.HORIZONTAL,
			getSlideLoc( init ), 1, 0, 257 );

		this.value_changed = new Signal();

		scroll.addAdjustmentListener( new AdjustmentListener() {
			public void adjustmentValueChanged( AdjustmentEvent ae )
			{
				updateFromSlider();
			}
		} );

		field.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae )
			{
				updateFromField();
			}
		} );
	}

	private void updateFromSlider()
	{
		double v = getValue( scroll.getValue() );
		field.setText( ftoa( v ) );
		val = v;

		value_changed.notify( new Double( val ) );
	}

	private void updateFromField()
	{
		try {
			double v = 0.0;
			
			if( integral ) {
				v = (double)( Integer.parseInt( field.getText() ) );
			} else {
				v = (new Double( field.getText() )).doubleValue();
			}

			if( v < min ) {
				v = min;
				field.setText( ftoa( v ) );
			} else if( v > max ) {
				v = max;
				field.setText( ftoa( v ) );
			}

			scroll.setValue( getSlideLoc( v ) );
			val = v;
			value_changed.notify( new Double( v ) );
		} catch( NumberFormatException nfe ) {
			field.setText( ftoa( val ) );
		}
	}

	/*
	 * Turn a floating-point value into a string, truncating to something
	 * like "%.3f" in C.
	 */
	private String ftoa( double d )
	{
		if( integral ) {
			return String.valueOf( (int)d );
		} else {
			String str = String.valueOf( d );
			int i = str.lastIndexOf( '.' );
			if( i != -1 ) {
				int declen = str.length() - (i+1);
				if( declen > 3 ) {
					str = str.substring( 0, i + 4 );
				}
			}

			return str;
		}
	}

	private int getSlideLoc( double v )
	{
		return (int)( 256.0 * ( v - min ) / ( max - min ) );
	}

	private double getValue( int loc )
	{
		return min + ((double)loc / 256.0) * ( max - min );
	}

	public double getValue()
	{
		return val;
	}

	public void setValue( double v, boolean propagate )
	{
		if( v < min ) {
			v = min;
		} else if( v > max ) {
			v = max;
		}

		scroll.setValue( getSlideLoc( v ) );
		field.setText( ftoa( v ) );
		val = v;

		if( propagate ) {
			value_changed.notify( new Double( v ) );
		}
	}

	public void setValue( double v )
	{
		setValue( v, true );
	}

	public void setValues( double v, double min, double max, boolean propagate )
	{
		this.min = min;
		this.max = max;
		setValue( v, propagate );
	}

	public void setValues( double v, double min, double max )
	{
		setValues( v, min, max, true );
	}

	/*
	 * Makes the values of the slider appear in the UI as integers.
	 */
	public void setIntegral( boolean integral )
	{
		this.integral = integral;
	}

	public void insert( Container cont, GridBagLayout gbl, int x, int y )
	{
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;

		gbl.setConstraints( name, gbc );
		cont.add( name );

		gbc.gridx = x + 1;
		gbc.weightx = 10;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbl.setConstraints( scroll, gbc );
		cont.add( scroll );

		gbc.gridx = x + 2;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;

		gbl.setConstraints( field, gbc );
		cont.add( field );
	}
}
