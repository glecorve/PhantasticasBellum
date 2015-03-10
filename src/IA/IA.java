package IA;

import Controleur.GestionnairePartie;
import Model.Coup;
import Model.Joueur;
import java.util.List;

/**
 *
 * @author Gwenole Lecorve
 */
public class IA extends Joueur {
        
    /**
     * Constructeur
     */
    public IA() {
        super("Random IA");
    }
    
    /**
     * Recherche le coup a jouer
     * @param p Partie en cours
     * @return un coup
     */
    public Coup getCoup(GestionnairePartie p) {
        List<Coup> coups = p.getTousCoups();
        return coups.get((int) (Math.random()*(coups.size()-1)));
    }
    
}
