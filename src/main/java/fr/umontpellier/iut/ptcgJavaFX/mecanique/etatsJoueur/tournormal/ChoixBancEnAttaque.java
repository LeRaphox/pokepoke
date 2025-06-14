package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.EnAttaque;

import java.util.List;

public class ChoixBancEnAttaque extends EnAttaque {

    public ChoixBancEnAttaque(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un emplacement de banc.");
    }

    public void bancChoisi(String numBanc) {
        List<String> choixPossibles = joueur.getIndicesDeBancVides();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numBanc)) {
            joueur.terminerJouerCarteEnJeu(numBanc);
            joueur.viderListChoixComplementaires();
            passer();
        }
    }

}
