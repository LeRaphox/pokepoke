package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.talent;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormal;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.TourNormalSansRetraite;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.ChoixPokemonDracolosse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TalentDracolosse extends TourNormalSansRetraite {

    private final Set<Type> typesPossibles;
    public TalentDracolosse(Joueur joueurActif, Set<Type> typesPossibles) {
        super(joueurActif);
        this.typesPossibles = typesPossibles;
        String descriptionsCartes = typesPossibles.stream()
                .map("une carte %s"::formatted)
                .collect(Collectors.joining(" ou "));
        getJeu().instructionProperty().setValue("Choisissez %s.".formatted(descriptionsCartes));
    }

    @Override
    public void carteChoisie(String numCarte) {
        List<String> choixPossibles = joueur.getChoixComplementaires().stream()
                        .map(Carte::getId)
                        .toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            joueur.setCarteEnJeu(Carte.get(numCarte));
            Carte carteEnergie = Carte.get(numCarte);
            typesPossibles.remove(carteEnergie.getTypeEnergie());
            joueur.removeCartesComplementaires(carteEnergie.getTypeEnergie());
            joueur.setEtatCourant(new ChoixPokemonDracolosse(joueur, "Choisissez un pokémon auquel ajouter l'énergie"));
        }
    }

    public void passer() {
        joueur.viderListChoixComplementaires();
        joueur.setEtatCourant(new TourNormal(joueur));
    }
}
