package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public abstract class CarteObjet extends CarteDresseur {
    public CarteObjet(String nom, String code) {
        super(nom, code);
    }

    @Override
    public boolean peutJouer(Joueur joueur) {
        return true;
    }

    @Override
    public int getRangComparaison() {
        return 4;
    }
}
