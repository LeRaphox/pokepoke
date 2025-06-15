package fr.umontpellier.iut.ptcgJavaFX.vues;

import fr.umontpellier.iut.ptcgJavaFX.ICarte;
import fr.umontpellier.iut.ptcgJavaFX.IJeu;
import fr.umontpellier.iut.ptcgJavaFX.IJoueur;
import fr.umontpellier.iut.ptcgJavaFX.IPokemon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.MapChangeListener;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.InputStream;

import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur;


public class VueJoueurActif extends VBox {

    @FXML private Label nomDuJoueur;
    @FXML private HBox panneauMain;
    @FXML private HBox panneauEnergiesEnMain;
    @FXML private HBox panneauBanc;

    private ObjectProperty<? extends IJoueur> joueurActif;
    private IJeu jeu;
    private VueDuJeu vueDuJeu; // Nécessaire pour notifier des actions spécifiques (évolution, etc.)

    private ChangeListener<IJoueur> joueurActifChangeListener;
    private ListChangeListener<ICarte> changementMainJoueurListener;
    private ListChangeListener<IPokemon> changementBancListener;

    private boolean enModeSelectionCibleEnergie = false;
    private List<String> idsCartesChoisissables = List.of(); // IDs des cibles pour attachement d'énergie
    private Map<String, MapChangeListener<String, List<String>>> listenersEnergiesBanc = new HashMap<>();

    public VueJoueurActif(VueDuJeu vueDuJeu, IJeu jeu, ObjectProperty<? extends IJoueur> joueurActifProperty) {
        this.vueDuJeu=vueDuJeu;
        this.jeu=jeu;
        this.joueurActif=joueurActifProperty;

        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/VueJoueurActif.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialiserListenersEtBindings();
    }

    private void initialiserListenersEtBindings() {
        joueurActifChangeListener=(observable, oldJoueur, newJoueur) -> {
            if (newJoueur!=null) {
                preparerListenersPourJoueur(newJoueur);
                placerMain();
                placerBanc();
            } else {
                if (panneauMain!=null) panneauMain.getChildren().clear();
                if (panneauEnergiesEnMain!=null) panneauEnergiesEnMain.getChildren().clear();
                if (nomDuJoueur!=null) nomDuJoueur.setText("en attente de joueur...");
                if (panneauBanc!=null) panneauBanc.getChildren().clear();
                informerModeAttachementEnergie(false, List.of());
            }
        };

        changementMainJoueurListener=change -> placerMain();
        changementBancListener=change -> placerBanc();
        bindJoueurActif();
    }

    private void bindJoueurActif() {
        if (nomDuJoueur!=null) {
            StringBinding nomJoueurBinding=Bindings.createStringBinding(() ->
                            (joueurActif.get()!=null) ? joueurActif.get().getNom() : "en attente de joueur...",
                    joueurActif
            );
            nomDuJoueur.textProperty().bind(nomJoueurBinding);
        }
        setJoueurActifChangeListener();
        IJoueur joueurInitial=joueurActif.get();
        if (joueurInitial!=null) {
            preparerListenersPourJoueur(joueurInitial);
            placerMain();
            placerBanc();
        } else {
            if (panneauMain!=null) panneauMain.getChildren().clear();
            if (panneauEnergiesEnMain!=null) panneauEnergiesEnMain.getChildren().clear();
            if (panneauBanc!=null) panneauBanc.getChildren().clear();
        }
    }

    private void setJoueurActifChangeListener() {
        this.joueurActif.addListener(joueurActifChangeListener);
    }

