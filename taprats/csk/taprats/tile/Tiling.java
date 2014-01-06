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
 * Tiling.java
 *
 * The representation of a tiling, which will serve as the skeleton for
 * Islamic designs.  A Tiling has two translation vectors and a set of
 * PlacedFeatures that make up a translational unit.  The idea is that
 * the whole tiling can be replicated across the plane by placing
 * a copy of the translational unit at every integer linear combination
 * of the translation vectors.  In practice, we only draw at those
 * linear combinations within some viewport.
 *
 * This class also has a whole pile of static helper methods for building
 * Tiling objects, and possibly placing them in a static dictionary of
 * built-in tilings.  The reason the helper functions may seem a bit
 * more complicated than necessary is that the code that creates the 
 * built-in tilings (in the static {} block at the bottom of the file) is 
 * generated automatically from the DesignerPanel tool.  The helper
 * functions help simplify the automatic code generation.
 *
 * The helper functions are also used by a system for reading tilings from
 * a file.  This could be useful if I decide to maintain a library of
 * tilings on the server side and serve them up dynamically.
 */

package csk.taprats.tile;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;

import csk.taprats.geometry.*;

public class Tiling
{
	private Point	t1;
	private Point	t2;

	private Vector/*of PlacedFeature*/	features;

	private String	name;
	private String 	desc;

	public Tiling( String name, Point t1, Point t2 )
	{
		this.t1 = t1;
		this.t2 = t2;
		this.features = new Vector();
		this.name = name;
		this.desc = null;
	}

	public void setDescription( String desc )
	{
		this.desc = desc;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return desc;
	}

	public void add( PlacedFeature pf ) 
	{
		features.addElement( pf );
	}

	public void add( Feature f, Transform T )
	{
		add( new PlacedFeature( f, T ) );
	}

	public final int numFeatures()
	{
		return features.size();
	}

	public final PlacedFeature getFeature( int n )
	{
		return (PlacedFeature)( features.elementAt( n ) );
	}

	public final Point getTrans1()
	{
		return t1;
	}

	public final Point getTrans2()
	{
		return t2;
	}

	/*
	 * The built-in library of tiling types.
	 */
	public static Hashtable 	builtins;

	public static Tiling find( String name )
	{
		if( builtins.containsKey( name ) ) {
			return (Tiling)( builtins.get( name ) );
		} else {
			return null;
		}
	}

	public static final int numTilings()
	{
		return builtins.size();
	}

	public static final Enumeration getTilingNames()
	{
		return builtins.keys();
	}

	public static final Enumeration getTilings()
	{
		return builtins.elements();
	}

	//
	// The helper functions for constructing tilings.
	//

	private static String 		b_name;
	private static String		b_desc;
	private static Point 		b_t1;
	private static Point 		b_t2;
	private static Vector 		b_pfs;
	private static Feature 		b_f;
	private static Point[] 		b_pts;
	private static int 			b_ct;

	private static void beginTiling( String name )
	{
		b_t1 = null;
		b_t2 = null;
		b_pts = null;
		b_f = null;
		b_pfs = new Vector();
		b_desc = null;

		b_name = name;
	}

	private static void setTranslations( Point t1, Point t2 )
	{
		b_t1 = t1;
		b_t2 = t2;
	}

	private static void beginPolygonFeature( int n )
	{
		b_pts = new Point[ n ];
		b_ct = 0;
	}

	private static void addPoint( Point pt ) 
	{
		b_pts[ b_ct ] = pt;
		++b_ct;
	}

	private static void commitPolygonFeature()
	{
		b_f = new Feature( b_pts );
	}

	private static void addPlacement( Transform T )
	{
		b_pfs.addElement( new PlacedFeature( b_f, T ) );
	}

    private static void endFeature()
	{
		// Don't really need to do anything.
		b_f = null;
		b_pts = null;
		b_ct = 0;
	}

	private static void beginRegularFeature( int n )
	{
		b_f = new Feature( n );
	}

