package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

public class FinPartie extends EtatJoueur {

    public FinPartie(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Fin de la partie");
        getJeu().setFinDePartie();
    }

}