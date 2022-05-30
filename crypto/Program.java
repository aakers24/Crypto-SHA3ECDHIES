package crypto;

import crypto.Envelope;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Program {

	public static void main(String[] args) {

		boolean runGUI = true;

		if (runGUI) {
			System.out.println("gui");

		} else {


			Scanner consoleIn = new Scanner(System.in);
			int option = -1;

			// Opening files
			String fileName = System.getProperty("user.dir") + "/crypto/" + "Data.txt";

			String fileName2 = System.getProperty("user.dir") + "/crypto/" + "Passphrase.txt";

			File file = new File(fileName);

			File file2 = new File(fileName2);

			byte[] dataBytes = new byte[1];

			byte[] passBytes = new byte[1];

			try {
				Scanner scanner = new Scanner(file);

				byte[] inputByteBuffer = new byte[256];

				int i = 0;

				while (scanner.hasNext()) {
					String inByte = scanner.next();

					inputByteBuffer[i] = (byte) Integer.parseInt(inByte, 16);

					i++;
				}

				dataBytes = new byte[i];

				System.arraycopy(inputByteBuffer, 0, dataBytes, 0, i);

				scanner = new Scanner(file2);

				inputByteBuffer = new byte[256];

				i = 0;

				while (scanner.hasNext()) {
					String inByte = scanner.next();

					inputByteBuffer[i] = (byte) Integer.parseInt(inByte, 16);

					i++;
				}

				passBytes = new byte[i];

				System.arraycopy(inputByteBuffer, 0, passBytes, 0, i);

				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// Opening files

			boolean loop = true;
			while (loop) {
				System.out.println("Pick an operation 1-12. To see the list of operations pick 0. To exit pick 22.");
				option = -1;

				try {
					option = consoleIn.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("Please enter an integer.");
					consoleIn.next();
					continue;
				}

				switch (option) {
					case (22):
						loop = false;
						break;

					case 0:
						System.out.println("Options:\n\t0 to see the list of operations.\n"
								+ "\t1 to compute a plain cryptographic hash of a given file.\n"
								+ "\t2 to compute a plain cryptographic hash of a text input.\n"
								+ "\t3 to encrypt a given data file symmetrically under a given passphrase.\n"
								+ "\t4 to decrypt a given data file symmetrically under a given passphrase.\n"
								+ "\t5 to compute an authentication tag (MAC) of a given file under a given passphrase.\n"
								+ "\t6 to generate an elliptic key pair from a given passphrase and write the public key to a file.\n" // !!!! THIS NEEDS TO WRITE TO A FILE. EXTRA CREDIT PART TOO? !!!!
								+ "\t7 to encrypt a data file under a given elliptic public key file.\n"
								+ "\t8 to decrypt a given elliptic-encrypted file from a given password.\n"
								+ "\t9 to encrypt text input under a given elliptic public key file.\n"
								+ "\t10 to decrypt text input from a given password.\n"
								+ "\t11 to sign a given file from a given password and write the signature to a file.\n"
								+ "\t12 verify a given data file and its signature file under a given public key file.\n"
								+ "\t22 to exit.");
						continue;

					case 1:
						byte[] hash = Envelope.hash(dataBytes);

						for (int i = 0; i < hash.length; i++) {
							System.out.print(String.format("%02X ", hash[i]));
						}
						System.out.println("\n");
						continue;


						// This is NOT functional
					case 2:
						byte[] input = new byte[0];
						int inPos = 0;
						while (consoleIn.hasNextByte()) {
							input[inPos] = consoleIn.nextByte();
							inPos++;
						}
						hash = Envelope.hash(input);

						for (int i = 0; i < hash.length; i++) {
							System.out.print(String.format("%02X ", hash[i]));
						}

					default:
						System.out.println("Please enter a valid integer 0-12.");
						continue;
				}
			}

			consoleIn.close();
		}
	}

}
