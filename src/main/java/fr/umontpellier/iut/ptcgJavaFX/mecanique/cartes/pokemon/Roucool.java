package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Roucool extends CartePokemonBase {
    public Roucool() {
        super(
                "Roucool",
                "TEU121",
                50,
                Type.INCOLORE,
                Type.ELECTRIQUE,
                Type.COMBAT,
                1);

        ajouterAttaque(new Attaque("Collecte", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                joueur.piocherEnMain();
                joueur.getEtatCourant().passer();
            }
        });
        
        ajouterAttaque(new Attaque("Tornade", this, Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 20);
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
