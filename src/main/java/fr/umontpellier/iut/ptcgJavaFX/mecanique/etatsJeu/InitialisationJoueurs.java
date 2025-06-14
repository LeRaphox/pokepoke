package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Jeu;

public class InitialisationJoueurs extends EtatJeu {

    public InitialisationJoueurs(Jeu jeu) {
        super(jeu);
        jeu.getJoueurActif().initialiserPokemons();
    }

}
