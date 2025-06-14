package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.initialisation;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

public class InitialisationPokemonActifInitial extends EtatJoueur {

    public InitialisationPokemonActifInitial(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un Pokémon de base.");
    }

    public void choisirPokemon() {
        joueur.setEtatCourant(new PokemonActifInitial(joueur));
    }

}
