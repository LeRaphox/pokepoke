package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public class Scout extends CarteSupporter {
    public Scout() {
        super("Scout", "UNM189");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        joueur.piocherEnMain(2);
        if (joueur.lancerPiece()) {
            joueur.piocherEnMain(2);
        }
    }
}