    public void placerMain() {
        if (panneauMain==null) return;
        panneauMain.getChildren().clear();
        if (panneauEnergiesEnMain!=null) panneauEnergiesEnMain.getChildren().clear();

        if (joueurActif.get()==null) return;

        for (ICarte carte : joueurActif.get().getMain()) {
            String imagePath="/images/cartes/" + carte.getCode() + ".png";
            ImageView imageView=new ImageView();
            InputStream imageStream=getClass().getResourceAsStream(imagePath);
            if (imageStream==null) {
                System.err.println("[VueJoueurActif] image non trouvee en main: " + imagePath);
                Label errorLabel=new Label("Img: " + carte.getNom());
                panneauMain.getChildren().add(errorLabel);
                continue;
            }
            Image img=new Image(imageStream);
            imageView.setImage(img);
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(120);

            StackPane cartePane=new StackPane(imageView);
            cartePane.setOnMouseEntered(event -> cartePane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10, 0.5, 0.0, 0.0);"));
            cartePane.setOnMouseExited(event -> cartePane.setStyle(""));

            boolean estEnergie=carte.getNom().toLowerCase().contains("énergie") || carte.getCode().startsWith("TEUEner");
            final String idCarte=carte.getId();

            cartePane.setOnMouseClicked(event -> { // gestion du clic sur une carte en main
                Carte carteMecaniqueInterne=Carte.get(idCarte);
                if (carteMecaniqueInterne instanceof CartePokemonEvolution) { // cas d'une carte evolution
                    CartePokemonEvolution carteEvo=(CartePokemonEvolution) carteMecaniqueInterne;
                    Joueur joueurMecanique=(Joueur) joueurActif.get();
                    if (carteEvo.peutJouer(joueurMecanique)) { // si l'evolution est possible
                        if (vueDuJeu!=null) vueDuJeu.activerModeSelectionBasePourEvolution(carteEvo);
                    } else {
                        this.jeu.uneCarteDeLaMainAEteChoisie(idCarte); // sinon, le jeu gere l'impossibilite
                    }
                } else { // autres types de cartes
                    this.jeu.uneCarteDeLaMainAEteChoisie(idCarte);
                }
            });

            if (estEnergie && panneauEnergiesEnMain!=null) {
                panneauEnergiesEnMain.getChildren().add(cartePane);
            } else {
                panneauMain.getChildren().add(cartePane);
            }
        }
    }

    public void preparerListenersPourJoueur(IJoueur joueur) {
        // attache les listeners aux listes observables du joueur (main, banc)
        if (joueur!=null) {
            joueur.getMain().addListener(this.changementMainJoueurListener);
            joueur.getBanc().addListener(this.changementBancListener);
            rafraichirAffichageCibles(); // pour mettre a jour les styles de selection
        }
    }

    public void informerModeAttachementEnergie(boolean estActif, List<String> idsCiblesDuBanc) {
        // active/desactive le mode de selection pour attacher une energie
        this.enModeSelectionCibleEnergie=estActif;
        this.idsCartesChoisissables=estActif ? idsCiblesDuBanc : List.of();
        rafraichirAffichageCibles();
    }

    private Type getTypeFromLetter(String letter) {
        if (letter==null || letter.isEmpty()) return null;
        for (Type t : Type.values()) {
            if (t.asLetter().equalsIgnoreCase(letter)) return t;
        }
        return null;
    }

    private void peuplerConteneurEnergies(IPokemon pokemon, HBox conteneurEnergies) {
        // affiche les icones d'energie sous un pokemon du banc
        if (conteneurEnergies==null) return;
        conteneurEnergies.getChildren().clear();
        if (pokemon==null || pokemon.getCartePokemon()==null || pokemon.energieProperty()==null) return;

        ObservableMap<String, List<String>> energiesMap=pokemon.energieProperty();
        if (energiesMap==null || energiesMap.isEmpty()) return;

        for (Map.Entry<String, List<String>> entry : energiesMap.entrySet()) {
            List<String> listeIdsEnergies=entry.getValue();
            int nombreEnergies=(listeIdsEnergies==null) ? 0 : listeIdsEnergies.size();
            if (nombreEnergies==0) continue;

            Type typeEnum=getTypeFromLetter(entry.getKey());
            if (typeEnum==null) {
                Label errorTypeLabel=new Label(entry.getKey() + "?x" + nombreEnergies);
                errorTypeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: purple;");
                conteneurEnergies.getChildren().add(errorTypeLabel);
                continue;
            }
            String cheminImageEnergie="/images/energie/" + typeEnum.asLetter() + ".png";
            InputStream imageStream=getClass().getResourceAsStream(cheminImageEnergie);

            if (imageStream==null) {
                Label errorImgLabel=new Label(typeEnum.asLetter() + "x" + nombreEnergies);
                errorImgLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
                conteneurEnergies.getChildren().add(errorImgLabel);
            } else {
                ImageView imgEnergieView=new ImageView(new Image(imageStream));
                imgEnergieView.setFitHeight(15); imgEnergieView.setFitWidth(15);
                Label lblNombre=new Label("x" + nombreEnergies);
                lblNombre.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 0 0 0 1px;");
                HBox energieGroupe=new HBox(imgEnergieView, lblNombre);
                energieGroupe.setSpacing(1);
                energieGroupe.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                conteneurEnergies.getChildren().add(energieGroupe);
            }
        }
    }

