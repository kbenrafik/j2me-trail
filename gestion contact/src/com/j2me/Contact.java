package com.j2me;

public class Contact {
	
	private String nomContact,prenomContact,numTelephone;

	public String getNomContact() {
		return nomContact;
	}

	public void setNomContact(String nomContact) {
		this.nomContact = nomContact;
	}

	public String getPrenomContact() {
		return prenomContact;
	}

	public void setPrenomContact(String prenomContact) {
		this.prenomContact = prenomContact;
	}

	public String getNumTelephone() {
		return numTelephone;
	}

	public void setNumTelephone(String numTelephone) {
		this.numTelephone = numTelephone;
	}

	public Contact(String nomContact, String prenomContact, String numTelephone) {
		super();
		this.nomContact = nomContact;
		this.prenomContact = prenomContact;
		this.numTelephone = numTelephone;
	}

	public Contact() {
		super();
		
	}
	
	

}
