package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

import java.util.HashMap;
import java.util.Map;

public abstract class Attaque {
    private final String nom;
    private final CartePokemon carte;
    private final Map<Type, Integer> coutEnergie;

    public Attaque(String nom, CartePokemon carte) {
        this.nom = nom;
        this.carte = carte;
        coutEnergie = new HashMap<>();
    }

    public Attaque(String nom, CartePokemon carte, Type type, int cout) {
        this(nom, carte);
        coutEnergie.put(type, cout);
    }

    public Attaque(String nom, CartePokemon carte, Type type1, int cout1, Type type2, int cout2) {
        this(nom, carte);
        coutEnergie.put(type1, cout1);
        coutEnergie.put(type2, cout2);
    }

    public Attaque(String nom, CartePokemon carte, Type type1, int cout1, Type type2, int cout2,
                   Type type3, int cout3) {
        this(nom, carte);
        coutEnergie.put(type1, cout1);
        coutEnergie.put(type2, cout2);
        coutEnergie.put(type3, cout3);
    }

    public CartePokemon getCarte() {
        return carte;
    }

    public Map<Type, Integer> getCoutEnergie() {
        return coutEnergie;
    }

    public abstract void attaquer(Joueur joueur);

    public void infligerDegatsAdversaire(Joueur joueur, int degats) {
        Pokemon pokemonAdverse = joueur.getAdversaire().getPokemonActif();
        if (pokemonAdverse == null || pokemonAdverse.getEstProtegeEffetsAttaques()) {
            return;
        }
        pokemonAdverse.ajouterDegats(degats + joueur.getBonusDegats(), carte.getTypePokemon());
    }

    public String getNom() {
        return nom;
    }

}
