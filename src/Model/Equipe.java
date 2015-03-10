package Model;

import java.util.*;

import Exception.ExceptionPersonnage;

/**
 * Modele pour une equipe
 * @author Warlot/Gasquez
 *
 */
public class Equipe {
	private List<Personnage> membres = new ArrayList<Personnage>();
	
	//Info sur le tour de jeu
	//Indique l'etat en cours, attaque, deplacement, rien (passif)
	private enum etatTour{
		DEPLACEMENT, ATTAQUE, PASSIF
	}
	private etatTour monEtatTour = etatTour.PASSIF;
	
	private Sort attaque = null;
	private List<Personnage> PersonnagesAttaques = null;
	private Personnage PersonnageActif = null;
	
	/**
	 * Constructeur
	 */
	public Equipe(){
		PersonnagesAttaques = new ArrayList<Personnage>();
	}
	
	/**
	 * Lance l'attaque sur les Personnage cibles
	 * @return String : le resultat de l'attaque
	 */
	public List<String> lancerAttaque(){
		List<String> resultatAttaque = new ArrayList<String>();
		for (Personnage o : getPersonnagesAttaques()){
			resultatAttaque.add(o.appliquerSort(getAttaque()));
		}
		getPersonnagesAttaques().clear();
		return resultatAttaque;
	}
	
	/**
	 * Ajoute un Personnage a l'equipe
	 * @param personnage Personnage que l'on souhaite ajouter
	 */
	public void ajouterPersonnage(Personnage personnage){
		getMembres().add(personnage);
	}
		
	/**
	 * Indique que tous les Personnage sont places
	 * @throws ExceptionPersonnage exception de placement
	 */
	public void personnagesTousPlaces() throws ExceptionPersonnage{
		for(Personnage o : membres){
			o.estPlace();
		}
	}

	/**
	 * Teste si l'etat en cours est deplacement
	 * @return boolean vrai si l'etat est deplacement, faux sinon
	 */
	public boolean isDeplacementEnCours(){
		if (getEtatTour() == getEtatTour().DEPLACEMENT){
			return true;
		}
		return false;
	}
	
	/**
	 * Teste si l'etat en cours est attaque
	 * @return boolean vrai si l'etat est attaque, faux sinon
	 */
	public boolean isAttaqueEnCours(){
		if (getEtatTour() == getEtatTour().ATTAQUE){
			return true;
		}
		return false;
	}

	/**
	 * Teste si l'etat en cours est passif
	 * @return boolean vrai si l'etat est passif, faux sinon
	 */
	public boolean isPassifEnCours(){
		if (getEtatTour() == getEtatTour().PASSIF){
			return true;
		}
		return false;
	}
	
	/**
	 * Reset le Personnage attaquer
	 */
	public void clearPersonnagesAttaques(){
		PersonnagesAttaques.clear();
	}
	
	/**
	 * Reset l'attaque selectionner
	 */
	public void clearAttaque(){
		setAttaque(null);
	}

	/**
	 * Reset le Personnage actif
	 */
	public void clearPersonnageActif(){
		setPersonnageActif(null);
	}
	
	/**
	 * Retourne les Personnage composant l'equipe
	 * @return liste de Personnage
	 */
	public List<Personnage> getMembres() {
		return membres;
	}

	/**
	 * Retourne l'attaque active
	 * @return Sort
	 */
	public Sort getAttaque() {
		return attaque;
	}
	
	/**
	 * Retourne la liste des Personnage attaques par l'attaque en cours
	 * @return liste de Personnage
	 */
	public List<Personnage> getPersonnagesAttaques() {
		return PersonnagesAttaques;
	}
	
	/**
	 * Retourne le Personnage actif
	 * @return Personnage
	 */
	public Personnage getPersonnageActif() {
		return PersonnageActif;
	}
	
	/**
	 * Retourne l'etat du tour de jeu
	 * @return etatTour
	 */
	etatTour getEtatTour() {
		return monEtatTour;
	}
	
	
	/**
	 * Affecte une position au Personnage actif
	 * @param maPosition position a affectee
	 */
	public void setPositionPersonnage(Position maPosition){
		getPersonnageActif().setPosition(maPosition);
	}
	
	/**
	 * Fixe les Personnage composant l'equipe
	 * @param pFsEquipe nouvelle liste des Personnage de l'equipe
	 */
	private void setMembres(List<Personnage> pFsEquipe) {
		membres = pFsEquipe;
	}

	/**
	 * Fixe l'attaque courrante
	 * @param attaque nouvelle attaque
	 */
	public void setAttaque(Sort attaque) {
		this.attaque = attaque;
	}

	/**
	 * Ajoute un Personnage a la liste des Personnage attaque
	 * @param personnageAttaque nouveau personnage attaque
	 */
	public void addPersonnageAttaque(Personnage personnageAttaque) {
		this.PersonnagesAttaques.add(personnageAttaque);
	}

	/**
	 * Fixe le Personnage actif
	 * @param personnageActif nouveau Personnage actif
	 */
	public void setPersonnageActif(Personnage personnageActif) {
		this.PersonnageActif = personnageActif;
	}
	
	/**
	 * Fixe l'etat du tour sur deplacement
	 */
	public void setEtatTourDeplacement() {
		this.monEtatTour = monEtatTour.DEPLACEMENT;
	}

	/**
	 * Fixe l'etat du tour sur attaque
	 */
	public void setEtatTourAttaque() {
		this.monEtatTour = monEtatTour.ATTAQUE;
	}

	/**
	 * Fixe l'etat du tour sur passif
	 */
	public void setEtatTourPassif() {
		this.monEtatTour = monEtatTour.PASSIF;
	}
}
