package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent.TalentNidoqueen;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.List;

/**
 * Remarque : Étant donné qu'il n'y a pas de Pokémon GX ou EX parmi les cartes
 * programmées dans ce projet, le texte descriptif du talent "Appel de la
 * Souveraine" peut être simplifié ainsi :
 * <p>
 * Une seule fois pendant votre tour (avant votre attaque), vous pouvez chercher
 * dans votre deck un Pokémon, le montrer, puis l'ajouter à votre main. Mélangez
 * ensuite votre deck.
 */
public class Nidoqueen extends CartePokemonEvolution {
    private boolean peutUtiliserTalent = true;

    public Nidoqueen() {
        super(
                "Nidoqueen",
                "TEU056",
                160,
                Type.PSY,
                Type.PSY,
                null,
                3,
                "Nidorina",
                2);

        ajouterAttaque(new Attaque("Lasso Puissant", this, Type.INCOLORE, 3) {
            @Override
            public void attaquer(Joueur joueur) {
                int nbPokemonEvolutionBanc = (int) joueur.getListePokemonDeBanc().stream()
                        .filter(p -> p.getCartePokemon().estPokemonEvolutif()).count();
                infligerDegatsAdversaire(joueur, 10 + 50 * nbPokemonEvolutionBanc);
                joueur.getEtatCourant().finAttaque();
            }
        });
    }

    @Override
    public boolean peutUtiliserTalent() {
        return peutUtiliserTalent;
    }

    @Override
    public void utiliserTalent(Joueur joueur) {
        List<Carte> cartes = joueur.getCartesPioche().stream()
                .filter(c -> c.getTypePokemon() != null)
                .toList();
        peutUtiliserTalent = false;
        if (!cartes.isEmpty()) {
            joueur.setListChoixComplementaires(cartes);
            joueur.setEtatCourant(new TalentNidoqueen(joueur));
        } else
            joueur.setEtatCourant(new TourNormal(joueur));
    }

    @Override
    public void onFinTour(Joueur joueur) {
        super.onFinTour(joueur);
        peutUtiliserTalent = true;
    }
}
