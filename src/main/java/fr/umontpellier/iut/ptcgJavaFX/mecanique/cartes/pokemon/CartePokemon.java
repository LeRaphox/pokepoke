package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.ArrayList;
import java.util.List;

public abstract class CartePokemon extends Carte {
    /**
     * Les propriétés d'une carte sont immuables
     * (on ne change pas le texte imprimé sur la carte...)
     */
    private final int pointsVie;
    private final Type type;
    private final Type faiblesse;
    private final Type resistance;
    private final int coutRetraite;
    private final List<Attaque> attaques;

    public CartePokemon(
            String nom,
            String code,
            int pv,
            Type type,
            Type faiblesse,
            Type resistance,
            int coutRetraite) {
        super(nom, code);
        this.pointsVie = pv;
        this.type = type;
        this.faiblesse = faiblesse;
        this.resistance = resistance;
        this.coutRetraite = coutRetraite;
        this.attaques = new ArrayList<>();
    }

    public List<Attaque> getAttaques() {
        return attaques;
    }

    protected void ajouterAttaque(Attaque attaque) {
        attaques.add(attaque);
    }

    public int getPointsVie() {
        return pointsVie;
    }

    @Override
    public Type getTypePokemon() {
        return type;
    }

    public Type getFaiblesse() {
        return faiblesse;
    }

    public Type getResistance() {
        return resistance;
    }

    public int getCoutRetraite(Pokemon pokemon) {
        return coutRetraite;
    }

    public void onFinTour(Joueur joueur) {
    }

    public boolean peutUtiliserTalent() {
        return false;
    }

    public void utiliserTalent(Joueur joueur) {
    }

    public void onPokemonKO(Pokemon pokemon, Joueur joueur) {
    }

    @Override
    public void jouerQuandEnJeu(Joueur joueur, String indexBanc) {
        joueur.setPokemonBanc(new Pokemon(this), Integer.parseInt(indexBanc));
        joueur.setCarteEnJeu(null);
    }
}
