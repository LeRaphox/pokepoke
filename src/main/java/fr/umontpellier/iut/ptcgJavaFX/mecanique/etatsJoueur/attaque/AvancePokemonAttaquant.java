package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public class AvancePokemonAttaquant extends AvancePokemonJoueurActif {

    public AvancePokemonAttaquant(Joueur joueurActif) {
        super(joueurActif);
    }

    @Override
    public void passerALEtatSuivant() {
        super.passerALEtatSuivant();
        joueur.getEtatCourant().passer();
    }

}
