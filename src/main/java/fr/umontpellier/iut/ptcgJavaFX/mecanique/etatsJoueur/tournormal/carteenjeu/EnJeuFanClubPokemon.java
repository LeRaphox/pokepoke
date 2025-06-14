package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.PiocheVersMain;

import java.util.List;

public class EnJeuFanClubPokemon extends CarteEnJeu {

    private int nbChoixRestants = 2;
    public EnJeuFanClubPokemon(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez jusqu'à %d pokémon%s de base.".formatted(nbChoixRestants, nbChoixRestants > 1 ? "s" : ""));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new PiocheVersMain());
            nbChoixRestants--;
            if (nbChoixRestants == 0) {
                onFinAction();
            } else {
                getJeu().instructionProperty().setValue("Choisissez jusqu'à %d pokémon de base.".formatted(nbChoixRestants));
            }
        }
    }

    @Override
    public void passer() {
        onFinAction();
    }

}