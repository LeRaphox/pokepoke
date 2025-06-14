package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersPioche;

import java.util.List;

public class SauvetageFamilial extends EnAttaque {

    private int nbPokemon;
    public SauvetageFamilial(Joueur joueurActif, int nbPokemon) {
        super(joueurActif);
        this.nbPokemon = nbPokemon;
        getJeu().instructionProperty().setValue("Choisissez %d pokémon PSY de votre défausse.".formatted(nbPokemon));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getCartesDefausse().stream()
                .filter(c -> c.getTypePokemon() == Type.PSY)
                .map(Carte::getId)
                .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.setCarteEnJeu(Carte.get(numCarte));
            joueur.deplacerCarteComplementaire(numCarte, new DefausseVersPioche());
            nbPokemon--;
            if (nbPokemon == 0 || joueur.getChoixComplementaires().isEmpty()) {
                joueur.viderListChoixComplementaires();
                passerAuJoueurSuivant();
            } else {
                getJeu().instructionProperty().setValue("Choisissez %d pokémon PSY de votre défausse.".formatted(nbPokemon));
            }
        }
    }

    @Override
    public void passer() {
    }

}