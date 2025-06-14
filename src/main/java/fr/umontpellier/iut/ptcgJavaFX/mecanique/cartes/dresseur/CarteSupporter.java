package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public abstract class CarteSupporter extends CarteDresseur {
    public CarteSupporter(String nom, String code) {
        super(nom, code);
    }

    @Override
    public boolean peutJouer(Joueur joueur) {
        return joueur.getPeutJouerSupporter();
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        joueur.setAJoueSupporter();
    }

    @Override
    public int getRangComparaison() {
        return 3;
    }
}
