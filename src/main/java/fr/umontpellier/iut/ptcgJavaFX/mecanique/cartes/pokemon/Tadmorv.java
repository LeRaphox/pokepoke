package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Tadmorv extends CartePokemonBase {
    public Tadmorv() {
        super(
                "Tadmorv",
                "UNM127",
                80,
                Type.OBSCURITE,
                Type.COMBAT,
                Type.PSY,
                2);

        ajouterAttaque(new Attaque("Collecte", this) {
            @Override
            public void attaquer(Joueur joueur) {
                joueur.piocherEnMain(2);
                joueur.getEtatCourant().passer();
            }
        });

        ajouterAttaque(new Attaque("Bomb-Beurk", this, Type.INCOLORE, 3) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 30);
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
