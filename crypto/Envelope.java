package crypto;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;



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
 * Most if not all test values are taken from NIST documentation and test vectors.
 * 
 * */



public class Envelope {
	
	// These wrapper methods were written following the descriptions as detailed in the project description
	// The test methods are just methods I used to execute the test vectors and play around with the methods. They are not formal, but I didn't feel it was necessary to remove them.
	
	public static byte[] hash(byte[] M) {
		return sha3.kmacxof256(new byte[]{}, M, 512, "D");
	}
	
	
	
	public static byte[] tag(byte[] PW, byte[] M) {
		return sha3.kmacxof256(PW, M, 512, "T");
	}
	
	
	
	public static byte[] symEncrypt(byte[] PW, byte[] M) {
		SecureRandom random = new SecureRandom();
		byte[] z = new byte[64];
		random.nextBytes(z);
		
		byte[] zpw = new byte[z.length + PW.length];
		
		System.arraycopy(z, 0, zpw, 0, z.length);
		System.arraycopy(PW, 0, zpw, z.length, PW.length);
		
		byte[] keka = sha3.kmacxof256(zpw, new byte[]{}, 1024, "S");
		
		byte[] ke = new byte[64];
		byte[] ka = new byte[64];
		
		System.arraycopy(keka, 0, ke, 0, ke.length);
		System.arraycopy(keka, 64, ka, 0, ka.length);
		
		byte[] ke2 = sha3.kmacxof256(ke, new byte[]{}, M.length*8, "SKE");
		byte[] c = xorBytes(ke2, M);
		
		byte[] t = sha3.kmacxof256(ka, M, 512, "SKA");
		
		byte[] zct = new byte[z.length + c.length + t.length];
		
		System.arraycopy(z, 0, zct, 0, z.length);
		System.arraycopy(c, 0, zct, z.length, c.length);
		System.arraycopy(t, 0, zct, z.length + c.length, t.length);
		
		return zct;
	}
	
	
	
	public static byte[] symDecrypt(byte[] zct, byte[] PW) {
		byte[] z = new byte[64];
		byte[] t = new byte[64];
		byte[] c = new byte[zct.length - (z.length + t.length)];
		
		System.arraycopy(zct, 0, z, 0, z.length);
		System.arraycopy(zct, z.length, c, 0, c.length);
		System.arraycopy(zct, zct.length - t.length, t, 0, t.length);
		
		byte[] zpw = new byte[z.length + PW.length];
		
		System.arraycopy(z, 0, zpw, 0, z.length);
		System.arraycopy(PW, 0, zpw, z.length, PW.length);
		
		byte[] keka = sha3.kmacxof256(zpw, new byte[]{}, 1024, "S");
		
		byte[] ke = new byte[64];
		byte[] ka = new byte[64];
		
		System.arraycopy(keka, 0, ke, 0, ke.length);
		System.arraycopy(keka, 64, ka, 0, ka.length);
		
		byte[] ke2 = sha3.kmacxof256(ke, new byte[]{}, c.length*8, "SKE");
		byte[] m = xorBytes(ke2, c);
		
		byte[] tPrime = sha3.kmacxof256(ka, m, 512, "SKA");
		
		// Integrity Check
		if(Arrays.equals(t, tPrime)) {
			return m;
		} else {
			System.out.println("Integrity Check Failed.");
			return null;
		}
		
	}
	
	
	
	private static byte[] xorBytes(byte[] a, byte[] b) {
		if(a.length != b.length) {
			System.out.println("Tried to xor different size byte[]s.");
			return null;
		}
		
		byte[] c = new byte[a.length];
		
		for(int i = 0; i < a.length; i++) {
			c[i] = (byte) (a[i] ^ b[i]);
		}
		
		return c;
	}
	
	
	
	public static KeyPair keyPair(byte[] pw) {
		return new KeyPair(pw);
	}
	
	
	
	public static byte[] ecEncrypt(Point V, byte[] M) {
		SecureRandom random = new SecureRandom();
		byte[] k = new byte[64];
		random.nextBytes(k);
		k[0] = (byte) 0;
		
		BigInteger k2 = new BigInteger(k);
		k2 = k2.multiply(BigInteger.valueOf(4));
		
		Point G = new Point(BigInteger.valueOf(4), false);
		
		Point W = V.multiplyScalar(k2);
		
		Point Z = G.multiplyScalar(k2);
		
		byte[] keka = sha3.kmacxof256(W.getX().toByteArray(), new byte[]{}, 1024, "P");
		
		byte[] ke = new byte[64];
		byte[] ka = new byte[64];
		
		System.arraycopy(keka, 0, ke, 0, ke.length);
		System.arraycopy(keka, 64, ka, 0, ka.length);
		
		byte[] ke2 = sha3.kmacxof256(ke, new byte[]{}, M.length*8, "PKE");
		byte[] c = xorBytes(ke2, M);
		
		byte[] t = sha3.kmacxof256(ka, M, 512, "PKA");
		
		byte[] zbytes = Point.pointToBytes(Z);
		
		byte[] zct = new byte[zbytes.length + c.length + t.length];
		
		System.arraycopy(zbytes, 0, zct, 0, zbytes.length);
		System.arraycopy(c, 0, zct, zbytes.length, c.length);
		System.arraycopy(t, 0, zct, zbytes.length + c.length, t.length);
		
		return zct;
	}
	
	
	
