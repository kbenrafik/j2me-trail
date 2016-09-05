package com.card;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class CreditCard extends Applet {
	final static byte CARD_CLA = (byte) 0xB0;
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

	// maximum balance
	final static short MAX_BALANCE = 0x7FFF;
	// maximum transaction amount
	final static byte MAX_TRANSACTION_AMOUNT = 127;
	// maximum number of incorrect tries before the
		
	// amount > MAX_TRANSACTION_AMOUNT or amount < 0
	final static short SW_INVALID_TRANSACTION_AMOUNT = 0x6A83;
	// signal that the balance exceed the maximum
	final static short SW_EXCEED_MAXIMUM_BALANCE = 0x6A84;
	// signal the the balance becomes negative
	final static short SW_NEGATIVE_BALANCE = 0x6A85;

	short balance;
	private byte[] pin = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };

	private CreditCard() {
		register();
	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new CreditCard();
	}

	public void process(APDU apdu) {
		byte[] buffer = apdu.getBuffer();
		if (this.selectingApplet())
			return;
		if (buffer[ISO7816.OFFSET_CLA] != CARD_CLA) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}
		switch (buffer[ISO7816.OFFSET_INS]) {
		case INS_CONSULTER_SOLDE:
			consulterSolde(apdu);
			break;

		case INS_TIRER_SOLDE:
			tirerArgent(apdu);
			break;

		case INS_VERSER_SOLDE:
			verserSolde(apdu);
			break;
		case INS_CONSULTER_PIN:
			consulterPin(apdu);
			break;
		case INS_MODIFIER_PIN:
			modifierPin(apdu);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	private void verserSolde(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		// Lc byte denotes the number of bytes in the
		// data field of the command APDU
		byte numBytes = buffer[ISO7816.OFFSET_LC];

		// indicate that this APDU has incoming data
		// and receive data starting from the offset
		// ISO7816.OFFSET_CDATA following the 5 header
		// bytes.
		byte byteRead = (byte) (apdu.setIncomingAndReceive());

		// it is an error if the number of data bytes
		// read does not match the number in Lc byte
		if ((numBytes != 1) || (byteRead != 1)) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}

		// get the credit amount
		byte creditAmount = buffer[ISO7816.OFFSET_CDATA];

		// check the credit amount
		if ((creditAmount > MAX_TRANSACTION_AMOUNT) || (creditAmount < 0)) {
			ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
		}

		// check the new balance
		if ((short) (balance + creditAmount) > MAX_BALANCE) {
			ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
		}

		// credit the amount
		balance = (short) (balance + creditAmount);

	} // end of deposit method

	private void tirerArgent(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte numBytes = (buffer[ISO7816.OFFSET_LC]);

		byte byteRead = (byte) (apdu.setIncomingAndReceive());

		if ((numBytes != 1) || (byteRead != 1)) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}

		// get debit amount
		byte debitAmount = buffer[ISO7816.OFFSET_CDATA];

		// check debit amount
		if ((debitAmount > MAX_TRANSACTION_AMOUNT) || (debitAmount < 0)) {
			ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
		}

		// check the new balance
		if ((short) (balance - debitAmount) < (short) 0) {
			ISOException.throwIt(SW_NEGATIVE_BALANCE);
		}

		balance = (short) (balance - debitAmount);

	} // end of debit method

	private void consulterSolde(APDU apdu) {

		byte[] buffer = apdu.getBuffer();
		short le = apdu.setOutgoing();
		if (le < 2) {
			ISOException.throwIt((byte) 0x6A86);
		}

		// informs the CAD the actual number of bytes
		// returned
		apdu.setOutgoingLength((byte) 2);

		// move the balance data into the APDU buffer
		// starting at the offset 0
		buffer[0] = (byte) (balance >> 8);
		buffer[1] = (byte) (balance & 0xFF);

		// send the 2-byte balance at the offset
		// 0 in the apdu buffer
		apdu.sendBytes((short) 0, (short) 2);

	}

	private void consulterPin(APDU apdu) {
		byte[] buffer = apdu.getBuffer();
		buffer = apdu.getBuffer();
		short l;
		l = (short) pin.length;
		Util.arrayCopyNonAtomic(pin, (short) 0, buffer, (short) 0, (short) l);
		apdu.setOutgoingAndSend((short) 0, l);
	}

	private void modifierPin(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte numBytes = (buffer[ISO7816.OFFSET_LC]);

		byte byteRead = (byte) (apdu.setIncomingAndReceive());

		if ((numBytes != 4) || (byteRead != 4)) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}
		
		byte[] nouveauPin = {buffer[ISO7816.OFFSET_CDATA+0],buffer[ISO7816.OFFSET_CDATA+1],buffer[ISO7816.OFFSET_CDATA+2], buffer[ISO7816.OFFSET_CDATA+3]};
		pin=nouveauPin;
	}
}