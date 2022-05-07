package fr.istic.androidrisk.client;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.ServiceName;
import java.util.List;

public interface MyRequestFactory extends RequestFactory {

    @ServiceName("fr.istic.androidrisk.server.MyService")
    interface MyRequest extends RequestContext {

        Request<Boolean> creerCompte(String login, String mdp);

        Request<Integer> connection(String login, String mdp);

        Request<Integer> creerPartie(String login, String nomPartie, int nbJoueurs);

        Request<String> listePartiesCompte(String login);

        Request<Integer> rejoindrePartie(String login, String nomPartie);

        Request<String> listePartiesNonCommencees(String login);

        Request<Integer> abandonnerPartie(String login, String nomPartie);

        Request<String> getPlay(String nomPartie);

        Request<Boolean> notification(String login);

        Request<Void> setPlay(String partie,boolean terminerTour); // à utiliser en fond de tâche pour echangerCartes, renforts, attaque, manoeuvre

        Request<Void> saveMonde(String s);
    }

    MyRequest myRequest();
}
