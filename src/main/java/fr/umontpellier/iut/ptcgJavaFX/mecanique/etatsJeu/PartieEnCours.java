package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Jeu;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

public class PartieEnCours extends EtatJeu {
    public PartieEnCours(Jeu jeu) {
        super(jeu);
    }

    @Override
    public void demarrerPartie() {
        initialiserEtatJoueurs();
        jeu.passeAuJoueurSuivant();
        jeu.getJoueurActif().jouerTour();
    }

    public void initialiserEtatJoueurs() {
        jeu.getJoueurActif().setEtatCourant(new TourNormal(jeu.getJoueurActif()));
        jeu.getJoueurActif().getAdversaire().setEtatCourant(new TourNormal(jeu.getJoueurActif().getAdversaire()));
    }

}
