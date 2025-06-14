package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public class TourNormal extends TourNormalSansRetraite {

    public TourNormal(Joueur joueur) {
        super(joueur);
        getJeu().instructionProperty().setValue("Choisissez une action ou passez");
    }

    @Override
    public void retraiteChoisie() {
        if (joueur.peutRetraite()) {
            joueur.setEtatCourant(new Retraite(joueur));
            joueur.getEtatCourant().retraiteChoisie();
        }
    }

    public void passerALEtatSuivant() {
        joueur.setEtatCourant(new TourNormal(joueur));
    }

}
