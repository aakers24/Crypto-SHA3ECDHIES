package crypto;

import java.util.Arrays;



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



public class sha3 {
	
	static long keccakf_rndc[] = {
	        0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
	        0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
	        0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
	        0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
	        0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
	        0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
	        0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
	        0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
	    };
	
	static int keccakf_rotc[] = {
	        1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
	        27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
	    };
	
	static int keccakf_piln[] = {
		        10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
		        15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
		    };
	
	// Based on the example given in the slides for the project.
	private static byte[] bytepad(byte[] X, int w) {
		if (w <= 0) {
			System.out.println("bytepad validity check failed.");
			return null;
		}
		
		// left_encode(w) || X
		byte[] lEnc = left_encode(w);
		
		int newSize = w*((lEnc.length + X.length + w - 1)/w);
		
		byte[] z = new byte[newSize];
		System.arraycopy(lEnc, 0, z, 0, lEnc.length);
		System.arraycopy(X, 0, z, lEnc.length, X.length);
		
		for(int i = lEnc.length + X.length; i < z.length; i++) {
			z[i] = (byte)0;
		}
		
		return z;
	}
	
	
	// left_encode as defined in the NIST Standards
	private static byte[] left_encode(int x) {
		if (0 > x || x >= Math.pow(2, 2040)) {
			System.out.println("left_encode validity check failed.");
			return null;
		}
		
		// Convert x to binary
		String intString = Integer.toBinaryString(x);
		int numBytes;
		
		if(intString.length() % 8 == 0) {
			numBytes = intString.length() / 8;
		} else {
			numBytes = intString.length() / 8 + 1;
			int padCount = 8 - (intString.length() % 8);
			
			StringBuilder s = new StringBuilder();
			for(int i = 0; i < padCount; i++) {
				s.insert(0, "0");
			}
			
			intString = s + intString;
		}
		
		// Iterate over the array of integers and append them in byte form to L. Prepend the length.
		
		byte[] L = new byte[numBytes + 1];
		
		//L[0] = (byte)swapEndian(numBytes); // Length of x in bytes in little-endian bit order
		L[0] = (byte) numBytes;
		
		for(int j = 0; j < numBytes; j++) {
			L[j+1] = (byte) Integer.parseInt(intString.substring(j*8, ((j+1)*8)), 2);
		}
		
		return L;
		
	}
	
	
	// left_encode as defined in the NIST Standards
	private static byte[] right_encode(int x) {
		if (0 > x || x >= Math.pow(2, 2040)) {
			System.out.println("right_encode validity check failed.");
			return null;
		}
		
		// Convert x to binary
		String intString = Integer.toBinaryString(x);
		int numBytes;
		
		if(intString.length() % 8 == 0) {
			numBytes = intString.length() / 8;
		} else {
			numBytes = intString.length() / 8 + 1;
			int padCount = 8 - (intString.length() % 8);
			
			StringBuilder s = new StringBuilder();
			for(int i = 0; i < padCount; i++) {
				s.insert(0, "0");
			}
			
			intString = s + intString;
		}
		
		// Iterate over the array of integers and append them in byte form to L. Prepend the length.
		
		byte[] L = new byte[numBytes + 1];
		
		//L[L.length - 1] = (byte)swapEndian(numBytes); // Length of x in bytes in little-endian bit order
		L[L.length - 1] = (byte) numBytes;
		
		// This reverses the order of the digits making it little-endian byte order
		for(int j = 0; j < numBytes; j++) {
			L[j] = (byte)swapEndian(Integer.parseInt(intString.substring(j*8, ((j+1)*8)), 2));
		}
		
		for(int j = 0; j < numBytes; j++) {
			L[j] = (byte) Integer.parseInt(intString.substring(j*8, ((j+1)*8)), 2);
		}
		
		return L;
		
	}
	
	
	// encode_string as defined in the NIST Standards
	private static byte[] encode_string(byte[] S) {
		if(0 > S.length || S.length >= Math.pow(2, 2040)) {
			System.out.println("encode_string validity check failed."); // Maybe change? ----------------------------------------------------------------
			return null;
		}
		
		byte[] leftEnc = left_encode(S.length*8);
		byte[] encodedString = new byte[leftEnc.length + S.length];
		
		System.arraycopy(leftEnc, 0, encodedString, 0, leftEnc.length);
		System.arraycopy(S, 0, encodedString, leftEnc.length, S.length);
		
		return encodedString;
	}
	
	
	
