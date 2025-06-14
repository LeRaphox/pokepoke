package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent.TalentDracolosse;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.ChoixPokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoixPokemonDracolosse extends ChoixPokemon {

    public ChoixPokemonDracolosse(Joueur joueurActif, String instruction) {
        super(joueurActif, instruction);
        getJeu().instructionProperty().setValue(instruction);
    }

    @Override
    public void carteChoisie(String numCartePokemon) {
        List<String> choixPossibles = joueur.getCartesJouablesEnSuite();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCartePokemon)) {
            joueur.ajouterCarteEnJeuAuPokemon(numCartePokemon);
            passerALEtatSuivant();
        }
    }

    @Override
    public void passerALEtatSuivant() {
        List<Carte> cartesComplementairesRestantes = joueur.getChoixComplementaires();
        if (!cartesComplementairesRestantes.isEmpty()) {
            Set<Type> typesPossibles = new HashSet<>();
            typesPossibles.add(cartesComplementairesRestantes.getFirst().getTypeEnergie());
            joueur.setEtatCourant(new TalentDracolosse(joueur, typesPossibles));
        } else joueur.setEtatCourant(new TourNormal(joueur));
    }
}
