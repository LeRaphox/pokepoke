package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public class Tili extends CarteSupporter {
    public Tili() {
        super("Tili", "CES132");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        joueur.piocherEnMain(3);
    }
}
