package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.PiocheVersMain;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

public class TalentRoucoups extends EtatJoueur {

    public TalentRoucoups(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez une carte à prendre en main.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                        .map(Carte::getId)
                        .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new PiocheVersMain());
            if (!joueur.getChoixComplementaires().isEmpty()) {
                Carte autreCarte = joueur.getChoixComplementaires().removeFirst();
                joueur.ajouterCarteSousLaPioche(autreCarte);
            }
            joueur.setEtatCourant(new TourNormal(joueur));
        }

    }
}
