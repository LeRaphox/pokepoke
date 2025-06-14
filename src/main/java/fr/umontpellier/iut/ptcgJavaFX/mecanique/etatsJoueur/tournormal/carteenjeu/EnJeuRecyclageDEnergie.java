package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

import java.util.List;

public class EnJeuRecyclageDEnergie extends CarteEnJeu {

    public EnJeuRecyclageDEnergie(Joueur joueurActif) {
        super(joueurActif);
        getJeu().instructionProperty().setValue("Choisissez une option");
    }

    @Override
    public void melangerAEteChoisi() {
        if (joueur.getChoixComplementaires().isEmpty()) { // dans le cas où on rechoisit l'option
            joueur.setPeutAjouter(false);
            List<Carte> cartesEnergieDeDefausse = joueur.getCartesDefausse().stream()
                    .filter(c -> c.getTypeEnergie() != null)
                    .toList();
            if (!cartesEnergieDeDefausse.isEmpty()) {
                joueur.setListChoixComplementaires(cartesEnergieDeDefausse);
                joueur.setEtatCourant(new OptionMelangeRecyclageDEnergie(joueur));
            } else {
                onFinAction();
            }
        }
    }

    @Override
    public void ajouterAEteChoisi() {
        if (joueur.getChoixComplementaires().isEmpty()) { // dans le cas où on rechoisit l'option
            joueur.setPeutMelanger(false);
            List<Carte> cartesEnergieDeDefausse = joueur.getCartesDefausse().stream()
                    .filter(c -> c.getTypeEnergie() != null)
                    .toList();
            if (!cartesEnergieDeDefausse.isEmpty()) {
                joueur.setListChoixComplementaires(cartesEnergieDeDefausse);
                joueur.setEtatCourant(new OptionAjoutRecyclageDEnergie(joueur));
            } else {
                onFinAction();
            }
        }
    }

    @Override
    public void onFinAction() {
        joueur.setPeutAjouter(false);
        joueur.setPeutMelanger(false);
        joueur.setCarteEnJeu(null);
        super.onFinAction();
    }

}
