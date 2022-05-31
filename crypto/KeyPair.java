package crypto;

import java.math.BigInteger;



/*
 * Author: Austin Akers
 * 
 * References: 
 * 		Materials provided by Professor Paulo Barreto including lecture slides, assignment description
 * 		https://github.com/mjosaarinen/tiny_sha3
 * 		https://github.com/NWc0de/KeccakUtils
 * 		https://github.com/XKCP/XKCP/tree/master/Standalone/CompactFIPS202/C
 * 		https://github.com/XKCP/XKCP/tree/master/Standalone/CompactFIPS202/Python
 * 		NIST documentation:
 * 			https://dx.doi.org/10.6028/NIST.SP.800-185
 * 			https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.202.pdf
 * 
 * */



public class KeyPair {
		private BigInteger privateKey;
		private Point V;
		
		public KeyPair(byte[] pw) {
			byte[] s = sha3.kmacxof256(pw, new byte[]{}, 512, "K");
			
			byte[] sSig = new byte[s.length + 1];
			
			System.arraycopy(s, 0, sSig, 1, s.length);
			
			BigInteger s2 = new BigInteger(sSig);
			
			s2 = s2.multiply(BigInteger.valueOf(4));
			
			Point G = new Point(BigInteger.valueOf(4), false);
			
			Point V = G.multiplyScalar(s2);
			
			this.privateKey = s2;
			this.V = V;
		}
		
		public BigInteger getPrivateKey() {
			return this.privateKey;
		}
		
		public Point getPoint() {
			return this.V;
		}
	}