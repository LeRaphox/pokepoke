package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.AppelALaFamille;

import java.util.List;

public class NidoranF extends CartePokemonBase {
    public NidoranF() {
        super(
                "Nidoran♀",
                "TEU054",
                60,
                Type.PSY,
                Type.PSY,
                null,
                1);

        ajouterAttaque(new Attaque("Appel à la Famille", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                if (joueur.getNbEmplacementsLibres() == 0) {
                    // l'attaque n'a pas d'effet s'il n'y a pas de place sur le banc
                    // on passe au joueur suivant
                    joueur.getEtatCourant().passer();
                }
                List<Carte> listeCartesPokemon = joueur.getCartesPioche().stream()
                        .filter(Carte::estPokemonDeBase)
                        .toList();
                if (!listeCartesPokemon.isEmpty()) {
                    joueur.setListChoixComplementaires(listeCartesPokemon);
                    joueur.setEtatCourant(new AppelALaFamille(joueur));
                } else
                    joueur.getEtatCourant().passer();
            }
        });

        ajouterAttaque(new Attaque("Griffe", this, Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 20);
                joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
