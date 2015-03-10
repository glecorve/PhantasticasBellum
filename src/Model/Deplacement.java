package Model;

import Controleur.GestionnairePartie;

/**
 * Classe qui modelise un deplacement d'un personnage
 * @author Gwenole Lecorve
 */
public class Deplacement implements Action {
    private final Position origine;
    private final Position destination;
    
    public Deplacement(Position origine, Position cible) {
        this.origine = origine;
        this.destination = cible;
    }
    
    public Position getOrigine() {
        return origine;
    }
    
    public Position getDestination() {
        return destination;
    }
    
    public String toString() {
        return "Deplacement de " + getOrigine().toString() + " vers " + getDestination().toString();
    }

    @Override
    public void appliquer(GestionnairePartie partie) {
        partie.getPersonnageActif().setPosition(destination);
        System.out.println("Après : " + partie.getPersonnageActif().toString());
    }
}
