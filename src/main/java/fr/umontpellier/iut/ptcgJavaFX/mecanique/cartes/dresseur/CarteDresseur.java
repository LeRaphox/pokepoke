package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;

public abstract class CarteDresseur extends Carte {
    public CarteDresseur(String nom, String code) {
        super(nom, code);
    }
    
    @Override
    public void jouer(Joueur joueur) {
        joueur.retirerCarteMain(this);
        joueur.ajouterCarteDefausse(this);
    }
}
