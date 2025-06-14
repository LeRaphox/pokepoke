package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.ImpactDuDragon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent.TalentDracolosse;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dracolosse extends CartePokemonEvolution {
    private boolean peutUtiliserTalent;

    public Dracolosse() {
        super(
                "Dracolosse",
                "UNM151",
                160,
                Type.DRAGON,
                Type.FEE,
                null,
                2,
                "Draco",
                2);
        peutUtiliserTalent = true;

        ajouterAttaque(new Attaque("Impact du Dragon", this, Type.EAU, 1, Type.ELECTRIQUE, 1,
                Type.INCOLORE, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                infligerDegatsAdversaire(joueur, 170);
                joueur.setEtatCourant(new ImpactDuDragon(joueur));
            }
        });
    }

    @Override
    public boolean peutUtiliserTalent() {
        return peutUtiliserTalent;
    }

    @Override
    public void utiliserTalent(Joueur joueur) {
        peutUtiliserTalent = false;
        Set<Type> typesPossibles = new HashSet<>();
        typesPossibles.add(Type.EAU);
        typesPossibles.add(Type.ELECTRIQUE);
        List<Carte> cartes = joueur.getCartesMain().stream()
                .filter(c -> c.getTypeEnergie() != null
                        && typesPossibles.contains(c.getTypeEnergie()))
                .toList();
        if (!cartes.isEmpty()) {
            joueur.setListChoixComplementaires(cartes);
            joueur.setEtatCourant(new TalentDracolosse(joueur, typesPossibles));
        } else {
            joueur.setEtatCourant(new TourNormal(joueur));
        }
    }

    @Override
    public void onFinTour(Joueur joueur) {
        super.onFinTour(joueur);
        peutUtiliserTalent = true;
    }

}
