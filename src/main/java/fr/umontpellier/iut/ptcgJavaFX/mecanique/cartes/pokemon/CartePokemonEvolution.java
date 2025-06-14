package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.ChoixPokemon;

import java.util.List;

public abstract class CartePokemonEvolution extends CartePokemon {
    private final int niveau;
    private final String evolutionDe;

    public CartePokemonEvolution(
            String nom,
            String code,
            int pv,
            Type type,
            Type faiblesse,
            Type resistance,
            int coutRetraite,
            String evolutionDe,
            int niveau) {
        super(nom, code, pv, type, faiblesse, resistance, coutRetraite);
        this.evolutionDe = evolutionDe;
        this.niveau = niveau;
    }

    @Override
    public boolean peutJouer(Joueur joueur) {
        for (Pokemon pokemon : joueur.getListePokemonEnJeu()) {
            if (pokemon.getPeutEvoluer() && pokemon.getCartePokemon().getNom().equals(evolutionDe)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void jouer(Joueur joueur) {
        joueur.setCarteEnJeu(this);
        joueur.setEtatCourant(new ChoixPokemon(joueur, "Choisissez un pokémon à faire évoluer"));
    }

    @Override
    public List<String> getChoixPossibles(Joueur joueur) {
        return joueur.getListePokemonEnJeu().stream()
                .filter(p -> p.getPeutEvoluer()
                        && p.getCartePokemon().getNom().equals(evolutionDe))
                .map(p -> p.getCartePokemon().getId())
                .toList();
    }

    @Override
    public void jouerQuandEnJeu(Joueur joueur, String numCarte) {
        Pokemon pokemon = joueur.getPokemon(Carte.get(numCarte));
        pokemon.evoluer(this);
        joueur.setCarteEnJeu(null);
    }

    @Override
    public boolean estPokemonEvolutif() {
        return true;
    }

    @Override
    public int getRangComparaison() {
        return niveau;
    }
}
