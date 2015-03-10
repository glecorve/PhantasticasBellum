package Model;

import java.util.List;

import Exception.ExceptionPersonnage;
/**
 * 
 * La classe Joueur represente virtuellement le participant, chaque joueur est caracterise par son nom et son equipe
 * @author Warlot/Gasquez/**
 * 
 * La classe Joueur represente virtuellement le participant, chaque joueur est caracterise par son nom et son equipe
 * @author Warlot/Gasquez
 *
 */

public class Joueur {
	private static int value = 1;
	private String nom = "Non defini";
	private Equipe equipe = null;
	private int nombreVictoire = 0; 
	
	/******************CONSTRUCTEUR******************/
	/**
	 * Constructeur de la classe Joueur
	 * @param nom nom du joueur
	 */
	public Joueur(String nom){
		setEquipe(new Equipe());
		setNom(nom);
	}

	/**
	 * Instancie une nouvelle equipe
	 */
	public void clearEquipe(){
		setEquipe(new Equipe());
	}
	
	/**
	 * Lance l'attaque active sur les Personnage cible
	 * @return String
	 */
	public List<String> lancerAttaque(){
		return getEquipe().lancerAttaque();
	}
	
	/**
	 * Donne l'equipe du joueur
	 * @return collection de Personnage composant l'equipe du joueur
	 */
	public List<Personnage> listerEquipe(){
		return getEquipe().getMembres();
	}
	
	/**
	 * Ajoute un Personnage a l'equipe du joueur
	 * @param personnage Personnage a ajouter
	 */
	public void ajouterMembre(Personnage personnage){
		personnage.setProprio(this);
		getEquipe().ajouterPersonnage(personnage);
	}
	
	/**
	 * Verifie si tous les Personnage de l'equipe ont ete place
	 * @throws ExceptionPersonnage exeption si un des Personnage n'a pas ete place
	 */
	public void personnagesTousPlaces() throws ExceptionPersonnage{
		getEquipe().personnagesTousPlaces();
	}
	
	/**
	 * Retourne vrai si l'action courrante est le deplacement, faux sinon
	 * @return booleen
	 */
	public boolean IsdeplacementEnCours(){
		return getEquipe().isDeplacementEnCours();
	}
	
	/**
	 * Retourne vrai si l'action courrante est l'attaque, faux sinon
	 * @return booleen
	 */
	public boolean IsAttaqueEnCours(){
		return getEquipe().isAttaqueEnCours();
	}
	
	/**
	 * Retourne vrai si l'action courrante est nulle, faux sinon
	 * @return booleen
	 */
	public boolean IsPassifEnCours(){
		return getEquipe().isPassifEnCours();
	}
	
	/**
	 * Incremente le nombre de victoire
	 */
	public void gagnee() {
		nombreVictoire += 1;
	}
	
	/**
	 * Fixe le Personnage actif du joueurActuel
	 * @param o nouveau Personnage actif
	 */
	public void setPersonnageActif(Personnage o){
		getEquipe().setPersonnageActif(o);
	}

	/**
	 * Fixe le Personnage actif du joueur
	 * @param o Personnage actif
	 */
	public void setAttaqueActif(Sort o){
		getEquipe().setAttaque(o);
	}
	
	/**
	 * Fixe l'etat du tour en mode passif
	 */
	public void setEtatTourPasser() {
		getEquipe().setEtatTourPassif();
	}

	/**
	 * Fixe l'etat du tour en mode deplacement
	 */
	public void setEtatTourDeplacer() {
		getEquipe().setEtatTourDeplacement();
	}
	
	/**
	 * Fixe l'etat du tour en mode attaque
	 */
	public void setEtatTourAttaquer() {
		getEquipe().setEtatTourAttaque();
	}
	
	/**
	 * Ajoute un Personnage a la liste des Personnage attaque
	 * @param monPersonnage Personnage cible
	 */
	public void addPersonnageAttaque(Personnage monPersonnage){
		getEquipe().addPersonnageAttaque(monPersonnage);
	}
	
	/**
	 * Donne le Personnage actif du joueurActuel
	 * @return Personnage actif
	 */
	public Personnage getPersonnageActif(){
		return getEquipe().getPersonnageActif();
	}
	
	/**
	 * Retourne le Personnage actif
	 * @return Personnage actif du joueur
	 */
	public Sort getAttaqueActif(){
		return getEquipe().getAttaque();
	}
		
	/**
	 * Retourne le nom du joueur
	 * @return String nom
	 */
	public String getNom() {
		return nom;
	}
	
	/**
	 * Retourne l'equipe du joueur
	 * @return Equipe
	 */
	public Equipe getEquipe() {
		return equipe;
	}
	
	/**
	 * Retourne le nombre de victoire
	 * @return int
	 */
	public int getNombreVictoire(){
		return nombreVictoire;
	}
	
	/**
	 * Fixe le nom du joueur
	 * @param nom String
	 */
	public void setNom(String nom) {
		if (nom.trim().isEmpty()){
			nom = "J" + value;
			Joueur.value += 1;
		}
		this.nom = nom;
	}
	
	/**
	 * Fixe l'equipe du joueur
	 * @param equipe Equipe
	 */
	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}
	
	/**
	 * Fixe le nombre de victoire
	 * @param nombreVictoire int
	 */
	public void setNombreVictoire(int nombreVictoire){
		this.nombreVictoire = nombreVictoire;
	}
}
