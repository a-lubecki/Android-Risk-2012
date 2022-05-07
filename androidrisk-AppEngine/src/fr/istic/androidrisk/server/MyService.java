/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package fr.istic.androidrisk.server;

import java.util.List;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.thoughtworks.xstream.XStream;
import com.wappworks.xstream.XStreamGae;

import fr.istic.androidrisk.dto.CompteDTO;
import fr.istic.androidrisk.dto.InfosPartieDTO;
import fr.istic.androidrisk.dto.MondeDTO;
import fr.istic.androidrisk.dto.PartieDTO;
import fr.istic.androidrisk.moteur.Joueur;
import fr.istic.androidrisk.moteur.Partie;
import java.util.ArrayList;

public class MyService {

    private static final int MAX_PARTIES_PAR_COMPTE = 10;

    public MyService() {
    }

    public static MondeDTO getMonde() {
        ObjectifyService.register(MondeDTO.class);
        Objectify ofy = ObjectifyService.begin();
        MondeDTO monde =  ofy.get(MondeDTO.class,new Long(1));
        return monde;
    }

    public synchronized static void saveMonde(String s) {
        ObjectifyService.register(MondeDTO.class);
        Objectify ofy = ObjectifyService.begin();
        XStream xStream = new XStreamGae();
        MondeDTO monde = (MondeDTO) xStream.fromXML(s);
        ofy.put(monde);
    }
    // return true si compte cree, false si le compte existe deja
    public synchronized static boolean creerCompte(String login, String mdp) {
        ObjectifyService.register(CompteDTO.class);
        Objectify ofy = ObjectifyService.begin();
        if (ofy.find(CompteDTO.class, login) == null) {
            ofy.put(new CompteDTO(login, mdp));
            return true;
        } else {
            return false;
        }
    }

