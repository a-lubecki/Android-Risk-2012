package fr.istic.androidrisk.ihm.utils;

public class GestionMessagesErreurs {

	/*
	 * Comptes
	 */
	public static String comptePasAuthentifie(){
		return "Vous n'êtes pas authentifié.";
	}

	public static String compteIdNonTrouve(String nomId){
		if(nomId == null || nomId == ""){
			return "Le compte n'a pas été trouvé.";
		}
		return "Le compte " + nomId + " n'existe pas.";
	}

	public static String compteIdDejaExistant(String nomId){
		if(nomId == null || nomId == ""){
			return "Un compte avec ce nom existe déjà.";
		}
		return "Le compte " + nomId + " existe déjà.";
	}

	public static String comptePassErrone(){
		return "Le mot de passe est incorrect";
	}

	/*
	 * Parties
	 */
	public static String partieNonExistante(String nomPartie){
		if(nomPartie == null || nomPartie == ""){
			return "La partie n'existe pas.";
		}
		return "La partie " + nomPartie + " n'existe pas.";
	}

	public static String partieDejaExistante(String nomPartie) {
		if(nomPartie == null || nomPartie == ""){
			return "Une partie avec ce nom existe déjà.";
		}
		return "Une partie avec le nom " + nomPartie + " existe déjà.";
	}

	public static String partieTropDeParticipation(int nbMaxParties){
		return "Vous ne pouvez participer qu'à "+nbMaxParties+" en même temps.";
	}

	public static String partiePasAssezDeJoueursSouhaites(int nbJoueursMin) {
		return "Le nombre de joueurs souhaité doit être plus petit que "+nbJoueursMin+".";
	}

	public static String partieTropDeJoueursSouhaites(int nbJoueursMax) {
		return "Le nombre de joueurs souhaité doit être plus grand que "+nbJoueursMax+".";
	}

	public static String partiePasDansListe(TypeListe type, String nomPartie){

		String deb = "La partie ";
		if(nomPartie != null && nomPartie != ""){
			deb += nomPartie + " ";
		}
		if(type == null){
			return deb + "n'est pas dans la liste.";
		}
		return deb + "n'est pas dans " + type.toString() + ".";
	}

	public static String partieDejaCommencee(String nomPartie) {
		if(nomPartie == null || nomPartie == ""){
			return "Cette partie est déjà commencée.";
		}
		return "La partie " + nomPartie + " est déjà commencée.";
	}

	public static String partieUtilisateurDejaParticipant() {
		return "Vous participez déjà à cette partie.";
	}

	/*
	 * Problèmes dans les Champs
	 */
	public static String champVide(TypeChamp type){
		if(type == null){
			return "Le champ est vide";
		}
		String champ = type.toString();
		champ = champ.substring(0, 1).toUpperCase() + champ.substring(1);
		return champ + " est vide.";
	}

	public static String champPasAssezDeCaracteres(TypeChamp type, int nbCaracMin){
		if(type == null){
			return "Il faut au moins " + nbCaracMin + " caractères.";
		}
		return "Il faut au moins " + nbCaracMin + " caractères pour "+type.toString()+".";
	}


	public enum TypeChamp {
		LOGIN () {
			@Override
			public String toString() {
				return "le nom d'utilisateur";
			}
		},
		PASS() {
			@Override
			public String toString() {
				return "le mot de passe";
			}
		}
		, NOUVELLE_PARTIE() {
			@Override
			public String toString() {
				return "le nom de la nouvelle partie";
			}
		}, PARTIE_EXISTANTE() {
			@Override
			public String toString() {
				return "le nom de la partie";
			}
		}
	}

	public enum TypeListe {
		PARTIES_SERVEUR_EN_ATTENTE () {
			@Override
			public String toString() {
				return "les parties existantes";
			}
		},
		MES_PARTIES_EN_ATTENTE () {
			@Override
			public String toString() {
				return "vos parties en attente";
			}
		},
		MES_PARTIES_COMMENCEES() {
			@Override
			public String toString() {
				return "vos parties commencées";
			}
		}
	}

}
