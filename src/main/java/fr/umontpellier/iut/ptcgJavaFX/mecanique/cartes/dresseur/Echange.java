package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.AvancePokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

public class Echange extends CarteObjet {
    public Echange() {
        super("Échange", "CES147");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        if (!joueur.getListePokemonDeBanc().isEmpty())
            joueur.setEtatCourant(new AvancePokemon(joueur));
        else
            joueur.setEtatCourant(new TourNormal(joueur));
    }

}
