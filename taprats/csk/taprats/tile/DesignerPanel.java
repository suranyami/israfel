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
 * DesignerPanel.java
 *
 * This file is not part of the applet.  It's a helper application
 * used to design the tilings that are used as skeletons for the
 * Islamic construction process.  It's fairly featureful, much more
 * rapid and accurate than expressing the tilings directly as code,
 * which is what I did in a previous version.  On the flipside, although I 
 * find the UI very intuitive, it's most definitely _not_ meant to
 * be user-friendly in the general sense.  I will refrain from 
 * documenting the interface (for now) on the grounds that it would
 * then force me to redesign this tool to make sense for someone
 * other than me.  In the meantime, the source code is the documentation :)
 *
 * If you really want to try this tool out, just try
 * 		java csk.taprats.tile.DesignerPanel
 */

package csk.taprats.tile;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import csk.taprats.geometry.*;

import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Point;
import csk.taprats.toolkit.GeoView;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.toolkit.WindowCloser;

public class DesignerPanel
	extends FeatureView
{
	private static final int SELECT_NOTHING 	= 0;
	private static final int SELECT_INTERIOR 	= 1;
	private static final int SELECT_EDGE 		= 2;
	private static final int SELECT_VERTEX 		= 3;

	private Vector		features;
	private Hashtable	in_trans;

	private int			selection_id;
	private int			selection_type;
	private int			selection_detail;

	private Vector		accum;

	private static Color	in_t_col;

	private Point 		t1_s;
	private Point 		t1_e;
	private Point 		t2_s;
	private Point 		t2_e;

	public DesignerPanel()
	{
		super( -5.0, 5.0, 10.0 );

		this.features = new Vector();
		this.selection_id = -1;
	
		DesignMouser dm = new DesignMouser();
		addMouseListener( dm );
		addMouseMotionListener( dm );
		addKeyListener( new DesignKey() );

		this.accum = new Vector();
		in_trans = new Hashtable();

		t1_s = null;
		t1_e = null;
		t2_s = null;
		t2_e = null;
	}

	void add( PlacedFeature pf )
	{
		features.addElement( pf );
		forceRedraw();
	}

	void remove( PlacedFeature pf )
	{
		// System.err.println( "remove called" );
		features.removeElement( pf );
		in_trans.remove( pf );
		forceRedraw();
	}

	public void redraw( GeoGraphics g2d )
	{
		for( int idx = 0; idx < features.size(); ++idx ) {
			PlacedFeature pf = getFeature( idx );
			if( in_trans.containsKey( pf ) ) {
				drawFeature( g2d, pf, true, in_t_col );
			} else {
				drawFeature( g2d, pf, true );
			}
		}

		g2d.setColor( Color.green.darker() );

		for( int idx = 1; idx < accum.size(); ++idx ) {
			Point p1 = (Point)( accum.elementAt( idx - 1 ) );
			Point p2 = (Point)( accum.elementAt( idx ) );

			g2d.drawLine( p1, p2 );
		}

		if( (t1_s != null) && (t1_e != null) ) {
			g2d.drawLine( t1_s, t1_e );
		}
		if( (t2_s != null) && (t2_e != null) ) {
			g2d.drawLine( t2_s, t2_e );
		}
	}

	public int numFeatures()
	{
		return features.size();
	}

	public PlacedFeature getFeature( int idx ) 
	{
		return (PlacedFeature)( features.elementAt( idx ) );
	}

	private final Point getTrack()
	{
		return new Point( (double)track_x, (double)track_y );
	}

	private void setStartTranslate( Point pt )
	{
		if( (t1_s != null) && (t1_e != null) ) {
			t2_s = pt;
			t2_e = null;
		} else {
			t1_s = pt;
			t1_e = null;
		}
	}

	private void setEndTranslate( Point pt )
	{
		if( (t1_s != null) && (t1_e != null) ) {
			t2_e = pt;
		} else {
			t1_e = pt;
		}
		forceRedraw();
	}

	private void exportTiling( boolean as_code ) {
		Point t1 = t1_e.subtract( t1_s );
		Point t2 = t2_e.subtract( t2_s );

		Hashtable ht = new Hashtable();
		for( Enumeration k = in_trans.keys(); k.hasMoreElements(); ) {
			PlacedFeature pf = (PlacedFeature)( k.nextElement() );
			Feature f = pf.getFeature();
			if( ht.containsKey( f ) ) {
				Vector v = (Vector)( ht.get( f ) );
				v.addElement( pf );
			} else {
				Vector v = new Vector();
				v.addElement( pf );
				ht.put( f, v );
			}
		}

		if( as_code ) {
			System.out.println( "        beginTiling( \"New_Tiling\" );\n" );
			System.out.println( "        setTranslations(\n" +
				"            new Point( " + 
					t1.getX() + ", " + t1.getY() + " ),\n" +
				"            new Point( " + 
					t2.getX() + ", " + t2.getY() + " ) );\n" );
		} else {
			System.out.println( "tiling No-Name " + ht.size() + '\n' );
			System.out.println( 
				"    " + t1.getX() + ' ' + t1.getY() + '\n' +
				"    " + t2.getX() + ' ' + t2.getY() + '\n' );
		}
			
		for( Enumeration k = ht.keys(); k.hasMoreElements(); ) {
			Feature f = (Feature)( k.nextElement() );
			Vector v = (Vector)( ht.get( f ) ); 
			Point[] pts = f.getPoints();

			if( f.isRegular() ) {
				if( as_code ) {
					System.out.println( "        beginRegularFeature( " + 
						pts.length + " );" );
				} else {
					System.out.println( 
						"        regular " + pts.length + ' ' + v.size() );
				}
			} else {
				if( as_code ) {
					System.out.println( "        beginPolygonFeature( " + 
						pts.length + " );" );
					for( int idx = 0; idx < pts.length; ++idx ) {
						Point p = pts[ idx ];
						System.out.println( 
							"        addPoint( new Point( " + 
								p.getX() + ", " + p.getY() + " ) );" );
					}
					System.out.println( 
						"        commitPolygonFeature();\n" );
				} else {
					System.out.println( 
						"        polygon " + pts.length + ' ' + v.size() );
					for( int idx = 0; idx < pts.length; ++idx ) {
						Point p = pts[ idx ];
						System.out.println( 
							"        " + p.getX() + ' ' + p.getY() );
					}
					System.out.println();
				}
			}
			
			for( int idx = 0; idx < v.size(); ++idx ) {
				double[] ds = new double[6];

				PlacedFeature pf = (PlacedFeature)(v.elementAt( idx ));
				Transform T = pf.getTransform();
				T.get( ds );

				if( as_code ) {
					System.out.println( 
						"        addPlacement( new Transform(\n" +
		"            " + ds[0] + ", " + ds[1] + ", " + ds[2] + ",\n" +
		"            " + ds[3] + ", " + ds[4] + ", " + ds[5] + " ) );" );
				} else {
					System.out.println( 
		"            " + ds[0] + ' ' + ds[1] + ' ' + ds[2] + '\n' +
		"            " + ds[3] + ' ' + ds[4] + ' ' + ds[5] );
				}
			}

			if( as_code ) {
				System.out.println( "        endFeature();\n" );
			}
		}

		if( as_code ) {
			System.out.println( "        endTiling();\n\n" );
		}
	}

	class DesignKey
		extends KeyAdapter
	{
		StringBuffer sb;

		DesignKey()
		{
			sb = new StringBuffer();
		}

		public void keyPressed( KeyEvent e )
		{
			int kc = e.getKeyCode();

			if( kc == KeyEvent.VK_C ) {
				Selection sel = findFeature( getTrack() );
				if( sel != null ) {
					PlacedFeature pf = getFeature( sel.feature );
					add( new PlacedFeature( pf.getFeature(),
						pf.getTransform() ) );
				}
			} else if( kc == KeyEvent.VK_D ) {
				Selection sel = findFeature( getTrack() );
				if( sel != null ) {
					PlacedFeature pf = getFeature( sel.feature );
					remove( pf );
				}
			} else if( kc == KeyEvent.VK_ENTER ) {
				try {
					int i = Integer.parseInt( sb.toString() );
					Feature f = new Feature( i );
					add( new PlacedFeature( f, Transform.IDENTITY ) );
				} catch( Exception ex ) {
				}
				sb = new StringBuffer();
			} else if( (kc >= '0') && (kc <= '9') ) {
				sb.append( (char)kc );
			} else if( kc == KeyEvent.VK_O ) {
				Point t1 = t1_e.subtract( t1_s );
				Point t2 = t2_e.subtract( t2_s );
				Tiling tiling = new Tiling( "New_Tiling", t1, t2 );

				for( Enumeration k = in_trans.keys(); k.hasMoreElements(); ) {
					PlacedFeature pf = (PlacedFeature)( k.nextElement() );
					tiling.add( pf );
				}

				TilingViewer tt = new TilingViewer( tiling );
				tt.setSize( 500, 500 );

				Frame frame = new Frame( "Tile Test" );
				frame.setLayout( new BorderLayout() );
				frame.add( "Center", tt );
				frame.pack();
				frame.show();

				frame.addWindowListener( new WindowCloser( frame, false ) );
			} else if( kc == KeyEvent.VK_T ) {
				Selection sel = findFeature( getTrack() );
				if( sel != null ) {
					PlacedFeature pf = getFeature( sel.feature );
					if( in_trans.containsKey( pf ) ) {
						in_trans.remove( pf );
					} else {
						in_trans.put( pf, pf );
					}
					forceRedraw();
				}
			} else if( kc == KeyEvent.VK_U ) {
				t1_s = null;
				t1_e = null;
				t2_s = null;
				t2_e = null;
				forceRedraw();
			} else if( e.getKeyChar() == 'p' ) {
				exportTiling( true );
			} else if( e.getKeyChar() == 'P' ) {
				exportTiling( false );
			} else {
				sb = new StringBuffer();
				accum = new Vector();
				forceRedraw();
			}
		}
	}

	class DesignMouser
		extends MouseAdapter
		implements MouseMotionListener
	{
		Point last;

		public void mousePressed( MouseEvent e )
		{
			Point spt = new Point( (double)( e.getX() ), (double)( e.getY() ) );
			Point pt = screenToWorld( spt );

			selection_id = -1;
			Selection sel;

			sel = findVertex( spt );
			if( sel != null ) {
				PlacedFeature pf = getFeature( sel.feature );
				Transform T = pf.getTransform();
				Feature f = pf.getFeature();
				Point[] pts = f.getPoints();
				Point pos = T.apply( pts[ sel.detail ] );

				if( isButton( e, 1 ) ) {
					if( (accum.size() > 1) && pos.dist2( 
							(Point)( accum.elementAt( 0 ) ) ) < 1e-10 ) {
						Polygon pgon = new Polygon( accum.size() );
						for( int i = 0; i < accum.size(); ++i ) {
							pgon.addVertex( (Point)( accum.elementAt( i ) ) );
						}
						accum = new Vector();
						add( new PlacedFeature( new Feature( pgon ), 
							Transform.IDENTITY ) );
						selection_id = -1;
					} else {
						accum.addElement( pos );
						forceRedraw();
					}
				} else if( isButton( e, 2 ) ) {
					selection_id = sel.feature;
					selection_type = SELECT_VERTEX;
					selection_detail = sel.detail;
					setStartTranslate( pos );
				}

				return;
			} else {
				accum = new Vector();
				forceRedraw();
			}

			sel = findEdge( spt );
			if( sel != null ) {
				if( isButton( e, 1 ) ) {
					selection_id = sel.feature;
					selection_type = SELECT_EDGE;
					selection_detail = sel.detail;
					return;
				}
			}

			sel = findFeature( spt );
			if( sel != null ) {
				selection_id = sel.feature;
				selection_type = SELECT_INTERIOR;
				selection_detail = -1;

				if( isButton( e, 1 ) ) {
					last = pt;
				} else if( isButton( e, 2 ) ) {
					PlacedFeature pf = getFeature( sel.feature );
					Transform T = pf.getTransform();
					Point tc = T.apply( featureCenter( pf.getFeature() ) );
					
					setStartTranslate( tc );
				} else if( isButton( e, 3 ) ) {
					PlacedFeature pf = getFeature( sel.feature );
					add( new PlacedFeature( pf.getFeature(),
						pf.getTransform() ) );
					// FIXME -- this assumes a certain policy for 'add'.
					// Solution is to make add return the index of the 
					// newly added thing.
					selection_id = numFeatures() - 1;
					last = pt;
				}
			}
		}

		public void mouseReleased( MouseEvent e )
		{
			Point spt = new Point( (double)(e.getX()), (double)(e.getY()) );
			Point pt = screenToWorld( spt );

			if( selection_id >= 0 ) {
				if( isButton( e, 1 ) ) {
					PlacedFeature pf = getFeature( selection_id );
					Transform T = pf.getTransform();
					Feature f = pf.getFeature();
					Point[] pts = f.getPoints();

					if( selection_type == SELECT_EDGE ) {
						Selection sel = findEdge( spt );
						if( sel != null ) {
							PlacedFeature pfo = getFeature( sel.feature );
							Transform To = pfo.getTransform();
							Feature fo = pfo.getFeature();
							Point[] ptso = fo.getPoints();

							if( sel.feature != selection_id ) {
								Point pfrom = pts[ selection_detail ];
								Point pto = pts[ (selection_detail + 1) % 
									pts.length ];

								Point qfrom = ptso[ sel.detail ];
								Point qto = 
									ptso[ (sel.detail + 1) % ptso.length ];

								Transform carry = To.compose( 
									Transform.matchTwoSegments(
										pfrom, pto, qto, qfrom ) );
								
								pf.setTransform( carry );
								forceRedraw();
							}
						}
					}
				} else if( isButton( e, 2 ) ) {
					Selection sel = findVertex( spt );
					if( sel != null ) {
						PlacedFeature pf = getFeature( sel.feature );
						Transform T = pf.getTransform();
						Feature f = pf.getFeature();
						Point[] pts = f.getPoints();
						Point pos = T.apply( pts[ sel.detail ] );

						setEndTranslate( pos );
					} else {
						sel = findFeature( spt );
						if( sel != null ) {
							PlacedFeature pf = getFeature( sel.feature );
							Transform T = pf.getTransform();
							Point tc = 
								T.apply( featureCenter( pf.getFeature() ) );
								
							setEndTranslate( tc );
						}
					}
				}

				selection_id = -1;
			}
		}

		public void mouseDragged( MouseEvent e )
		{
			if( selection_id >= 0 ) {
				if( isButton( e, 1 ) || isButton( e, 3 ) ) {
					if( selection_type == SELECT_INTERIOR ) {
						Point w = screenToWorld( 
							new Point( (double)e.getX(), (double)e.getY() ) );
						PlacedFeature pf = getFeature( selection_id );
						
						Point diff = w.subtract( last );
						pf.setTransform( Transform.translate( diff ).compose(
							pf.getTransform() ) );
						forceRedraw();
						last = w;
					}
				}
			}
		}

		public void mouseMoved( MouseEvent e )
		{
		}
	}

	static {
		in_t_col = new Color( 1.0f, 0.85f, 0.85f );
	}

	public static final void main( String[] args )
	{
		DesignerPanel dp = new DesignerPanel();
		dp.setSize( 500, 400 );

		if( args.length == 1 ) {
			Tiling tiling = Tiling.find( args[ 0 ] );
			if( tiling == null ) {
				System.err.println( "No such tiling." );
				System.exit( -1 );
			} else {
				for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
					dp.add( tiling.getFeature( idx ) );
				}
			}
		}

		Frame frame = new Frame( "Tiling Designer" );
		frame.setLayout( new BorderLayout() );
		frame.add( "Center", dp );
		frame.pack();
		frame.show();
	}
}
