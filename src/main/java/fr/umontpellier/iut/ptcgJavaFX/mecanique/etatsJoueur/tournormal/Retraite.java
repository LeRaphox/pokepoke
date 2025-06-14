package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

public class Retraite extends EtatJoueur {
    public Retraite(Joueur joueurActif) {
        super(joueurActif);
    }

    public void retraiteChoisie() {
        int coutRestant = joueur.getPokemonActif().getCoutRetraite();
        if (coutRestant > 0)
            joueur.setEtatCourant(new DefausseEnergie(joueur, coutRestant));
        else
            joueur.setEtatCourant(new AvancePokemon(joueur));
    }

}