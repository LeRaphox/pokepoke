package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

public class CarteEnJeu extends EtatJoueur {

    public CarteEnJeu(Joueur joueurActif) {
        super(joueurActif);
    }

    @Override
    public void onFinAction() {
        joueur.setCarteEnJeu(null);
        joueur.viderListChoixComplementaires();
        joueur.setEtatCourant(new TourNormal(joueur));
    }
}
