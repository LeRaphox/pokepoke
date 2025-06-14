package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Galopa extends CartePokemonEvolution {
    public Galopa() {
        super(
                "Galopa",
                "TEU018",
                100,
                Type.FEU,
                Type.EAU,
                null,
                1,
                "Ponyta",
                1);

        ajouterAttaque(new Attaque("Flammes Calcinantes", this, Type.FEU, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 20);
                joueur.getAdversaire().getPokemonActif().setEstBrule();
                joueur.getEtatCourant().finAttaque();
            }
        });

        ajouterAttaque(new Attaque("Hâte", this, Type.FEU, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 60);
                if (joueur.lancerPiece()) {
                    // le pokémon est protégé des effets et dégâts des attaques adverses pendant le
                    // prochain tour
                    Pokemon pokemon = joueur.getPokemon(this.getCarte());
                    pokemon.setEstProtegeEffetsAttaques();
                }
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
