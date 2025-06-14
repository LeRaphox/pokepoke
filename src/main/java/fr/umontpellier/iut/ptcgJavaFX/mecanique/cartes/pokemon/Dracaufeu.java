package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent.TalentDracaufeu;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.VerificationPokemonJoueurActif;

import java.util.ArrayList;
import java.util.List;

public class Dracaufeu extends CartePokemonEvolution {
    private boolean peutUtiliserTalent;

    public Dracaufeu() {
        super(
                "Dracaufeu",
                "TEU014",
                150,
                Type.FEU,
                Type.EAU,
                null,
                2,
                "Reptincel",
                2);
        peutUtiliserTalent = true;

        ajouterAttaque(new Attaque("Balle Incendiaire Continue", this, Type.FEU, 2) {
            @Override
            public void attaquer(Joueur joueur) {
                // liste des cartes énergie FEU
                List<Carte> cartesEnergie = new ArrayList<>();
                for (Carte c : joueur.getPokemon(getCarte()).getCartes()) {
                    Type t = c.getTypeEnergie();
                    if (t == Type.FEU) {
                        cartesEnergie.add(c);
                    }
                }
                for (Carte c : cartesEnergie) {
                    joueur.getPokemon(getCarte()).retirerCarte(c);
                    joueur.ajouterCarteDefausse(c);
                }
                infligerDegatsAdversaire(joueur, 30 + 50 * cartesEnergie.size());
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
        peutUtiliserTalent = false;
        Pokemon pokemon = joueur.getPokemon(this);
        pokemon.ajouterDegats(20);
        List<Carte> cartes = joueur.getCartesPioche().stream()
                .filter(c -> c.getTypeEnergie() == Type.FEU)
                .toList();
        peutUtiliserTalent = false;
        if (!cartes.isEmpty()) {
            joueur.setListChoixComplementaires(cartes);
            joueur.setEtatCourant(new TalentDracaufeu(joueur, pokemon));
        } else {
            joueur.setEtatCourant(new VerificationPokemonJoueurActif(joueur));
            joueur.getEtatCourant().verifierPokemonKO();
        }
    }

    @Override
    public void onFinTour(Joueur joueur) {
        super.onFinTour(joueur);
        peutUtiliserTalent = true;
    }
}
