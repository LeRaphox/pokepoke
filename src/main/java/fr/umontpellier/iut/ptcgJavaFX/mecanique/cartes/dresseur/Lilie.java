package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

public class Lilie extends CarteSupporter {
    public Lilie() {
        super("Lilie", "UPR125");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        int n = joueur.getCompteurTour() <= 2 ? 8 : 6;
        while(joueur.getNombreDeCartesEnMain() < n) {
            Carte c = joueur.piocherEnMain();
            if (c == null) {
                break;
            }
        }
    }
}
