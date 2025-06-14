package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuFaibloBall;

import java.util.List;

public class FaibloBall extends CarteObjet {
    public FaibloBall() {
        super("Faiblo Ball", "SUM123");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        if (joueur.getNbEmplacementsLibres() == 0) {
            // si le joueur n'a pas de place sur le banc la carte n'a pas d'effet
            joueur.setEtatCourant(new TourNormal(joueur));
        }
        else {
            List<Carte> choixCartes = joueur.getCartesPioche().stream()
                    .filter(Carte::estPokemonDeBase)
                    .toList();
            if (!choixCartes.isEmpty()) {
                joueur.setCarteEnJeu(this);
                joueur.setListChoixComplementaires(choixCartes);
                joueur.setEtatCourant(new EnJeuFaibloBall(joueur));
            }
        }
    }

}
