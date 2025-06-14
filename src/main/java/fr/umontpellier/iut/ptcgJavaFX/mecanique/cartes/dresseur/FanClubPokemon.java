package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuFanClubPokemon;

import java.util.List;

public class FanClubPokemon extends CarteSupporter {
    public FanClubPokemon() {
        super("Fan Club Pokémon", "UPR133");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        List<Carte> choixCartes = joueur.getCartesPioche().stream()
                .filter(Carte::estPokemonDeBase)
                .toList();
        if (!choixCartes.isEmpty()) {
            joueur.setCarteEnJeu(this);
            joueur.setListChoixComplementaires(choixCartes);
            joueur.setEtatCourant(new EnJeuFanClubPokemon(joueur));
        }
    }

}
