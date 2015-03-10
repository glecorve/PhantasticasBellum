package Model;

import Controleur.GestionnairePartie;

/**
 * Classe qui modelise un deplacement d'un Personnage
 * @author Gwenole Lecorve
 */
public class Attaque implements Action {
    private Sort sort;
    private Personnage cible;
    
    /**
     * Constructeur
     * @param sort Sort a lancer
     * @param cible Personnage vise
     */
    public Attaque(Sort sort, Personnage cible) {
        this.sort = sort;
        this.cible = cible;
    }
    
    /**
     * Renvoie le sort de l'attaque
     * @return un sort
     */
    public Sort getSort() {
        return sort;
    }
    
    /**
     * Renvoie le personnage vise
     * @return un personnage
     */
    public Personnage getCible() {
        return cible;
    }
    
    @Override
    public String toString() {
        return getSort().toString() + " sur " + getCible().toString();
    }

    @Override
    public void appliquer(GestionnairePartie partie) {
        getCible().appliquerSort(getSort());
    }
}
