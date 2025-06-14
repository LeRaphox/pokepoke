package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.ArrayList;
import java.util.List;

public class FrapEclair extends EnAttaque {

    private final Pokemon pokemon;
    public FrapEclair(Joueur joueurActif) {
        super(joueurActif);
        pokemon = joueur.getPokemonActif();
        getJeu().instructionProperty().setValue("Défausser toute l'énergie électrique attachée à ce Pokémon ?");
    }

    @Override
    public void defausserEnergieAEteChoisi() {
        List<Carte> cartesEnergieElectrique = new ArrayList<>();
        for (Carte carte : pokemon.getCartes()) {
            Type t = carte.getTypeEnergie();
            if (t == Type.ELECTRIQUE) {
                cartesEnergieElectrique.add(carte);
            }
        }
        for (Carte carte : cartesEnergieElectrique) {
            pokemon.retirerCarte(carte);
            joueur.ajouterCarteDefausse(carte);
        }
        joueur.setPeutDefausserEnergie(false);
        pokemon.getCartePokemon().getAttaques().getFirst().infligerDegatsAdversaire(joueur, 140);
        finAttaque();
    }

    @Override
    public void defausserEnergieNAPasEteChoisi() {
        joueur.setPeutDefausserEnergie(false);
        pokemon.getCartePokemon().getAttaques().getFirst().infligerDegatsAdversaire(joueur, 70);
        finAttaque();
    }

    @Override
    public void passer() {
    }

}
