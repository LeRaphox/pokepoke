package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

public class Copieuse extends CarteSupporter {
    public Copieuse() {
        super("Copieuse", "CES127");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        for (Carte carte: joueur.getCartesMain()) {
            joueur.retirerCarteMain(carte);
            joueur.ajouterCartePioche(carte);
        }
        joueur.melangerPioche();
        joueur.piocherEnMain(joueur.getAdversaire().getNombreDeCartesEnMain());
    }
}
