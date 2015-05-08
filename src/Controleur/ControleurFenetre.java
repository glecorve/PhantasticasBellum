package Controleur;

import java.util.List;

import Exception.*;
import GUI.*;
import GUI.Vue1.VueParamJeu;
import GUI.Vue2.VueParamJoueur;
import GUI.Vue3.VueJeuCombat;
import GUI.Vue3.VueJeuPlacement;
import GUI.Vue4.VueFinJeu;
import IA.AbstractIA;
import IA.IAAleatoire;
import IA.IAThread;
import Model.*;
import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Classe controleur (principale) de la fenetre de jeu, permet de changer le contenu de la fenetre principale
 * @author Warlot/Gasquez
 *
 */

public class ControleurFenetre {
    
	private Fenetre vue = null;

        private Partie configurationInitiale = null;

	//Totalise le nombre de joueurs configures au total
	private static int nbJoueurConfigurer = 0;
	
	/**
	 * Constructeur du controleur de la fenetre
	 * @param frameVue fenetre principale de l'application
	 */
	public ControleurFenetre(Fenetre frameVue){
		this.vue = frameVue;
	}
	
	/**
	 * Initialiase le contenu de la fenetre sur la premiere vue
	 */
	public void initVue(){
		getVue().naviguer(
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
			getPartie().setTailleEquipePlateau(tailleEquipe, longueurPlateau, largeurPlateau);
		} catch (ExceptionParamJeu e) {
			e.printStackTrace();
			return;
		}

		//Assigne un nom au joueur
		getPartie().getJoueurs().get(0).setNom(nomJoueur1);
		getPartie().getJoueurs().get(1).setNom(nomJoueur2);
		
		//Initialise le nombre de joueur deja configure
		ControleurFenetre.nbJoueurConfigurer = 0;
		
		//Change le contenu de la fenetre principale
		getVue().naviguer(
				new VueParamJoueur(
						this,
						getPartie().getNomJoueur()
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
		int tailleEquipeTotal = getPartie().getTailleEquipe();

		//Test equipe compete ou non
		if (tailleEquipeTotal != tailleEquipe) {
			err = true;
			return;
		};

		//Sauvegarde Personnage joueurActuel
		for(Personnage o : collectionPersonnagesJoueur){
			getPartie().ajouterPersonnageJoueur(o);
		}
		
		//Joueur suivant
		ControleurFenetre.nbJoueurConfigurer += 1;
		getPartie().joueurSuivant();
		
		//si tous les joueurs sont parametres, on passe a l'etape de placement
		if (getPartie().getNbJoueurs() == ControleurFenetre.nbJoueurConfigurer){
			ControleurFenetre.nbJoueurConfigurer = 0;
			
			//Placer Personnage
			getVue().naviguer(new VueJeuPlacement(
							this,
							getPartie().getPlateauHauteur(),
							getPartie().getPlateauLargeur(),
							getPartie().getJoueurs(),
							getPartie().getJoueurActuel()
						)
					);
		} else {
			//sinon on parametre le joueur suivant
			getVue().naviguer(
					new VueParamJoueur(
							this,
							getPartie().getNomJoueur()
						)
					);
		}
	}
	
	/**
	 * Change le contenu de la fenetre vers la classe du deroulement d'un tour de jeu si tous les joueurs ont place leurs Personnage
	 */
	public void continueGeneralJeu(){
		try {
			getPartie().personnagesTousPlaces();
		} catch (ExceptionPersonnage e) {
			e.printStackTrace();
			return;
		}

		//Joueur suivant
		getPartie().joueurSuivant();
		ControleurFenetre.nbJoueurConfigurer += 1;
		
		//Si tous les joueurs ont place leurs Personnage
		if (getPartie().getNbJoueurs() == ControleurFenetre.nbJoueurConfigurer){
			getVue().naviguer(new VueJeuCombat(
							this,
							getPartie().getPlateauHauteur(),
							getPartie().getPlateauLargeur(),
							getPartie().getJoueurs(),
							getPartie().getJoueurActuel()
						)
					);
                        if (getPartie().getJoueurActuel() instanceof AbstractIA) {
                            coupSuivant();
                            tourSuivant();
                        }
		} else {
			//sinon on parametre le joueur suivant
			getVue().naviguer(new VueJeuPlacement(
							this,
							getPartie().getPlateauHauteur(),
							getPartie().getPlateauLargeur(),
							getPartie().getJoueurs(),
							getPartie().getJoueurActuel()
						)
					);
                        setConfigurationInitiale(getPartie().clone());
		}
                
                vue.pack();
	}
	
	/**
	 *  Change le contenu de la fenetre vers la classe du deroulement d'un tour de jeu si le jeu n'est pas termine
	 */
	public void tourSuivant(){
            boolean continuer = false;
            // Enchainer les tours tant que le joueur suivant est un joueur artificiel
            do {
                continuer = getPartie().tourSuivant();
                if (getPartie().getNumeroTour() == 100) {
                    System.err.println(getPartie().getJoueurs().get(0).getNom() + " contre " + getPartie().getJoueurs().get(1).getNom() + " : match nul");
                    System.exit(0);
                }
		if (continuer) {
                    System.gc();
                    getVue().naviguer(new VueJeuCombat(
						this,
						getPartie().getPlateauHauteur(),
						getPartie().getPlateauLargeur(),
						getPartie().getJoueurs(),
						getPartie().getJoueurActuel()
					)
				);
                    if (getPartie().getJoueurActuel() instanceof AbstractIA) {
                        coupSuivant();
                    }
                }
                else {
                    Joueur j = getPartie().listerEquipes().get(0).getProprio();
                    int pv = 0;
                    for (Personnage perso : j.listerEquipe()) {
                        pv += perso.getVie();
                    }
                    JOptionPane.showMessageDialog(getVue(), "<html>Victoire de <a color='" + j.getCouleurHTML() + "'>" + j.getNom() + "</a> ! Félicitations !<br>Points de vie restants : " + pv + ".</html>");
//                    System.err.println(getPartie().getJoueurs().get(0).getNom() + " contre " + getPartie().getJoueurs().get(1).getNom() + " : victoire de " + j.getNom() + " (" + pv + " PV restants)");
                    getPartie().signifierVictoire(getPartie().listerEquipes().get(0).getProprio());
                    getVue().naviguer(new VueFinJeu(this));
                    System.exit(0);
                }
                
            } while (continuer && getPartie().getJoueurActuel() instanceof AbstractIA);
                
	}
        
        private String getHTMLColorString(Color color) {
            String red = Integer.toHexString(color.getRed());
            String green = Integer.toHexString(color.getGreen());
            String blue = Integer.toHexString(color.getBlue());

            return "#" + 
                    (red.length() == 1? "0" + red : red) +
                    (green.length() == 1? "0" + green : green) +
                    (blue.length() == 1? "0" + blue : blue);        
        }
        
        private boolean coupValide(Joueur joueur, Coup coup) {
            if (coup == null) { System.out.println("Coup nul"); return false; }
            
            Personnage auteur = null;

            for (Personnage perso : getPartie().getJoueurActuel().listerEquipe()) {
                if (perso.equals(coup.getAuteur())) {
                    auteur = perso;
                }
            }
            if (auteur == null || auteur.getProprio() == null) { System.out.println("Auteur ou proprio nul");return false; }
            if (!auteur.getProprio().equals(joueur)) { System.out.println("Auteur != joueur"); return false; }
            if (auteur.isDejaJoue()) { System.out.println("Perso a déjà joué"); return false; }
            for (Action a : coup.getActions()) {
                if (a instanceof Deplacement && !getPartie().isCaseLibre(((Deplacement) a).getDestination())) {
                    System.out.println("Déplacement interdit");
                    return false;
                }
            }
            return true;
        }
        
        /**
         * Passe au coup suivant et joue ce coup pour les joueurs artificiels
         */
        public synchronized void coupSuivant() {
            if (getPartie().getJoueurActuel() instanceof AbstractIA) {
                synchronized(getPartie()) {
                    System.out.println(Thread.currentThread().getName()+": "+"================================[ " + getPartie().getJoueurActuel().getClass().toString() + " " + getPartie().getJoueurActuel().getNom() + " ]==========================================");
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    AbstractIA ia = (AbstractIA) getPartie().getJoueurActuel();
                    IAThread calcul = new IAThread(ia, getPartie(), executor);
                    executor.execute(calcul);
                    try {
    //                    System.out.println("Top");
                        if (!executor.awaitTermination(AbstractIA.DELAI_DE_REFLEXION, TimeUnit.MILLISECONDS))
                        {
                            // Forcer la fin du thread du joueur artificiel
    //                        System.out.println(Thread.currentThread().getName()+": "+"Forcer l'interruption");
                            executor.shutdownNow();
    //                        System.out.println(Thread.currentThread().getName()+": "+"est interrompu ? = " + (calcul.isInterrupted()?"oui":"non"));
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Partie.class.getName()).log(Level.SEVERE, null, ex);
                    }
    //                System.out.println("Stop");
                    try {
                        calcul.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControleurFenetre.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    while (calcul.isAlive()) {
    //                    calcul.stop();
                    }
                    System.gc();
                    Coup coup;
                    // Si aucun coup n'a ete retourne, prendre le dernier coup memorise
                    if (calcul.getCoupChoisi() == null) {
    //                    System.out.println(Thread.currentThread().getName()+": "+"Aucun coup choisi");
                        coup = ia.getCoupMemorise();
                    }
                    else {
    //                    System.out.println(Thread.currentThread().getName()+": "+"coup choisi = " + calcul.getCoupChoisi());
                        coup = calcul.getCoupChoisi();
                    }
                    // Si aucun coup memorise, prendre un coup au hasard
                    while (!coupValide(ia, coup)) {
                        System.out.println(Thread.currentThread().getName()+": "+"Aucun coup memorise");
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.out.println("!!!!!!!!!!!!!!!! HASARD !!!!!!!!!!!!!!!!");
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        coup = (new IAAleatoire()).getCoup(getPartie());
    //                    System.out.println("Nouveau coup calcule = " + coup);
                    }

                    System.out.println("Coup choisi = "+coup.toString());

                    try {
                        getPartie().appliquerCoup(coup);
                    }
                    catch (NullPointerException e) {
                        coup = (new IAAleatoire()).getCoup(getPartie());
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.out.println("!!!!!!!!!!!!!!!! HASARD !!!!!!!!!!!!!!!!");
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        getPartie().appliquerCoup(coup);
                    }
                    finally  {
                        if (coup.getActions().isEmpty()) {
                           ((VueJeuCombat) vue.getContentPane().getComponent(0)).majConsole(getPartie().getNumeroTour(), ia, coup.getAuteur().toString() + " passe son tour");
                    }
                        else {
                            ((VueJeuCombat) vue.getContentPane().getComponent(0)).majConsole(getPartie().getNumeroTour(), ia, coup.toString());
                        }
                    }


                    getVue().revalidate();
                    try {
                        Thread.sleep(10);
//                        Thread.sleep(400);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControleurCombat.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    System.out.println("Fin de tour");
                    for (Joueur j : getPartie().getJoueurs()) {
                        System.out.println(j.toString());
                    }
                }
            }
        }
	
	/**
	 *  Change le contenu de la fenetre vers la classe de composition de l'equipe du joueur actuel
	 */
	public void nouvellePartie(){
		ControleurFenetre.nbJoueurConfigurer = 0;
		getPartie().reset();
		getVue().naviguer(
				new VueParamJoueur(this, getPartie().getNomJoueur())
				);
	}
	/****************** GETTERS ******************/
	/**
	 * Getter sur la fenetre de jeu
	 * @return fenetre de jeu
	 */
	public Fenetre getVue(){
		return vue;
	}
	
	/**
	 * Getter sur le modele du jeu en cours
	 * @return jeu en cours
	 */
	public Partie getPartie(){
		return vue.getPartie();
	}

        /**
         * Renvoie la derniere configuration initiale
         * @return the configurationInitiale
         */
        public Partie getConfigurationInitiale() {
            return configurationInitiale;
        }
        
        /****************** SETTER ******************/
        

        /**
         * @param configurationInitiale the configurationInitiale to set
         */
        public void setConfigurationInitiale(Partie configurationInitiale) {
            this.configurationInitiale = configurationInitiale;
        }
}
