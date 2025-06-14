package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuLevyEtTatia;

public class LevyEtTatia extends CarteSupporter {
    public LevyEtTatia() {
        super("Lévy et Tatia", "CES148");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        joueur.setPeutMelanger(true);
        joueur.setCarteEnJeu(this);
        joueur.setEtatCourant(new EnJeuLevyEtTatia(joueur));
    }

}
