package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public abstract class VerificationPokemon extends EtatJoueur {
    public VerificationPokemon(Joueur joueurActif) {
        super(joueurActif); // joueurActif est celui sur lequel se fait la vérification
    }

    public void verifierPokemonKO() {
        joueur.defausserPokemonsKO(joueur);
        List<Pokemon> pokemonsDeBanc = joueur.getListePokemonDeBanc();
        if (joueur.getPokemonActif() == null) {
            if (!pokemonsDeBanc.isEmpty()) {
                avancerPokemon();
            } else {
                joueur.setEtatCourant(new FinPartie(joueur));
            }
        } else {
            continuerVerification();
        }
    }

    public abstract void continuerVerification();

    public abstract void avancerPokemon();

}