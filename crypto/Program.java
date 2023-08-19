package crypto;

import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
 * Depending on the environment the program is being run in, I have provided 2 ways of accessing the file locations.
 * These were the ways I got it to work in the different environments I tested the program in.
 * Again, more flexible, creative, and elegant solutions are possible, but this is the functionality as it is provided.
 *
 * */



/*
* There are a few major bugs present now as a result of changes made to advance the functionalities.
* Text is currently the only format that can be input and get the exact same output,
* 	but the main upcoming feature is adding filetypes to behave the same way.
*
* See ToDo.txt
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

	private static byte[] selectDataFile() {
		byte[] ba = new byte[]{};
		boolean fileAccepted = false;
		while(!fileAccepted) {
			System.out.println("Please select a data file.");

			// Select a data file
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			JDialog dialog = new JDialog();
			int returnVal = chooser.showOpenDialog(dialog);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			} else {
				System.out.println("Cancelled");
			}

			// Getting the bytes from the selected file
			try {
				File file = chooser.getSelectedFile();

				//String fileString = Files.readString(Paths.get(file.getAbsolutePath()));
				ba = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
				//ba = fileString.getBytes();

				fileAccepted = true;
			} catch (IOException e) {
				System.out.println("File not accepted, please try again.");
				fileAccepted = false;
			}
		}
		return ba;
	}

	private static void saveFile(byte[] ba, Scanner input) {
		boolean fileWritten = false;
		while (!fileWritten) {
			try {

				System.out.println();
				System.out.print("Please enter the file save location: ");
				String filePath = Paths.get(input.nextLine()).toString();

				// Identify file type and append the extension to the path.
				// https://en.wikipedia.org/wiki/List_of_file_signatures

				if ( ba.length < 4 ) { // The file is too short to be anything too meaningful other than probably a couple characters.
					filePath += ".txt";
				} else if ( String.format("%02X", ba[0]).equals("FF") && String.format("%02X", ba[1]).equals("D8") ) { // jpeg
					filePath += ".jpg";
				} else if ( String.format("%02X", ba[0]).equals("1F") && ( String.format("%02X", ba[1]).equals("9D") || String.format("%02X", ba[1]).equals("A0") ) ) { // .tar.z
					filePath += ".tar.z";
				} else if ( String.format("%02X", ba[0]).equals("4D") && String.format("%02X", ba[1]).equals("5A") ) { // exe, dll, and more but most likely exe
					filePath += ".exe";
				} else if ( String.format("%02X", ba[0]).equals("50") && String.format("%02X", ba[1]).equals("4B") ) { // zip
					filePath += ".zip";
				} else if ( String.format("%02X", ba[0]).equals("52") && String.format("%02X", ba[1]).equals("61") ) { // rar
					filePath += ".rar";
				} else if ( String.format("%02X", ba[0]).equals("7F") && String.format("%02X", ba[1]).equals("45") ) { // .elf
					filePath += ".elf";
				} else if ( String.format("%02X", ba[0]).equals("89") && String.format("%02X", ba[1]).equals("50") ) { // .png
					filePath += ".png";
				} else if ( String.format("%02X", ba[0]).equals("25") && String.format("%02X", ba[1]).equals("50") ) { // .pdf
					filePath += ".pdf";
				} else if ( String.format("%02X", ba[0]).equals("52") && String.format("%02X", ba[1]).equals("49") ) { // This could be like 6 different things but for my purposes .wav is most likely
					filePath += ".elf";
				} else if ( String.format("%02X", ba[0]).equals("49") && String.format("%02X", ba[1]).equals("44") ) { // .mp3
					filePath += ".mp3";
				} else if ( String.format("%02X", ba[0]).equals("D0") && String.format("%02X", ba[1]).equals("CF") ) { // .doc / .xls
					filePath += ".doc";
				} else if ( String.format("%02X", ba[0]).equals("75") && String.format("%02X", ba[1]).equals("73") ) { // .tar
					filePath += ".tar";
				} else if ( String.format("%02X", ba[0]).equals("37") && String.format("%02X", ba[1]).equals("7A") ) { // .7z
					filePath += ".7z";
				} else if ( String.format("%02X", ba[0]).equals("1F") && String.format("%02X", ba[1]).equals("8B") ) { // .gz / .tar.gz
					filePath += ".gz";
				} else if ( String.format("%02X", ba[0]).equals("FD") && String.format("%02X", ba[1]).equals("37") ) { // .xz / .tar.xz
					filePath += ".xz";
				} else {
					filePath += ".txt";
				}

				FileOutputStream output = new FileOutputStream(filePath);
				output.write(ba);

				System.out.println();

				fileWritten = true;
				output.close();

			} catch (NullPointerException n) {
				System.out.println("Please enter a valid file location.");
				continue;
			} catch (IOException ioe) {
				System.out.println("Couldn't save the file.");
				continue;
			}
		}
	}

	public static void main(String[] args) {

		Scanner consoleIn = new Scanner(System.in);
		int option = -1;
		int inputOption = -1;
		KeyPair kp = null;



		// Selecting a data file and
		byte[] dataBytes = new byte[]{};
		while(dataBytes.length == 0) {
			System.out.println("-----\nWould you like to work with a file or text input? Enter 1 for file and 2 for text.");
			try {
				inputOption = consoleIn.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Please enter an integer.");
				consoleIn.next();
				continue;
			}
			if (inputOption == 1) { // File Picker
				try {
					dataBytes = selectDataFile();
				} catch (NullPointerException e) {
					System.out.println("A file wasn't chosen.");
					continue;
				}
			} else if (inputOption == 2) { // Text was chosen
				System.out.print("Enter your data: ");
				consoleIn.nextLine();
				// Take in new data
				String rawInput = consoleIn.nextLine();
				//byte[] inputData = parseData(rawInput);
				dataBytes = rawInput.getBytes();
			} else {
				continue;
			}

			// For debugging. Files and text with same data should print the same results at this stage. If not, there is something wrong with data intake.
//			for(int i = 0; i < dataBytes.length; i++) {
//				System.out.print(String.format("%02X ", dataBytes[i]));
//			}

			inputOption = -1;
		}



		String fileName2 = "PublicKey";

		String fileName3 = "Signature";

		String fileName4 = "Encrypted";



		// Main Program Loop
		boolean loop = true;
		while(loop) {
			System.out.println("\nPick an operation 1-9. To see the list of operations pick 0. To exit pick 22.");
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
				case 21:
					dataBytes = new byte[]{};
					while(dataBytes.length == 0) {
						System.out.println("-----\nWould you like to work with a file or text input? Enter 1 for file and 2 for text.");
						try {
							inputOption = consoleIn.nextInt();
						} catch (InputMismatchException e) {
							System.out.println("Please enter an integer.");
							consoleIn.next();
							continue;
						}
						if (inputOption == 1) { // File Picker
							try {
								dataBytes = selectDataFile();
							} catch (NullPointerException e) {
								System.out.println("A file wasn't chosen.");
								continue;
							}
						} else if (inputOption == 2) { // Text was chosen
							System.out.print("Enter your data: ");
							consoleIn.nextLine();
							// Take in new data
							String rawInput = consoleIn.nextLine();
							//byte[] inputData = parseData(rawInput);
							dataBytes = rawInput.getBytes();
						} else {
							continue;
						}
						continue;
					}
					continue;

				case 20:
					for(int i = 0; i < dataBytes.length; i++) {
						System.out.print(String.format("%02X ", dataBytes[i]));
					}
					continue;

				case 22:
					loop = false;
					System.exit(0);
					break;

				case 0: System.out.println("Options:\n\t0 to see the list of operations.\n"
						+ "\t1 to compute a plain cryptographic hash of a given input.\n"
						+ "\t2 to encrypt a given data input symmetrically under a given passphrase.\n"
						+ "\t3 to decrypt a given data input symmetrically under a given passphrase.\n"
						+ "\t4 to compute an authentication tag (MAC) of a given input under a given passphrase.\n"
						+ "\t5 to generate an elliptic key pair from a given passphrase and write the public key to a file.\n"
						+ "\t6 to encrypt a data input under a given elliptic public key file.\n"
						+ "\t7 to decrypt a given elliptic-encrypted input from a given password.\n"
						+ "\t8 to sign a given input from a given password and write the signature to a file.\n"
						+ "\t9 to verify a given data file and its signature file under a given public key file.\n"
						+ "\t20 to display the current input data in bytes.\n"
						+ "\t21 to select a new data input.\n"
						+ "\t22 to exit.");
					continue;

					// compute a plain cryptographic hash of a given input
				case 1:
					byte[] hash = Envelope.hash(dataBytes);

					for(int i = 0; i < hash.length; i++) {
						System.out.print(String.format("%02X ", hash[i]));
					}
					System.out.println();
					continue;



					// encrypt given data symmetrically under a given passphrase.
				case 2:
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
						System.out.println("Wrote to Encrypted");
					} catch (IOException e) {
						System.out.println("Encrypted file not found.");
						continue;
					}
					continue;

					// decrypt a given data file symmetrically under a given passphrase.
				case 3:
					int decryptOption = -1;
					while(decryptOption == -1) {
						System.out.println("What would you like to decrypt? Enter 1 for a file, 2 for text input, or 3 for the default Encrypted file.");

						try {
							decryptOption = consoleIn.nextInt();
						} catch (InputMismatchException e) {
							System.out.println("Please enter an integer.");
							consoleIn.next();
							decryptOption = -1;
							continue;
						}
						consoleIn.nextLine();
					}

					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();

					if (decryptOption == 1) {
						// Select a data file
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
						JDialog dialog = new JDialog();
						int returnVal = chooser.showOpenDialog(dialog);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser.getSelectedFile();
							System.out.println("Selected file: " + selectedFile.getAbsolutePath());
							try {
								Scanner sc = new Scanner(selectedFile);

								byte[] encData = parseData(sc.nextLine());

								byte[] decrypted = Envelope.symDecrypt(encData, pass);

								saveFile(decrypted, consoleIn);

								System.out.println("Decrypted Data: ");
								try {
									System.out.println(new String(decrypted, "UTF-8"));
								} catch (UnsupportedEncodingException e){
									System.out.println("There was a problem returning your data.");
								}

								sc.close();
								continue;

							} catch (NullPointerException e) {
								System.out.println("Couldn't decrypt file.");
								decryptOption = -1;
								continue;
							} catch (FileNotFoundException f) {
								System.out.println("Couldn't decrypt file.");
								decryptOption = -1;
								continue;
							}
						} else {
							System.out.println("Cancelled");
						}
					}

					if (decryptOption == 2) {
						// Uncomment to allow decrypting of text input data.
						System.out.print("Enter your encrypted data: ");
						byte[] encData = parseData(consoleIn.nextLine());
						byte[] decrypted = Envelope.symDecrypt(encData, pass);
						System.out.println();

						saveFile(decrypted, consoleIn);

						System.out.println("Decrypted Data: ");
						try {
							System.out.println(new String(decrypted, "UTF-8"));
						} catch (UnsupportedEncodingException e){
							System.out.println("There was a problem returning your data.");
						}
						continue;
					}

					if (decryptOption == 3) {
						try {
							File file4 = new File(fileName4);
							Scanner sc = new Scanner(file4);

							byte[] encData = parseData(sc.nextLine());

							byte[] decrypted = Envelope.symDecrypt(encData, pass);

							saveFile(decrypted, consoleIn);

							System.out.println("Decrypted Data: ");
							try {
								System.out.println(new String(decrypted, "UTF-8"));
							} catch (UnsupportedEncodingException e){
								System.out.println("There was a problem returning your data.");
							}

							sc.close();
							continue;
						} catch (FileNotFoundException e) {
							System.out.println("Couldn't retrieve information from Encrypted");
							continue;
						}
					}



					// compute an authentication tag (MAC) of a given input under a given passphrase.
				case 4:
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
				case 5:
					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();
					kp = Envelope.keyPair(pass);

					try {
						FileWriter w = new FileWriter(fileName2);

						w.write(kp.getPoint().getX() + " " + kp.getPoint().getY());
						w.close();
						System.out.println("Wrote to PublicKey");
					} catch (IOException e) {
						System.out.println("Public Key file not found.");
						continue;
					}
					continue;



					// encrypt a data input under a given elliptic public key file.
				case 6:
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
							System.out.println("Wrote to Encrypted");
						} catch (IOException e) {
							System.out.println("Encrypted file not found.");
							continue;
						}

						sc.close();
						continue;
					} catch (FileNotFoundException e) {
						System.out.println("Couldn't retrieve information from PublicKey");
						continue;
					}



					// decrypt a given elliptic-encrypted input from a given password.
				case 7:
					decryptOption = -1;
					while(decryptOption == -1) {
						System.out.println("What would you like to decrypt? Enter 1 for a file, 2 for text input, or 3 for the default Encrypted file.");

						try {
							decryptOption = consoleIn.nextInt();
						} catch (InputMismatchException e) {
							System.out.println("Please enter an integer.");
							consoleIn.next();
							decryptOption = -1;
							continue;
						}
						consoleIn.nextLine();
					}

					System.out.print("Enter your passphrase: ");
					pass = consoleIn.nextLine().getBytes();
					System.out.println();

					if (decryptOption == 1) {
						// Select a data file
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
						JDialog dialog = new JDialog();
						int returnVal = chooser.showOpenDialog(dialog);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser.getSelectedFile();
							System.out.println("Selected file: " + selectedFile.getAbsolutePath());
							try {
								Scanner sc = new Scanner(selectedFile);

								byte[] encData = parseData(sc.nextLine());

								byte[] decrypted = Envelope.ecDecrypt(encData, pass);

								saveFile(decrypted, consoleIn);

								System.out.println("Decrypted Data: ");
								try {
									System.out.println(new String(decrypted, "UTF-8"));
								} catch (UnsupportedEncodingException e){
									System.out.println("There was a problem returning your data.");
								}

								sc.close();
								continue;

							} catch (NullPointerException e) {
								System.out.println("Couldn't decrypt file.");
								decryptOption = -1;
								continue;
							} catch (FileNotFoundException f) {
								System.out.println("Couldn't decrypt file.");
								decryptOption = -1;
								continue;
							}
						} else {
							System.out.println("Cancelled");
						}
					}

					if (decryptOption == 2) {
						// Uncomment to allow decrypting of text input data.
						System.out.print("Enter your encrypted data: ");
						byte[] encData = parseData(consoleIn.nextLine());
						byte[] decrypted = Envelope.ecDecrypt(encData, pass);
						System.out.println();

						saveFile(decrypted, consoleIn);

						System.out.println("Decrypted Data: ");
						try {
							System.out.println(new String(decrypted, "UTF-8"));
						} catch (UnsupportedEncodingException e){
							System.out.println("There was a problem returning your data.");
						}
						continue;
					}

					if (decryptOption == 3) {
						try {
							File file4 = new File(fileName4);
							Scanner sc = new Scanner(file4);

							byte[] encData = parseData(sc.nextLine());

							byte[] decrypted = Envelope.ecDecrypt(encData, pass);

							saveFile(decrypted, consoleIn);

							System.out.println("Decrypted Data: ");
							try {
								System.out.println(new String(decrypted, "UTF-8"));
							} catch (UnsupportedEncodingException e){
								System.out.println("There was a problem returning your data.");
							}

							sc.close();
							continue;
						} catch (FileNotFoundException e) {
							System.out.println("Couldn't retrieve information from Encrypted");
							continue;
						}
					}



					// sign a given file from a given password and write the signature to a file.
				case 8:
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
						System.out.println("Wrote to Signature");
					} catch (IOException e) {
						System.out.println("Public Key file not found.");
						continue;
					}
					continue;



					// verify a given data file and its signature file under a given public key file.
				case 9:
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
						System.out.println("Couldn't retrieve information from PublicKey");
						continue;
					}


				default:
					System.out.println("Please enter a valid integer 0-9.");
					continue;
			}
		}

		consoleIn.close();
		return;
	}

}