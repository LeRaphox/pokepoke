package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.AvancePokemonAdversaire;

public class VerificationPokemonAdversaire extends VerificationPokemon {
    public VerificationPokemonAdversaire(Joueur joueurActif) {
        super(joueurActif.getAdversaire());
    }

    @Override
    public void continuerVerification() {
        // ici joueur représente celui sur lequel on fait la vérifi soit l'adversaire de l'actif dans le jeu
        // on continue sur la vérification du joueut Attaquant, cad l'adversaire du joueur en cours de vérification
        joueur.getAdversaire().setEtatCourant(new VerificationPokemonAttaquant(joueur.getAdversaire()));
        joueur.getAdversaire().getEtatCourant().verifierPokemonKO();
    }

    @Override
    public void avancerPokemon() {
        joueur.getAdversaire().setEtatCourant(new AvancePokemonAdversaire(joueur));
    }

}