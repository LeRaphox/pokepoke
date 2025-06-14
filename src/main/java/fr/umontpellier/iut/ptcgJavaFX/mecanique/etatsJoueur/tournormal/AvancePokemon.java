package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public class AvancePokemon extends EtatJoueur {

    public AvancePokemon(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un nouveau pokémon actif.");
    }

    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getListePokemonDeBanc().stream().map(p -> p.getCartePokemon().getId()).toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.avancerPokemon(numCarte);
            passerALEtatSuivant();
        }
    }

    public void passerALEtatSuivant() {
        joueur.setEtatCourant(new TourNormal(joueur));
    }

}
