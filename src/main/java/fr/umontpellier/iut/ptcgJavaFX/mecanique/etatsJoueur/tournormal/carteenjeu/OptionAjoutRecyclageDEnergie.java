package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersMain;

import java.util.List;

public class OptionAjoutRecyclageDEnergie extends CarteEnJeu {

    public OptionAjoutRecyclageDEnergie(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez une énergie à ajouter en main");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new DefausseVersMain());
            onFinAction();
        }
    }

}
