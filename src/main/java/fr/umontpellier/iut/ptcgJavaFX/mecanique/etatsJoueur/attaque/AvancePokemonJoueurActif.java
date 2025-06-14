package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.AvancePokemon;

public class AvancePokemonJoueurActif extends AvancePokemon {

    public AvancePokemonJoueurActif(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un nouveau pokémon actif.");
    }

}
