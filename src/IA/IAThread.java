/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IA;

import Controleur.GestionnairePartie;
import Model.Coup;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe qui execute la recherche d'un coup par une intelligence artificielle
 *
 * @author Gwenole Lecorve
 */
public class IAThread extends Thread {

    /**
     * Joueur artificiel
     */
    private final IA ia;

    /**
     * Partie en cours
     */
    private final GestionnairePartie partie;
    
    /**
     * Service d'execution du thread
     */
    private final ExecutorService executor;

    /**
     * Coup choisi a l'issu de la recherche
     */
    private Coup coupChoisi;

    /**
     * Constructeur
     * @param ia Joueur artificiel
     * @param partie Partie en cours
     * @param executor Service d'execution du thread
     */
    public IAThread(IA ia, GestionnairePartie partie, ExecutorService executor) {
        this.ia = ia;
        this.partie = partie;
        this.executor = executor;
        this.coupChoisi = null;
    }
    
    public Coup getCoupChoisi() {
        return coupChoisi;
    }

    /**
     * Lance la recherche d'un nouveau coup dans un thread separe
     */
    @Override
    public void run() {
        try {
            coupChoisi = ia.getCoup(partie);
        }
        catch (Exception ex) { /* Rien */ }
        finally {
            executor.shutdown();
        }
        try {
            sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(IAThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
