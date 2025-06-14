package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Ponyta extends CartePokemonBase {
    public Ponyta() {
        super(
                "Ponyta",
                "TEU017",
                70,
                Type.FEU,
                Type.EAU,
                null,
                1);

        ajouterAttaque(new Attaque("Charbon Mutant", this, Type.FEU, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 10);
                joueur.getEtatCourant().finAttaque();
            }
        });
        
        ajouterAttaque(new Attaque("Écrasement", this, Type.FEU, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                if (joueur.lancerPiece()) {
                    infligerDegatsAdversaire(joueur, 40);
                } else {
                    infligerDegatsAdversaire(joueur, 10);
                }
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
