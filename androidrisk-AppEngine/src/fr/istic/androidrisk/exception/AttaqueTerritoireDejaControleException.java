package fr.istic.androidrisk.exception;

public class AttaqueTerritoireDejaControleException extends Exception {

	private static final long serialVersionUID = 42;

	public AttaqueTerritoireDejaControleException()
	{
		super("Vous essayez d'attaquer un territoire que vous contrôler déjà ce qui, en plus d'être impossible, est stupide.");
		System.out.println("Vous essayez d'attaquer un territoire que vous contrôler déjà ce qui, en plus d'être impossible, est stupide.");
	}
	
}
