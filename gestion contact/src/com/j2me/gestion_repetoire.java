package com.j2me;

import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class gestion_repetoire extends MIDlet implements CommandListener {
	private Display display = null;
	private List liste;
	private Command cmdExit;
	private Command cmdAjouter;
	//private Alert alert;
	private Form FormText;
	TextField NomTextField;
	TextField PrenomTextField;
	TextField TelephoneTextField;
	//private Vector vectorContact;

	public gestion_repetoire() {
		// TODO Auto-generated constructor stub
		//vectorContact = new Vector();
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		liste = new List("contact list", List.IMPLICIT,null,null);
		cmdExit = new Command("Exit", Command.EXIT, 1);
		cmdAjouter = new Command("Ajouter", Command.SCREEN, 1);
		
		
		FormText = new Form("MyForm");
		NomTextField = new TextField("Nom", null, 20, 0);
		PrenomTextField = new TextField("Prénom", null, 20, 0);
		TelephoneTextField = new TextField("Télephone", null, 20, 0);
		
		FormText.append(NomTextField);
		FormText.append(PrenomTextField);
		FormText.append(TelephoneTextField);

				
		
		cmdAjouter = new Command("Modifier", Command.SCREEN, 1);
		FormText.addCommand(cmdExit);
		FormText.addCommand(cmdAjouter);
		FormText.setCommandListener(this);
		
		liste.addCommand(cmdExit);
		liste.addCommand(cmdAjouter);
		liste.setCommandListener(this);
		
		display = Display.getDisplay(this);
		display.setCurrent(liste);
		
	}

	public void commandAction(Command arg0, Displayable arg1) {
		/*
		if (arg0 == cmdExit) {
			try {
				destroyApp(false);
				notifyDestroyed();
			} catch (MIDletStateChangeException e) {
				e.printStackTrace();
			}

		} else if (arg0 == cmdAjouter) {
			Contact contact = new Contact(NomTextField.getString(),
					PrenomTextField.getString(), TelephoneTextField.getString());
			vectorContact.addElement(contact);
		}
		*/
	}

}
