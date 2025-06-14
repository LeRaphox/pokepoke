package fr.umontpellier.iut.ptcgJavaFX;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public interface IJoueur {

    ObjectProperty<? extends IPokemon> pokemonActifProperty();
    ObservableList<? extends ICarte> getMain();
    ObservableList<? extends IPokemon> getBanc();
    ObservableList<? extends ICarte> piocheProperty();
    ObservableList<? extends ICarte> defausseProperty();
    ObservableList<? extends ICarte> recompensesProperty();
    ObservableList<? extends ICarte> getChoixComplementaires();
    ObjectProperty<? extends ICarte> carteEnJeuProperty();
    BooleanProperty peutRetraiteProperty();
    BooleanProperty peutMelangerProperty();
    BooleanProperty peutAjouterProperty();
    BooleanProperty peutDefausserEnergieProperty();

    String getNom();
    IJoueur getAdversaire();

}