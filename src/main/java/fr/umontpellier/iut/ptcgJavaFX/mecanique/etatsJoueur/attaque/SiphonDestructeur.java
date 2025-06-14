package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class SiphonDestructeur extends EnAttaque {

    public SiphonDestructeur(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Défaussez une énergie du pokémon adverse");
    }

    @Override
    public void defausseEnergie(String numCarte) {
        Joueur adversaire = joueur.getAdversaire();
        Pokemon pokemonAdverse = adversaire.getPokemonActif();
        List<String> choixPossibles = pokemonAdverse.getCartes().stream()
                .filter(c -> c.getTypeEnergie() != null)
                .map(Carte::getId)
                .toList();
        if (choixPossibles.contains(numCarte)) {
            adversaire.finaliserAttaque(numCarte);
            finAttaque();
        }
    }
}
