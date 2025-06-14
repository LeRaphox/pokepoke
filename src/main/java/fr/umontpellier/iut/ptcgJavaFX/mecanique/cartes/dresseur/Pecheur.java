package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DefausseVersMain;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuPecheur;

import java.util.List;

public class Pecheur extends CarteSupporter {
    public Pecheur() {
        super("Pêcheur", "CES130");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        List<Carte> cartesEnergieDeLaDefausse = joueur.getCartesDefausse().stream()
                .filter(c -> c.getTypeEnergie() != null)
                .toList();
        if (!cartesEnergieDeLaDefausse.isEmpty()) {
            if (cartesEnergieDeLaDefausse.size() <= 4) {
                for (Carte c : cartesEnergieDeLaDefausse) {
                    joueur.deplacerCarteComplementaire(c.getId(), new DefausseVersMain());
                }
            } else { // on ne choisit que si il y a plus de 4 cartes énergie en défausse
                joueur.setCarteEnJeu(this);
                joueur.setListChoixComplementaires(cartesEnergieDeLaDefausse);
                joueur.setEtatCourant(new EnJeuPecheur(joueur));
            }
        }
    }
}
