package fr.umontpellier.iut.ptcgJavaFX.mecanique;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.dresseur.*;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.energie.EnergieEau;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.energie.EnergieElectrique;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.energie.EnergieFeu;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.*;

import java.util.ArrayList;
import java.util.List;

public class FabriqueDecks {
    public static List<Carte> makeSoaringStorm() {
        List<Carte> deck = new ArrayList<>();
        deck.add(new Dracolosse());
        deck.add(new Dracolosse());
        deck.add(new Draco());
        deck.add(new Draco());
        deck.add(new Draco());
        deck.add(new Minidraco());
        deck.add(new Minidraco());
        deck.add(new Minidraco());
        deck.add(new Lanturn());
        deck.add(new Lanturn());
        deck.add(new Loupio());
        deck.add(new Loupio());
        deck.add(new Roucarnage());
        deck.add(new Roucoups());
        deck.add(new Roucoups());
        deck.add(new Roucool());
        deck.add(new Roucool());
        deck.add(new Roucool());
        deck.add(new Fulguris());
        deck.add(new Fulguris());
        deck.add(new Boreas());
        deck.add(new Boreas());
        deck.add(new Tadmorv());
        deck.add(new Tadmorv());
        deck.add(new Scout());
        deck.add(new Scout());
        deck.add(new Cynthia());
        deck.add(new Cynthia());
        deck.add(new RecyclageDEnergie());
        deck.add(new Pecheur());
        deck.add(new Tili());
        deck.add(new Tili());
        deck.add(new Lilie());
        deck.add(new Lilie());
        deck.add(new CommunicationPokemon());
        deck.add(new CommunicationPokemon());
        deck.add(new FanClubPokemon());
        deck.add(new FanClubPokemon());
        deck.add(new Echange());
        deck.add(new LevyEtTatia());

        for (int i = 0; i < 11; i++) {
            deck.add(new EnergieElectrique());
        }
        for (int i = 0; i < 9; i++) {
            deck.add(new EnergieEau());
        }
        return deck;
    }

    public static List<Carte> makeRelentlessFlame() {
        List<Carte> deck = new ArrayList<>();
        deck.add(new Dracaufeu());
        deck.add(new Dracaufeu());
        deck.add(new Reptincel());
        deck.add(new Reptincel());
        deck.add(new Salameche());
        deck.add(new Salameche());
        deck.add(new Salameche());
        deck.add(new Nidoqueen());
        deck.add(new Nidoqueen());
        deck.add(new Nidorina());
        deck.add(new Nidorina());
        deck.add(new NidoranF());
        deck.add(new NidoranF());
        deck.add(new NidoranF());
        deck.add(new Galopa());
        deck.add(new Galopa());
        deck.add(new Ponyta());
        deck.add(new Ponyta());
        deck.add(new Ponyta());
        deck.add(new Canarticho());
        deck.add(new Canarticho());
        deck.add(new Canarticho());
        deck.add(new VaillanceDePierre());
        deck.add(new VaillanceDePierre());
        deck.add(new Copieuse());
        deck.add(new Copieuse());
        deck.add(new Cynthia());
        deck.add(new Cynthia());
        deck.add(new Tili());
        deck.add(new Tili());
        deck.add(new FaibloBall());
        deck.add(new FaibloBall());
        deck.add(new FanClubPokemon());
        deck.add(new FanClubPokemon());
        deck.add(new ProfEuphorbe());
        deck.add(new ProfEuphorbe());
        deck.add(new Echange());
        deck.add(new Echange());
        deck.add(new ChronoBall());
        deck.add(new ChronoBall());

        for (int i = 0; i < 20; i++) {
            deck.add(new EnergieFeu());
        }
        return deck;
    }
}
