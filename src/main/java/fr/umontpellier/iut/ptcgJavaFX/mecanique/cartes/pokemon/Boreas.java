package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Boreas extends CartePokemonBase {
    public Boreas() {
        super(
                "Boréas",
                "UNM178",
                120,
                Type.INCOLORE,
                Type.ELECTRIQUE,
                Type.COMBAT,
                1);

        ajouterAttaque(new Attaque("Coud'Phalange", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 20);
                joueur.getEtatCourant().finAttaque();
            }
        });

        ajouterAttaque(new Attaque("Tornade Fulgurante", this, Type.INCOLORE, 3) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 80);

                // infliger 20 dégats à chacun des Pokémon de Banc de l'adversaire si Fulguris
                // est présent sur le banc
                for (Pokemon pokemon : joueur.getListePokemonDeBanc()) {
                    if (pokemon.getCartePokemon().getNom().equals("Fulguris")) {
                        for (Pokemon pokemonCible : joueur.getAdversaire().getListePokemonDeBanc()) {
                            if (pokemonCible.getEstProtegeEffetsAttaques()) {
                                continue;
                            }
                            pokemonCible.ajouterDegats(20);
                        }
                        break;
                    }
                }
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
