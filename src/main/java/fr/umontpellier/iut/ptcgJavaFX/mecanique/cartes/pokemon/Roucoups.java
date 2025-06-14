package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent.TalentRoucoups;

import java.util.List;

public class Roucoups extends CartePokemonEvolution {
    private boolean peutUtiliserTalent;

    public Roucoups() {
        super(
                "Roucoups",
                "TEU123",
                60,
                Type.INCOLORE,
                Type.ELECTRIQUE,
                Type.COMBAT,
                1,
                "Roucool",
                1);

        ajouterAttaque(new Attaque("Tornade", this, Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 30);
                joueur.getEtatCourant().finAttaque();
            }
        });

        peutUtiliserTalent = true;
    }

    @Override
    public void onFinTour(Joueur joueur) {
        super.onFinTour(joueur);
        peutUtiliserTalent = true;
    }

    @Override
    public boolean peutUtiliserTalent() {
        return peutUtiliserTalent;
    }

    @Override
    public void utiliserTalent(Joueur joueur) {
        peutUtiliserTalent = false;
        List<Carte> cartes = joueur.piocher(2);
        if (!cartes.isEmpty()) {
            joueur.setListChoixComplementaires(cartes);
            joueur.setEtatCourant(new TalentRoucoups(joueur));
        }
    }

}
