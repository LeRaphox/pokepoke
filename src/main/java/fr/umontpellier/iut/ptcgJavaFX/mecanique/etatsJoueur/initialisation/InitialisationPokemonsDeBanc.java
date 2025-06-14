package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.initialisation;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJeu.PartieEnCours;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public class InitialisationPokemonsDeBanc extends EtatJoueur {

    public InitialisationPokemonsDeBanc(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un Pokémon de base à poser sur le banc.");
    }

    public void carteChoisie(String numPokemon) {
        List<String> choixPossibles = joueur.getPokemonsDeBaseEnMain();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numPokemon)) {
            joueur.jouerCarteEnMain(numPokemon);
            joueur.setEtatCourant(new ChoixBanc(joueur));
        }
    }

    @Override
    public void passer() {
        if (getJeu().initialisationTerminee()) {
            getJeu().setEtatCourant(new PartieEnCours(getJeu()));
            getJeu().getEtatCourant().demarrerPartie();
        } else {
            getJeu().initialiserJoueurSuivant();
        }
    }

}
