import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class program {
	
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
	
	public static Point keyPair(byte[] pw) {
		byte[] s = sha3.kmacxof256(pw, new byte[]{}, 512, "K");
		
		BigInteger s2 = new BigInteger(s);
		
//		if(s2.signum() == -1) {
//			s2.negate();
//		}
		
		s2 = s2.multiply(BigInteger.valueOf(4));
		
		Point G = new Point(BigInteger.valueOf(4), false);
		
		Point V = G.multiplyScalar(s2);
		
		return V;
	}
	
	private static void testPart1() {
		
		System.out.println("Pick an operation or something");
		
		String fileName = System.getProperty("user.dir") + "/src/" + "Input.txt";
		
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
		
		test = hash(testInput);
		
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
		
		String fileName = System.getProperty("user.dir") + "/src/" + "Input.txt";
		
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
		
		byte[] key = {(byte) 0x40, (byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
				(byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a, (byte) 0x4b, (byte) 0x4c, (byte) 0x4d, (byte) 0x4e, (byte) 0x4f,
				(byte) 0x50, (byte) 0x51, (byte) 0x52, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
				(byte) 0x59, (byte) 0x5a, (byte) 0x5b, (byte) 0x5c, (byte) 0x5d, (byte) 0x5e, (byte) 0x5f};
		
		
		
		// !!!!!!!! These are the outward facing, callable functionality methods. !!!!!!!!
		
		System.out.println("KeyPair");
		
		Point p = keyPair(key);
		
		System.out.println(p.getX() + " " + p.getY());
		
		
		
//		System.out.println("\nTag");
//		
//		test = tag(key, testInput);
//		
//		for(int i = 0; i < test.length; i++) {
//			System.out.print(String.format("%02X ", test[i]));
//		}
//		
//		System.out.println();
//		
//		System.out.println("\n Enc:");
//		
//		test = symEncrypt(key, inputBytes);
//		
//		for(int i = 0; i < test.length; i++) {
//			System.out.print(String.format("%02X ", test[i]));
//		}
//		
//		
//		
//		System.out.println("\n Dec:");
//		
//		test = symDecrypt(test, key);
//		
//		for(int i = 0; i < test.length; i++) {
//			System.out.print(String.format("%02X ", test[i]));
//		}
		
	}
	
	public static void main(String[] args) {
		testPart1();
		testPart2();
	}
}
