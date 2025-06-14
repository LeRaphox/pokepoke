package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.Flammeche;

public class Salameche extends CartePokemonBase {
    public Salameche() {
        super(
                "Salamèche",
                "TEU012",
                70,
                Type.FEU,
                Type.EAU,
                null,
                1);

        ajouterAttaque(new Attaque("Flammèche", this, Type.FEU, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 30);
                joueur.setEtatCourant(new Flammeche(joueur));
            }

        });
    }
}
