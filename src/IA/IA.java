package IA;

import Controleur.Partie;
import Model.Coup;
import Model.Joueur;
import java.util.List;

/**
 *
 * @author Gwenole Lecorve
 */
public class IA extends Joueur {
    
    private Coup coupMemorise;
        
    /**
     * Constructeur
     */
    public IA() {
        super("IA aleatoire");
        coupMemorise = null;
    }
    
    /**
     * Renvoie le dernier coup memorise
     * @return un coup
     */
    public final Coup getCoupMemorise() {
        return coupMemorise;
    }
    
    /**
     * Memorise un coup
     * @param coup Le coup a memoriser
     */
    public final void memoriseCoup(Coup coup) {
        coupMemorise = coup;
    }
    
    /**
     * Recherche le coup a jouer
     * @param p Partie en cours
     * @return un coup
     */
    public Coup getCoup(Partie p) {
        List<Coup> coups = p.getTousCoups();
        return coups.get((int) (Math.random()*(coups.size()-1)));
    }
    
}
