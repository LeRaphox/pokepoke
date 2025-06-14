package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.initialisation.ChoixBanc;

public abstract class CartePokemonBase extends CartePokemon {
    public CartePokemonBase(
            String nom,
            String code,
            int pv,
            Type type,
            Type faiblesse,
            Type resistance,
            int coutRetraite) {
        super(nom, code, pv, type, faiblesse, resistance, coutRetraite);
    }

    @Override
    public boolean peutJouer(Joueur joueur) {
        return joueur.getNbEmplacementsLibres() > 0;
    }

    @Override
    public boolean peutJouerInit(Joueur joueur) {
        return peutJouer(joueur);
    }

    @Override
    public void jouer(Joueur joueur) {
        joueur.setCarteEnJeu(this);
        if (joueur.getPokemonActif() == null) {
            // Cas spécial de l'initialisation où la carte est jouée pour occuper le rôle
            // actif
            joueur.setPokemonActif(new Pokemon(this));
            joueur.setCarteEnJeu(null);
        } else {
            joueur.setEtatCourant(new ChoixBanc(joueur));
        }
    }

    @Override
    public int getRangComparaison() {
        return 0;
    }

    @Override
    public boolean estPokemonDeBase() {
        return true;
    }

}
