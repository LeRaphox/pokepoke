package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.SauvetageFamilial;

import java.util.List;

public class Nidorina extends CartePokemonEvolution {
    public Nidorina() {
        super(
                "Nidorina",
                "TEU055",
                90,
                Type.PSY,
                Type.PSY,
                null,
                2,
                "Nidoran♀",
                1);

        ajouterAttaque(new Attaque("Sauvetage Familial", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                List<Carte> pokemonPsyEnEDefausse = joueur.getCartesDefausse().stream()
                        .filter(c -> c.getTypePokemon() == Type.PSY)
                        .toList();
                if (!pokemonPsyEnEDefausse.isEmpty()) {
                    joueur.setListChoixComplementaires(pokemonPsyEnEDefausse);
                    joueur.setEtatCourant(new SauvetageFamilial(joueur, 5));
                } else
                    joueur.getEtatCourant().passer();
            }
        });

        ajouterAttaque(new Attaque("Morsure", this, Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 30);
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}