package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuRecyclageDEnergie;

public class RecyclageDEnergie extends CarteObjet {
    public RecyclageDEnergie() {
        super("Recyclage d'Énergie", "CES128");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        joueur.setPeutMelanger(true);
        joueur.setPeutAjouter(true);
        joueur.setCarteEnJeu(this);
        joueur.setEtatCourant(new EnJeuRecyclageDEnergie(joueur));
    }

}
