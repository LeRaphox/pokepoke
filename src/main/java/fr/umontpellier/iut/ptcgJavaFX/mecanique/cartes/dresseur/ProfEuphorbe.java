package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public class ProfEuphorbe extends CarteSupporter {
    public ProfEuphorbe() {
        super("Prof. Euphorbe", "SUM128");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        joueur.piocherEnMain(2);
        joueur.incrementerBonusDegats(20);
    }
}
