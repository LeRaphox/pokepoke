package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersPioche;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuVaillanceDePierre;

import java.util.List;

public class VaillanceDePierre extends CarteSupporter {
    public VaillanceDePierre() {
        super("Vaillance de Pierre", "TEU135");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        List<Carte> cartesEnergieOuPokemonDeLaDefausse = joueur.getCartesDefausse().stream()
                .filter(c -> c.getTypePokemon() != null || c.getTypeEnergie() != null)
                .toList();
        if (!cartesEnergieOuPokemonDeLaDefausse.isEmpty()) {
            if (cartesEnergieOuPokemonDeLaDefausse.size() <= 6) {
                for (Carte c : cartesEnergieOuPokemonDeLaDefausse) {
                    joueur.deplacerCarteComplementaire(c.getId(), new DefausseVersPioche());
                }
            } else { // on ne choisit que si il y a plus de 6 cartes en défausse
                joueur.setCarteEnJeu(this);
                joueur.setListChoixComplementaires(cartesEnergieOuPokemonDeLaDefausse);
                joueur.setEtatCourant(new EnJeuVaillanceDePierre(joueur));
            }
        }
    }

}
