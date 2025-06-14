package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class EnJeuLevyEtTatia extends CarteEnJeu {

    public EnJeuLevyEtTatia(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un pokémon de votre banc ou mélangez votre main avec votre deck.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getListePokemonDeBanc().stream()
                .map(p -> p.getCartePokemon().getId())
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.avancerPokemon(numCarte);
            onFinAction();
        }
    }

    @Override
    public void onFinAction() {
        joueur.setPeutMelanger(false);
        super.onFinAction();
    }

    @Override
    public void melangerAEteChoisi() {
        // mélanger la main du joueur avec son deck et piocher 5 cartes en main
        for (Carte carte : joueur.getCartesMain()) {
            joueur.retirerCarteMain(carte);
            joueur.ajouterCartePioche(carte);
        }
        joueur.melangerPioche();
        joueur.piocherEnMain(5);
        onFinAction();
    }

}
