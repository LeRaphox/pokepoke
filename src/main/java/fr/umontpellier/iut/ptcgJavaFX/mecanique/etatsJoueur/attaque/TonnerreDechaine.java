package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class TonnerreDechaine extends EnAttaque {

    public TonnerreDechaine(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Infligez 40 dégâts à l'un de vos pokémon de banc.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getListePokemonDeBanc().stream()
                .map(p -> p.getCartePokemon().getId())
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            Carte cartePokemon = Carte.get(numCarte);
            Pokemon pokemon = joueur.getPokemon(cartePokemon);
            pokemon.ajouterDegats(40);
            finAttaque();
        }
    }
}
