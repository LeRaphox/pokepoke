package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class Flammeche extends EnAttaque {

    public Flammeche(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Défaussez une énergie de ce pokémon.");
    }

    @Override
    public void defausseEnergie(String numCarte) {
        // Défausser une carte énergie du pokémon
        List<String> choixPossibles = joueur.getPokemonActif().getCartes().stream()
                        .filter(c -> c.getTypeEnergie() != null)
                        .map(Carte::getId)
                        .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.finaliserAttaque(numCarte);
            finAttaque();
        }
    }
}
