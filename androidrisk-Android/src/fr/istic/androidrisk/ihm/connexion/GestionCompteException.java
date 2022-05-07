package fr.istic.androidrisk.ihm.connexion;

public class GestionCompteException extends Exception {

	private static final long serialVersionUID = 111111;

	public GestionCompteException() {
		super("Erreur lors de la gestion du compte.");
	}
	public GestionCompteException(String exception){
		super(exception);
	}

}
