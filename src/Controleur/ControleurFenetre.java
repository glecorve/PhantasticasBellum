package Controleur;

import java.util.List;

import Exception.*;
import GUI.*;
import GUI.Vue1.VueParamJeu;
import GUI.Vue2.VueParamJoueur;
import GUI.Vue3.VueJeuCombat;
import GUI.Vue3.VueJeuPlacement;
import GUI.Vue4.VueFinJeu;
import IA.IA;
import IA.IAThread;
import Model.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe controleur (principale) de la fenetre de jeu, permet de changer le contenu de la fenetre principale
 * @author Warlot/Gasquez
 *
 */

public class ControleurFenetre{
	private Fenetre maVue = null;

	//Totalise le nombre de joueurs configures au total
	private static int nbJoueurConfigurer = 0;
	
	/**
	 * Constructeur du controleur de la fenetre
	 * @param frameVue fenetre principale de l'application
	 */
	public ControleurFenetre(Fenetre frameVue){
		this.maVue = frameVue;
	}
	
	/**
	 * Initialiase le contenu de la fenetre sur la premiere vue
	 */
	public void initVue(){
		getMaVue().naviguer(
				new VueParamJeu(this)
				);
	}

	/**
	 * Change le contenu de la fenetre vers la classe de selection de l'equipe
	 * @param tailleEquipe taille de l'equipe
	 * @param longueurPlateau longueur du plateau
	 * @param largeurPlateau largeur du plateau
	 * @param nomJoueur1 nom du joueur 1
	 * @param nomJoueur2 nom du joueur 2
	 */
	public void continueParamJeu(int tailleEquipe, int longueurPlateau, int largeurPlateau, String nomJoueur1, String nomJoueur2){
		try {
			getMaPartie().setTailleEquipePlateau(tailleEquipe, longueurPlateau, largeurPlateau);
		} catch (ExceptionParamJeu e) {
			e.printStackTrace();
			return;
		}

		//Assigne un nom au joueur
		getMaPartie().getJoueurs().get(0).setNom(nomJoueur1);
		getMaPartie().getJoueurs().get(1).setNom(nomJoueur2);
		
		//Initialise le nombre de joueur deja configure
		ControleurFenetre.nbJoueurConfigurer = 0;
		
		//Change le contenu de la fenetre principale
		getMaVue().naviguer(
				new VueParamJoueur(
						this,
						getMaPartie().getNomJoueur()
					)
				);
	}

	/**
	 * Change le contenu de la fenetre vers la classe de placement si tous les joueurs ont compose leur equipe
	 * @param collectionPersonnagesJoueur list des Personnage du joueur actuel
	 * @param err boolean
	 */
	public void continueParamJoueur(List<Personnage> collectionPersonnagesJoueur, boolean err){
		err = false;
		
		int tailleEquipe = collectionPersonnagesJoueur.size();
		int tailleEquipeTotal = getMaPartie().getTailleEquipe();

		//Test equipe compete ou non
		if (tailleEquipeTotal != tailleEquipe) {
			err = true;
			return;
		};

		//Sauvegarde Personnage joueurActuel
		for(Personnage o : collectionPersonnagesJoueur){
			getMaPartie().ajouterPersonnageJoueur(o);
		}
		
		//Joueur suivant
		ControleurFenetre.nbJoueurConfigurer += 1;
		getMaPartie().joueurSuivant();
		
		//si tous les joueurs sont parametres, on passe a l'etape de placement
		if (getMaPartie().getNbJoueurs() == ControleurFenetre.nbJoueurConfigurer){
			ControleurFenetre.nbJoueurConfigurer = 0;
			
			//Placer Personnage
			getMaVue().naviguer(new VueJeuPlacement(
							this,
							getMaPartie().getPlateauHauteur(),
							getMaPartie().getPlateauLargeur(),
							getMaPartie().getJoueurs(),
							getMaPartie().getJoueurActuel()
						)
					);
		} else {
			//sinon on parametre le joueur suivant
			getMaVue().naviguer(
					new VueParamJoueur(
							this,
							getMaPartie().getNomJoueur()
						)
					);
		}
	}
	
