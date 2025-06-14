package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.FrapEclair;

public class Lanturn extends CartePokemonEvolution {
    public Lanturn() {
        super(
                "Lanturn",
                "CES050",
                110,
                Type.ELECTRIQUE,
                Type.COMBAT,
                Type.METAL,
                2,
                "Loupio",
                1);

        ajouterAttaque(new Attaque("Frap'Éclair", this, Type.ELECTRIQUE, 2, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                joueur.setPeutDefausserEnergie(true);
                joueur.setEtatCourant(new FrapEclair(joueur));
                joueur.setPeutDefausserEnergie(true);
            }
        });
    }

    @Override
    public void onPokemonKO(Pokemon pokemon, Joueur joueur) {
        // A implémenter
    }
}
