package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class ImpactDuDragon extends EnAttaque {

    private int nbEnergies = 3;
    public ImpactDuDragon(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Défaussez %d énergie%s".formatted(nbEnergies, nbEnergies > 1 ? "s" : ""));
    }

    @Override
    public void defausseEnergie(String numCarte) {
        Pokemon pokemon = joueur.getPokemonActif();
        List<String> choixPossibles = pokemon.getCartes().stream()
                .filter(c -> c.getTypeEnergie() != null)
                .map(Carte::getId)
                .toList();
        if (choixPossibles.contains(numCarte)) {
            Carte carteEnergie = Carte.get(numCarte);
            pokemon.retirerCarte(carteEnergie);
            joueur.ajouterCarteDefausse(carteEnergie);
            nbEnergies--;
            if (nbEnergies == 0) {
                finAttaque();
            }
        }
    }

    @Override
    public void passer() {
    }
}

