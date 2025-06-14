package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.Cyclone;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.FinPartie;

import java.util.List;

public class Roucarnage extends CartePokemonEvolution {
    public Roucarnage() {
        super(
                "Roucarnage",
                "TEU124",
                130,
                Type.INCOLORE,
                Type.ELECTRIQUE,
                Type.COMBAT,
                0,
                "Roucoups",
                2);

        ajouterAttaque(new Attaque("Cyclone", this, Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 60);
                Joueur adversaire = joueur.getAdversaire();
                Pokemon pokemonAdverse = adversaire.getPokemonActif();
                if (pokemonAdverse == null || pokemonAdverse.getEstProtegeEffetsAttaques()) {
                    // L'attaque n'a aucun effet supplémentaire si le pokémon adverse est protégé
                    joueur.getEtatCourant().passer();
                } else {
                    List<Pokemon> pokemonsDeBancDeLAdversaire = adversaire.getListePokemonDeBanc();
                    if (pokemonsDeBancDeLAdversaire.isEmpty()) {
                        joueur.setEtatCourant(new FinPartie(joueur));
                    } else
                        joueur.setEtatCourant(new Cyclone(joueur));
                }
            }
        });

        ajouterAttaque(new Attaque("Orage Virevoltant", this, Type.INCOLORE, 3) {
            public void attaquer(Joueur joueur) {
                Joueur adversaire = joueur.getAdversaire();
                Pokemon pokemonAdverse = adversaire.getPokemonActif();
                if (pokemonAdverse == null || pokemonAdverse.getEstProtegeEffetsAttaques()) {
                    // L'attaque n'a aucun effet supplémentaire si le pokémon adverse est protégé
                    joueur.getEtatCourant().passer();
                } else {
                    for (Carte c : adversaire.getPokemonActif().getCartes()) {
                        adversaire.ajouterCarteMain(c);
                    }
                    adversaire.setPokemonActif(null);
                    joueur.getEtatCourant().finAttaque();
                }
            }
        });
    }
}
