package fr.istic.androidrisk.exception;

public class TerritoireNonLimitropheAttaqueException extends Exception {

	private static final long serialVersionUID = 42;

	public TerritoireNonLimitropheAttaqueException()
	{
		super("Erreur, capture de territoire entre territoire non-limitrophes !");
		System.out.println("Erreur, capture de territoire entre territoire non-limitrophes !");
	}
}
