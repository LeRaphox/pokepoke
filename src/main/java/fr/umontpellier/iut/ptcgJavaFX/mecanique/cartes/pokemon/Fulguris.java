package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.TonnerreDechaine;

import java.util.List;

public class Fulguris extends CartePokemonBase {
    public Fulguris() {
        super(
                "Fulguris",
                "UNM068",
                120,
                Type.ELECTRIQUE,
                Type.COMBAT,
                Type.METAL,
                1);

        ajouterAttaque(new Attaque("Bourrasque Fulgurante", this, Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                Pokemon boreas = joueur.getListePokemonDeBanc().stream()
                        .filter(p -> p.getCartePokemon().getNom().equals("Boréas"))
                        .findFirst()
                        .orElse(null);
                if (boreas != null)
                    infligerDegatsAdversaire(joueur, 70);
                else
                    infligerDegatsAdversaire(joueur, 20);
                joueur.getEtatCourant().finAttaque();
            }
        });

        ajouterAttaque(new Attaque("Tonnerre Déchaîné", this, Type.ELECTRIQUE, 2, Type.INCOLORE, 1) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 120);
                List<String> listeCartesPokemon = joueur.getListePokemonDeBanc().stream()
                        .map(p -> p.getCartePokemon().getId())
                        .toList();
                if (!listeCartesPokemon.isEmpty()) {
                    joueur.setEtatCourant(new TonnerreDechaine(joueur));
                } else joueur.getEtatCourant().finAttaque();
            }
        });
    }
}
