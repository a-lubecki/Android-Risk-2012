package fr.istic.androidrisk.exception;

public class NotAuthorizedMoveException extends Exception {

	private static final long serialVersionUID = 42;

	public NotAuthorizedMoveException()
	{
		super("Erreur, vous essayez de déplacer des unités vers un territoire que vous ne contr?lez pas ou vous essayez de déplacer la dernière unité d'un territoire vers un autre...");
		System.out.println("Erreur, vous essayez de déplacer des unités vers un territoire que vous ne contr?lez pas ou vous essayez de déplacer la dernière unité d'un territoire vers un autre...");
	}
}
