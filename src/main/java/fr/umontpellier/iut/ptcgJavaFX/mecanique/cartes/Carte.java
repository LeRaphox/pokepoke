package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes;

import fr.umontpellier.iut.ptcgJavaFX.ICarte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Carte implements Comparable<Carte>, ICarte {
    /**
     * Permet de donner un identifiant unique à chaque carte
     * (utile pour l'interface utilisateur)
     */
    private static int compteur = 0;
    /**
     * Dictionnaire de toutes les cartes du jeu,
     * indexées par leur identifiant
     */
    private static final Map<String, Carte> listeCartes = new HashMap<>();
    private final String id;
    private final String nom;
    private final String code;

    public Carte(String nom, String code) {
        this.id = "%d".formatted(compteur);
        compteur++;
        listeCartes.put(this.id, this);
        this.nom = nom;
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    public static Carte get(String id) {
        return listeCartes.get(id);
    }

    /**
     * @return le type de l'énergie fournie pour les cartes énergie
     *         et {@code null} pour les autres cartes
     */
    public Type getTypeEnergie() {
        return null;
    }

    /**
     * @return le type du pokémon pour les cartes pokemon
     *         et {@code null} pour les autres cartes
     */
    public Type getTypePokemon() {
        return null;
    }

    public boolean estPokemonDeBase() {
        return false;
    }

    public boolean estPokemonEvolutif() {
        return false;
    }

    /**
     * @return {@code true} si le joueur peut jouer cette carte depuis sa main,
     *         {@code false} sinon
     */
    public abstract boolean peutJouer(Joueur joueur);

    public boolean peutJouerInit(Joueur joueur) {
        return false;
    }

    public abstract void jouer(Joueur joueur);

    public void jouerQuandEnJeu(Joueur joueur, String num) {}

    public abstract int getRangComparaison();

    public List<String> getChoixPossibles(Joueur joueur) {
        return null;
    }

    public int compareTo(Carte o) {
        if (this.getRangComparaison() != o.getRangComparaison()) {
            return this.getRangComparaison() - o.getRangComparaison();
        }
        if (this.nom.equals(o.nom)) {
            return this.id.compareTo(o.id);
        }
        return this.nom.compareTo(o.nom);
    }

}
