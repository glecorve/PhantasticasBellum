package GUI.Vue3;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Controleur.*;
import GUI.Vue3.Joueur.VueJoueur;
import GUI.Vue3.Plateau.VuePlateau;
import Model.*;

/**
 * 
 * Classe abstraite d'interface qui va contenir les elements commun au placement et au deroulement du combat
 * @author Warlot/Gasquez
 */

public abstract class VueJeu extends JPanel{
	protected AbstractControleurJeu controleur = null;
	private Joueur joueurActuel = null;
	
	private JPanel panelOuest = new JPanel();
	private JPanel panelEst = new JPanel();
	private JPanel panelCentre = new JPanel();
	private JPanel panelSud = new JPanel();
	
	protected VueJoueur panelJoueur1 = null;
	protected VueJoueur panelJoueur2 = null;
	protected VueJoueur panelJoueurActuel = null;
	protected VuePlateau panelPlateau = null;
	
	/**
	 * Constructeur
	 * @param controleurParent controleur parent
	 * @param listJoueurs listes de tous les joueurs
	 * @param joueurActuel joueur actif
	 */
	public VueJeu(ControleurFenetre controleurParent, List<Joueur> listJoueurs, Joueur joueurActuel){
		this.joueurActuel = joueurActuel;
		setControleur(controleurParent);
		
		setLayout(new BorderLayout(0, 0));
		
		panelOuest.setLayout(new BoxLayout(panelOuest, BoxLayout.Y_AXIS));
		panelEst.setLayout(new BoxLayout(panelEst, BoxLayout.Y_AXIS));
		panelCentre.setLayout(new GridLayout());
		panelSud.setLayout(new FlowLayout());

		add(panelOuest, BorderLayout.WEST);
		add(panelCentre, BorderLayout.CENTER);
		add(panelEst, BorderLayout.EAST);
		add(panelSud, BorderLayout.SOUTH);
		
		panelOuest.add(new JLabel("Joueur : " + listJoueurs.get(0).getNom()));
		panelEst.add(new JLabel("Joueur : " + listJoueurs.get(1).getNom()));
		
		panelCentre.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				majDimension();
			}
		});
		
		majDimension();
	}

	/**
	 * Cache le joueur non actif
	 */
	public void cacherJoueur(){
		if (panelJoueur1.getMonJoueur() == joueurActuel) {
			panelJoueur2.setVisible(false);
		} else {
			panelJoueur1.setVisible(false);
		}
	}
	
	/**
	 * initialise le joueur actuel
	 */
	protected void initPanelJoueurActuel(){
		if (panelJoueur1.getMonJoueur() == joueurActuel) {
			panelJoueurActuel = panelJoueur1;
		} else {
			panelJoueurActuel = panelJoueur2;
		}
	}
	
	/**
	 * Ajoute un object au panel Ouest
	 * @param obj obj a ajouter au panel
	 */
	public void panelOuestAdd(Object obj){
		JScrollPane scroll = new JScrollPane((Component) obj);
		
		panelOuest.add(scroll);
	}	

	/**
	 * Ajoute un object au panel Est
	 * @param obj obj a ajouter au panel
	 */
	public void panelEstAdd(Object obj){
		JScrollPane scroll = new JScrollPane((Component) obj);

		panelEst.add(scroll);
	}	

	/**
	 * Ajoute un object au panel Centre
	 * @param obj obj a ajouter au panel
	 */
	public void panelCenterAdd(Object obj){
		JScrollPane scroll = new JScrollPane((Component) obj);

		panelCentre.add(scroll);
	}
	
	/**
	 * Ajoute un object au panel Sud
	 * @param obj obj a ajouter au panel
	 */
	public void panelSudAdd(Object obj){
		JScrollPane scroll = new JScrollPane((Component) obj);

		panelSud.add(scroll);
	}	
	
	/**
	 * met a jour la dimension des panel Ouest et Est 
	 */
	private void majDimension() {
		panelOuest.setPreferredSize(new Dimension(
				(int) (controleur.getControleurParent().getMaVue().getWidth() * 0.25), 
				(int) (controleur.getControleurParent().getMaVue().getHeight() * 0.25))
		);
		panelEst.setPreferredSize(new Dimension(
				(int) (controleur.getControleurParent().getMaVue().getWidth() * 0.25), 
				(int) (controleur.getControleurParent().getMaVue().getHeight() * 0.25))
		);
	}
	
	public abstract AbstractControleurJeu getControleur();
	public abstract VueJeu getMaVue();
	public abstract VueJoueur getPanelJoueur1();
	public abstract VueJoueur getPanelJoueur2();
	public abstract VueJoueur getPanelJoueurActuel();
	public abstract VuePlateau getPanelPlateau();
	
	/**
	 * Fixe AbstractControleurJeu
	 * @param controleurParent controleur de la fenetre principale
	 */
	protected abstract void setControleur(ControleurFenetre controleurParent);
}