	public static byte[] ecDecrypt(byte[] zct, byte[] pw) {
		byte[] z = new byte[132];
		byte[] t = new byte[64];
		byte[] c = new byte[zct.length - (z.length + t.length)];
		
		System.arraycopy(zct, 0, z, 0, z.length);
		System.arraycopy(zct, z.length, c, 0, c.length);
		System.arraycopy(zct, zct.length - t.length, t, 0, t.length);
		
		byte[] s = sha3.kmacxof256(pw, new byte[]{}, 512, "K");
		
		byte[] sSig = new byte[s.length + 1];
		
		System.arraycopy(s, 0, sSig, 1, s.length);
		
		BigInteger s2 = new BigInteger(sSig);
		s2 = s2.multiply(BigInteger.valueOf(4));
		
		Point Z = Point.bytesToPoint(z);
		
		Point W = Z.multiplyScalar(s2);
		
		byte[] keka = sha3.kmacxof256(W.getX().toByteArray(), new byte[]{}, 1024, "P");
		
		byte[] ke = new byte[64];
		byte[] ka = new byte[64];
		
		System.arraycopy(keka, 0, ke, 0, ke.length);
		System.arraycopy(keka, 64, ka, 0, ka.length);
		
		byte[] ke2 = sha3.kmacxof256(ke, new byte[]{}, c.length*8, "PKE");
		byte[] m = xorBytes(ke2, c);
		
		byte[] tPrime = sha3.kmacxof256(ka, m, 512, "PKA");
		
		// Integrity Check
			if(Arrays.equals(t, tPrime)) {
				return m;
			} else {
				System.out.println("Integrity Check Failed.");
				return null;
			}
	}
	
	
	
	public static byte[] ecSign(byte[] pw, byte[] m) {
		
		byte[] k = sha3.kmacxof256(pw, m, 512, "N");
		byte[] kSign = new byte[65];
		
		System.arraycopy(k, 0, kSign, 1, k.length);
		
		BigInteger k2 = new BigInteger(kSign);
		k2 = k2.multiply(BigInteger.valueOf(4));
		
		Point G = new Point(BigInteger.valueOf(4), false);
		
		Point U = G.multiplyScalar(k2);
		
		byte[] h = sha3.kmacxof256(U.getX().toByteArray(), m, 512, "T");
		
		byte[] hSig = new byte[h.length + 1];
		
		System.arraycopy(h, 0, hSig, 1, h.length);
		
		BigInteger h2 = new BigInteger(hSig);
		h2 = h2.multiply(new BigInteger(pw));
		
		String rOperand = "337554763258501705789107630418782636071904961214051226618635150085779108655765";
		
		BigInteger R = (BigInteger.TWO.pow(519)).subtract(new BigInteger(rOperand));
		
		BigInteger z = (k2.subtract(h2)).mod(R);
		
		byte[] zbytes = z.toByteArray();
		
		byte[] hz = new byte[h.length + zbytes.length + 1];
		
		System.arraycopy(h, 0, hz, 1, h.length);
		System.arraycopy(zbytes, 0, hz, 1 + h.length, zbytes.length);
		
		return hz;
	}
	
	
	
