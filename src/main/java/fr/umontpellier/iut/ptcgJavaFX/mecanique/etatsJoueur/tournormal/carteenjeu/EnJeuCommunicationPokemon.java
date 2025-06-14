package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.MainVersPioche;

import java.util.List;

public class EnJeuCommunicationPokemon extends CarteEnJeu {

    public EnJeuCommunicationPokemon(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un pokémon de votre main.");
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getCartesMain().stream()
                .filter(c -> c.getTypePokemon() != null)
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            List<Carte> pokemonsDeLaPioche = joueur.getCartesPioche().stream()
                    .filter(c -> c.getTypePokemon() != null)
                    .toList();
            joueur.deplacer(numCarte, new MainVersPioche());
            joueur.setListChoixComplementaires(pokemonsDeLaPioche);
            joueur.setEtatCourant(new SuiteChoixCommunicationPokemon(joueur));
        }
    }

    @Override
    public void passer() {
        onFinAction();
    }

}