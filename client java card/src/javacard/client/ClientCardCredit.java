package javacard.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;

public class ClientCardCredit {
	public static final byte CREDIT_CARD_TEST = (byte) 0xB0;
	final static byte INS_CONSULTER_SOLDE = (byte) 0x01;
	// 0xB0 0x01 0x00 0x00 0x00 0xFF;
	final static byte INS_VERSER_SOLDE = (byte) 0x02;
	// 0xB0 0x02 0x00 0x00 0x01 0x55 0x00;
	final static byte INS_TIRER_SOLDE = (byte) 0x03;
	// 0xB0 0x03 0x00 0x00 0x01 0x05 0x00;

	final static byte INS_CONSULTER_PIN = (byte) 0x11;
	// 0xB0 0x11 0x00 0x00 0x00 0x00;
	final static byte INS_MODIFIER_PIN = (byte) 0x22;
	// 0xB0 0x22 0x00 0x00 0x04 0x05 0x09 0x07 0x03 0x00;

	public static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		CadT1Client cad;
		Socket sckCarte;
		byte[] donnes;
		try {
			// connexion client
			sckCarte = new Socket("localhost", 9025);
			sckCarte.setTcpNoDelay(true);
			BufferedInputStream input = new BufferedInputStream(
					sckCarte.getInputStream());
			BufferedOutputStream ouput = new BufferedOutputStream(
					sckCarte.getOutputStream());
			cad = new CadT1Client(input, ouput);
			System.out.println("la connexion est etablie");
			// Selection de l'apdu
			Apdu apdu = new Apdu();
			apdu.command[Apdu.CLA] = 0x00;
			apdu.command[Apdu.INS] = (byte) 0xA4;
			apdu.command[Apdu.P1] = 0x04;
			apdu.command[Apdu.P2] = 0x00;
			byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
					0x08, 0x09, 0x00, 0x00 };
			apdu.setDataIn(appletAID);
			cad.powerUp();
			cad.exchangeApdu(apdu);
			System.out.println(apdu.getStatus());
			if (apdu.getStatus() != 0x9000) {
				System.out.println("erreur de la selection de l'applet");
				System.exit(1);
			}
			apdu = new Apdu();
			apdu.command[Apdu.CLA] = CREDIT_CARD_TEST;
			apdu.command[Apdu.P1] = 0x00;
			apdu.command[Apdu.P2] = 0x00;

			apdu.command[Apdu.INS] = INS_CONSULTER_PIN;
			cad.exchangeApdu(apdu);
			if (apdu.getStatus() != 0x9000) {
				System.out.println("Erreur: Probleme de consultation de pin");
			}
			byte[] pinCard;
			pinCard = apdu.getDataOut();
			String pinCardString = "";
			for (int i = 0; i < pinCard.length; i++) {
				pinCardString = pinCardString + pinCard[i];
			}

			boolean f = false;
			while (!f) {
				System.out.println("saisir code PIN : ");
				String pin = sc.next();
				if (pinCardString.equals(pin)) {
					f = true;
				}
			}

			boolean fin = false;
			while (!fin) {
				System.out
						.println("-------------------------------------------------");
				System.out
						.println("----------------Menu Principale------------------");
				System.out.println("--------1:Conulter le solde");
				System.out.println("--------2:Verser d'argent");
				System.out.println("--------3:tirer d'argent");
				System.out.println("--------4:modifier Code Pin");
				System.out.println("--------5:consulter Code Pin");
				System.out.println("--------6:Quitter");
				System.out
						.println("-------------------------------------------------");

				System.out.println("Entrer votre choix 1....6");
				int choix = System.in.read();
				while (!(choix >= '1' && choix <= '5')) {
					choix = System.in.read();
				}
				apdu = new Apdu();
				apdu.command[Apdu.CLA] = CREDIT_CARD_TEST;
				apdu.command[Apdu.P1] = 0x00;
				apdu.command[Apdu.P2] = 0x00;

				switch (choix) {
				case '1':
					apdu.command[Apdu.INS] = INS_CONSULTER_SOLDE;
					cad.exchangeApdu(apdu);
					if (apdu.getStatus() != 0x9000) {
						System.out.println("erreur : probleme de consultation");
					} else {
						System.out.println("le solde est : " + apdu.dataOut[1]);
					}
					break;
				case '2':
					apdu.command[Apdu.INS] = INS_VERSER_SOLDE;
					donnes = new byte[1];
					System.out.println("saisir le montant à verser :");
					int argent = sc.nextInt();
					donnes[0] = (byte) argent;
					apdu.setDataIn(donnes);

					cad.exchangeApdu(apdu);
					if (apdu.getStatus() == 0x9000) {
						System.out.println("bien verser");
					} else {
						System.out.println("erreur : probleme");
					}
					break;
				case '3':
					apdu.command[Apdu.INS] = INS_TIRER_SOLDE;
					System.out.println("saisir le montant à retirer :");
					donnes = new byte[1];
					int argentARetirer = sc.nextInt();
					donnes[0] = (byte) argentARetirer;
					apdu.setDataIn(donnes);
					cad.exchangeApdu(apdu);
					if (apdu.getStatus() == 0x9000) {
						System.out.println("bien retirer");
					} else {
						System.out.println("erreur : probleme");
					}
					break;
				case '4':
					apdu.command[Apdu.INS] = INS_MODIFIER_PIN;
					donnes = new byte[4];
					System.out
							.println("saisir le code nouveau code PIN 4 chiffres : ");
					String nouveauPin = sc.next();
					if (nouveauPin.length() == 4) {
						for (int i = 0; i < 4; i++) {
							donnes[i] = (byte) nouveauPin.charAt(i);
						}
						apdu.setDataIn(donnes);
						cad.exchangeApdu(apdu);
						if (apdu.getStatus() == 0x9000) {
							System.out.println("Pin modifier");
						} else {
							System.out.println("erreur : probleme");
						}
					} else {
						System.out.println("PIN doit etre 4 chiffre");
					}

					break;
				case '5':
					apdu.command[Apdu.INS] = INS_CONSULTER_PIN;
					cad.exchangeApdu(apdu);
					if (apdu.getStatus() == 0x9000) {
						System.out.println("code Pin est : " + apdu.dataOut[0]
								+ apdu.dataOut[1] + apdu.dataOut[2]
								+ apdu.dataOut[3]);
					} else {
						System.out.println("erreur : probleme de consultation");
					}
					break;
				case '6':
					fin = true;
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}

	}
}
