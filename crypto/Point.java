package crypto;

import java.math.BigInteger;

public class Point {
	
	private BigInteger x, y;
	
	private static final BigInteger D = BigInteger.valueOf(-376014);
	
	private static final BigInteger P = BigInteger.valueOf(2).pow(521).subtract(BigInteger.ONE);
	
	private static final Point NeutralElement = new Point(BigInteger.valueOf(0), BigInteger.valueOf(1));

	
	
	
	
	public Point(BigInteger x, BigInteger y) {
		
		// Neutral Element Constructor (?)
		if(x == BigInteger.valueOf(0) && y == BigInteger.valueOf(1)) {
			this.x = BigInteger.valueOf(0);
			this.y = BigInteger.valueOf(1);
			return;
		}
		
		BigInteger subEq1 = (x.pow(2).add(y.pow(2))).mod(P);
		BigInteger subEq2 = (BigInteger.ONE.add(D.multiply(x.pow(2).multiply(y.pow(2))))).mod(P);
		
		// If these points do not satisfy the equation of the curve
		if(!subEq1.equals(subEq2)) {
			System.out.println("These points are not on the curve.");
			return;
		}
		
		this.x = x;
		this.y = y;
	}
	
	
	
	// Solve the curve equation for y given x and least significant bit of y
	// The curve equation solved for y is: y = +/- sqrt((1 - x^2)/(1 - dx^2)) where d != 1/x^2
	public Point(BigInteger x, boolean lsb) {
		
		// ((1 - x^2)/(1 - dx^2))
		BigInteger preY = (BigInteger.ONE.subtract(x.pow(2))).multiply(BigInteger.ONE.subtract(D.multiply(x.pow(2))).modInverse(P)).mod(P);
		
		// Square root of the above, with modulus P and given least significant bit value
		BigInteger y = Point.sqrt(preY, P, lsb);
		
		// Checking if this point is acceptable as in the main constructor
		if(x != null && y != null) {
			BigInteger subEq1 = (x.pow(2).add(y.pow(2))).mod(P);
			BigInteger subEq2 = (BigInteger.ONE.add(D.multiply(x.pow(2).multiply(y.pow(2))))).mod(P);
			
			// If these points do not satisfy the equation of the curve
			if(!subEq1.equals(subEq2)) {
				System.out.println("These points are not on the curve.");
				return;
			}
		} else {
			System.out.println("At least one of the coordinates was null.");
			return;
		}
		
		this.x = x;
		this.y = y;
	}
	
	
	
	// Exponentiation algorithm adapted from the project description/lecture slides wasn't working properly for all cases
	// So I ended up modifying it to the be more like the reference implementation
	public Point multiplyScalar(BigInteger s) {
		Point pointFactor = this;
		
		// s = (sk sk-1 ... s1 s0)2, sk = 1.
		int k = s.bitLength();
		Point V = new Point(BigInteger.ZERO, BigInteger.ONE); // initialize with sk*P, which is simply P
		int i = k;
		while(i >= 0) {
			V = addPoints(V, V); // invoke the Edwards point addition formula
			if (s.testBit(i)) { // test the i-th bit of s
				V = addPoints(V, pointFactor); // invoke the Edwards point addition formula
			}
			i--;
		}
		return V; // now finally V = s*P
	}
	
	
	
	public static Point addPoints(Point p1, Point p2) {
		
		BigInteger newX = ((p1.getX().multiply(p2.getY()).add(p1.getY().multiply(p2.getX()))).multiply((BigInteger.ONE.add(D.multiply(p1.getX().multiply(p2.getX().multiply(p1.getY().multiply(p2.getY())))))).modInverse(P))).mod(P);
		
		BigInteger newY = ((p1.getY().multiply(p2.getY()).subtract(p1.getX().multiply(p2.getX()))).multiply((BigInteger.ONE.subtract(D.multiply(p1.getX().multiply(p2.getX().multiply(p1.getY().multiply(p2.getY())))))).modInverse(P))).mod(P);
		
		return new Point(newX, newY);
	}
	
	
	
	public boolean equals(Object p2) {
		Point p = (Point) p2;
		
		if(x == null || y == null || p.getX() == null || p.getY() == null) {
			return false;
		}
		
		if(x.equals(p.getX()) && y.equals(p.getY())) {
			return true;
		}
		
		return false;
	}
	
	
	
	// Returns the opposite of a point given the initial point
	public static Point opposite(Point ip) {
		return new Point(ip.getX().negate(), ip.getY());
	}
	
	
	
	// Taken from assignment description
	public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
		assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
		if (v.signum() == 0) {
			return BigInteger.ZERO;
		}
		BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
		if (r.testBit(0) != lsb) {
			r = p.subtract(r); // correct the lsb
		}
		return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
	}
	
	
	// Logic comes from reference code KeccakUtils
	public static byte[] pointToBytes(Point p) {
		byte[] bytes = new byte[P.toByteArray().length  * 2];
		byte[] x = p.getX().toByteArray();
		byte[] y = p.getY().toByteArray();
		
		int xPos = (P.toByteArray().length  * 2) / 2 - x.length;
		int yPos = bytes.length - y.length;

	    if (p.getX().signum() < 0) {
	    	for(int i = 0; i < xPos; i++) {
	    		bytes[i] = (byte) 0xff;
	    	}
	    }
	    if (p.getY().signum() < 0) {
	    	for(int i = (P.toByteArray().length  * 2) / 2; i < yPos; i++) {
	    		bytes[i] = (byte) 0xff;
	    	}
	    }
	    
	    System.arraycopy(x, 0, bytes, xPos, x.length);
	    System.arraycopy(y, 0, bytes, yPos, y.length);
	    
	    return bytes;
	}
	
	//Logic comes from reference code KeccakUtils
	public static Point bytesToPoint(byte[] b) {
		byte[] bx = new byte[(P.toByteArray().length  * 2) / 2];
		byte[] by = new byte[(P.toByteArray().length  * 2) / 2];
		
		System.arraycopy(b, 0, bx, 0, bx.length);
		System.arraycopy(b, bx.length, by, 0, by.length);
		
		BigInteger x = new BigInteger(bx);
		BigInteger y = new BigInteger(by);
		
		return new Point(x, y);
	}
	
	
	// Redundant
	public Point getNeutralElement() {
		return NeutralElement;
	}
	
	public BigInteger getX() {
		return x;
	}
	
	public BigInteger getY() {
		return y;
	}
	
}
