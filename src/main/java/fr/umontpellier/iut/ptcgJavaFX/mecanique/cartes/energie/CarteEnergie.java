package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.energie;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.ChoixPokemon;

import java.util.List;

public abstract class CarteEnergie extends Carte {
    private final Type type;

    public CarteEnergie(String nom, String code, Type type) {
        super(nom, code);
        this.type = type;
    }

    @Override
    public boolean peutJouer(Joueur joueur) {
        return joueur.peutJouerEnergie();
    }

    @Override
    public void jouer(Joueur joueur) {
        joueur.setCarteEnJeu(this);
        joueur.setEtatCourant(new ChoixPokemon(joueur, "Choisissez un pokémon auquel ajouter l'énergie"));
    }

    public void jouerQuandEnJeu(Joueur joueur, String numCarte) {
        Pokemon pokemon = joueur.getPokemon(Carte.get(numCarte));
        pokemon.ajouterCarte(this);
        joueur.setAJoueEnergie();
        joueur.setCarteEnJeu(null);
    }

    public List<String> getChoixPossibles(Joueur joueur) {
        return joueur.getListePokemonEnJeu().stream().map(Pokemon::getCartePokemon).map(CartePokemon::getId)
                .toList();
    }

    @Override
    public Type getTypeEnergie() {
        return type;
    }

    @Override
    public int getRangComparaison() {
        return 5;
    }

}
