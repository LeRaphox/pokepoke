package fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

public class DefausseVersMain implements DeplacementCarte {

    @Override
    public void deplacer(Carte carte, Joueur joueur) {
        joueur.retirerCarteDefausse(carte);
        joueur.ajouterCarteMain(carte);
    }

}
