package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.PiocheVersMain;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

public class TalentNidoqueen extends EtatJoueur {

    public TalentNidoqueen(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un pokémon de votre deck.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        // Défausser une carte énergie du pokémon
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                        .map(Carte::getId)
                        .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new PiocheVersMain());
            joueur.viderListChoixComplementaires();
            joueur.setEtatCourant(new TourNormal(joueur));
        }
    }

}
