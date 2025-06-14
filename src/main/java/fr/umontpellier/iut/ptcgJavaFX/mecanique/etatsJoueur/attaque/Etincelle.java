package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Etincelle extends EnAttaque {

    private int nbPokemonAAttaquer = 2;
    private final List<String> choixPossibles;
    public Etincelle(Joueur joueurActif) {
        super(joueurActif);
        choixPossibles = joueur.getAdversaire().getListePokemonDeBanc().stream()
                .map(p -> p.getCartePokemon().getId()).collect(Collectors.toCollection(ArrayList::new));
        getJeu().instructionProperty().setValue("Ajoutez 10 dégâts à un pokémon de banc.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            choixPossibles.remove(numCarte);
            Carte carte = Carte.get(numCarte);
            Pokemon pokemon = joueur.getAdversaire().getPokemon(carte);
            pokemon.ajouterDegats(10);
            nbPokemonAAttaquer--;
            if (nbPokemonAAttaquer == 0 || choixPossibles.isEmpty()) {
                finAttaque();
            }
        }
    }

    @Override
    public void passer() {
    }

}
