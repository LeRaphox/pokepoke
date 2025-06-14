package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.PiocheVersBanc;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.ChoixBancEnAttaque;

import java.util.List;

public class AppelALaFamille extends EnAttaque {

    public AppelALaFamille(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un Pokémon de base de votre deck.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getCartesPioche().stream()
                        .filter(Carte::estPokemonDeBase)
                        .map(Carte::getId)
                        .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.setCarteEnJeu(Carte.get(numCarte));
            joueur.deplacerCarteComplementaire(numCarte, new PiocheVersBanc());
            joueur.setEtatCourant(new ChoixBancEnAttaque(joueur));
            joueur.viderListChoixComplementaires();
        }
    }
}
