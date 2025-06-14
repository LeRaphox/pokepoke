package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.VerificationPokemonJoueurActif;

import java.util.List;

public class TalentDracaufeu extends EtatJoueur {

    private int nbCartesEnergie = 2;
    private final Pokemon pokemonUtilisantSonTalent;
    public TalentDracaufeu(Joueur joueurActif, Pokemon pokemonUtilisantSonTalent) {
        super(joueurActif);
        this.pokemonUtilisantSonTalent = pokemonUtilisantSonTalent;
        getJeu().instructionProperty().setValue("Choisissez jusqu'à %d énergie%s.".formatted(nbCartesEnergie, nbCartesEnergie > 1 ? "s" : ""));
    }

    @Override
    public void carteChoisie(String numCarte) {
        // Défausser une carte énergie du pokémon
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                        .map(Carte::getId)
                        .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            Carte carte = Carte.get(numCarte);
            joueur.retirerCartePioche(carte);
            pokemonUtilisantSonTalent.ajouterCarte(carte);
            nbCartesEnergie--;
            if (nbCartesEnergie == 0) {
                onFinAction();
            } else {
                getJeu().instructionProperty().setValue("Choisissez jusqu'à %d énergie%s.".formatted(nbCartesEnergie, nbCartesEnergie > 1 ? "s" : ""));
            }
        }
    }

    @Override
    public void passer() {
        onFinAction();
        super.passer();
    }

    @Override
    public void onFinAction() {
        joueur.melangerPioche();
        joueur.viderListChoixComplementaires();
        joueur.setEtatCourant(new VerificationPokemonJoueurActif(joueur));
        joueur.getEtatCourant().verifierPokemonKO();
    }
}
