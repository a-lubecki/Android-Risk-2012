package fr.istic.androidrisk.exception;

public class PasAssezDUniteException extends Exception {

	private static final long serialVersionUID = 42;
	
	public PasAssezDUniteException(){
		super("Vous essayez d'attaquer depuis un territoire ne contenant qu'une unité, ce qui est impossible...");
		System.out.println("Vous essayez d'attaquer depuis un territoire ne contenant qu'une unité, ce qui est impossible...");
	}

}
