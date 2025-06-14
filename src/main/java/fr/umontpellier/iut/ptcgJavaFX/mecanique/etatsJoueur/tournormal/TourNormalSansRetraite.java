package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public class TourNormalSansRetraite extends EtatJoueur {

    public TourNormalSansRetraite(Joueur joueur) {
        super(joueur);
        getJeu().instructionProperty().setValue("Choisissez une action ou passez");
    }

    @Override
    public void passer() {
        getJeu().controlePokemon();
        joueur.onFinTour();
        if (!getJeu().estTermine())
            passerAuJoueurSuivant();
        else
            joueur.setEtatCourant(new FinPartie(joueur));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getCartesEnMainJouables();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            List<String> pokemonsEnJeuAvecTalent = joueur.getListePokemonEnJeu().stream().filter(Pokemon::peutUtiliserTalent).map(Pokemon::getCartePokemon).map(Carte::getId).toList();
            if (pokemonsEnJeuAvecTalent.contains(numCarte)) {
                joueur.getPokemon(Carte.get(numCarte)).utiliserTalent(joueur);
            } else
                joueur.jouerCarteEnMain(numCarte);
        } else passerALEtatSuivant();
    }

    public void passerAuJoueurSuivant() {
        getJeu().passeAuJoueurSuivant();
        joueur = getJeu().getJoueurActif();
        passerALEtatSuivant();
        joueur.jouerTour();
    }

    public void passerALEtatSuivant() {
        joueur.setEtatCourant(new TourNormalSansRetraite(joueur));
    }

}
