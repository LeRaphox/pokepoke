package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.Etincelle;

import java.util.List;

public class Loupio extends CartePokemonBase {
    public Loupio() {
        super(
                "Loupio",
                "CES049",
                60,
                Type.ELECTRIQUE,
                Type.COMBAT,
                Type.METAL,
                1);

        ajouterAttaque(new Attaque("Écras'Face", this, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 10);
                joueur.getEtatCourant().finAttaque();
            }
        });

        ajouterAttaque(new Attaque("Étincelle", this, Type.ELECTRIQUE, 1, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 10);
                List<Pokemon> cartesPokemonDeBanc = joueur.getAdversaire().getListePokemonDeBanc();
                if (!cartesPokemonDeBanc.isEmpty()) {
                    joueur.setEtatCourant(new Etincelle(joueur));
                } else
                    joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
