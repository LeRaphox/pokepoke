package fr.umontpellier.iut.ptcgJavaFX.vues;

import fr.umontpellier.iut.ptcgJavaFX.ICarte;
import fr.umontpellier.iut.ptcgJavaFX.IJeu;
import fr.umontpellier.iut.ptcgJavaFX.IJoueur;
import fr.umontpellier.iut.ptcgJavaFX.IPokemon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList; // Import ajoute
import javafx.collections.ObservableMap;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemon;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty; // Ajout import
import javafx.beans.property.SimpleObjectProperty; // Ajout import

import java.io.IOException;
import java.io.InputStream;

public class VueDuJeu extends BorderPane {

    private IJeu jeu;


    @FXML private Label instructionLabel;
    @FXML private Button boutonPasser;
    @FXML private ImageView grandeCarteActiveView;
    @FXML private VBox conteneurBas;
    @FXML private ScrollPane scrollPanePourVueJoueur;
    @FXML private HBox energiesPokemonActifHBoxJeu;
    @FXML private VBox attaquesDisponiblesVBoxJeu;
    @FXML private Button boutonEchangerPokemon;
    @FXML private Label pvPokemonActifLabel;
    @FXML private Button boutonUtiliserTalent;

    // Nouveaux FXML pour la zone adversaire
    @FXML private Label nomAdversaireLabel;
    @FXML private ImageView pokemonActifAdversaireView;
    @FXML private Label pvPokemonActifAdversaireLabel;
    @FXML private HBox bancAdversaireHBox;
    @FXML private HBox energiesPokemonActifAdversaireHBox;
    @FXML private VBox attaquesAdversaireVBox;
    @FXML private Label nbCartesMainAdversaireLabel;
    @FXML private Label nbCartesPiocheAdversaireLabel;
    @FXML private Label nbCartesDefausseAdversaireLabel;
    @FXML private Label nbRecompensesAdversaireLabel;

    // Nouveaux FXML pour les compteurs du JOUEUR ACTIF
    @FXML private Label nbCartesMainJoueurActifLabel;
    @FXML private Label nbCartesPiocheJoueurActifLabel;
    @FXML private Label nbCartesDefausseJoueurActifLabel;
    @FXML private Label nbRecompensesJoueurActifLabel;

    private VueJoueurActif vueJoueurActif;
    private String idPokemonActifCourant_PourGrandeCarte = null;
    private boolean estEnModeAttachementEnergie_Global = false;
    private MapChangeListener<String, List<String>> energiesPokemonActifListenerJeu;
    private javafx.collections.ListChangeListener<String> attaquesPokemonActifListenerJeu;
    private IPokemon pokemonActifObserveCourant = null;
    private boolean modeSelectionRemplacantApresRetraiteActif = false;
    private boolean modePaiementCoutRetraiteActif = false;
    private int coutRetraiteRestant = 0;
    private boolean modeSelectionBasePourEvolution = false;
    private fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution carteEvolutionSelectionnee = null;

    private ObjectProperty<IJoueur> adversaireProperty = new SimpleObjectProperty<>(null);

    private int attaquesEffectueesCeTour = 0;
    private boolean attaquePermise = true;

