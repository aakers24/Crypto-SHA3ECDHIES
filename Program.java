import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.InputMismatchException;
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
 * It is expected that data be in hexadecimal bytes, separated by spaces, and no newlines in between. For instance: 01 02 2A 2B CA CB F1 FF
 * This is the format in which the data is received and delivered.
 * The exception is the password/passphrase, which can be input as any string.
 * 		
 * Data is read from and written to the provided files. It is possible to change this, but that would require manual modification of the program.
 * Depending on the environment the program is being run in, I have provided 2 ways of accessing the file locations.
 * These were the ways I got it to work in the different environments I tested the program in.
 * Again, more flexible, creative, and elegant solutions are possible, but this is the functionality as it is provided.
 * 
 * */



public class Program {
	
	// Method to parse strings of hex input in this program's output format(ie 01 A1 3E FF) into their proper byte array equivalent
	// For passwords/phrases call .getBytes()
	public static byte[] parseData(String data) {
		String[] splitData = data.split(" ");
		
		byte[] parsedData = new byte[splitData.length];
		
		for(int i = 0; i < parsedData.length; i++) {
			parsedData[i] = (byte) Integer.parseInt(splitData[i], 16);
		}
		
		return parsedData;
	}

	public static void main(String[] args) {
		
		Scanner consoleIn = new Scanner(System.in);
		int option = -1;
		KeyPair kp = null;
		
		
		
		
		
		// Opening files
//		String fileName = System.getProperty("user.dir") + "/src/" + "Data.txt";
//
//		String fileName2 = System.getProperty("user.dir") + "/src/" + "PublicKey.txt";
//
//		String fileName3 = System.getProperty("user.dir") + "/src/" + "Signature.txt";
//
//		String fileName4 = System.getProperty("user.dir") + "/src/" + "Encrypted.txt";
		
		// THE ABOVE LINES WORK IN ECLIPSE, WHILE THE BELOW WORK IN INTELLIJ
		// COMMENT THE 4 LINES ACCORDING TO YOUR ENVIRONMENT
		
		String fileName = "Data.txt";

		String fileName2 = "PublicKey.txt";

		String fileName3 = "Signature.txt";

		String fileName4 = "Encrypted.txt";
		
		File file = new File(fileName);
		
		byte[] dataBytes = new byte[1];
		
		try {
			Scanner scanner = new Scanner(file);
			
			byte[] inputByteBuffer = new byte[Integer.MAX_VALUE / 2];
			
			int i = 0;
			
			while(scanner.hasNext()) {
				String inByte = scanner.next();
				
				inputByteBuffer[i] = (byte) Integer.parseInt(inByte, 16);
				
				i++;
			}
			
			dataBytes = new byte[i];
			
			System.arraycopy(inputByteBuffer, 0, dataBytes, 0, i);
			
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Opening files
		
		
		
		
		
		boolean loop = true;
		while(loop) {
			System.out.println("\nPick an operation 1-12. To see the list of operations pick 0. To exit pick 22.");
			option = -1;
			
			try {
				option = consoleIn.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Please enter an integer.");
				consoleIn.next();
				continue;
			}
			consoleIn.nextLine();
			
			switch(option) {
				case 22:
					loop = false;
					break;
			
				case 0: System.out.println("Options:\n\t0 to see the list of operations.\n"
						+ "\t1 to compute a plain cryptographic hash of a given file.\n"
						+ "\t2 to compute a plain cryptographic hash of a text input.\n"
						+ "\t3 to encrypt a given data file symmetrically under a given passphrase.\n"
						+ "\t4 to decrypt a given data file symmetrically under a given passphrase.\n"
						+ "\t5 to compute an authentication tag (MAC) of a given file under a given passphrase.\n"
						+ "\t6 to generate an elliptic key pair from a given passphrase and write the public key to a file.\n"
						+ "\t7 to encrypt a data file under a given elliptic public key file.\n"
						+ "\t8 to decrypt a given elliptic-encrypted file from a given password.\n"
						+ "\t9 to encrypt text input under a given elliptic public key file.\n"
						+ "\t10 to decrypt text input from a given password.\n"
						+ "\t11 to sign a given file from a given password and write the signature to a file.\n"
						+ "\t12 verify a given data file and its signature file under a given public key file.\n"
						+ "\t22 to exit.");
					continue;
				
				// compute a plain cryptographic hash of a given file
				case 1:
					byte[] hash = Envelope.hash(dataBytes);
					
					for(int i = 0; i < hash.length; i++) {
						System.out.print(String.format("%02X ", hash[i]));
					}
					System.out.println();
					continue;
					
					
				
				// compute a plain cryptographic hash of a text input.
				case 2:
					
					System.out.print("Enter your data: ");
					// Take in new data
					String rawInput = consoleIn.nextLine();
					System.out.println();
					byte[] inputData = parseData(rawInput);
					hash = Envelope.hash(inputData);
					
					for(int i = 0; i < hash.length; i++) {
						System.out.print(String.format("%02X ", hash[i]));
					}
					System.out.println();
					continue;
					
					
					
				// encrypt a given data file symmetrically under a given passphrase.	
				case 3:
					// Uncomment and change dataBytes to inputDatato allow encrypting of text input data.
//					System.out.print("Enter your data: ");
//					rawInput = consoleIn.nextLine();
//					System.out.println();
//					inputData = parseData(rawInput);
					System.out.print("Enter your passphrase: ");
					byte[] pass = consoleIn.nextLine().getBytes();
					System.out.println();
					byte[] encrypted = Envelope.symEncrypt(pass, dataBytes);
					
					System.out.println("Encrypted Data: ");
					for(int i = 0; i < encrypted.length; i++) {
						System.out.print(String.format("%02X ", encrypted[i]));
					}
					
					try {
						FileWriter w = new FileWriter(fileName4);
						
						w.write(""); // Clear any old data
						
						for(int i = 0; i < encrypted.length; i++) {
							String temp = String.format("%02X ", encrypted[i]);
							w.append(temp);
						}
						w.close();
						System.out.println();
						System.out.println("Wrote to Encrypted.txt");
					} catch (IOException e) {
						System.out.println("Encrypted file not found.");
						continue;
					}
					continue;
					
				// decrypt a given data file symmetrically under a given passphrase.
				case 4:
					// Uncomment to allow decrypting of text input data.
//					System.out.print("Enter your encrypted data: ");
//					byte[] encData = parseData(consoleIn.nextLine());
//					System.out.println();
					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();
					
					try {
						File file4 = new File(fileName4);
						Scanner sc = new Scanner(file4);
						
						byte[] encData = parseData(sc.nextLine());
						
						byte[] decrypted = Envelope.symDecrypt(encData, pass);

						System.out.println("Decrypted Data: ");
						for(int i = 0; i < decrypted.length; i++) {
							System.out.print(String.format("%02X ", decrypted[i]));
						}
						System.out.println();
						
						sc.close();
						continue;
					} catch (FileNotFoundException e) {
						System.out.println("Couldn't retrieve information from Encrypted.txt");
						continue;
					}
					
					
					
				// compute an authentication tag (MAC) of a given file under a given passphrase.
				case 5:
					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();
					byte[] tag = Envelope.tag(pass, dataBytes);
					
					for(int i = 0; i < tag.length; i++) {
						System.out.print(String.format("%02X ", tag[i]));
					}
					System.out.println();
					continue;
					
					
					
				// generate an elliptic key pair from a given passphrase and write the public key to a file.
				case 6:
					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();
					kp = Envelope.keyPair(pass);
					
					try {
						FileWriter w = new FileWriter(fileName2);
						
						w.write(kp.getPoint().getX() + " " + kp.getPoint().getY());
						w.close();
						System.out.println("Wrote to PublicKey.txt");
					} catch (IOException e) {
						System.out.println("Public Key file not found.");
						continue;
					}
					continue;
					
				
					
				// encrypt a data file under a given elliptic public key file.
				case 7:
					Point p;
					try {
						File file2 = new File(fileName2);
						Scanner sc = new Scanner(file2);
						
						BigInteger x = new BigInteger(sc.next());
						BigInteger y = new BigInteger(sc.next());
						
						p = new Point(x, y);
						
						encrypted = Envelope.ecEncrypt(p, dataBytes);
						
						System.out.println("Encrypted Data: ");
						for(int i = 0; i < encrypted.length; i++) {
							System.out.print(String.format("%02X ", encrypted[i]));
						}
						
						try {
							FileWriter w = new FileWriter(fileName4);
							
							w.write(""); // Clear any old data
							
							for(int i = 0; i < encrypted.length; i++) {
								String temp = String.format("%02X ", encrypted[i]);
								w.append(temp);
							}
							w.close();
							System.out.println();
							System.out.println("Wrote to Encrypted.txt");
						} catch (IOException e) {
							System.out.println("Encrypted file not found.");
							continue;
						}
						
						sc.close();
						continue;
					} catch (FileNotFoundException e) {
						System.out.println("Couldn't retrieve information from PublicKey.txt");
						continue;
					}
					
					
					
				// decrypt a given elliptic-encrypted file from a given password.
				case 8:
					// Uncomment to allow decrypting of text input data.
//					System.out.print("Enter your encrypted data: ");
//					byte[] encData = parseData(consoleIn.nextLine());
//					System.out.println();
					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();
					
					try {
						File file4 = new File(fileName4);
						Scanner sc = new Scanner(file4);
						
						byte[] encData = parseData(sc.nextLine());
						
						byte[] decrypted = Envelope.ecDecrypt(encData, pass);

						System.out.println("Decrypted Data: ");
						for(int i = 0; i < decrypted.length; i++) {
							System.out.print(String.format("%02X ", decrypted[i]));
						}
						System.out.println();
						
						sc.close();
						continue;
					} catch (FileNotFoundException e) {
						System.out.println("Couldn't retrieve information from Encrypted.txt");
						continue;
					}
					
					
					
				// encrypt text input under a given elliptic public key file.
				case 9:
					try {
						File file2 = new File(fileName2);
						Scanner sc = new Scanner(file2);
						
						BigInteger x = new BigInteger(sc.next());
						BigInteger y = new BigInteger(sc.next());
						
						p = new Point(x, y);
						
						System.out.println("Enter your data: ");
						// Take in new data
						rawInput = consoleIn.nextLine();
						System.out.println();
						inputData = parseData(rawInput);
						
						encrypted = Envelope.ecEncrypt(p, inputData);
						
						System.out.println("Encrypted Data: ");
						for(int i = 0; i < encrypted.length; i++) {
							System.out.print(String.format("%02X ", encrypted[i]));
						}
						System.out.println();
						sc.close();
						continue;
					} catch (FileNotFoundException e) {
						System.out.println("Couldn't retrieve information from PublicKey.txt");
						continue;
					}
					
					
					
				// decrypt text input from a given password.
				case 10:
					System.out.print("Enter your encrypted data: ");
					byte[] encData = parseData(consoleIn.nextLine());
					System.out.println();
					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();
					byte[] decrypted = Envelope.ecDecrypt(encData, pass);
					
					System.out.println("Decrypted Data: ");
					for(int i = 0; i < decrypted.length; i++) {
						System.out.print(String.format("%02X ", decrypted[i]));
					}
					System.out.println();
					continue;
					
					
					
				// sign a given file from a given password and write the signature to a file.
				case 11:
					byte[] signature = Envelope.ecSign(kp.getPrivateKey().toByteArray(), dataBytes);
					
					System.out.println("Signature: ");
					for(int i = 0; i < signature.length; i++) {
						System.out.print(String.format("%02X ", signature[i]));
					}
					
					try {
						FileWriter w = new FileWriter(fileName3);
						
						w.write(""); // Clear any old signatures
						
						for(int i = 0; i < signature.length; i++) {
							String temp = String.format("%02X ", signature[i]);
							w.append(temp);
						}
						w.close();
						System.out.println();
						System.out.println("Wrote to Signature.txt");
					} catch (IOException e) {
						System.out.println("Public Key file not found.");
						continue;
					}
					continue;
					
					
					
				// verify a given data file and its signature file under a given public key file.
				case 12:
					try {
						File file2 = new File(fileName2);
						Scanner sc = new Scanner(file2);
						
						BigInteger x = new BigInteger(sc.next());
						BigInteger y = new BigInteger(sc.next());
						
						p = new Point(x, y);
						
						sc.close();
						File file3 = new File(fileName3);
						sc = new Scanner(file3);
						
						byte[] sig = parseData(sc.nextLine());
						
						boolean verified = Envelope.ecVerify(sig, dataBytes, p);
						
						System.out.println("Signature Verified: " + verified);
						
						sc.close();
						continue;
					} catch (FileNotFoundException e) {
						System.out.println("Couldn't retrieve information from PublicKey.txt");
						continue;
					}
					
					
				default: 
					System.out.println("Please enter a valid integer 0-12.");
					continue;
			}
		}
		
		consoleIn.close();
	}

}
