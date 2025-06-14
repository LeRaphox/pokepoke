package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersPioche;

import java.util.List;

public class OptionMelangeRecyclageDEnergie extends EnJeuRecyclageDEnergie {

    private int nbCartesAPrendre;
    public OptionMelangeRecyclageDEnergie(Joueur joueurActif) {
        super(joueurActif);
        nbCartesAPrendre = Math.min(3, joueur.getChoixComplementaires().size());
        getJeu().instructionProperty().setValue("Choisissez %d énergie%s à mélanger dans le deck.".formatted(nbCartesAPrendre, nbCartesAPrendre > 1 ? "s" : ""));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new DefausseVersPioche());
            nbCartesAPrendre--;
            if (nbCartesAPrendre != 0) {
                getJeu().instructionProperty().setValue("Choisissez %d énergie%s à mélanger dans le deck.".formatted(nbCartesAPrendre, nbCartesAPrendre > 1 ? "s" : ""));
            } else
                onFinAction();
        }
    }

}
