package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.SiphonDestructeur;

import java.util.List;

public class Draco extends CartePokemonEvolution {
    public Draco() {
        super(
                "Draco",
                "UNM150",
                100,
                Type.DRAGON,
                Type.FEE,
                null,
                1,
                "Minidraco",
                2);

        ajouterAttaque(new Attaque("Queue Battoir", this, Type.INCOLORE, 1) {

            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 20);
                joueur.getEtatCourant().finAttaque();
            }
        });

        ajouterAttaque(new Attaque("Siphon Destructeur", this, Type.EAU, 1, Type.ELECTRIQUE, 1,
                Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 70);
                Joueur adversaire = joueur.getAdversaire();
                Pokemon pokemonAdverse = adversaire.getPokemonActif();
                List<Carte> cartesEnergieAdversaire = pokemonAdverse.getCartes().stream()
                        .filter(c -> c.getTypeEnergie() != null)
                        .toList();
                if (pokemonAdverse == null || pokemonAdverse.getEstProtegeEffetsAttaques() || cartesEnergieAdversaire.isEmpty()) {
                    joueur.getEtatCourant().finAttaque();
                } else {
                    joueur.setEtatCourant(new SiphonDestructeur(joueur));
                }
            }
        });
    }
}
