package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.PiocheVersBanc;

import java.util.List;

public class EnJeuFaibloBall extends CarteEnJeu {

    public EnJeuFaibloBall(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un Pokémon de base de votre deck.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                    .map(Carte::getId).toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.setCarteEnJeu(Carte.get(numCarte));
            joueur.deplacerCarteComplementaire(numCarte, new PiocheVersBanc());
            joueur.viderListChoixComplementaires();
        }
    }

    @Override
    public void passer() {
        onFinAction();
    }
}