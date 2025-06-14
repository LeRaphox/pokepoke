package fr.umontpellier.iut.ptcgJavaFX;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;

public interface IPokemon {

    ObjectProperty<? extends ICarte> cartePokemonProperty();
    ObservableList<? extends ICarte> cartesProperty();
    ObservableList<String> attaquesProperty();
    ObservableMap<String, List<String>> energieProperty();
    IntegerProperty pointsDeVieProperty();
    BooleanProperty estBruleProperty();
    BooleanProperty estProtegeEffetsAttaquesProperty();

    ICarte getCartePokemon();

}
