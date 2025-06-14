package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Reptincel extends CartePokemonEvolution {
    public Reptincel() {
        super(
                "Reptincel",
                "TEU013",
                90,
                Type.FEU,
                Type.EAU,
                null,
                2,
                "Salamèche",
                1);

        ajouterAttaque(new Attaque("Crocs Feu", this, Type.FEU, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 30);
                Pokemon pokemonAdverse = joueur.getAdversaire().getPokemonActif();
                if (pokemonAdverse == null || pokemonAdverse.getEstProtegeEffetsAttaques()) {
                    return;
                }
                pokemonAdverse.setEstBrule();
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
