package fr.umontpellier.iut.ptcgJavaFX;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.FabriqueDecks;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Jeu;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;
import fr.umontpellier.iut.ptcgJavaFX.vues.VueChoixJoueurs;
import fr.umontpellier.iut.ptcgJavaFX.vues.VueDuJeu;
import fr.umontpellier.iut.ptcgJavaFX.vues.VueResultats;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PokemonTCGIHM extends Application {

    public static final double pourcentageEcran = .65;
    private VueDuJeu vueDuJeu;
    private VueChoixJoueurs vueChoixJoueurs;
    private Stage primaryStage;
    private static Jeu jeu;

    private boolean avecVueChoixJoueurs = false;

    public PokemonTCGIHM() {}

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (avecVueChoixJoueurs) {
            vueChoixJoueurs = new VueChoixJoueurs();
            vueChoixJoueurs.setNomsDesJoueursDefinisListener(quandLesNomsJoueursSontDefinis);
            vueChoixJoueurs.show();
        } else {
            definirNomsJoueursEtJeu();
            demarrerPartie();
        }
    }

    public void demarrerPartie() {
        vueDuJeu = new VueDuJeu(jeu);
        vueDuJeu.creerBindings();
        Scene scene = new Scene(vueDuJeu, Screen.getPrimary().getBounds().getWidth() * pourcentageEcran,  Screen.getPrimary().getBounds().getHeight() * pourcentageEcran);
        jeu.run();

        VueResultats vueResultats = new VueResultats(this);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PokemonTCG");
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> {
            arreterJeu();
            event.consume();
        });

        primaryStage.show();
    }

    public void definirNomsJoueursEtJeu() {
        List<String> nomsJoueurs;
        if (avecVueChoixJoueurs) {
            nomsJoueurs = vueChoixJoueurs.getNomsJoueurs();
        } else {
            nomsJoueurs = new ArrayList<>();
            nomsJoueurs.add("John");
            nomsJoueurs.add("Paul");
        }
        jeu = new Jeu(new Joueur(nomsJoueurs.get(0), FabriqueDecks.makeRelentlessFlame()),
                new Joueur(nomsJoueurs.get(1), FabriqueDecks.makeSoaringStorm()));
    }

    private final ListChangeListener<String> quandLesNomsJoueursSontDefinis = change -> {
        if (!vueChoixJoueurs.getNomsJoueurs().isEmpty()) {
            definirNomsJoueursEtJeu();
            demarrerPartie();
        }
    };

    public static void setJeu(Jeu jeu) {
        PokemonTCGIHM.jeu = jeu;
    }

    public static Jeu getJeu() {
        return jeu;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void arreterJeu() {
/*        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("On arrête de jouer ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {*/
        Platform.exit();
//        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}