    // return 0 si ok, 1 si le compte n'existe pas, 2 si le mdp est incorrect
    public synchronized static int connection(String login, String mdp) {
        ObjectifyService.register(CompteDTO.class);
        Objectify ofy = ObjectifyService.begin();
        CompteDTO compte = ofy.find(CompteDTO.class, login);
        if (compte != null) {
            if (compte.getMdp().equals(mdp)) {
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    // return 0 si ok, 1 si le joueur à déjà atteind son nombre max de parties,
    // 2 si la partie existe deja
    public synchronized static int creerPartie(String login,
            String nomPartie, int nbJoueurs) {
        ObjectifyService.register(InfosPartieDTO.class);
        ObjectifyService.register(CompteDTO.class);
        Objectify ofy = ObjectifyService.begin();
        // verification du nombre de parties actuelles pour ce compte
        CompteDTO compte = ofy.find(CompteDTO.class, login);
        if (compte.getParties().size() < MAX_PARTIES_PAR_COMPTE) {
            InfosPartieDTO partie = ofy.find(InfosPartieDTO.class, nomPartie);
            if (partie == null) {
                // creation partie
                partie = new InfosPartieDTO(nomPartie, nbJoueurs, login);
                compte.getParties().add(nomPartie);
                ofy.put(partie);
                ofy.put(compte);
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    // return les parties liées à un compte
    public synchronized static String listePartiesCompte(String login) {
        ObjectifyService.register(InfosPartieDTO.class);
        ObjectifyService.register(CompteDTO.class);
        Objectify ofy = ObjectifyService.begin();
        CompteDTO compte = ofy.find(CompteDTO.class, login);
        XStream xstream = new XStreamGae();
        List<InfosPartieDTO> parties = new ArrayList<InfosPartieDTO>();
        for (String partie : compte.getParties()) {
            parties.add(ofy.get(InfosPartieDTO.class, partie));
        }
        return xstream.toXML(parties);
    }

    // return les parties non commencées
    public synchronized static String listePartiesNonCommencees(String login) {
        ObjectifyService.register(InfosPartieDTO.class);
        Objectify ofy = ObjectifyService.begin();
        XStream xstream = new XStreamGae();
        List<InfosPartieDTO> parties = new ArrayList<InfosPartieDTO>();
        Query<InfosPartieDTO> query = ofy.query(InfosPartieDTO.class);
        for (InfosPartieDTO partie : query.list()) {
            if (!partie.getJoueurs().contains(login) && !partie.isCommence()) {
                parties.add(partie);
            }
        }
        return xstream.toXML(parties);
    }

    // return 0 si ok, 
    // 1 si la partie n'existe pas, 2 si le joueur n'existe pas dans la partie
    public synchronized static int abandonnerPartie(String login, String nomPartie) {
        ObjectifyService.register(InfosPartieDTO.class);
        ObjectifyService.register(CompteDTO.class);
        ObjectifyService.register(PartieDTO.class);
        Objectify ofy = ObjectifyService.begin();
        InfosPartieDTO infosPartie = ofy.get(InfosPartieDTO.class, nomPartie);
        CompteDTO compte = ofy.get(CompteDTO.class, login);
        // si la partie existe
        if (infosPartie != null) {
            // si le joueur est bien inscrit dans la partie
            if (infosPartie.getJoueurs().contains(login)) {
                compte.getParties().remove(nomPartie);
                infosPartie.getJoueurs().remove(login);
                ofy.put(compte);
                // si la partie n'est pas commencee
                if (!infosPartie.isCommence()) {
                    // supprimer le joueur
                    if (infosPartie.getJoueurs().size() > 0) {
                        ofy.put(infosPartie);
                    } else {
                        // si il n'y a plus personne sur la partie, la supprimer 
                        ofy.delete(infosPartie);
                    }
                    // si la partie est deja commencée
                } else {
                    boolean sauvegarde = false;
                    for (String j : infosPartie.getJoueurs()) {
                        CompteDTO c = ofy.get(CompteDTO.class, j);
                        if (c.getParties().contains(nomPartie)) {
                            sauvegarde = true;
                            break;
                        }
                    }
                    if (!sauvegarde) {
                        ofy.delete(infosPartie);
                        ofy.delete(ofy.get(PartieDTO.class, nomPartie));
                    }
                }
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    public synchronized static int rejoindrePartie(String login, String nomPartie) {
        ObjectifyService.register(InfosPartieDTO.class);
        ObjectifyService.register(PartieDTO.class);
        ObjectifyService.register(CompteDTO.class);
        Objectify ofy = ObjectifyService.begin();
        // verification du nombre de parties actuelles pour ce compte
        CompteDTO compte = ofy.get(CompteDTO.class, login);
        if (compte.getParties().size() < MAX_PARTIES_PAR_COMPTE) {
            // si le joueur n'est pas déjà inscrit dans la partie
            InfosPartieDTO partie = ofy.get(InfosPartieDTO.class, nomPartie);
            if (!partie.getJoueurs().contains(login)) {
                if (!partie.isCommence()) {
                    compte.getParties().add(nomPartie);
                    if (partie.addJoueur(login)) {
                        ArrayList<Joueur> j = new ArrayList<Joueur>();
                        for (String s : partie.getJoueurs()) {
                            j.add(new Joueur(s));
                        }
                        Partie p = new Partie(nomPartie, j);
                        CompteDTO premierJoueur = ofy.get(CompteDTO.class, p.getJoueurCourant().getPseudo());
                        premierJoueur.setChanged(true);
                        partie.setJoueurCourant(premierJoueur.getLogin());
                        ofy.put(premierJoueur);
                        ofy.put(new PartieDTO(p, nomPartie));
                    }
                    ofy.put(partie);
                    ofy.put(compte);
                    return 0;
                } else {
                    return 3;
                }
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    public synchronized static String getPartie(String nomPartie) {
        ObjectifyService.register(PartieDTO.class);
        Objectify ofy = ObjectifyService.begin();
        XStream xstream = new XStreamGae();
        return xstream.toXML(ofy.get(PartieDTO.class, nomPartie).getPartie());
    }

    public synchronized static boolean notification(String login) {
        ObjectifyService.register(CompteDTO.class);
        Objectify ofy = ObjectifyService.begin();
        CompteDTO compte = ofy.get(CompteDTO.class, login);
        boolean changed = compte.isChanged();
        compte.setChanged(false);
        ofy.put(compte);
        return changed;
    }

    public synchronized static void setPartie(String partie,boolean terminerTour) {
        ObjectifyService.register(PartieDTO.class);
        Objectify ofy = ObjectifyService.begin();
        XStream xStream = new XStreamGae();
        Partie p = (Partie) xStream.fromXML(partie);
        PartieDTO partieDTO = new PartieDTO(p, p.getNom());
        if (terminerTour) {
            InfosPartieDTO infosPartie = ofy.get(InfosPartieDTO.class, p.getNom());
            String joueur = partieDTO.getPartie().getJoueurCourant().getPseudo();
            infosPartie.setNumTour(partieDTO.getPartie().getCompteTour());
            infosPartie.setJoueurCourant(joueur);
            ofy.put(infosPartie);
            CompteDTO compte = ofy.get(CompteDTO.class, joueur);
            compte.setChanged(true);
            ofy.put(compte);
        }
        ofy.put(partieDTO);
    }
    
}