	// Method to swap the bit order of a byte
	private static int swapEndian(int i) {
		i = (i & 0xF0) >> 4 | (i & 0x0F) << 4;
		i = (i & 0xCC) >> 2 | (i & 0x33) << 2;
		i = (i & 0xAA) >> 1 | (i & 0x55) << 1;
		
		return i;
	}
	
	
	
	// Adapted from C readable version
	private static long loadWord(byte[] x)
	{
	    long u=0L;

	    for(int i=7; i>=0; --i) {
	        u <<= 8;
	        u |= x[i] & 0xff;
	    }
	    return u;
	}
	
	
	
	// Adapted from KeccakUtils reference
	private static int kecLog(int n) throws Exception {
		if (n < 0) throw new Exception();
        int exp = -1;
        while (n > 0) {
            n = n>>>1;
            exp++;
        }
        return exp;
	}
    
	
	
    private static long rotL64(long x, int y) {
    	long xHelper = (x >> 1) & (0x7fffffffffffffffL);
    	return ((x) << (y)) | ((xHelper) >> (63 - (y)));
    }
	
	
    // The keccak permutation function. Used all references for aid with this method and the methods which it calls, with a focus on the tiny_sha3 reference.
	private static long[] keccakp(long[] S, int b, int n) {
		// Convert S into a state array A
		long[] S2 = S;
		int l;
		try {
			l = kecLog(b/25);
			for(int i = 12 + 2*l - n; i < 12 + 2*l; i++) {
				S2 = theta(S2);
				S2 = rhoPi(S2);
				S2 = chi(S2);
				S2 = iota(S2, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return S2;
	}
	
    private static long[] theta(long[] S) {
        long[] S2 = new long[25];
        long[] BC = new long[5];

        for (int i = 0; i < 5; i++) {
            BC[i] = S[i] ^ S[i + 5] ^ S[i + 10] ^ S[i + 15] ^ S[i + 20];
        }

        for (int i = 0; i < 5; i++) {
            long t = BC[(i+4) % 5] ^ rotL64(BC[(i+1) % 5], 1);

            for (int j = 0; j < 25; j += 5) {
                S2[i + j] = S[i + j] ^ t;
            }
        }

        return S2;
    }
    
    private static long[] rhoPi(long[] S) {
    	long[] S2 = new long[25];
        long BC;
    	
        S2[0] = S[0];
        
        long t = S[1];
    	for (int i = 0; i < 24; i++) {
            int j = keccakf_piln[i];
            BC = S[j];
            S2[j] = rotL64(t, keccakf_rotc[i]);
            t = BC;
        }
    	
    	return S2;
    }
    
    private static long[] chi(long[] S) {
    	long[] S2 = new long[25];
        long BC;
    	
    	for (int j = 0; j < 25; j += 5) {
            for (int i = 0; i < 5; i++) {
            	BC = ~S[(i + 1) % 5 + j] & S[(i + 2) % 5 + j];
                S2[j + i] = S[j + i] ^ BC;
            }
        }
    	
    	return S2;
    }
    
    private static long[] iota(long[] S, int r) {
    	
    	S[0] ^= keccakf_rndc[r];
    	
    	return S;
    }
    
    
    
    private static byte[] kecPad(byte[] N, int x) {
    	int numBytes = x / 8;
    	int j = numBytes - N.length % numBytes;
    	int newLen = j + N.length;
    	
    	byte[] P = new byte[newLen];
    	
    	int i = 0;
    	while(i < N.length) {
    		P[i] = N[i];
    		i++;
    	}
    	while(i < newLen) {
    		if(i == newLen - 1) {
    			P[i] = (byte) -128;
    		}
    		else {
    			P[i] = 0;
    		}
    		i++;
    	}
    	
    	return P;
    }
    
    
    
    // The polymorphic version of the stateArrayConversion method which converts TO state array
    private static long[][] stateArrayConversion(byte[] X, int rate){
    	
    	int depth = X.length*8 / rate;
    	
    	// The state array is a block of that equates to 5x5x64. Maybe cannot just make it 64?
    	long[][] stateArray = new long[depth][25];
    	
    	int xPos = 0;
    	for(int s = 0; s < stateArray.length; s++) {
    	
	    	byte[] holder = new byte[8];
	    	
	    	for(int i = 0; i < rate/64; i++) {
	    		holder = new byte[8];
	    		for(int j = 0; j < 8; j++) {
	    			holder[j] = X[xPos*8 + j];
	    		}
	    		stateArray[s][i] = loadWord(holder);
	    		xPos++;
	    	}
    	}
    	
    	return stateArray;
    }
    
    
    
    // The polymorphic version of the stateArrayConversion method which converts FROM state array TO a byte array
    private static byte[] stateArrayConversion(long[] S, int d) {
    	// Size of byte array should be 8 bytes for each long in the 5x5 state array
    	byte[] x = new byte[d/8];
    	
    	for(int i = 0; i < d/64; i++) {
    		long temp = S[i];
    		
    		for(int j = 0; j < 8; j++) {
    			byte b = (byte) (temp>>>(8*j) & 0xFF);
    			x[i*8 + j] = b;
    		}
    	}
    	
    	return x;
    }
    
    
    
    private static byte[] kecSponge(byte[] N, int capacity, int d) {
    	int rate = 1600 - capacity;
    	
    	byte[] P;
    	if(N.length % (rate / 8) != 0) {
    		P = kecPad(N, rate);
    	}
    	else {
    		P = N;
    	}
    	
    	long[][] stateArray = stateArrayConversion(P, rate);
    	
    	long[] state = new long[25];
    	
    	for(int i = 0; i < stateArray.length; i++) {
    		long[] xorState = new long[25];
    		for(int j = 0; j < 25; j++) {
    			xorState[j] = state[j] ^ stateArray[i][j];
    		}
    		
    		state = keccakp(xorState, 1600, 24);
    	}
    	
    	// Heavily adapted from reference
    	long[] outputState = {};
        int offset = 0;
        while(outputState.length*64 < d) {
            outputState = Arrays.copyOf(outputState, offset + rate/64);
            System.arraycopy(state, 0, outputState, offset, rate/64);
            offset += rate/64;
            state = keccakp(state, 1600, 24);
        }
        
    	return stateArrayConversion(outputState, d);
    }
    
    
    
    public static byte[] sha3(byte[] M, int L) {
    	
    	byte[] mPad = new byte[M.length + 1];
    	System.arraycopy(M, 0, mPad, 0, M.length);
    	if((1600 - 2 * L) / 8 - M.length % (1600 - 2 * L) == 1) {
    		mPad[M.length] = (byte) 0x86;
    	} else {
    		mPad[M.length] = (byte) 0x06;
    	}
    	
    	return kecSponge(mPad, L*2, L);
    }
    
    
    
    public static byte[] kmacxof256(byte[] K, byte[] X, int L, String S) {
    	byte[] encK = encode_string(K);
    	byte[] bpK = bytepad(encK, 136);
    	
    	byte[] newX;
    	
    	newX = new byte[bpK.length + X.length + 2];
    	
    	System.arraycopy(bpK, 0, newX, 0, bpK.length);
		System.arraycopy(X, 0, newX, bpK.length, X.length);
		System.arraycopy(right_encode(0), 0, newX, bpK.length + X.length, 2);
    	
    	return cShake256(newX, L, "KMAC", S);
    	
    }
    
    
    
    public static byte[] shake256(byte[] X, int L) {
    	
    	byte[] xPad = new byte[X.length + 1];
    	System.arraycopy(X, 0, xPad, 0, X.length);
    	
    	if(136 - X.length % (136) == 1) {
    		xPad[X.length] = (byte) 0x9f;
    	} else {
    		xPad[X.length] = (byte) 0x1f;
    	}
    	
    	return kecSponge(xPad, 512, L);
    }
    
    
    
    public static byte[] cShake256(byte[] X, int L, String N, String S) {
    	if(N.length() * 8 > Math.pow(2, 2040) || S.length() * 8 > Math.pow(2, 2040)) {
			System.out.println("cShake256 validity check failed.");
			return null;
		}
    	
    	if(N.length() == 0 && S.length() == 0) {
    		return shake256(X, L);
    	}
    	
    	// From NIST: KECCAK[512](bytepad(encode_string(N) || encode_string(S), 136) || X || 00, L)
    	
    	byte[] encN = encode_string(N.getBytes());
    	byte[] encS = encode_string(S.getBytes());
    	
    	byte[] encNS = new byte[encN.length + encS.length];
    	
    	System.arraycopy(encN, 0, encNS, 0, encN.length);
		System.arraycopy(encS, 0, encNS, encN.length, encS.length);
    	
    	byte[] nsPad = bytepad(encNS, 136);
    	
    	byte[] xPad = new byte[X.length + 1];
    	System.arraycopy(X, 0, xPad, 0, X.length);
    	for(int i = X.length; i < xPad.length; i++) {
    		xPad[i] = (byte) 4;
    	}
    	
    	byte[] nsx = new byte[nsPad.length + xPad.length];
    	
    	System.arraycopy(nsPad, 0, nsx, 0, nsPad.length);
		System.arraycopy(xPad, 0, nsx, nsPad.length, xPad.length);
		
		return kecSponge(nsx, 512, L);
    }

}
