package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public class ChoixPokemon extends EtatJoueur {

    public ChoixPokemon(Joueur joueurActif, String instruction) {
        super(joueurActif);
        getJeu().instructionProperty().setValue(instruction);
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getCartesJouablesEnSuite();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.terminerJouerCarteEnJeu(numCarte);
            passerALEtatSuivant();
        }
    }

    public void passerALEtatSuivant() {
        joueur.setEtatCourant(new TourNormal(joueur));
    }

}
