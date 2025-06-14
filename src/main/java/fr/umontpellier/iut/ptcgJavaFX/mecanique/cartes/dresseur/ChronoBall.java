package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuChronoBall;

import java.util.List;

public class ChronoBall extends CarteObjet {
    public ChronoBall() {
        super("Chrono Ball", "SUM134");
    }

    private int nbPokemon = 0, nbPokemonTest = 0; // variable à affecter dans les tests pour simuler le random

    public void setNbPokemon(Joueur joueur) { // artificiel pour tests IHM
        if (nbPokemonTest == 0) {
            for (int i = 0; i < 2; i++) {
                if (joueur.lancerPiece()) {
                    nbPokemon += 1;
                }
            }
        } else nbPokemon = nbPokemonTest - 1;
    }

    public void setNbPokemonPourTest(int nbPokemonTest) {
        this.nbPokemonTest = nbPokemonTest;
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        setNbPokemon(joueur);
        nbPokemonTest = 0;
        if (nbPokemon > 0) {
            List<Carte> pokemonsEvolutifsDeLaPioche = joueur.getCartesPioche().stream()
                    .filter(Carte::estPokemonEvolutif)
                    .toList();
            if (!pokemonsEvolutifsDeLaPioche.isEmpty()) {
                joueur.setCarteEnJeu(this);
                joueur.setListChoixComplementaires(pokemonsEvolutifsDeLaPioche);
                joueur.setEtatCourant(new EnJeuChronoBall(joueur, Math.min(nbPokemon, pokemonsEvolutifsDeLaPioche.size())));
            }
        }
    }

}
