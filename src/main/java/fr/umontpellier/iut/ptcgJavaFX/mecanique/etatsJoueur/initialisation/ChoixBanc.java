package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.initialisation;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

public class ChoixBanc extends EtatJoueur {

    public ChoixBanc(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un emplacement de banc.");
    }

    public void bancChoisi(String numBanc) {
        List<String> choixPossibles = joueur.getIndicesDeBancVides();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numBanc)) {
            joueur.terminerJouerCarteEnJeu(numBanc);
            if (getJeu().initialisationTerminee())
                joueur.setEtatCourant(new TourNormal(joueur));
            else
                joueur.setEtatCourant(new InitialisationPokemonsDeBanc(joueur));
        }
    }

}
