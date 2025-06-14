package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.initialisation;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public class PokemonActifInitial extends EtatJoueur {

    public PokemonActifInitial(Joueur joueurActif) {
        super(joueurActif);
    }

    public void carteChoisie(String numPokemon) {
        List<String> choixPossibles = joueur.getPokemonsDeBaseEnMain();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numPokemon)) {
            joueur.jouerCarteEnMain(numPokemon);
            joueur.setEtatCourant(new InitialisationPokemonsDeBanc(joueur));
        }
    }

}
