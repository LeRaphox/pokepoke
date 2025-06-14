package fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

public class PiocheVersBanc implements DeplacementCarte {

    @Override
    public void deplacer(Carte carte, Joueur joueur) {
        joueur.retirerCartePioche(carte);
        joueur.jouerCarte(carte);
        joueur.melangerPioche();
    }

}

