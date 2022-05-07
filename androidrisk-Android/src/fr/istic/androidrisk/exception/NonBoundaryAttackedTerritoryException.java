package fr.istic.androidrisk.exception;

public class NonBoundaryAttackedTerritoryException extends Exception {

	private static final long serialVersionUID = 42;

	public NonBoundaryAttackedTerritoryException()
	{
		super("Erreur, capture de territoire entre territoire non-limitrophes !");
		System.out.println("Erreur, capture de territoire entre territoire non-limitrophes !");
	}
}
