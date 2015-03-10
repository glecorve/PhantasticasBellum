package Controleur;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import GUI.Vue3.VueJeu;
import GUI.Vue3.VueJeuCombat;
import GUI.Vue3.Joueur.VueJoueurCombat;
import IA.IA;
import IA.IAThread;
import Model.Sort;
import Model.Effet;
import Model.Matrice;
import Model.Personnage;
import Model.Position;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe controleur de combat (deroulement d'un tour de jeu)
 * @author Warlot/Gasquez
 *
 */

public class ControleurCombat extends AbstractControleurJeu {
	//Case cliquable, sauvegarde du status du plateau, utile lors des actions de souris (click, entered, exited)
	private List<Position> caseClickable = new ArrayList<Position>();
	
	//Etat du tour, attaque multiple, attaque, deplacement
	private boolean coupsRestant = true;
	private boolean attaque = true;
	private boolean deplacement = true;
	
	/**
	 * Constructeur du tour de jeu
	 * @param maVue panel comprenant les deux panels joueur et le panel plateau
	 * @param controleurParent controleur de la fenetre
	 */
	public ControleurCombat(VueJeu maVue, ControleurFenetre controleurParent){
		super(maVue, controleurParent);
		
		getMaPartie().setEtatTourPasser();
	}
	
	/**
	 * Clique sur la case du plateau
	 * @param maPosition emplacement de la case clique
	 */
	public void actionClick(Position maPosition){
		//Si case non cliquable
		if (!caseClickable.contains(maPosition)){
			return;
		}
		
		//Si le tous a demarre
		if (getMaPartie().isDeplacementEnCours() || getMaPartie().isAttaqueEnCours()){
			//Verouille le choix des Personnage (le tour demarre)
			((VueJoueurCombat) getMaVue().getPanelJoueurActuel()).verouillerChoixPersonnage();
		}
	
		if (getMaPartie().isDeplacementEnCours() && deplacement){
			//si deplacement alors on se deplace
			if (getMaPartie().setPositionPersonnage(
					maPosition
				)){
				deplacement = false;
			}
			
			
			//empecher le choix d'un autre deplacement
			((VueJoueurCombat) getMaVue().getPanelJoueurActuel()).verouillerChoixDeplacement();
			
			//Reset couleur plateau
			getMaVue().getPanelPlateau().afficherPlateauParDefaut();
			
		} else if (getMaPartie().isAttaqueEnCours() && attaque){
			//Sinon si attaque alors on attaque la case
			
			//Fixer les Personnage attaque par l'attaque en cours
			List<Position> caseAccessible = getListCaseAttaqueZone(maPosition);
			
			for (Position maPos : caseAccessible){	
				getMaPartie().setPersonnagesAttaques(
						maPos,
						getAttaqueActif()
						);
			}

			((VueJoueurCombat) getMaVue().getPanelJoueurActuel()).verouillerChoixAttaque();

			//si attaque multiple
			if (getAttaqueActif().isAttaqueMultiple()){
				if (!coupsRestant){
					attaque = false;
				} else {
					coupsRestant = false;
				}
			} else {
				attaque = false;
			}
			
			//Applique l'attaque et recuperer le resultat
			List<String> resultatAttaque = getMaPartie().lancerAttaque();
			
			//Met a jour la console
			if (resultatAttaque.isEmpty()){
				majConsole(new JLabel("Attaque sans effet sur les personnages."));
			}else{
				for (String o : resultatAttaque){
					if (attaque){
						o += " 1 attaque restante.";
					}
					majConsole(new JLabel(o));
				}
			}
		} else {
			//Sinon ne rien faire
		}		
	}
	
	/**
	 * Action effectue lors de l'entree de la sourie sur une case
	 * @param maPosition case clique
	 */
	public void actionEntered(Position maPosition){
		//Si case non cliquable
		if (!caseClickable.contains(maPosition)){
			return;
		}
		
		if (getMaPartie().isDeplacementEnCours() && deplacement){
			afficherCaseDeplacementAutorise(maPosition);
		} else if (getMaPartie().isAttaqueEnCours() && attaque){
			afficherZoneAttaque(maPosition);
		}
	}
	
	/**
	 * Action effectue lors de la sortie de la sourie sur une case
	 * @param maPosition case clique
	 */
	public void actionExited(Position maPosition){
		//Si case non cliquable
		if (!caseClickable.contains(maPosition)){
			return;
		}
		
		if (getMaPartie().isDeplacementEnCours() || getMaPartie().isAttaqueEnCours()){
			getMaVue().getPanelPlateau().afficherCouleurPlateau();
		}
	}

	/**
	 * Affiche le label en parametre dans le panel sud
	 * @param monLabel element a afficher
	 */
	public void majConsole(JLabel monLabel){
		getMaVue().majConsole(monLabel);
	}
	
	/**
	 * Design pattern observateur/observe, ajoute l'observateur plateau sur les Personnage
	 */
	public void observePersonnages(){
		for(Personnage o : getMaPartie().listerEquipes()){
			o.addObserver(
				getMaVue().getPanelPlateau()
			);
		}
	}

	/**
	 * Passe au tour de jeu suivant et fixe l'etat passif aupres du model
	 */
	public void passerSonTour() {
		//Indique que ce Personnage a deja joue
		getPersonnageActif().setDejaJoue(true);

		//decremente de 1 tour les effets de tous les Personnage
		getPersonnageActif().effetsTourSuivant();
		
		//Fixe l'etat passif
		getMaPartie().setEtatTourPasser();
		
		//Passe au tour suivant
		getControleurParent().tourSuivant();
                
                //Demarre le coup suivant
                coupSuivant();
	}
        
        
        
        
        
        /**
         * Passe au coup suivant et joue ce coup pour les joueurs artificiels
         */
        public void coupSuivant() {
            if (getMaPartie().getJoueurActuel() instanceof IA) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                IAThread calcul = new IAThread((IA) getMaPartie().getJoueurActuel(), getMaPartie(), executor);
                executor.execute(calcul);
                try {
                    if (!executor.awaitTermination(4000, TimeUnit.MILLISECONDS))
                    {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(GestionnairePartie.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Coup choisi = "+calcul.getCoupChoisi().toString());
                getMaPartie().appliquerCoup(calcul.getCoupChoisi());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ControleurCombat.class.getName()).log(Level.SEVERE, null, ex);
                }
                getMaVue().revalidate();
                passerSonTour();
            }
        }
	
	/**
	 * Affiche la comme autorise
	 * @param maPosition emplacement de la case concerne
	 */
	public void afficherCaseDeplacementAutorise(Position maPosition){
		//Affichage temporaire, donc pas d'enregistrement et seulement un affichage
		getMaVue().getPanelPlateau().caseCible(maPosition);
	}
	
	/**
	 * Affiche la portee du deplacement du Personnage actif
	 */
	public void afficherPorteeDeplacement(){
		Personnage o = getMaPartie().getPersonnageActif();
		
		Position positionPersonnage = o.getPosition();
		Matrice mouvementPersonnage = o.getMouvement();
		
		//Si effet alors on les appliques(reduction de la portee de deplacement)
		int malusDeplacement = 0;
		for(Effet oEffet : o.getEffet()){
			if (oEffet.getPmRetirer() > 0){
				malusDeplacement += oEffet.getPmRetirer();
				mouvementPersonnage = mouvementPersonnage.reduireZone(malusDeplacement);
			}
		}
		
		//Recuperer les positions des cases cible
		List<Position> caseAccessible = mouvementPersonnage.getCasesAccessible(positionPersonnage);

		List<Position> caseAccessibleDispo = new ArrayList();
                List<Position> caseInaccessibleDispo = getMaPartie().getToutesPositions();
                caseInaccessibleDispo.removeAll(caseAccessible);
		
		//Enlever les cases occupés par les Personnage
		for (Position maPosition : caseAccessible){
			if (getMaPartie().isCaseLibre(maPosition)){
				caseAccessibleDispo.add(maPosition);
			}
		}

		//Sauvegarde des cases cliquables
		this.caseClickable = caseAccessibleDispo;
		
		for (Position maPosition : caseInaccessibleDispo){
				//Enregistrer les cases
				getMaVue().getPanelPlateau().caseNonCiblable(maPosition);
		}
		//Afficher les cases
		getMaVue().getPanelPlateau().afficherCouleurPlateau();
	}
	
	/**
	 * Affiche la portee de l'attaque selectionne, du Personnage selectionne
	 */
	public void afficherPorteeAttaque(){
		Personnage o = getMaPartie().getPersonnageActif();
		Sort oA = getMaPartie().getAttaqueActif();
		
		Position positionPersonnage = o.getPosition();
		int porteeMin = oA.getPorteeMin();
		int porteeMax = oA.getPorteeMax();
		
		if (porteeMax != - 1){
			Matrice porteePersonnage = new Matrice(porteeMin, porteeMax, false);
			
			List<Position> caseAccessible = porteePersonnage.getCasesAccessible(positionPersonnage);
                        List<Position> caseInaccessible = getMaPartie().getToutesPositions();
                        caseInaccessible.removeAll(caseAccessible);

			//Sauvegarde des cases cliquables
			this.caseClickable = caseAccessible;
			
			for (Position maPosition : caseInaccessible){
				//Enregistrer les cases
				getMaVue().getPanelPlateau().caseNonCiblable(maPosition);
			}
		} else {
			//Toute la map est attaquable
			
			//Sauvegarde des cases cliquables
			for (int y = 0; y < getMaVue().getPanelPlateau().getHauteur(); y += 1){
				for (int x = 0; x < getMaVue().getPanelPlateau().getHauteur(); x += 1){
					this.caseClickable.add(new Position(x, y));
				}
			}
			
			//Enregistrer les cases
			getMaVue().getPanelPlateau().toutesLesCasesCiblable();
		}
		//Afficher les cases
		getMaVue().getPanelPlateau().afficherCouleurPlateau();
	}
	
	/**
	 * Affiche la zone d'effet de l'attaque
	 * @param positionCase emplacement de l'epicentre de l'attaque
	 */
	public void afficherZoneAttaque(Position positionCase){
		List<Position> caseAccessible = getListCaseAttaqueZone(positionCase);
		
		for (Position maPosition : caseAccessible){
			//Affichage temporaire, donc pas d'enregistrement et seulement un affichage
			getMaVue().getPanelPlateau().caseCible(maPosition);
		}
	}
	
	/**
	 * Donne toutes les cases atteintes par la zone d'attaque, l'epicentre est donne par l'emplacement fourni en parametre
	 * @param positionCase emplacement du centre de la zone d'attaque
	 * @return retourne la liste des cases touchables par la zone d'attaque
	 */
	public List<Position> getListCaseAttaqueZone(Position positionCase){
		Sort oA = getMaPartie().getAttaqueActif();
		Matrice attaquePersonnage = oA.getZone();
		
		List<Position> caseAccessible = attaquePersonnage.getCasesAccessible(positionCase);
		
		return caseAccessible;
	}
	
	/**
	 * Retourne le  Personnage actif
	 * @return Personnage
	 */
	public Personnage getPersonnageActif(){
		return getMaPartie().getPersonnageActif();
	}

	/**
	 * Fixe le Personnage actif
	 * @param o nouveau Personnage actif
	 */
	public void setAttaqueActif(Sort o){
		getMaPartie().setAttaqueActif(o);
	}
	
	/**
	 * 	Retourne l'attaque active
	 * @return Sort
	 */
	public Sort getAttaqueActif(){
		return getMaPartie().getAttaqueActif();
	}

	/**
	 * Retourne la liste d'attaque du Personnage actif
	 * @return liste d'attaque
	 */
	public List<Sort> getAttaque(){
		return this.getPersonnageActif().getAttaques();
	}
	
	/**
	 * Retourne la liste de Personnage de l'equipe du joueur actuel
	 * @return liste de Personnage
	 */
	public List<Personnage> getMonEquipe(){
		return getMaPartie().listerEquipeJoueur();
	}
	
	@Override
	public VueJeuCombat getMaVue() {
		return (VueJeuCombat) maVue;
	}
	
	/**
	 * Fixe le Personnage fournie en tant que Personnage actif aupres du model et change la couleur de la case
	 * @param o Personnage actif
	 */
	public void setPersonnageActif(Personnage o){
		getMaPartie().setPersonnageActif(o);
		
		getMaPartie().setEtatTourPasser();
		getMaVue().getPanelPlateau().afficherPlateauParDefaut();
		getMaVue().getPanelPlateau().enregistrerCouleurPersonnagesJoueur();
	}
	
	/**
	 * Affiche les deplacements autorises sur la carte et fixe l'etat deplacement aupres du model
	 */
	public void setDeplacer() {
		getMaPartie().setEtatTourDeplacer();
		
		//Reset couleur plateau
		getMaVue().getPanelPlateau().afficherPlateauParDefaut();
		
		//Afficher portee deplacement
		afficherPorteeDeplacement();
	}
	
	/**
	 * Affiche la portee de l'attaque et fixe l'etat attaque aupres du model
	 * @param monAttaque attaque en cours
	 */
	public void setAttaquer(Sort monAttaque) {
		setAttaqueActif(monAttaque);
		getMaPartie().setEtatTourAttaquer();
		
		//Reset couleur plateau
		getMaVue().getPanelPlateau().afficherPlateauParDefaut();
		
		//Afficher portee attaque
		afficherPorteeAttaque();
	}
}
