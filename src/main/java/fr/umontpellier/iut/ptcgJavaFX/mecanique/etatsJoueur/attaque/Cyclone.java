package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class Cyclone extends EnAttaque {

    public Cyclone(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un pokémon de banc à échanger avec votre pokémon actif.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        Joueur adversaire = joueur.getAdversaire();
        List<String> choixPossibles = adversaire.getListePokemonDeBanc().stream()
                .map(p -> p.getCartePokemon().getId())
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            Carte cartePokemon = Carte.get(numCarte);
            Pokemon pokemon = adversaire.getPokemon(cartePokemon);
            adversaire.avancerPokemonDeBanc(pokemon);
        } else joueur.getEtatCourant().passer();
    }

}