	private static void endTiling()
	{
		Tiling tiling = new Tiling( b_name, b_t1, b_t2 );
		for( int idx = 0; idx < b_pfs.size(); ++idx ) {
			tiling.add( (PlacedFeature)( b_pfs.elementAt( idx ) ) ); 
		}

		if( b_desc != null ) {
			tiling.setDescription( b_desc );
		}

		builtins.put( b_name, tiling );
	}

	//
	// Functions for reading tilings in from files.  No real error-checking
	// is done, because I don't expect you to write these files by hand --
	// they should be auto-generated from DesignerPanel.
	//

	private static final double nextDouble( StreamTokenizer st )
		throws IOException
	{
		st.nextToken();
		return (new Double( st.sval )).doubleValue();
	}

	public static Tiling readTiling( Reader r )
		throws IOException
	{
		StreamTokenizer st = new StreamTokenizer( r );

		// reset the tokenizer to not recognize numbers, since its
		// handling of numbers is impovrished.  We can't just call
		// wordChars(): in StreamTokenizer.java, you can see that
		// 'being in a word' and 'being in a number' are _independent_ (!)
		// So first turn off everything using ordinaryChars()
		st.ordinaryChars( '0', '9' );
		st.wordChars( '0', '9' );
		st.ordinaryChar( '.' );
		st.wordChars( '.', '.' );
		st.ordinaryChar( '-' );
		st.wordChars( '-', '-' );

		// Turn on all possible commenting styles.  The actual syntax
		// of the file is trivial, so there's no reason not to give
		// everyone their favorite commenting style.
		st.commentChar( '#' );
		st.commentChar( '%' );
		st.slashSlashComments( true );
		st.slashStarComments( true );

		st.nextToken();
		if( !st.sval.equals( "tiling" ) ) {
			System.err.println( "This isn't a tiling file." );
			return null;
		}
		st.nextToken();
		beginTiling( st.sval );

		st.nextToken();
		int nf = Integer.parseInt( st.sval );

		double x1 = nextDouble( st );
		double y1 = nextDouble( st );
		double x2 = nextDouble( st );
		double y2 = nextDouble( st );

		setTranslations( new Point( x1, y1 ), new Point( x2, y2 ) );

		for( int idx = 0; idx < nf; ++idx ) {
			st.nextToken();
			boolean reg = false;
			if( st.sval.equals( "regular" ) ) {
				reg = true;
			}

			st.nextToken();
			int num_sides = Integer.parseInt( st.sval );
			st.nextToken();
			int num_xforms = Integer.parseInt( st.sval );

			if( reg ) {
				beginRegularFeature( num_sides );
			} else {
				beginPolygonFeature( num_sides );
				for( int v = 0; v < num_sides; ++v ) {
					x1 = nextDouble( st );
					y1 = nextDouble( st );
					addPoint( new Point( x1, y1 ) );
				}
				commitPolygonFeature();
			}

			for( int jdx = 0; jdx < num_xforms; ++jdx ) {
				double a = nextDouble( st );
				double b = nextDouble( st );
				double c = nextDouble( st );
				double d = nextDouble( st );
				double e = nextDouble( st );
				double f = nextDouble( st );

				addPlacement( new Transform( a, b, c, d, e, f ) );
			}
			endFeature();
		}

		Tiling tiling = new Tiling( b_name, b_t1, b_t2 );
		for( int idx = 0; idx < b_pfs.size(); ++idx ) {
			tiling.add( (PlacedFeature)( b_pfs.elementAt( idx ) ) ); 
		}

		return tiling;
	}

	// The code to create the built-in tiling types.