	/**
	 * Change le contenu de la fenetre vers la classe du deroulement d'un tour de jeu si tous les joueurs ont place leurs Personnage
	 */
	public void continueGeneralJeu(){
		try {
			getMaPartie().personnagesTousPlaces();
		} catch (ExceptionPersonnage e) {
			e.printStackTrace();
			return;
		}

		//Joueur suivant
		getMaPartie().joueurSuivant();
		ControleurFenetre.nbJoueurConfigurer += 1;
		
		//Si tous les joueurs ont place leurs Personnage
		if (getMaPartie().getNbJoueurs() == ControleurFenetre.nbJoueurConfigurer){
			getMaVue().naviguer(new VueJeuCombat(
							this,
							getMaPartie().getPlateauHauteur(),
							getMaPartie().getPlateauLargeur(),
							getMaPartie().getJoueurs(),
							getMaPartie().getJoueurActuel()
						)
					);
		} else {
			//sinon on parametre le joueur suivant
			getMaVue().naviguer(new VueJeuPlacement(
							this,
							getMaPartie().getPlateauHauteur(),
							getMaPartie().getPlateauLargeur(),
							getMaPartie().getJoueurs(),
							getMaPartie().getJoueurActuel()
						)
					);
		}
                
                maVue.pack();
	}
	
	/**
	 *  Change le contenu de la fenetre vers la classe du deroulement d'un tour de jeu si le jeu n'est pas termine
	 */
	public void tourSuivant(){
		//Recupere tous les personnages qui sont encore en jeu
		List<Personnage> tousLesPersonnages = getMaPartie().listerEquipes();
		boolean encoreDeuxAdversaire = false;
		//On regarde si il y a un personnage qui a un joueur different du premier personnage
		if (getMaPartie().listerEquipes().isEmpty()){
			encoreDeuxAdversaire = false;
		}else{
			//Si les deux joueurs sont encore sur le plateau
			for (Personnage o : getMaPartie().listerEquipes()){
				if(o.getProprio() != tousLesPersonnages.get(0).getProprio()){
					encoreDeuxAdversaire = true;
					break;
				}
			}
		}
		
		//Test si il n'y a plus qu'un joueur
		if(encoreDeuxAdversaire == false){
			//Si il y a un gagnant, alors on le notify
			if(getMaPartie().listerEquipes().isEmpty() == false){
				getMaPartie().partieGagnee(tousLesPersonnages.get(0).getProprio());
			}
			//Si il n'y a plus deux adversaire alors on fini le jeu
			getMaVue().naviguer(new VueFinJeu(this));
			return;
		}

		//Joueur suivant
		getMaPartie().joueurSuivant();
		
		//Recherche un Personnage du joueur actuel qui n'a pas deja joue
		boolean tousPersonnagesjoueurOntJoue = true;
		for(Personnage o : getMaPartie().listerEquipeJoueur()){
			if (!o.isDejaJoue()){
				tousPersonnagesjoueurOntJoue = false;
				break;
			}
		}
		//Si la totalite des personnages du joueur actuel ont joue
		if (tousPersonnagesjoueurOntJoue){
			//Si tous les personnages du jeu ont joue
			boolean tousPFontJoue = true;
			for(Personnage o : tousLesPersonnages){
				if (!o.isDejaJoue()){
					tousPFontJoue = false;
					break;
				}
			}
			//Si tous les personnages du plateau ont joue
			if (tousPFontJoue){
				//remise a zero de l'etat aJoue de chaque personnage
				for(Personnage o : tousLesPersonnages){
					o.setDejaJoue(false);
				}
			} else {
				//Le tour de jeu repasse au meme joueur
				getMaPartie().joueurSuivant();
			}
		}
		
		getMaVue().naviguer(new VueJeuCombat(
						this,
						getMaPartie().getPlateauHauteur(),
						getMaPartie().getPlateauLargeur(),
						getMaPartie().getJoueurs(),
						getMaPartie().getJoueurActuel()
					)
				);
	}
	
	/**
	 *  Change le contenu de la fenetre vers la classe de composition de l'equipe du joueur actuel
	 */
	public void nouvellePartie(){
		ControleurFenetre.nbJoueurConfigurer = 0;
		getMaPartie().reset();
		getMaVue().naviguer(
				new VueParamJoueur(this, getMaPartie().getNomJoueur())
				);
	}
	/****************** GETTERS ******************/
	/**
	 * Getter sur la fenetre de jeu
	 * @return fenetre de jeu
	 */
	public Fenetre getMaVue(){
		return maVue;
	}
	
	/**
	 * Getter sur le modele du jeu en cours
	 * @return jeu en cours
	 */
	public GestionnairePartie getMaPartie(){
		return maVue.getMaPartie();
	}
}
