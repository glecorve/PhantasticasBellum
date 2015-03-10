package Controleur;

import GUI.Vue3.VueJeu;

/**
 * Classe abstraite de controleur de placement et combat
 * @author Warlot/Gasquez
 */

public abstract class AbstractControleurJeu {
	protected VueJeu maVue;
	private ControleurFenetre controleurParent = null;
	
	/**
	 *  Controleur
	 * @param maVue panel comprenant les deux panels joueur et le panel plateau
	 * @param controleurParent controleur parent
	 */
	public AbstractControleurJeu(VueJeu maVue, ControleurFenetre controleurParent){
		this.maVue = maVue;
		this.controleurParent = controleurParent;
	}

	/**
	 * Affiche les Personnages sur le plateau et change leur couleur
	 */
	public void afficherPersonnages(){
		getMaVue().getPanelPlateau().afficherPlateauParDefaut();
		getMaVue().getPanelPlateau().enregistrerCouleurPersonnagesJoueur();
	}

	/**
	 * Retourne le controleur de la fenetre principale
	 * @return ControleurFenetre
	 */
	public ControleurFenetre getControleurParent() {
		return controleurParent;
	}
	
	/**
	 * Retourne la specialisation de VueJeu
	 * @return VueJeu
	 */
	public abstract VueJeu getMaVue();

	/**
	 * Methode abstraite qui renverra la vue courrante
	 * @return la vue courante
	 */
	public Partie getMaPartie() {
		return controleurParent.getMaPartie();
	}
}
