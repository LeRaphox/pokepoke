package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersPioche;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

public class EnJeuVaillanceDePierre extends CarteEnJeu {

    private int nbChoixRestants = 6;
    public EnJeuVaillanceDePierre(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez %d carte%s Pokémon ou Énergie de base.".formatted(nbChoixRestants, nbChoixRestants > 1 ? "s" : ""));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new DefausseVersPioche());
            nbChoixRestants--;
            if (nbChoixRestants == 0) {
                onFinAction();
            } else
                if (!joueur.getChoixComplementaires().isEmpty()) {
                    getJeu().instructionProperty().setValue("Choisissez %d carte%s Pokémon ou Énergie de base.".formatted(nbChoixRestants, nbChoixRestants > 1 ? "s" : ""));
                } else
                    joueur.setEtatCourant(new TourNormal(joueur));
        }
    }

}