    private void rafraichirAffichageCibles() {
        // System.out.println("[VueJoueurActif] Rafraichissement affichage cibles. Mode energie: " + enModeSelectionCibleEnergie);
        placerBanc(); // le plus simple est de redessiner le banc pour appliquer les styles
    }

    public void placerBanc() {
        // affiche les pokemons sur le banc et gere leur interactivite selon le mode de jeu
        // String nomJoueurActifVueJoueur = (this.joueurActif != null && this.joueurActif.get() != null) ? this.joueurActif.get().getNom() : "null";
        // boolean modeRemplacantActifVueDuJeu = (vueDuJeu != null) ? vueDuJeu.isModeSelectionRemplacantApresRetraiteActif() : false;
        // System.out.println("[VueJoueurActif LOG] placerBanc(): Pour " + nomJoueurActifVueJoueur + ", vueDuJeu.isModeSelectionRemplacantApresRetraiteActif() = " + modeRemplacantActifVueDuJeu);
        if (panneauBanc==null) { System.err.println("[VueJoueurActif] ERREUR: panneauBanc est null!"); return; }
        if (joueurActif.get()==null) { panneauBanc.getChildren().clear(); return; }

        // detache les anciens listeners d'energie du banc pour eviter les fuites ou doublons
        joueurActif.get().getBanc().forEach(pokemon -> {
            if (pokemon!=null && pokemon.getCartePokemon()!=null) {
                String pokemonId=pokemon.getCartePokemon().getId();
                if (listenersEnergiesBanc.containsKey(pokemonId) && pokemon.energieProperty()!=null) {
                    pokemon.energieProperty().removeListener(listenersEnergiesBanc.get(pokemonId));
                }
            }
        });
        listenersEnergiesBanc.clear();

        panneauBanc.getChildren().clear();
        ObservableList<? extends IPokemon> banc = joueurActif.get().getBanc();
        for (int i=0; i < 5; i++) { // 5 places sur le banc
            if (i < banc.size()) {
                final IPokemon currentPokemonFinal=banc.get(i);
                if (currentPokemonFinal!=null) {
                    final ICarte cartePokemonInterface=currentPokemonFinal.getCartePokemon();
                    final String idCartePokemonFinal=cartePokemonInterface.getId();
                    // final int indexPokemonFinal = i; // non utilise actuellement

                    VBox pokemonAvecEnergiesContainer=new VBox(5); // conteneur pour carte, pv, energies
                    pokemonAvecEnergiesContainer.setAlignment(javafx.geometry.Pos.CENTER);

                    Label pvLabelBanc=new Label();
                    pvLabelBanc.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.7); -fx-padding: 1px 3px;");
                    StringBinding pvBindingBanc=Bindings.createStringBinding(() -> {
                        if (currentPokemonFinal!=null && currentPokemonFinal.getCartePokemon()!=null) {
                            CartePokemon cartePkm=(CartePokemon) currentPokemonFinal.getCartePokemon();
                            return String.format("%d/%d PV", Math.max(0, currentPokemonFinal.pointsDeVieProperty().get()), cartePkm.getPointsVie());
                        }
                        return "--/-- PV";
                    }, currentPokemonFinal.pointsDeVieProperty(), currentPokemonFinal.cartePokemonProperty());
                    pvLabelBanc.textProperty().bind(pvBindingBanc);
                    pokemonAvecEnergiesContainer.getChildren().add(pvLabelBanc);

                    ImageView imageView=new ImageView();
                    String imagePath="/images/cartes/" + cartePokemonInterface.getCode() + ".png";
                    InputStream imageStream=getClass().getResourceAsStream(imagePath);
                    StackPane cartePane;

                    if (imageStream==null) {
                        // System.err.println("[VueJoueurActif] image non trouvee sur banc: " + imagePath);
                        Label errorLabel=new Label("Img: " + cartePokemonInterface.getNom());
                        cartePane=new StackPane(errorLabel);
                        pokemonAvecEnergiesContainer.getChildren().add(cartePane);
                    } else {
                        Image img=new Image(imageStream);
                        imageView.setImage(img);
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(100);
                        if (currentPokemonFinal.pointsDeVieProperty().get()<=0) imageView.setStyle("-fx-effect: grayscale(100%);"); // style KO
                        else imageView.setStyle("");
                        cartePane=new StackPane(imageView);
                        pokemonAvecEnergiesContainer.getChildren().add(cartePane);
                    }

                    final HBox energiesPokemonBancHBox=new HBox(3);
                    energiesPokemonBancHBox.setAlignment(javafx.geometry.Pos.CENTER);

                    String stylePourCartePane=""; // pour les effets de selection (evolution, energie)
                    EventHandler<MouseEvent> clicHandlerPourCartePane;

                    // final String pokemonNomForLog = cartePokemonInterface.getNom();
                    // boolean conditionRemplacementLog = (vueDuJeu != null) && vueDuJeu.isModeSelectionRemplacantApresRetraiteActif();
                    // System.out.println("[VueJoueurActif LOG] placerBanc() pour " + pokemonNomForLog + ": Mode Remplacement = " + conditionRemplacementLog);

                    boolean estCibleEvolutionValide=false;
                    if (vueDuJeu!=null && vueDuJeu.isModeSelectionBasePourEvolution() && vueDuJeu.getCarteEvolutionSelectionnee()!=null && joueurActif.get()!=null) {
                        Joueur joueurMecanique=(Joueur) joueurActif.get();
                        if (joueurMecanique!=null) {
                            List<String> ciblesValides=vueDuJeu.getCarteEvolutionSelectionnee().getChoixPossibles(joueurMecanique);
                            if (ciblesValides.contains(idCartePokemonFinal)) estCibleEvolutionValide=true;
                        }
                    }

                    if (estCibleEvolutionValide) { // si c'est une cible pour evolution
                        stylePourCartePane="-fx-effect: dropshadow(gaussian, lawngreen, 20, 0.8, 0.0, 0.0); -fx-border-color: lawngreen; -fx-border-width: 4;";
                        clicHandlerPourCartePane=event -> vueDuJeu.pokemonDeBaseChoisiPourEvolution(idCartePokemonFinal);
                    } else if (vueDuJeu!=null && vueDuJeu.isModeSelectionRemplacantApresRetraiteActif()) { // si on choisit un remplacant
                        // System.out.println("[VueJoueurActif LOG] placerBanc() pour " + pokemonNomForLog + ": Attachement handler Remplacant.");
                        clicHandlerPourCartePane=event -> vueDuJeu.pokemonDuBancChoisiPourRemplacer(idCartePokemonFinal);
                    } else if (enModeSelectionCibleEnergie && idsCartesChoisissables.contains(idCartePokemonFinal)) { // si on choisit une cible pour energie
                        stylePourCartePane="-fx-effect: dropshadow(gaussian, gold, 15, 0.7, 0.0, 0.0); -fx-border-color: gold; -fx-border-width: 3;";
                        clicHandlerPourCartePane=event -> {
                            this.jeu.uneCarteComplementaireAEteChoisie(idCartePokemonFinal);
                            if (currentPokemonFinal!=null && currentPokemonFinal.energieProperty()!=null) peuplerConteneurEnergies(currentPokemonFinal, energiesPokemonBancHBox);
                            else placerBanc();
                        };
                    } else { // cas par defaut: clic pour un talent de banc possible
                        clicHandlerPourCartePane=event -> {
                            if (currentPokemonFinal.pointsDeVieProperty().get()<=0) return; // pas de talent si KO
                            this.jeu.uneCarteDeLaMainAEteChoisie(idCartePokemonFinal); // supposition: talent de banc active comme ca
                        };
                        if (currentPokemonFinal.pointsDeVieProperty().get() > 0) { // effet de survol si pas KO et pas d'autre mode
                            cartePane.setOnMouseEntered(e -> {
                                String currentStyle=cartePane.getStyle();
                                if (currentStyle==null || currentStyle.isEmpty()) cartePane.setStyle("-fx-effect: dropshadow(gaussian, cornflowerblue, 8, 0.4, 0.0, 0.0);");
                            });
                            cartePane.setOnMouseExited(e -> {
                                String currentStyle=cartePane.getStyle();
                                if (currentStyle!=null && currentStyle.contains("cornflowerblue")) cartePane.setStyle("");
                            });
                        } else {
                            cartePane.setOnMouseEntered(null); cartePane.setOnMouseExited(null);
                        }
                    }

                    cartePane.setStyle(stylePourCartePane); // applique le style de selection (evolution, energie)
                    cartePane.setOnMouseClicked(clicHandlerPourCartePane);

                    pokemonAvecEnergiesContainer.getChildren().add(energiesPokemonBancHBox);
                    peuplerConteneurEnergies(currentPokemonFinal, energiesPokemonBancHBox); // affiche ses energies

                    // ajoute un listener pour les energies de ce pokemon du banc
                    String pokemonId=cartePokemonInterface.getId();
                    MapChangeListener<String, List<String>> listenerPourCePokemon=change -> peuplerConteneurEnergies(currentPokemonFinal, energiesPokemonBancHBox);
                    if(currentPokemonFinal.energieProperty()!=null){
                        currentPokemonFinal.energieProperty().addListener(listenerPourCePokemon);
                        listenersEnergiesBanc.put(pokemonId, listenerPourCePokemon); // on le stocke pour pouvoir l'enlever plus tard
                    }
                    panneauBanc.getChildren().add(pokemonAvecEnergiesContainer);
                } else { // si l'element dans la liste du banc est null
                    Button boutonVide=new Button("Vide (null)");
                    final int indexEmplacement=i;
                    boutonVide.setUserData(String.valueOf(indexEmplacement));
                    boutonVide.setOnAction(event -> this.jeu.unEmplacementVideDuBancAEteChoisi(String.valueOf(indexEmplacement)));
                    boutonVide.setStyle("-fx-font-size: 18px;");
                    boutonVide.setDisable(enModeSelectionCibleEnergie || (vueDuJeu!=null && vueDuJeu.isModeSelectionBasePourEvolution()) || (vueDuJeu!=null && vueDuJeu.isModeSelectionRemplacantApresRetraiteActif()));
                    panneauBanc.getChildren().add(boutonVide);
                }
            } else { // si la place est vide (moins d'elements dans la liste que de places sur le banc)
                Button boutonVide=new Button("Vide");
                final int indexEmplacement=i;
                boutonVide.setUserData(String.valueOf(indexEmplacement));
                boutonVide.setOnAction(event -> this.jeu.unEmplacementVideDuBancAEteChoisi(String.valueOf(indexEmplacement)));
                boutonVide.setStyle("-fx-font-size: 18px;");
                boutonVide.setDisable(enModeSelectionCibleEnergie || (vueDuJeu!=null && vueDuJeu.isModeSelectionBasePourEvolution()) || (vueDuJeu!=null && vueDuJeu.isModeSelectionRemplacantApresRetraiteActif()));
                panneauBanc.getChildren().add(boutonVide);
            }
        }
    }

    public void setModeSelectionPourRemplacantApresRetraite(boolean estActif) {
        // appele par VueDuJeu pour mettre a jour l'affichage du banc quand ce mode change
        placerBanc();
    }

    public void rafraichirAffichagePourSelectionEvolution() {
        // appele par VueDuJeu pour mettre a jour l'affichage du banc quand le mode evolution change
        placerBanc();
    }
}