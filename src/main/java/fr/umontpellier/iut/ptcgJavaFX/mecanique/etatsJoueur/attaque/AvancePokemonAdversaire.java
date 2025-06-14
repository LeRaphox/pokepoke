package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.AvancePokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.VerificationPokemonAttaquant;

public class AvancePokemonAdversaire extends AvancePokemon {

    public AvancePokemonAdversaire(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez un nouveau pokémon actif.");
    }

    @Override
    public void passerALEtatSuivant() {
        joueur.getAdversaire().setEtatCourant(new VerificationPokemonAttaquant(joueur.getAdversaire()));
        joueur.getAdversaire().getEtatCourant().verifierPokemonKO();
    }

}
