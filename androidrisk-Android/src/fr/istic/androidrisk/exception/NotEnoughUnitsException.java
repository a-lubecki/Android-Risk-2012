package fr.istic.androidrisk.exception;

public class NotEnoughUnitsException extends Exception {

	private static final long serialVersionUID = 42;
	
	public NotEnoughUnitsException(){
		super("Vous essayez d'attaquer depuis un territoire ne contenant qu'une unité, ce qui est impossible...");
		System.out.println("Vous essayez d'attaquer depuis un territoire ne contenant qu'une unité, ce qui est impossible...");
	}

}