	static {
		try {
		builtins = new Hashtable();

        beginTiling( "csk_7" );

        setTranslations(
            new Point( -3.3375233734285267E-16, 3.899711648727295 ),
            new Point( 4.89008373582526, -1.8488927466117464E-32 ) );

        beginPolygonFeature( 8 );
        addPoint( new Point( 1.0, 0.48157461880752866 ) );
        addPoint( new Point( 1.7530203962825335, 1.082088346128531 ) );
        addPoint( new Point( 1.3351256037378867, 1.949855824363648 ) );
        addPoint( new Point( 1.753020396282533, 2.817623302598764 ) );
        addPoint( new Point( 0.9999999999999999, 3.418137029919767 ) );
        addPoint( new Point( 0.2469796037174669, 2.817623302598764 ) );
        addPoint( new Point( 0.6648743962621135, 1.9498558243636477 ) );
        addPoint( new Point( 0.24697960371746702, 1.082088346128531 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
            1.0000000000000002, 1.6653345369377348E-16, 2.4450418679126296,
            -1.6653345369377348E-16, 1.0000000000000002, -1.949855824363648 ) );        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 7 );
        addPlacement( new Transform(
            0.9009688679024195, 0.43388373911755823, 2.0000000000000004,
            -0.43388373911755823, 0.9009688679024195, -2.220446049250313E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.9009688679024193, 0.43388373911755795, -0.44504186791262884,
            -0.43388373911755795, 0.9009688679024193, 1.9498558243636475 ) );
        addPlacement( new Transform(
            0.623489801858734, 0.7818314824680299, 2.4450418679126296,
            -0.7818314824680299, 0.623489801858734, 1.949855824363648 ) );
        endFeature();

		b_desc = 
			"A novel tiling based on an arrangement of heptagons with " +
			"octagonal regions filling in the gaps.  This tiling would " +
			"probably not have been used by Islamic artisans.";

        endTiling();


        beginTiling( "4.8^2" );

        setTranslations(
            new Point( 1.6653345369377358E-16, 1.9999999999999987 ),
            new Point( 2.000000000000001, -4.996003610813204E-16 ) );

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            0.29289321881345176, -0.29289321881345187, 0.9999999999999989,
            0.29289321881345187, 0.29289321881345176, 0.9999999999999999 ) );
        endFeature();

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"The classic Archimedean tiling by octagons and squares. " +
			"Extremely important in Islamic design as the source of the " +
			"Seal of Solomon, the [8/3]2 star.";

        endTiling();


        beginTiling( "10" );

        setTranslations(
            new Point( 2.0, -5.551115123125783E-17 ),
            new Point( 0.6180339887498949, 1.9021130325903073 ) );

        beginPolygonFeature( 6 );
        addPoint( new Point( 0.9999999999999996, 0.32491969623290634 ) );
        addPoint( new Point( 1.381966011250105, 0.8506508083520401 ) );
        addPoint( new Point( 2.0, 1.0514622242382679 ) );
        addPoint( new Point( 1.6180339887498947, 1.5771933363574013 ) );
        addPoint( new Point( 1.2360679774997896, 1.0514622242382676 ) );
        addPoint( new Point( 0.6180339887498947, 0.8506508083520403 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 10 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"A tiling by regular decagons and hexagonal fillers that " +
			"gives rise to one of the most famous of all Islamic patterns.";

        endTiling();


        beginTiling( "6" );

        setTranslations(
            new Point( 2.0, -5.551115123125783E-16 ),
            new Point( 1.0, 1.7320508075688767 ) );

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = "The regular tiling by hexagons.";

        endTiling();


        beginTiling( "4.6.12" );

        setTranslations(
            new Point( 5.265320091679377E-16, 2.5358983848622456 ),
            new Point( 2.1961524227066316, 1.2679491924311224 ) );

        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.46410161513775433, -1.1102230246251565E-16, 0.7320508075688772,
            1.1102230246251565E-16, 0.46410161513775433, 1.2679491924311224 ) );        addPlacement( new Transform(
            0.2320508075688775, 0.4019237886466846, 1.4641016151377548,
            -0.4019237886466846, 0.2320508075688775, -6.661338147750939E-16 ) );        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginRegularFeature( 4 );
        addPlacement( new Transform(
            0.13397459621556135, 0.23205080756887725, 1.098076211353316,
            -0.23205080756887725, 0.13397459621556135, 0.6339745962155612 ) );
        addPlacement( new Transform(
            0.2679491924311227, -3.985521342092001E-18, 2.220446049250313E-16,
            3.985521342092001E-18, 0.2679491924311227, 1.2679491924311226 ) );
        addPlacement( new Transform(
            -0.13397459621556124, 0.23205080756887697, 1.0980762113533153,
            -0.23205080756887697, -0.13397459621556124, -0.6339745962155618 ) );        endFeature();

		b_desc = 
			"Another of the semiregular, or Archimedean tilings, " + 
			"4.6.12 is sometimes encountered as an Islamic design " +
			"as is, without any further embellishment.";

        endTiling();

        beginTiling( "3.12^2" );

        setTranslations(
            new Point( 0, 2 ),
            new Point( 1.7320508075688772, 1 ) );

        beginRegularFeature( 3 );
        addPlacement( new Transform(
            0.07735026918962594, -0.13397459621556157, 1.1547005383792515,
            0.13397459621556157, 0.07735026918962594, 0 ) );
        addPlacement( new Transform(
            0.15470053837925152, 0, 0.5773502691896257,
            0, 0.15470053837925152, 1 ) );
        endFeature();

        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"The Archimedean tiling by dodecagons and triangles. " +
			"Very common in the construction of Islamic designs.";

        endTiling();
		
