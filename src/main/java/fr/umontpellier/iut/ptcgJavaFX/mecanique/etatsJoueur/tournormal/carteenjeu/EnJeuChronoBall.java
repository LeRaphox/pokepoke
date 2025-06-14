package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.PiocheVersMain;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

public class EnJeuChronoBall extends CarteEnJeu {

    private int nbPokemons;
    public EnJeuChronoBall(Joueur joueurActif, int nbPokemons) {
        super(joueurActif);
        this.nbPokemons = nbPokemons;
        getJeu().instructionProperty().setValue("Choisissez %d pokémon évolutif%s.".formatted(nbPokemons, nbPokemons > 1 ? "s" : ""));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream().map(Carte::getId).toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.deplacerCarteComplementaire(numCarte, new PiocheVersMain());
            nbPokemons--;
            if (nbPokemons == 0) {
                onFinAction();
            } else
                if (!joueur.getChoixComplementaires().isEmpty()) {
                    getJeu().instructionProperty().setValue("Choisissez %d pokémon évolutif%s.".formatted(nbPokemons, nbPokemons > 1 ? "s" : ""));
                } else
                    joueur.setEtatCourant(new TourNormal(joueur));
        }
    }

    @Override
    public void passer() {
        onFinAction();
    }

}