    private boolean permettreChoixRemplacantPourAdversaireParJoueurActif = false;


    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VueDuJeu.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.vueJoueurActif = new VueJoueurActif(this, this.jeu, this.jeu.joueurActifProperty());
        if (scrollPanePourVueJoueur != null) {
            scrollPanePourVueJoueur.setContent(vueJoueurActif);
        } else {
            System.err.println("[VueDuJeu] Erreur: scrollpane est nul");
            if (conteneurBas != null) {
                conteneurBas.getChildren().add(0, vueJoueurActif);
            }
        }
        creerBindings();
    }

    private void mettreAJourGrandeCarteActive() {
        if (grandeCarteActiveView == null) {
            System.err.println("[VueDuJeu] ERREUR: gandecarteactive est null");
            idPokemonActifCourant_PourGrandeCarte = null;
        }

        IJoueur joueurCourant = this.jeu.joueurActifProperty().get();
        IPokemon pokemonPrecedent = pokemonActifObserveCourant;
        IPokemon pokemonCourantPourAffichage = null;

        if (joueurCourant != null && joueurCourant.pokemonActifProperty().get() != null) {
            pokemonCourantPourAffichage = joueurCourant.pokemonActifProperty().get();
            ICarte cartePokemon = pokemonCourantPourAffichage.getCartePokemon();
            idPokemonActifCourant_PourGrandeCarte = cartePokemon.getId();
            String imagePath = "/images/cartes/" + cartePokemon.getCode() + ".png";
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream == null) {
                System.err.println("[VueDuJeu] pas d'image trouver : " + imagePath);
                if(grandeCarteActiveView!=null) grandeCarteActiveView.setImage(null);
                idPokemonActifCourant_PourGrandeCarte = null;
            } else {
                if(grandeCarteActiveView!=null) grandeCarteActiveView.setImage(new Image(imageStream));
            }
        } else {
            if(grandeCarteActiveView!=null) grandeCarteActiveView.setImage(null);
            idPokemonActifCourant_PourGrandeCarte = null;
        }

        pokemonActifObserveCourant = pokemonCourantPourAffichage;

        if (pvPokemonActifLabel != null) {
            if (pokemonActifObserveCourant != null && pokemonActifObserveCourant.getCartePokemon() != null) {
                StringBinding pvBinding = Bindings.createStringBinding(() -> {
                    if (pokemonActifObserveCourant == null || pokemonActifObserveCourant.getCartePokemon() == null) {
                        return "--/-- PV";
                    }
                    CartePokemon cartePkm = (CartePokemon) pokemonActifObserveCourant.getCartePokemon();
                    // Afficher 0 PV si les PV sont négatifs
                    return String.format("%d/%d PV",
                            Math.max(0, pokemonActifObserveCourant.pointsDeVieProperty().get()),
                            cartePkm.getPointsVie());
                }, pokemonActifObserveCourant.pointsDeVieProperty(), pokemonActifObserveCourant.cartePokemonProperty());
                pvPokemonActifLabel.textProperty().bind(pvBinding);
                pvPokemonActifLabel.setVisible(true);
            } else {
                if (pvPokemonActifLabel.textProperty().isBound()) {
                    pvPokemonActifLabel.textProperty().unbind();
                }
                pvPokemonActifLabel.setText("--/-- PV");
                pvPokemonActifLabel.setVisible(false);
            }
        }

        if (boutonUtiliserTalent != null) {
            if (pokemonActifObserveCourant != null && jeu.joueurActifProperty().get() != null && !jeu.finDePartieProperty().get()) {
                Pokemon pokemonMecanique = (Pokemon) pokemonActifObserveCourant;
                boolean peutUtiliser = pokemonMecanique.peutUtiliserTalent();
                boutonUtiliserTalent.setDisable(!peutUtiliser);
            } else {
                boutonUtiliserTalent.setDisable(true);
            }
        }

        appliquerStyleGrandeCarteActive();

        if (pokemonPrecedent != pokemonActifObserveCourant) {
            if (pokemonPrecedent != null) {
                if (pokemonPrecedent.energieProperty() != null && energiesPokemonActifListenerJeu != null) {
                    pokemonPrecedent.energieProperty().removeListener(energiesPokemonActifListenerJeu);
                }
                if (pokemonPrecedent.attaquesProperty() != null && attaquesPokemonActifListenerJeu != null) {
                    pokemonPrecedent.attaquesProperty().removeListener(attaquesPokemonActifListenerJeu);
                }
            }

            if (pokemonActifObserveCourant != null) {
                afficherEnergiesGenerique(pokemonActifObserveCourant, energiesPokemonActifHBoxJeu, true);
                afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
                if (energiesPokemonActifListenerJeu == null) {
                    energiesPokemonActifListenerJeu = change -> {
                        afficherEnergiesGenerique(pokemonActifObserveCourant, energiesPokemonActifHBoxJeu, true);
                        afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
                    };
                }
                if (pokemonActifObserveCourant.energieProperty() != null) {
                    pokemonActifObserveCourant.energieProperty().addListener(energiesPokemonActifListenerJeu);
                }
                if (attaquesPokemonActifListenerJeu == null) {
                    attaquesPokemonActifListenerJeu = change -> afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
                }
                if (pokemonActifObserveCourant.attaquesProperty() != null) {
                    pokemonActifObserveCourant.attaquesProperty().addListener(attaquesPokemonActifListenerJeu);
                }
            } else {
                if (energiesPokemonActifHBoxJeu != null) energiesPokemonActifHBoxJeu.getChildren().clear();
                if (attaquesDisponiblesVBoxJeu != null) attaquesDisponiblesVBoxJeu.getChildren().clear();
            }
        } else if (pokemonActifObserveCourant != null) {
            afficherEnergiesGenerique(pokemonActifObserveCourant, energiesPokemonActifHBoxJeu, true);
            afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
        }
    }

    private Type getTypeFromLetter(String letter) {
        if (letter == null || letter.isEmpty()) return null;
        for (Type t : Type.values()) {
            if (t.asLetter().equalsIgnoreCase(letter)) return t;
        }
        return null;
    }

    private void afficherEnergiesGenerique(IPokemon pokemon, HBox conteneurEnergies, boolean cliquablePourDefausse) {
        if (conteneurEnergies == null) return;
        conteneurEnergies.getChildren().clear();
        if (pokemon == null || pokemon.getCartePokemon() == null || pokemon.energieProperty() == null) return;

        ObservableMap<String, List<String>> energiesMap = pokemon.energieProperty();
        if (energiesMap == null || energiesMap.isEmpty()) return;

        for (Map.Entry<String, List<String>> entry : energiesMap.entrySet()) {
            List<String> listeIdsEnergies = entry.getValue();
            int nombreEnergies = (listeIdsEnergies == null) ? 0 : listeIdsEnergies.size();
            if (nombreEnergies == 0) continue;

            Type typeEnum = getTypeFromLetter(entry.getKey());
            if (typeEnum == null) {
                Label errorTypeLabel = new Label(entry.getKey() + "?x" + nombreEnergies);
                errorTypeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: purple;");
                conteneurEnergies.getChildren().add(errorTypeLabel);
                continue;
            }
            String cheminImageEnergie = "/images/energie/" + typeEnum.asLetter() + ".png";

            if (cliquablePourDefausse && this.modePaiementCoutRetraiteActif && this.coutRetraiteRestant > 0 && pokemon == this.pokemonActifObserveCourant) {
                if (listeIdsEnergies != null) {
                    for (String idCarteEnergie : listeIdsEnergies) {
                        InputStream imgStreamIndiv = getClass().getResourceAsStream(cheminImageEnergie);
                        if (imgStreamIndiv == null) {
                            Label errorImgLabel = new Label(typeEnum.asLetter() + "!");
                            errorImgLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: orange;");
                            conteneurEnergies.getChildren().add(errorImgLabel);
                            continue;
                        }
                        ImageView imgEnergieViewClic = new ImageView(new Image(imgStreamIndiv));
                        imgEnergieViewClic.setFitHeight(20); imgEnergieViewClic.setFitWidth(20);
                        Button boutonEnergieIndividuelle = new Button();
                        boutonEnergieIndividuelle.setGraphic(imgEnergieViewClic);
                        boutonEnergieIndividuelle.setStyle("-fx-padding: 1px; -fx-background-color: transparent; -fx-border-color: gold; -fx-border-width: 1px;");
                        final String idPourAction = idCarteEnergie;
                        boutonEnergieIndividuelle.setOnAction(event -> energieDuPokemonActifChoisiePourDefausse(idPourAction));
                        boutonEnergieIndividuelle.setOnMouseEntered(e -> boutonEnergieIndividuelle.setStyle("-fx-padding: 1px; -fx-background-color: lightgoldenrodyellow; -fx-border-color: darkgoldenrod; -fx-border-width: 2px;"));
                        boutonEnergieIndividuelle.setOnMouseExited(e -> boutonEnergieIndividuelle.setStyle("-fx-padding: 1px; -fx-background-color: transparent; -fx-border-color: gold; -fx-border-width: 1px;"));
                        conteneurEnergies.getChildren().add(boutonEnergieIndividuelle);
                    }
                }
            } else {
                InputStream imageStream = getClass().getResourceAsStream(cheminImageEnergie);
                if (imageStream == null) {
                    Label errorImgLabel = new Label(typeEnum.asLetter() + "x" + nombreEnergies);
                    errorImgLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
                    conteneurEnergies.getChildren().add(errorImgLabel);
                } else {
                    ImageView imgEnergieView = new ImageView(new Image(imageStream));
                    imgEnergieView.setFitHeight(20); imgEnergieView.setFitWidth(20);
                    Button boutonEnergieGroupe = new Button();
                    boutonEnergieGroupe.setGraphic(imgEnergieView);
                    boutonEnergieGroupe.setText("x" + nombreEnergies);
                    boutonEnergieGroupe.setStyle("-fx-padding: 2px;");
                    conteneurEnergies.getChildren().add(boutonEnergieGroupe);
                }
            }
        }
    }

    private void afficherAttaquesGenerique(IPokemon pokemon, VBox conteneurAttaques, boolean cliquable) {
        if (conteneurAttaques == null) return;
        conteneurAttaques.getChildren().clear();
        if (pokemon == null) return;

        javafx.collections.ObservableList<String> nomsAttaques = pokemon.attaquesProperty();
        if (nomsAttaques == null || nomsAttaques.isEmpty()) {
            Label pasDAttaqueLabel = new Label(cliquable ? "aucune attaque disponible" : "(aucune attaque)");
            pasDAttaqueLabel.setStyle("-fx-font-style: italic; -fx-font-size: 12px;");
            conteneurAttaques.getChildren().add(pasDAttaqueLabel);
            return;
        }

        for (String nomAttaque : nomsAttaques) {
            Button boutonAttaque = new Button(nomAttaque);
            boutonAttaque.setMaxWidth(Double.MAX_VALUE);
            if (cliquable) {
                boutonAttaque.setOnAction(event -> {
                    if (this.attaquePermise) {
                        this.jeu.uneAttaqueAEteChoisie(nomAttaque);
                        this.attaquesEffectueesCeTour++;
                        this.attaquePermise = false;
                        afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
                        IJoueur adv = adversaireProperty.get();
                        if (adv != null) {
                            afficherAttaquesGenerique(adv.pokemonActifProperty().get(), attaquesAdversaireVBox, false);
                        }
                    } else {
                        System.out.println("Attaque non permise");
                    }
                });
                if (!this.attaquePermise) {
                    boutonAttaque.setDisable(true);
                }
            } else {
                boutonAttaque.setDisable(true);
                boutonAttaque.setStyle("-fx-font-size: 11px; -fx-opacity: 0.8;");
            }
            conteneurAttaques.getChildren().add(boutonAttaque);
        }
    }


    private void appliquerStyleGrandeCarteActive() {
        if (grandeCarteActiveView == null) return;
        String styleFinal = "";
        if (modeSelectionBasePourEvolution && carteEvolutionSelectionnee != null && idPokemonActifCourant_PourGrandeCarte != null && jeu.joueurActifProperty().get() != null) {
            fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur joueurMecanique = (fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur) jeu.joueurActifProperty().get();
            if (joueurMecanique != null && carteEvolutionSelectionnee != null) {
                List<String> ciblesValides = carteEvolutionSelectionnee.getChoixPossibles(joueurMecanique);
                if (ciblesValides.contains(idPokemonActifCourant_PourGrandeCarte)) {
                    styleFinal = "-fx-effect: dropshadow(gaussian, lawngreen, 20, 0.8, 0.0, 0.0); -fx-border-color: lawngreen; -fx-border-width: 4;";
                }
            }
        } else if (estEnModeAttachementEnergie_Global && idPokemonActifCourant_PourGrandeCarte != null) {
            styleFinal = "-fx-effect: dropshadow(gaussian, gold, 15, 0.7, 0.0, 0.0); -fx-border-color: gold; -fx-border-width: 3;";
        }
        if (pokemonActifObserveCourant != null && pokemonActifObserveCourant.pointsDeVieProperty().get() <= 0) {
            if (!styleFinal.isEmpty() && !styleFinal.endsWith(";") && !styleFinal.endsWith(" ")) {
                styleFinal += ";";
            }
            styleFinal += " -fx-effect: grayscale(100%);";
        }
        grandeCarteActiveView.setStyle(styleFinal.trim());
    }

    public void creerBindings() {
        if (instructionLabel != null) {
            instructionLabel.textProperty().bind(jeu.instructionProperty());
        }

        this.jeu.instructionProperty().addListener((obs, oldInstruction, newInstruction) -> {
            System.out.println("[VueDuJeu] Instruction changée: \"" + newInstruction + "\" (Ancienne: \"" + oldInstruction + "\")");
            String instructionLower = newInstruction.toLowerCase();

            boolean previousModeEvo = modeSelectionBasePourEvolution;
            boolean previousModePaiementRetraite = modePaiementCoutRetraiteActif;
            boolean previousModeSelectionRemplacant = modeSelectionRemplacantApresRetraiteActif;
            boolean previousModeAttachementEnergie = estEnModeAttachementEnergie_Global;
            boolean previousAllowChoiceForOpponent = this.permettreChoixRemplacantPourAdversaireParJoueurActif;

            this.permettreChoixRemplacantPourAdversaireParJoueurActif = false;
            modePaiementCoutRetraiteActif = false;
            modeSelectionRemplacantApresRetraiteActif = false;
            estEnModeAttachementEnergie_Global = false;

            if (instructionLower.startsWith("défaussez") && instructionLower.contains("énergie")) {
                System.out.println("[VueDuJeu] Activation MODE PAIEMENT COUT RETRAITE.");
                modePaiementCoutRetraiteActif = true;
                modeSelectionBasePourEvolution = false;
                carteEvolutionSelectionnee = null;
                try {
                    String[] parts = instructionLower.split(" ");
                    coutRetraiteRestant = Integer.parseInt(parts[1]);
                    System.out.println("[VueDuJeu] cout retraite restant: " + coutRetraiteRestant);
                } catch (Exception e) {
                    System.err.println("[VueDuJeu] erreur : " + newInstruction);
                    coutRetraiteRestant = 1;
                }
                if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);
                if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);

            } else if (instructionLower.equals("choisissez un nouveau pokémon actif.")) {
                System.out.println("[VueDuJeu] Activation du mode retrait");
                modeSelectionRemplacantApresRetraiteActif = true;
                modeSelectionBasePourEvolution = false;
                carteEvolutionSelectionnee = null;
                coutRetraiteRestant = 0;
                if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);

                IJoueur joueurActifAuMomentInstruction = jeu.joueurActifProperty().get();
                IJoueur adversaireAuMomentInstruction = (joueurActifAuMomentInstruction != null) ? joueurActifAuMomentInstruction.getAdversaire() : null;
                boolean advActifIsNull = adversaireAuMomentInstruction != null && adversaireAuMomentInstruction.pokemonActifProperty().get() == null;
                boolean joueurActifPKMIsNull = joueurActifAuMomentInstruction != null && joueurActifAuMomentInstruction.pokemonActifProperty().get() == null;
                System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': JA=" + (joueurActifAuMomentInstruction != null ? joueurActifAuMomentInstruction.getNom() : "null") +
                        ", AdvPKMActifNull=" + advActifIsNull +
                        ", JAPKMActifNull=" + joueurActifPKMIsNull);
                IJoueur joueurCourant = jeu.joueurActifProperty().get();
                IJoueur adversaire = (joueurCourant != null) ? joueurCourant.getAdversaire() : null;

                boolean adversaireDoitChoisir = adversaire != null && adversaire.pokemonActifProperty().get() == null && joueurCourant != null && joueurCourant.pokemonActifProperty().get() != null;
                boolean joueurCourantDoitChoisir = joueurCourant != null && joueurCourant.pokemonActifProperty().get() == null;


                if (adversaireDoitChoisir) {
                    System.out.println("[VueDuJeu] L'adversaire doit choisir un nouveau Pokémon actif.");
                    if (instructionLabel.textProperty().isBound()) {
                        instructionLabel.textProperty().unbind();
                    }
                    instructionLabel.setText("L'adversaire doit choisir. Cliquez sur un Pokémon de son banc."); // Updated
                    this.permettreChoixRemplacantPourAdversaireParJoueurActif = true;
                    // System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': Tentative de MAJ modeSelectionRemplacantApresRetraiteActif. Sera: " + false);
                    modeSelectionRemplacantApresRetraiteActif = false; // L'adversaire choisit, pas le joueur actif via UI
                    if (vueJoueurActif != null) {
                        // System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': Appel de vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(" + false + ")");
                        vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
                    }
                } else if (joueurCourantDoitChoisir) {
                    System.out.println("[VueDuJeu] Le joueur courant doit choisir un nouveau Pokémon actif.");
                    // Rétablir le binding si ce n'est pas déjà fait ou si l'instruction est spécifiquement pour le joueur courant
                    if (!instructionLabel.textProperty().isBound()) {
                        instructionLabel.textProperty().bind(jeu.instructionProperty()); // Re-bind si c'était unbind pour l'adversaire
                    }
                    // Assurez-vous que l'instruction est correcte pour le joueur actif si elle a été modifiée
                    // jeu.instructionProperty().set("Choisissez un nouveau Pokémon actif."); // Déjà fait par le moteur de jeu
                    // System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': Tentative de MAJ modeSelectionRemplacantApresRetraiteActif. Sera: " + true);
                    modeSelectionRemplacantApresRetraiteActif = true;
                    if (vueJoueurActif != null) {
                        // System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': Appel de vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(" + true + ")");
                        vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(true);
                    }
                } else {
                    // Cas par défaut ou si aucun des deux n'a besoin de choisir (ne devrait pas arriver avec cette instruction)
                    // ou si le binding original est toujours correct
                    System.out.println("[VueDuJeu] Mode sélection remplaçant: ni joueur ni adversaire clairement identifié pour choix, ou état inattendu.");
                    if (!instructionLabel.textProperty().isBound()) {
                        instructionLabel.textProperty().bind(jeu.instructionProperty());
                    }
                    // System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': Tentative de MAJ modeSelectionRemplacantApresRetraiteActif. Sera: " + true + " (défaut)");
                    modeSelectionRemplacantApresRetraiteActif = true; // Comportement par défaut original
                    if (vueJoueurActif != null) {
                        // System.out.println("[VueDuJeu LOG] instructionListener - 'choisissez...actif': Appel de vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(" + true + ") (défaut)");
                        vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(true);
                    }
                }
                modeSelectionBasePourEvolution = false;
                carteEvolutionSelectionnee = null;
                coutRetraiteRestant = 0;
                // END MODIFICATION

            } else if (instructionLower.equals("choisissez un pokémon à faire évoluer")) {

                if (!modeSelectionBasePourEvolution) {
                }

            } else if (instructionLower.contains("choisissez un pokémon") && instructionLower.contains("énergie")) {
                estEnModeAttachementEnergie_Global = true;
                modeSelectionBasePourEvolution = false;
                carteEvolutionSelectionnee = null;

                List<String> idsCiblesBanc = List.of();
                if (this.jeu.joueurActifProperty().get() != null) {
                    idsCiblesBanc = this.jeu.joueurActifProperty().get().getBanc().stream()
                            .filter(p -> p != null)
                            .map(p -> p.getCartePokemon().getId())
                            .collect(Collectors.toList());
                }
                if (vueJoueurActif != null) {
                    vueJoueurActif.informerModeAttachementEnergie(true, idsCiblesBanc);
                }

            } else {

                boolean etaitEnModeEvo = modeSelectionBasePourEvolution;

                modeSelectionBasePourEvolution = false;
                carteEvolutionSelectionnee = null;
                coutRetraiteRestant = 0;

                IJoueur joueurCourant = jeu.joueurActifProperty().get();
                if (boutonEchangerPokemon != null && joueurCourant != null) {
                    boutonEchangerPokemon.setDisable(!joueurCourant.peutRetraiteProperty().get());
                } else if (boutonEchangerPokemon != null) {
                    boutonEchangerPokemon.setDisable(true);
                }

                if (vueJoueurActif != null) {
                    if(previousModeSelectionRemplacant) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
                    if(previousModeAttachementEnergie) vueJoueurActif.informerModeAttachementEnergie(false, List.of());
                    if(etaitEnModeEvo) { // previousModeEvo refers to the state before this listener processed any instruction
                        vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
                    }
                }
            }

            // If the mode for allowing opponent choice changed, refresh opponent's zone
            if (previousAllowChoiceForOpponent && !this.permettreChoixRemplacantPourAdversaireParJoueurActif) {
                mettreAJourZoneAdversaire();
            }
            // If the mode was just activated, ensure opponent's zone is updated to show clickable bench
            if (this.permettreChoixRemplacantPourAdversaireParJoueurActif) {
                mettreAJourZoneAdversaire();
            }

            mettreAJourGrandeCarteActive();
        });

        this.jeu.joueurActifProperty().addListener((obs, oldJ, newJ) -> {
            if (newJ != null) {
                System.out.println("[VueDuJeu LOG] joueurActifProperty Listener: Nouveau joueur actif = " + newJ.getNom());
                // Réinitialisation pour le nouveau tour du joueur actif
                this.attaquesEffectueesCeTour = 0;
                this.attaquePermise = true;
                System.out.println("[VueDuJeu] Nouveau tour pour " + newJ.getNom() + ". Attaques réinitialisées. attaquePermise: " + this.attaquePermise);

                adversaireProperty.set(newJ.getAdversaire());

                System.out.println("[VueDuJeu LOG] joueurActifProperty Listener: Préparation de vueJoueurActif pour " + newJ.getNom());
                vueJoueurActif.preparerListenersPourJoueur(newJ);
                vueJoueurActif.placerMain();
                vueJoueurActif.placerBanc();

                newJ.pokemonActifProperty().addListener((obsPok, oldPok, newPok) -> {
                    mettreAJourGrandeCarteActive();
                });

                // Correction des bindings pour les compteurs de cartes du joueur actif
                final IJoueur joueurPourBindingActif = newJ;
                if (nbCartesMainJoueurActifLabel != null) {
                    if (nbCartesMainJoueurActifLabel.textProperty().isBound()) nbCartesMainJoueurActifLabel.textProperty().unbind();
                    StringBinding mainSizeBindingActif = Bindings.createStringBinding(() ->
                                    "Main: " + (joueurPourBindingActif != null && joueurPourBindingActif.getMain() != null ? joueurPourBindingActif.getMain().size() : "-"),
                            joueurPourBindingActif.getMain()
                    );
                    nbCartesMainJoueurActifLabel.textProperty().bind(mainSizeBindingActif);
                }
                if (nbCartesPiocheJoueurActifLabel != null) {
                    if (nbCartesPiocheJoueurActifLabel.textProperty().isBound()) nbCartesPiocheJoueurActifLabel.textProperty().unbind();
                    StringBinding piocheSizeBindingActif = Bindings.createStringBinding(() ->
                                    "Pioche: " + (joueurPourBindingActif != null && joueurPourBindingActif.piocheProperty() != null ? joueurPourBindingActif.piocheProperty().size() : "--"),
                            joueurPourBindingActif.piocheProperty()
                    );
                    nbCartesPiocheJoueurActifLabel.textProperty().bind(piocheSizeBindingActif);
                }
                if (nbCartesDefausseJoueurActifLabel != null) {
                    if (nbCartesDefausseJoueurActifLabel.textProperty().isBound()) nbCartesDefausseJoueurActifLabel.textProperty().unbind();
                    StringBinding defausseSizeBindingActif = Bindings.createStringBinding(() ->
                                    "Défausse: " + (joueurPourBindingActif != null && joueurPourBindingActif.defausseProperty() != null ? joueurPourBindingActif.defausseProperty().size() : "--"),
                            joueurPourBindingActif.defausseProperty()
                    );
                    nbCartesDefausseJoueurActifLabel.textProperty().bind(defausseSizeBindingActif);
                }
                if (nbRecompensesJoueurActifLabel != null) {
                    if (nbRecompensesJoueurActifLabel.textProperty().isBound()) nbRecompensesJoueurActifLabel.textProperty().unbind();
                    StringBinding recompensesSizeBindingActif = Bindings.createStringBinding(() ->
                                    "Récompenses: " + (joueurPourBindingActif != null && joueurPourBindingActif.recompensesProperty() != null ? joueurPourBindingActif.recompensesProperty().size() : "-"),
                            joueurPourBindingActif.recompensesProperty()
                    );
                    nbRecompensesJoueurActifLabel.textProperty().bind(recompensesSizeBindingActif);
                }

                if (boutonEchangerPokemon != null) {
                    newJ.peutRetraiteProperty().addListener((obsRetraite, oldValRetraite, newValRetraite) -> {
                        boutonEchangerPokemon.setDisable(!newValRetraite || modeSelectionRemplacantApresRetraiteActif);
                    });
                    boutonEchangerPokemon.setDisable(!newJ.peutRetraiteProperty().get() || modeSelectionRemplacantApresRetraiteActif);
                }
            } else {
                adversaireProperty.set(null);
                if (boutonEchangerPokemon != null) {
                    boutonEchangerPokemon.setDisable(true);
                }
                if (boutonUtiliserTalent != null) {
                    boutonUtiliserTalent.setDisable(true);
                }
                if (nbCartesMainJoueurActifLabel != null) { if(nbCartesMainJoueurActifLabel.textProperty().isBound()) nbCartesMainJoueurActifLabel.textProperty().unbind(); nbCartesMainJoueurActifLabel.setText("Main: -");}
                if (nbCartesPiocheJoueurActifLabel != null) { if(nbCartesPiocheJoueurActifLabel.textProperty().isBound()) nbCartesPiocheJoueurActifLabel.textProperty().unbind(); nbCartesPiocheJoueurActifLabel.setText("Pioche: --");}
                if (nbCartesDefausseJoueurActifLabel != null) { if(nbCartesDefausseJoueurActifLabel.textProperty().isBound()) nbCartesDefausseJoueurActifLabel.textProperty().unbind(); nbCartesDefausseJoueurActifLabel.setText("Défausse: --");}
                if (nbRecompensesJoueurActifLabel != null) { if(nbRecompensesJoueurActifLabel.textProperty().isBound()) nbRecompensesJoueurActifLabel.textProperty().unbind(); nbRecompensesJoueurActifLabel.setText("Récompenses: -");}
            }
            // mettreAJourGrandeCarteActive() est appelé plus bas, ce qui rafraîchira les boutons d'attaque.
            mettreAJourGrandeCarteActive();
            mettreAJourZoneAdversaire();
        });

        IJoueur premierJoueurActif = this.jeu.joueurActifProperty().get();
        if (premierJoueurActif != null) {
            if (premierJoueurActif.getAdversaire() != null) {
                adversaireProperty.set(premierJoueurActif.getAdversaire());
            }
            // Correction des bindings pour les compteurs de cartes du premier joueur actif (initialisation)
            final IJoueur joueurInitialPourBinding = premierJoueurActif;
            if (nbCartesMainJoueurActifLabel != null) {
                if (nbCartesMainJoueurActifLabel.textProperty().isBound()) nbCartesMainJoueurActifLabel.textProperty().unbind();
                StringBinding mainSizeBindingInitial = Bindings.createStringBinding(() ->
                                "Main: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.getMain() != null ? joueurInitialPourBinding.getMain().size() : "-"),
                        joueurInitialPourBinding.getMain()
                );
                nbCartesMainJoueurActifLabel.textProperty().bind(mainSizeBindingInitial);
            }
            if (nbCartesPiocheJoueurActifLabel != null) {
                if (nbCartesPiocheJoueurActifLabel.textProperty().isBound()) nbCartesPiocheJoueurActifLabel.textProperty().unbind();
                StringBinding piocheSizeBindingInitial = Bindings.createStringBinding(() ->
                                "Pioche: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.piocheProperty() != null ? joueurInitialPourBinding.piocheProperty().size() : "--"),
                        joueurInitialPourBinding.piocheProperty()
                );
                nbCartesPiocheJoueurActifLabel.textProperty().bind(piocheSizeBindingInitial);
            }
            if (nbCartesDefausseJoueurActifLabel != null) {
                if (nbCartesDefausseJoueurActifLabel.textProperty().isBound()) nbCartesDefausseJoueurActifLabel.textProperty().unbind();
                StringBinding defausseSizeBindingInitial = Bindings.createStringBinding(() ->
                                "Défausse: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.defausseProperty() != null ? joueurInitialPourBinding.defausseProperty().size() : "--"),
                        joueurInitialPourBinding.defausseProperty()
                );
                nbCartesDefausseJoueurActifLabel.textProperty().bind(defausseSizeBindingInitial);
            }
            if (nbRecompensesJoueurActifLabel != null) {
                if (nbRecompensesJoueurActifLabel.textProperty().isBound()) nbRecompensesJoueurActifLabel.textProperty().unbind();
                StringBinding recompensesSizeBindingInitial = Bindings.createStringBinding(() ->
                                "Récompenses: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.recompensesProperty() != null ? joueurInitialPourBinding.recompensesProperty().size() : "-"),
                        joueurInitialPourBinding.recompensesProperty()
                );
                nbRecompensesJoueurActifLabel.textProperty().bind(recompensesSizeBindingInitial);
            }
        }

        mettreAJourGrandeCarteActive();
        mettreAJourZoneAdversaire();

        if (grandeCarteActiveView != null) {
            grandeCarteActiveView.setOnMouseClicked(event -> {
                if (isModeSelectionBasePourEvolution()) {
                    if (idPokemonActifCourant_PourGrandeCarte != null && carteEvolutionSelectionnee != null && jeu.joueurActifProperty().get() != null) {
                        fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur joueurMecanique = (fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur) jeu.joueurActifProperty().get();
                        if (joueurMecanique != null) {
                            List<String> ciblesValides = carteEvolutionSelectionnee.getChoixPossibles(joueurMecanique);
                            if (ciblesValides.contains(idPokemonActifCourant_PourGrandeCarte)) {
                                System.out.println("[VueDuJeu] Pokemon actif (ID: " + idPokemonActifCourant_PourGrandeCarte + ") choisi comme base pour evolution.");
                                pokemonDeBaseChoisiPourEvolution(idPokemonActifCourant_PourGrandeCarte);
                            } else {
                                System.out.println("[VueDuJeu] Pokemon actif (ID: " + idPokemonActifCourant_PourGrandeCarte + ") NON VALIDE pour evolution avec " + carteEvolutionSelectionnee.getNom());
                            }
                        } else {
                            System.err.println("[VueDuJeu] joueurMecanique est null apres cast dans le clic grande carte mode evolution.");
                        }
                    } else {
                        System.err.println("[VueDuJeu] Clic sur grande carte en mode evolution, mais infos manquantes (pokemon actif, carte evo, ou joueur).");
                    }
                } else if (estEnModeAttachementEnergie_Global) {
                    if (idPokemonActifCourant_PourGrandeCarte != null) {

                        String targetPokemonId = idPokemonActifCourant_PourGrandeCarte;
                        this.jeu.uneCarteComplementaireAEteChoisie(targetPokemonId);
                        IJoueur joueurCourant = this.jeu.joueurActifProperty().get();
                        if (joueurCourant != null) {
                            IPokemon pokemonCible = joueurCourant.pokemonActifProperty().get();
                            if (pokemonCible != null && pokemonCible.getCartePokemon().getId().equals(targetPokemonId)) {

                                afficherEnergiesGenerique(pokemonCible, energiesPokemonActifHBoxJeu, true);
                                afficherAttaquesGenerique(pokemonCible, attaquesDisponiblesVBoxJeu, true);
                            } else {

                                mettreAJourGrandeCarteActive();
                            }
                        } else {

                            mettreAJourGrandeCarteActive();
                        }
                    } else {
                        System.out.println("a");

                    }
                } else {
                    if (idPokemonActifCourant_PourGrandeCarte != null) {
                        System.out.println("a");

                    }
                }
            });
        } else {
            System.out.println("a");
        }

        jeu.finDePartieProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                String nomGagnant = jeu.getNomDuGagnant();

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Fin de la Partie");
                alert.setHeaderText("La partie est terminée !");
                alert.setContentText("Le gagnant est : " + nomGagnant + " ! Félicitations !");

                Platform.runLater(() -> {
                    alert.showAndWait();
                });

                if (boutonPasser != null) {
                    boutonPasser.setDisable(true);
                }
                if (boutonEchangerPokemon != null) {
                    boutonEchangerPokemon.setDisable(true);
                }
                if (boutonUtiliserTalent != null) {
                    boutonUtiliserTalent.setDisable(true);
                }

                modePaiementCoutRetraiteActif = false;
                modeSelectionRemplacantApresRetraiteActif = false;
                estEnModeAttachementEnergie_Global = false;
                modeSelectionBasePourEvolution = false;
                carteEvolutionSelectionnee = null;
                coutRetraiteRestant = 0;

                mettreAJourGrandeCarteActive();
                if (vueJoueurActif != null) {
                    vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
                    vueJoueurActif.informerModeAttachementEnergie(false, java.util.List.of());
                    vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
                }
                jeu.instructionProperty().set("Partie terminée. Gagnant: " + nomGagnant);
            }
        });
    }

    @FXML
    protected void actionPasser(ActionEvent event) {
        this.jeu.passerAEteChoisi();
    }

    private void afficherAttaquesDisponibles() { // Remplacé par afficherAttaquesGenerique
        afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
    }
    private void afficherEnergiesPourPokemon(IPokemon pokemon, HBox conteneurEnergies) { // Remplacé par afficherEnergiesGenerique
        afficherEnergiesGenerique(pokemon, conteneurEnergies, true);
    }


    @FXML
    private void echangerPokemon() {
        IJoueur joueurCourant = jeu.joueurActifProperty().get();
        if (joueurCourant == null || !joueurCourant.peutRetraiteProperty().get()) {
            System.err.println("[VueDuJeu] Tentative de retraite alors que ce n'est pas permis.");
            return;
        }

        if (modePaiementCoutRetraiteActif || modeSelectionRemplacantApresRetraiteActif) {
            modePaiementCoutRetraiteActif = false;
            modeSelectionRemplacantApresRetraiteActif = false;
            coutRetraiteRestant = 0;
            jeu.instructionProperty().set("retraite annulee.");
            if (boutonEchangerPokemon != null) {
                boutonEchangerPokemon.setDisable(!joueurCourant.peutRetraiteProperty().get());
            }
            if (vueJoueurActif != null) {
                vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
            }
            mettreAJourGrandeCarteActive();
        } else {
            this.jeu.retraiteAEteChoisie();

        }
    }

    @FXML
    private void actionUtiliserTalent() {
        if (pokemonActifObserveCourant != null && jeu.joueurActifProperty().get() != null) {
            Pokemon pokemonMecanique = (Pokemon) pokemonActifObserveCourant;
            if (pokemonMecanique.peutUtiliserTalent()) {
                System.out.println("[VueDuJeu] Bouton 'Utiliser Talent' cliqué pour: " + pokemonMecanique.getCartePokemon().getNom());
                this.jeu.uneCarteDeLaMainAEteChoisie(pokemonMecanique.getCartePokemon().getId());
            } else {
                System.out.println("a");
            }
        } else {
            System.out.println("a");

        }
    }

    public void pokemonDuBancChoisiPourRemplacer(String idPokemonBanc) {
        if (!this.modeSelectionRemplacantApresRetraiteActif) {
            System.out.println("a");

            return;
        }
        this.jeu.uneCarteComplementaireAEteChoisie(idPokemonBanc);
        this.modeSelectionRemplacantApresRetraiteActif = false;
        if (boutonEchangerPokemon != null && jeu.joueurActifProperty().get() != null) {
            boutonEchangerPokemon.setDisable(!jeu.joueurActifProperty().get().peutRetraiteProperty().get());
        } else if (boutonEchangerPokemon != null) {
            boutonEchangerPokemon.setDisable(true);
        }
        if (vueJoueurActif != null) {
            vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
        }
        System.out.println("a");

    }

    public boolean isModeSelectionRemplacantApresRetraiteActif() {
        return modeSelectionRemplacantApresRetraiteActif;
    }

    public void energieDuPokemonActifChoisiePourDefausse(String idCarteEnergie) {
        if (modePaiementCoutRetraiteActif && coutRetraiteRestant > 0) {

            this.jeu.uneCarteEnergieAEteChoisie(idCarteEnergie);
        } else {
            System.err.println("[VueDuJeu] peut pas");
        }
    }

    public void activerModeSelectionBasePourEvolution(fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution carteEvo) {
        this.modePaiementCoutRetraiteActif = false;
        this.modeSelectionRemplacantApresRetraiteActif = false;
        this.estEnModeAttachementEnergie_Global = false;
        this.carteEvolutionSelectionnee = carteEvo;
        this.modeSelectionBasePourEvolution = true;
        this.jeu.uneCarteDeLaMainAEteChoisie(carteEvo.getId());
        mettreAJourGrandeCarteActive();
        if (vueJoueurActif != null) {
            vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
        }
    }

    public void pokemonDeBaseChoisiPourEvolution(String idPokemonBase) {
        if (!this.modeSelectionBasePourEvolution || this.carteEvolutionSelectionnee == null) {
            this.modeSelectionBasePourEvolution = false;
            this.carteEvolutionSelectionnee = null;
            mettreAJourGrandeCarteActive();
            if (vueJoueurActif != null) vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
            return;
        }
        this.jeu.uneCarteComplementaireAEteChoisie(idPokemonBase);
        this.modeSelectionBasePourEvolution = false;
        this.carteEvolutionSelectionnee = null;
        mettreAJourGrandeCarteActive();
        if (vueJoueurActif != null) {
            vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
        }
    }

    public boolean isModeSelectionBasePourEvolution() {
        return modeSelectionBasePourEvolution;
    }

    public fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution getCarteEvolutionSelectionnee() {
        return carteEvolutionSelectionnee;
    }

    private void mettreAJourZoneAdversaire() {
        System.out.println("[VueDuJeu] mettreAJourZoneAdversaire().");
        IJoueur joueurActifCourant = jeu.joueurActifProperty().get();
        IJoueur adversaire = adversaireProperty.get(); // Utiliser la propriété

        if (adversaire == null) {
            if (nomAdversaireLabel != null) nomAdversaireLabel.setText("Adversaire: ---");
            if (pokemonActifAdversaireView != null) pokemonActifAdversaireView.setImage(null);
            if (pvPokemonActifAdversaireLabel != null) pvPokemonActifAdversaireLabel.setText("PV: --/--");
            if (bancAdversaireHBox != null) bancAdversaireHBox.getChildren().clear();
            if (energiesPokemonActifAdversaireHBox != null) energiesPokemonActifAdversaireHBox.getChildren().clear();
            if (attaquesAdversaireVBox != null) attaquesAdversaireVBox.getChildren().clear();

            // Delier et reinitialiser les compteurs de l'adversaire
            if (nbCartesMainAdversaireLabel != null) { if(nbCartesMainAdversaireLabel.textProperty().isBound()) nbCartesMainAdversaireLabel.textProperty().unbind(); nbCartesMainAdversaireLabel.setText("Main: -");}
            if (nbCartesPiocheAdversaireLabel != null) { if(nbCartesPiocheAdversaireLabel.textProperty().isBound()) nbCartesPiocheAdversaireLabel.textProperty().unbind(); nbCartesPiocheAdversaireLabel.setText("Pioche: --");}
            if (nbCartesDefausseAdversaireLabel != null) { if(nbCartesDefausseAdversaireLabel.textProperty().isBound()) nbCartesDefausseAdversaireLabel.textProperty().unbind(); nbCartesDefausseAdversaireLabel.setText("Défausse: --");}
            if (nbRecompensesAdversaireLabel != null) { if(nbRecompensesAdversaireLabel.textProperty().isBound()) nbRecompensesAdversaireLabel.textProperty().unbind(); nbRecompensesAdversaireLabel.setText("Récompenses: -");}
            return;
        }


        if (nomAdversaireLabel != null) {
            nomAdversaireLabel.setText("Adversaire: " + adversaire.getNom());
        }

        IPokemon pokemonActifAdv = adversaire.pokemonActifProperty().get();

        if (pokemonActifAdversaireView != null && pvPokemonActifAdversaireLabel != null) {
            if (pokemonActifAdv != null && pokemonActifAdv.getCartePokemon() != null) {
                String imagePath = "/images/cartes/" + pokemonActifAdv.getCartePokemon().getCode() + ".png";
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                pokemonActifAdversaireView.setImage(imageStream == null ? null : new Image(imageStream));

                CartePokemon cartePkmAdv = (CartePokemon) pokemonActifAdv.getCartePokemon();

                if (pvPokemonActifAdversaireLabel.textProperty().isBound()) {
                    pvPokemonActifAdversaireLabel.textProperty().unbind();
                }
                // Afficher 0 PV si les PV sont négatifs pour l'adversaire
                pvPokemonActifAdversaireLabel.setText(String.format("%d/%d PV",
                        Math.max(0, pokemonActifAdv.pointsDeVieProperty().get()),
                        cartePkmAdv.getPointsVie()));
                pvPokemonActifAdversaireLabel.setVisible(true);
                pokemonActifAdversaireView.setVisible(true);

                // Appliquer le style KO si nécessaire au Pokémon actif de l'adversaire
                if (pokemonActifAdv.pointsDeVieProperty().get() <= 0) {
                    pokemonActifAdversaireView.setStyle("-fx-effect: grayscale(100%);");
                } else {
                    pokemonActifAdversaireView.setStyle(""); // Réinitialiser le style
                }

                if (energiesPokemonActifAdversaireHBox != null) {
                    afficherEnergiesGenerique(pokemonActifAdv, energiesPokemonActifAdversaireHBox, false);
                }
                if (attaquesAdversaireVBox != null) {
                    afficherAttaquesGenerique(pokemonActifAdv, attaquesAdversaireVBox, false);
                }

            } else {
                pokemonActifAdversaireView.setImage(null);
                pokemonActifAdversaireView.setVisible(false);
                pokemonActifAdversaireView.setStyle(""); // Réinitialiser le style si pas de Pokémon
                if (pvPokemonActifAdversaireLabel.textProperty().isBound()) {
                    pvPokemonActifAdversaireLabel.textProperty().unbind();
                }
                pvPokemonActifAdversaireLabel.setText("--/-- PV");
                pvPokemonActifAdversaireLabel.setVisible(false);
                if (energiesPokemonActifAdversaireHBox != null) energiesPokemonActifAdversaireHBox.getChildren().clear();
                if (attaquesAdversaireVBox != null) attaquesAdversaireVBox.getChildren().clear();
            }
        }

        if (bancAdversaireHBox != null) {
            bancAdversaireHBox.getChildren().clear();
            for (IPokemon pokemonBancAdv : adversaire.getBanc()) {
                if (pokemonBancAdv != null && pokemonBancAdv.getCartePokemon() != null) {
                    ImageView imgBancAdv = new ImageView();
                    String pathImgBanc = "/images/cartes/" + pokemonBancAdv.getCartePokemon().getCode() + ".png";
                    InputStream streamImgBanc = getClass().getResourceAsStream(pathImgBanc);
                    if (streamImgBanc != null) {
                        imgBancAdv.setImage(new Image(streamImgBanc));
                        imgBancAdv.setFitHeight(60);
                        imgBancAdv.setPreserveRatio(true);

                        String currentStyle = "";
                        // Appliquer le style KO si nécessaire
                        if (pokemonBancAdv.pointsDeVieProperty().get() <= 0) {
                            currentStyle = "-fx-effect: grayscale(100%);";
                        } else {
                            currentStyle = ""; // Base style if not KO
                        }

                        if (this.permettreChoixRemplacantPourAdversaireParJoueurActif && pokemonBancAdv.pointsDeVieProperty().get() > 0) { // Only allow choice of non-KO'd Pokemon
                            final String idPokemonChoisi = pokemonBancAdv.getCartePokemon().getId();
                            imgBancAdv.setOnMouseClicked(event -> {
                                this.jeu.uneCarteComplementaireAEteChoisie(idPokemonChoisi);
                                this.permettreChoixRemplacantPourAdversaireParJoueurActif = false; // Reset flag after choice
                                // instructionLabel.textProperty().bind(jeu.instructionProperty()); // Rebind to game instruction
                                if (!instructionLabel.textProperty().isBound()) { // Rebind if it was unbound
                                    instructionLabel.textProperty().bind(jeu.instructionProperty());
                                }
                                mettreAJourZoneAdversaire(); // Refresh opponent's bench (remove clickability)
                                mettreAJourGrandeCarteActive(); // Refresh current player's view
                            });
                            if (!currentStyle.isEmpty() && !currentStyle.endsWith(";")) currentStyle += ";";
                            currentStyle += " -fx-border-color: blue; -fx-border-width: 2;"; // Clickable style
                            imgBancAdv.setCursor(javafx.scene.Cursor.HAND);
                        } else {
                            imgBancAdv.setOnMouseClicked(null);
                            imgBancAdv.setCursor(javafx.scene.Cursor.DEFAULT);
                        }
                        imgBancAdv.setStyle(currentStyle.trim());
                        bancAdversaireHBox.getChildren().add(imgBancAdv);
                    } else {
                        Label errLabel = new Label("Err");
                        // KO style for error label if underlying Pokemon is KO'd
                        if (pokemonBancAdv != null && pokemonBancAdv.pointsDeVieProperty().get() <= 0) {
                            errLabel.setStyle("-fx-effect: grayscale(100%);");
                        }
                        bancAdversaireHBox.getChildren().add(errLabel);
                    }
                }
            }
        }

        // Correction des bindings pour les compteurs de cartes de l'adversaire
        final IJoueur adversairePourBinding = adversaire; // Rendre 'adversaire' effectivement final pour la lambda
        if (nbCartesMainAdversaireLabel != null) {
            if (nbCartesMainAdversaireLabel.textProperty().isBound()) nbCartesMainAdversaireLabel.textProperty().unbind();
            StringBinding mainSizeBindingAdv = Bindings.createStringBinding(() ->
                            "Main: " + (adversairePourBinding != null && adversairePourBinding.getMain() != null ? adversairePourBinding.getMain().size() : "-"),
                    adversairePourBinding.getMain()
            );
            nbCartesMainAdversaireLabel.textProperty().bind(mainSizeBindingAdv);
        }
        if (nbCartesPiocheAdversaireLabel != null) {
            if (nbCartesPiocheAdversaireLabel.textProperty().isBound()) nbCartesPiocheAdversaireLabel.textProperty().unbind();
            StringBinding piocheSizeBindingAdv = Bindings.createStringBinding(() ->
                            "Pioche: " + (adversairePourBinding != null && adversairePourBinding.piocheProperty() != null ? adversairePourBinding.piocheProperty().size() : "--"),
                    adversairePourBinding.piocheProperty()
            );
            nbCartesPiocheAdversaireLabel.textProperty().bind(piocheSizeBindingAdv);
        }
        if (nbCartesDefausseAdversaireLabel != null) {
            if (nbCartesDefausseAdversaireLabel.textProperty().isBound()) nbCartesDefausseAdversaireLabel.textProperty().unbind();
            StringBinding defausseSizeBindingAdv = Bindings.createStringBinding(() ->
                            "Défausse: " + (adversairePourBinding != null && adversairePourBinding.defausseProperty() != null ? adversairePourBinding.defausseProperty().size() : "--"),
                    adversairePourBinding.defausseProperty()
            );
            nbCartesDefausseAdversaireLabel.textProperty().bind(defausseSizeBindingAdv);
        }
        if (nbRecompensesAdversaireLabel != null) {
            if (nbRecompensesAdversaireLabel.textProperty().isBound()) nbRecompensesAdversaireLabel.textProperty().unbind();
            StringBinding recompensesSizeBindingAdv = Bindings.createStringBinding(() ->
                            "Récompenses: " + (adversairePourBinding != null && adversairePourBinding.recompensesProperty() != null ? adversairePourBinding.recompensesProperty().size() : "-"),
                    adversairePourBinding.recompensesProperty()
            );
            nbRecompensesAdversaireLabel.textProperty().bind(recompensesSizeBindingAdv);
        }
    }
}

