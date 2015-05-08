package IA;

import Controleur.Partie;
import Model.Coup;
import Model.Joueur;

/**
 *
 * @author Gwenole Lecorve
 */
public abstract class AbstractIA extends Joueur {

    /**
     * Delai de reflexion (c.-a-d. de calcul) autorise pour tous les joueurs artificiels
     */
    public static final int DELAI_DE_REFLEXION = 4000;

    private Coup coupMemorise;

    /**
     * Constructeur
     * @param nom Nom du joueur artificiel
     */
    public AbstractIA(String nom) {
        super(nom);
        coupMemorise = null;
    }

    /**
     * Renvoie le dernier coup memorise
     *
     * @return un coup
     */
    public final Coup getCoupMemorise() {
        return coupMemorise;
    }

    /**
     * Memorise un coup
     *
     * @param coup Le coup a memoriser
     */
    public final void memoriseCoup(Coup coup) {
        if (coup != null) {
            coupMemorise = (Coup) coup.clone();
//            System.out.println("##############################");
//            System.out.println("Mémorisation de " + coupMemorise.toString());
//            System.out.println("##############################");
        }
        else {
            coupMemorise = coup;
        }
    }

    /**
     * Recherche le coup a jouer
     *
     * @param p Partie en cours
     * @return un coup
     */
    public abstract Coup getCoup(Partie p);

}
