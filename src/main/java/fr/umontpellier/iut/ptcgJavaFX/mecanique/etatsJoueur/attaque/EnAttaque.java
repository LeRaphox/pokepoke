package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

public class EnAttaque extends TourNormal {

    public EnAttaque(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("En attaque");
    }

    @Override
    public void finAttaque() {
        joueur.getJeu().verifierPokemonKO();
    }
}