	public static boolean ecVerify(byte[] sig, byte[] m, Point V) {
		byte[] h = new byte[sig.length / 2];
		byte[] z = new byte[sig.length / 2];
		
		System.arraycopy(sig, 0, h, 0, h.length);
		System.arraycopy(sig, h.length, z, 0, z.length);
		
		BigInteger zInt = new BigInteger(z);
		BigInteger hInt = new BigInteger(h);
		
		Point G = new Point(BigInteger.valueOf(4), false);
		Point G2 = G.multiplyScalar(new BigInteger(z));
		
		Point V2 = V.multiplyScalar(new BigInteger(h));
		
		Point U = Point.addPoints(G2, V2);
		
		// Integrity Check
		byte[] preU2 = sha3.kmacxof256(U.getX().toByteArray(), m, 512, "T");
		byte[] U2 = new byte[65];
		System.arraycopy(preU2, 0, U2, 1, preU2.length);
		if(Arrays.equals(h, U2)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	
	
	
	
	private static void testPart1() {
		
		System.out.println("Pick an operation or something");
		
		String fileName = System.getProperty("user.dir") + "/src/" + "Data.txt";
		
		File file = new File(fileName);
		
		byte[] inputBytes = new byte[1];
		
		try {
			Scanner scanner = new Scanner(file);
			
			byte[] inputByteBuffer = new byte[256];
			
			int i = 0;
			
			while(scanner.hasNext()) {
				String inByte = scanner.next();
				
				inputByteBuffer[i] = (byte) Integer.parseInt(inByte, 16);
				
				i++;
			}
			
			inputBytes = new byte[i];
			
			System.arraycopy(inputByteBuffer, 0, inputBytes, 0, i);
			
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		byte[] testInput = {(byte) 0, (byte) 1, (byte) 2, (byte) 3};
		
		byte[] test = sha3.cShake256(inputBytes, 512, "", "Email Signature");
		
		test = sha3.cShake256(testInput, 512, "", "");
		
		test = sha3.sha3(testInput, 512);
		
//		for(int i = 0; i < test.length; i++) {
//			System.out.print(String.format("%02X ", test[i]));
//		}
		
		System.out.println("\n");
		
		byte[] key = {(byte) 0x40, (byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
				(byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a, (byte) 0x4b, (byte) 0x4c, (byte) 0x4d, (byte) 0x4e, (byte) 0x4f,
				(byte) 0x50, (byte) 0x51, (byte) 0x52, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
				(byte) 0x59, (byte) 0x5a, (byte) 0x5b, (byte) 0x5c, (byte) 0x5d, (byte) 0x5e, (byte) 0x5f};
		
		test = sha3.kmacxof256(key, inputBytes, 512, "My Tagged Application");
		System.out.println("\n");
		
//		for(int i = 0; i < test.length; i++) {
//			System.out.print(String.format("%02X ", test[i]));
//		}
		
		
		// !!!!!!!! Everything here has been tested and works properly !!!!!!!!
		
		
		
		// !!!!!!!! These are the outward facing, callable functionality methods. !!!!!!!!
		
		System.out.println("Hash");
		
		test = hash(inputBytes);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
		System.out.println("\nTag");
		
		test = tag(key, testInput);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
		System.out.println();
		
		System.out.println("\n Enc:");
		
		test = symEncrypt(key, inputBytes);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
		
		
		System.out.println("\n Dec:");
		
		test = symDecrypt(test, key);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
	}
	
	
	
	private static void testPart2() {
		
		System.out.println("Pick an operation or something");
		
		String fileName = System.getProperty("user.dir") + "/src/" + "Data.txt"; // was Input.txt
		
		File file = new File(fileName);
		
		byte[] inputBytes = new byte[1];
		
		try {
			Scanner scanner = new Scanner(file);
			
			byte[] inputByteBuffer = new byte[256];
			
			int i = 0;
			
			while(scanner.hasNext()) {
				String inByte = scanner.next();
				
				inputByteBuffer[i] = (byte) Integer.parseInt(inByte, 16);
				
				i++;
			}
			
			inputBytes = new byte[i];
			
			System.arraycopy(inputByteBuffer, 0, inputBytes, 0, i);
			
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n\n");
		
		
		
		
		byte[] testInput = {(byte) 0, (byte) 1, (byte) 2, (byte) 3};
		
		byte[] test = sha3.cShake256(inputBytes, 512, "", "Email Signature");
		
//		byte[] key = {(byte) 0x40, (byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
//				(byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a, (byte) 0x4b, (byte) 0x4c, (byte) 0x4d, (byte) 0x4e, (byte) 0x4f,
//				(byte) 0x50, (byte) 0x51, (byte) 0x52, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
//				(byte) 0x59, (byte) 0x5a, (byte) 0x5b, (byte) 0x5c, (byte) 0x5d, (byte) 0x5e, (byte) 0x5f};
		
		byte[] key = "d".getBytes();
		
		
		
		// !!!!!!!! These are the outward facing, callable functionality methods. !!!!!!!!
		
		System.out.println("KeyPair");
		
		KeyPair p = new KeyPair(key);
		
		System.out.println(p.getPoint().getX() + " " + p.getPoint().getY());
		
		
		
		System.out.println();
		
		System.out.println(" ecEnc:");
		
		test = ecEncrypt(p.getPoint(), inputBytes);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
		
		
		System.out.println("\n ecDec:");
		
		test = ecDecrypt(test, key);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
		
		
		System.out.println("\n");
		System.out.println("Sign:");
		
		test = ecSign(p.getPrivateKey().toByteArray(), testInput);
		
		for(int i = 0; i < test.length; i++) {
			System.out.print(String.format("%02X ", test[i]));
		}
		
		
		System.out.println("\n");
		System.out.println("Verify:");
		
		boolean verification = ecVerify(test, testInput, p.getPoint());
		
		System.out.println(verification);
		
	}
	
	
	
	public static void test() {
		testPart1();
		testPart2();
	}
	
	
	
}
