package fr.istic.androidrisk.exception;

public class AlreadyControledAttackedTerritoryException extends Exception {

	private static final long serialVersionUID = 42;

	public AlreadyControledAttackedTerritoryException()
	{
		super("Vous essayez d'attaquer un territoire que vous contrôlez.");
		System.out.println("Vous essayez d'attaquer un territoire que vous contrôlez.");
	}

}
