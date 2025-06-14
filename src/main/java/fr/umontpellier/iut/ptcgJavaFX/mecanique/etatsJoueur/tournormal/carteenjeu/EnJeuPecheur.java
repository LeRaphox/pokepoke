package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersMain;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

public class EnJeuPecheur extends CarteEnJeu {

    private int nbChoixRestants = 4;
    public EnJeuPecheur(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez %d carte%s Énergie de base de votre défausse.".formatted(nbChoixRestants, nbChoixRestants > 1 ? "s" : ""));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new DefausseVersMain());
            nbChoixRestants--;
            if (nbChoixRestants == 0) {
                onFinAction();
            } else
                if (!joueur.getChoixComplementaires().isEmpty()) {
                    getJeu().instructionProperty().setValue("Choisissez %d carte%s Énergie de base de votre défausse.".formatted(nbChoixRestants, nbChoixRestants > 1 ? "s" : ""));
                } else
                    joueur.setEtatCourant(new TourNormal(joueur));
        }
    }

}