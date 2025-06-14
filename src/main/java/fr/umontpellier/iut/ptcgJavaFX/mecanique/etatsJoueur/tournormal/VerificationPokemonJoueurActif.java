package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.AvancePokemonJoueurActif;

public class VerificationPokemonJoueurActif extends VerificationPokemon {
    public VerificationPokemonJoueurActif(Joueur joueurActif) {
        super(joueurActif);
    }

    @Override
    public void avancerPokemon() {
        joueur.setEtatCourant(new AvancePokemonJoueurActif(joueur));
    }

    @Override
    public void continuerVerification() {
        joueur.setEtatCourant(new TourNormal(joueur));
    }

}