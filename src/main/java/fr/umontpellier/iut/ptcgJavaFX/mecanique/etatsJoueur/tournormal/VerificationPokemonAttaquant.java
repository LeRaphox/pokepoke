package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.AvancePokemonAttaquant;

public class VerificationPokemonAttaquant extends VerificationPokemonJoueurActif {
    public VerificationPokemonAttaquant(Joueur joueurActif) {
        super(joueurActif); // ici joueurActif représente le joueur actif du jeu, donc l'attaquant
    }

    @Override
    public void avancerPokemon() {
        joueur.setEtatCourant(new AvancePokemonAttaquant(joueur));
    }

    @Override
    public void continuerVerification() {
        super.continuerVerification();
        joueur.getEtatCourant().passer();
    }

}