package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

public class Minidraco extends CartePokemonBase {
    public Minidraco() {
        super(
            "Minidraco", 
            "UNM148", 
            60, 
            Type.DRAGON,
            Type.FEE,
            null, 
            2);

            ajouterAttaque(new Attaque("Saut", this, Type.INCOLORE, 2) {
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

    @Override
    public int getCoutRetraite(Pokemon pokemon) {
        // Le talent de la carte modifie le coût de retraite
        if (pokemon.getEnergie().getOrDefault(Type.EAU, 0) > 0) {
            return 0;
        }
        return super.getCoutRetraite(pokemon);
    }
}
