package fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.carteenjeu.EnJeuCommunicationPokemon;

import java.util.List;

public class CommunicationPokemon extends CarteObjet {
    public CommunicationPokemon() {
        super("Communication Pokémon", "TEU152");
    }

    @Override
    public void jouer(Joueur joueur) {
        super.jouer(joueur);
        List<Carte> pokemonsEnMain = joueur.getCartesMain().stream()
                        .filter(c -> c.getTypePokemon() != null)
                        .toList();
        if (!pokemonsEnMain.isEmpty()) {
            joueur.setCarteEnJeu(this);
            joueur.setEtatCourant(new EnJeuCommunicationPokemon(joueur));
        }
    }

}
