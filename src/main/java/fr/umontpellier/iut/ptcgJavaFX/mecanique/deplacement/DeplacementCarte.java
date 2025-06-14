package fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

public interface DeplacementCarte {

    void deplacer(Carte carte, Joueur joueur);
}
