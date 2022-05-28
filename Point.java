import java.math.BigInteger;

public class Point {
	
	private BigInteger x, y;
	
	private static BigInteger D = BigInteger.valueOf(-376014);
	
	private static BigInteger P = BigInteger.valueOf(2).pow(521).subtract(BigInteger.ONE);
	
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
	
	
	
	// Probably unnecessary
	public Point getNeutralElement() {
		return NeutralElement;
	}
	
	public BigInteger getX() {
		return x;
	}
	
	public BigInteger getY() {
		return y;
	}
	
	
	
	
	
	public static void main(String[] args) {
		
//		System.out.println();
//		
//		Point testPoint = new Point(BigInteger.valueOf(0), BigInteger.valueOf(1));
//		
//		System.out.println(testPoint.getX().toString() + " " + testPoint.getY().toString());
//		
//		Point testPoint2 = new Point(BigInteger.valueOf(1), BigInteger.valueOf(0));
//		
//		System.out.println(testPoint2.getX().toString() + " " + testPoint2.getY().toString());
//		
//		Point testPointAdd = Point.addPoints(testPoint, testPoint2);
//		
//		System.out.println(testPointAdd.getX().toString() + " " + testPointAdd.getY().toString());
//		
//		testPoint = new Point(BigInteger.valueOf(4), true);
//		
//		System.out.println(testPoint.getX().toString() + " " + testPoint.getY().toString());
//		
//		//3032432114286052459734493346589116727620774438186426554571035049389122174539777808394925429974663047808802579026830290200483205984431081241005173227620421363
//		
//		testPoint2 = opposite(testPoint);
//		
//		System.out.println(testPoint2.getX().toString() + " " + testPoint2.getY().toString());
//		
//		System.out.println(testPoint.equals(testPoint2));
		
		
		
		
		Point testPoint = new Point(BigInteger.valueOf(4), true);
		
		System.out.println(testPoint.getX().toString() + " " + testPoint.getY().toString());
		
		testPoint = testPoint.multiplyScalar(BigInteger.valueOf(-4));
		
		System.out.println(testPoint.getX().toString() + " " + testPoint.getY().toString());
		
		//x 2495955776396604226407267356908013368834389573875394083557177494000105902197484529509184115676817108285435959901009662698340790193587169943973819919739337152
		//y 6859636288096913776645387651130902474064331811082450899727219308510706266067313575729816646597853898764807212586496241359457440546189884484864185540395703452
		
		
		
	}
}
