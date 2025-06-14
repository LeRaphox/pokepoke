package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

/**
 * Remarque : Étant donné qu'il n'y a pas de cartes Outil dans les cartes
 * programmées dans ce projet, le texte descriptif de l'attaque "Destructeur
 * d'Outils" peut être totalement ignoré.
 */
public class Canarticho extends CartePokemonBase {
    public Canarticho() {
        super(
                "Canarticho",
                "TEU127",
                80,
                Type.INCOLORE,
                Type.ELECTRIQUE,
                Type.COMBAT,
                1);
        
        ajouterAttaque(new Attaque("Collecte", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                joueur.piocherEnMain(2);
                joueur.getEtatCourant().passer();
            }
        });

        ajouterAttaque(new Attaque("Destructeur d'Outils", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 20);
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
