package fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Jeu;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;

public abstract class EtatJoueur {
    protected Joueur joueur;

    public EtatJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public void passer() {}
    public void finAttaque() {}
    public void choisirPokemon() {}
    public void carteChoisie(String numPokemon) {}
    public void bancChoisi(String numBanc) {}
    public void retraiteChoisie() {}
    public void defausseEnergie(String energie) {}
    public void melangerAEteChoisi() {}
    public void ajouterAEteChoisi() {}
    public void defausserEnergieAEteChoisi() {}
    public void defausserEnergieNAPasEteChoisi() {}
    public void onFinAction() {}

    public void verifierPokemonKO() {}
    protected Jeu getJeu() {
        return joueur.getJeu();
    }
}