/*
		* Since we always infer on squares, this tiling is pretty useless.
		* Whatever.

        beginTiling( "4^4" );
 
        setTranslations(
            new Point( 2.220446049250313E-16, 1.9999999999999998 ),
            new Point( 2.0, -2.220446049250313E-16 ) );
 
        beginRegularFeature( 4 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = "The plain old tiling by squares.";
 
        endTiling();
*/

        beginTiling( "csk_5" );
        setTranslations(
            new Point( 2.2204460492503126E-16, 2.3511410091698925 ),
            new Point( 3.618033988749896, 1.1755705045849458 ) );
 
        beginRegularFeature( 5 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.8090169943749477, -0.5877852522924732, 2.0,
            0.5877852522924732, 0.8090169943749477, -3.3306690738754696E-16 ) );
        endFeature();
 
        beginPolygonFeature( 4 );
        addPoint( new Point( -0.38196601125010476, 1.1755705045849458 ) );
        addPoint( new Point( 0.9999999999999998, 0.7265425280053609 ) );
        addPoint( new Point( 2.3819660112501055, 1.1755705045849463 ) );
        addPoint( new Point( 0.9999999999999991, 1.624598481164532 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"A tiling by pentagons and rhombs derived from the example " +
			"in figure 1.5.11 of Grunbaum and Shephard.";
 
        endTiling();


        beginTiling( "csk_9" );
 
        setTranslations(
            new Point( -8.870997979654522E-16, 3.464101615137757 ),
            new Point( 3.0000000000000027, 1.7320508075688774 ) );
 
        beginRegularFeature( 6 );
        addPlacement( new Transform(
            0.701866670643066, -0.4052229112310017, 1.0000000000000018,
            0.4052229112310017, 0.701866670643066, 1.732050807568877 ) );
        endFeature();
 
        beginPolygonFeature( 3 );
        addPoint( new Point( 0.5320888862379561, 0.9216049851068762 ) );
        addPoint( new Point( 1.0000000000000002, 0.3639702342662023 ) );
        addPoint( new Point( 1.4679111137620449, 0.9216049851068764 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            0.5000000000000002, 0.8660254037844377, -0.9999999999999998,
            -0.8660254037844377, 0.5000000000000002, 1.7320508075688763 ) );
        addPlacement( new Transform(
            -0.5000000000000007, -0.8660254037844385, 3.0000000000000027,
            0.8660254037844385, -0.5000000000000007, 1.732050807568878 ) );
        addPlacement( new Transform(
            -1.000000000000001, 4.440892098500626E-16, 2.000000000000002,
            -4.440892098500626E-16, -1.000000000000001, 3.464101615137757 ) );
        addPlacement( new Transform(
            0.4999999999999999, -0.8660254037844388, 2.0000000000000018,
            0.8660254037844388, 0.4999999999999999, -2.220446049250313E-16 ) );
        addPlacement( new Transform(
            -0.5000000000000013, 0.8660254037844404, -8.881784197001252E-16,
            -0.8660254037844404, -0.5000000000000013, 3.4641016151377575 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginRegularFeature( 9 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.9396926207859095, -0.3420201433256686, 2.000000000000001,
            0.3420201433256686, 0.9396926207859095, -1.0547118733938987E-15 ) );
        endFeature();
 
		b_desc = 
			"A really unusual base for an Islamic design, derived from " +
			"the star tiling in figure 2.5.4(m) of Grunbaum and Shephard. " +
			"Probably wouldn't appear in historial work because the regular " +
			"polygons don't line up edge-to-edge.";

        endTiling();

        beginTiling( "18" );
 
        setTranslations(
            new Point( 0.3472963553338614, 1.969615506024418 ),
            new Point( 1.8793852415718204, 0.6840402866513363 ) );
 
        beginPolygonFeature( 6 );
        addPoint( new Point( -0.6527036446661408, 0.7778619134302066 ) );
        addPoint( new Point( -0.3472963553338607, 0.9541888941386714 ) );
        addPoint( new Point( 5.551115123125783E-17, 1.0154266118857453 ) );
        addPoint( new Point( -0.30540728933227923, 1.1917535925942109 ) );
        addPoint( new Point( -0.5320888862379569, 1.461902200081545 ) );
        addPoint( new Point( -0.5320888862379574, 1.1092482386646145 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.49999999999999767, -0.8660254037844398, 1.8793852415718184,
            0.8660254037844398, 0.49999999999999767, 0.6840402866513395 ) );
        endFeature();
 
        beginRegularFeature( 18 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

		b_desc = 
			"A simple modification of the 3.12^2 tiling yields " +
			"this variant with 18-gons and 3-star fillers.  Also found " +
			"in figure 2.5.4(m) of Grunbaum and Shephard. ";

		endTiling();

        beginTiling( "8.12" );
 
        setTranslations(
            new Point( 6.049433830906403E-16, 3.2937731487882695 ),
            new Point( 1.6468865743941357, 1.6468865743941352 ) );
 
        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginPolygonFeature( 6 );
        addPoint( new Point( 0.26794919243112286, 1.0 ) );
        addPoint( new Point( 0.7320508075688773, 0.7320508075688772 ) );
        addPoint( new Point( 0.9999999999999999, 0.26794919243112264 ) );
        addPoint( new Point( 1.378937381963013, 0.6468865743941334 ) );
        addPoint( new Point( 0.9148357668252576, 0.9148357668252568 ) );
        addPoint( new Point( 0.6468865743941348, 1.3789373819630117 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            -1.6653345369377348E-16, 0.9999999999999989, 7.771561172376096E-16,
            -0.9999999999999989, -1.6653345369377348E-16, -7.216449660063518E-16 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginRegularFeature( 8 );
        addPlacement( new Transform(
            -3.937023596388081E-17, -0.6468865743941347, 1.6653345369377348E-16,
            0.6468865743941347, -3.937023596388081E-17, 1.6468865743941348 ) );
        endFeature();
 
		b_desc = 
			"A tiling by octagons, dodecagons and hexagonal fillers " +
            "derived by examination of an Islamic pattern from Bougoin.";

        endTiling();


        beginTiling( "9.12" );
 
        setTranslations(
            new Point( 1.5035797577461385, 2.6042765336484153 ),
            new Point( 3.007159515492277, -5.551115123125783E-16 ) );
 
        beginRegularFeature( 12 );
        addPlacement( new Transform(
            0.9999999999999999, -5.551115123125783E-17, 5.551115123125783E-17,
            5.551115123125783E-17, 0.9999999999999999, 0.0 ) );
        endFeature();
 
        beginPolygonFeature( 6 );
        addPoint( new Point( 0.9999999999999999, 0.26794919243112214 ) );
        addPoint( new Point( 0.9999999999999996, -0.26794919243112403 ) );
        addPoint( new Point( 1.5035797577461378, -0.08466115003254471 ) );
        addPoint( new Point( 2.007159515492278, -0.26794919243112264 ) );
        addPoint( new Point( 2.007159515492278, 0.26794919243112225 ) );
        addPoint( new Point( 1.5035797577461387, 0.0846611500325426 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            -0.4999999999999996, -0.866025403784437, -1.2767564783189298E-15,
            0.866025403784437, -0.4999999999999996, 1.221245327087672E-15 ) );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            0.4999999999999984, -0.8660254037844374, 9.992007221626405E-16,
            0.8660254037844374, 0.4999999999999984, 1.9984014443252814E-15 ) );
        endFeature();
 
        beginRegularFeature( 9 );
        addPlacement( new Transform(
            0.7250000612042714, -0.12783707180560153, 1.5035797577461387,
            0.12783707180560153, 0.7250000612042714, 0.8680921778828049 ) );
        addPlacement( new Transform(
            0.7250000612042713, 0.12783707180560155, 2.220446049250313E-16,
            -0.12783707180560155, 0.7250000612042713, 1.7361843557656105 ) );
        endFeature();
 
		b_desc = 
			"A tiling by nonagons, dodecagons and hexagonal fillers " +
            "derived by examination of an Islamic pattern from " +
			"Abas and Salman.";

        endTiling();

        beginTiling( "square_12" );
 
        setTranslations(
            new Point( 4.440892098500627E-16, 1.9999999999999991 ),
            new Point( 2.000000000000001, -3.9968028886505635E-15 ) );
 
        beginRegularFeature( 12 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
        beginPolygonFeature( 8 );
        addPoint( new Point( 0.2679491924311229, 0.9999999999999997 ) );
        addPoint( new Point( 0.7320508075688773, 0.7320508075688772 ) );
        addPoint( new Point( 0.9999999999999991, 0.26794919243111953 ) );
        addPoint( new Point( 1.2679491924311224, 0.7320508075688748 ) );
        addPoint( new Point( 1.7320508075688779, 0.9999999999999978 ) );
        addPoint( new Point( 1.2679491924311228, 1.2679491924311208 ) );
        addPoint( new Point( 1.0, 1.732050807568876 ) );
        addPoint( new Point( 0.7320508075688775, 1.2679491924311224 ) );
        commitPolygonFeature();
 
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();
 
		b_desc = 
			"A dodecagonal alternative to 3.12^2, 12-gons placed in a " +
			"square arrangement with 4-star fillers.";

        endTiling();
		
        beginTiling( "16.8" );

        setTranslations(
            new Point( -8.625193531108086E-17, 2.9604338701034165 ),
            new Point( 1.4802169350517105, 1.4802169350517103 ) );

        beginRegularFeature( 8 );
        addPlacement( new Transform(
            0.4802169350517094, -6.435664431831275E-17, 1.3877787807814457E-16,
            6.435664431831275E-17, 0.4802169350517094, 1.4802169350517094 ) );
        endFeature();

        beginRegularFeature( 16 );
        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        endFeature();

        beginPolygonFeature( 8 );
        addPoint( new Point( 0.1989123673796581, 1.0 ) );
        addPoint( new Point( 0.5664544973505217, 0.8477590650225735 ) );
        addPoint( new Point( 0.8477590650225735, 0.5664544973505214 ) );
        addPoint( new Point( 1.0, 0.19891236737965795 ) );
        addPoint( new Point( 1.2813045676720523, 0.48021693505171037 ) );
        addPoint( new Point( 0.9137624377011893, 0.6324578700291367 ) );
        addPoint( new Point( 0.6324578700291368, 0.9137624377011888 ) );
        addPoint( new Point( 0.4802169350517105, 1.2813045676720518 ) );
        commitPolygonFeature();

        addPlacement( new Transform(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0 ) );
        addPlacement( new Transform(
            -3.3306690738754696E-16, -1.0000000000000013, 1.2212453270876722E-15,
            1.0000000000000013, -3.3306690738754696E-16, -6.661338147750939E-16 ) );
        endFeature();

		b_desc = 
			"A tiling constructing by random exploration with the tiling " +
			"design tool that complements Taprats.  I'm not sure if this " +
			"tiling occurs in Islamic art, but I wouldn't be surprised.";

        endTiling();

		} catch( Exception e ) {
			System.err.println( e );
			e.printStackTrace();
		}
	}
}
