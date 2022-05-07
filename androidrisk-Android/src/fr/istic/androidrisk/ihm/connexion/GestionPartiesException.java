package fr.istic.androidrisk.ihm.connexion;

public class GestionPartiesException extends Exception {

	private static final long serialVersionUID = 222222;

	public GestionPartiesException() {
		super("Erreur lors de la gestion des parties.");
	}
	public GestionPartiesException(String exception){
		super(exception);
	}
	
}
