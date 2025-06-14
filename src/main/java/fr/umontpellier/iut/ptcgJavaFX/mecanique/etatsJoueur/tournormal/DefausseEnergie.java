package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;

import java.util.List;

public class DefausseEnergie extends EtatJoueur {

    private int coutRestant;
    public DefausseEnergie(Joueur joueurActif, int coutRestant) {
        super(joueurActif);
        this.coutRestant = coutRestant;
        getJeu().instructionProperty().setValue("Défaussez %d énergie%s".formatted(coutRestant, coutRestant > 1 ? "s" : ""));
    }

    public void defausseEnergie(String numCarte) {
        List<String> choixPossibles = joueur.getPokemonActif().getCartes().stream().filter(c -> c.getTypeEnergie() != null)
                .map(Carte::getId).toList();
        if (!choixPossibles.isEmpty() && choixPossibles.contains(numCarte)) {
            Carte carteEnergie = Carte.get(numCarte);
            joueur.getPokemonActif().retirerCarte(carteEnergie);
            joueur.ajouterCarteDefausse(carteEnergie);
            coutRestant -= 1;
            if (coutRestant > 0)  {
                getJeu().instructionProperty().setValue("Défaussez %d énergie%s".formatted(coutRestant, coutRestant > 1 ? "s" : ""));
            } else
                joueur.setEtatCourant(new AvancePokemon(joueur));
        } else joueur.setEtatCourant(new TourNormal(joueur));
    }

}