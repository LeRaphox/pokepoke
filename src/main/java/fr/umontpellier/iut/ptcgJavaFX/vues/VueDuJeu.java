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
import javafx.collections.ObservableList; // cet import est la
import javafx.collections.ObservableMap;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Type;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.Pokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemon;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty; // et celui la aussi
import javafx.beans.property.SimpleObjectProperty; // et celui-ci

import java.io.IOException;
import java.io.InputStream;

public class VueDuJeu extends BorderPane {

    private IJeu jeu; // notre lien vers la logique du jeu

    // elements FXML definis dans le fichier .fxml
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

    // elements FXML pour la zone de l'adversaire
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

    // elements FXML pour les compteurs du joueur actif
    @FXML private Label nbCartesMainJoueurActifLabel;
    @FXML private Label nbCartesPiocheJoueurActifLabel;
    @FXML private Label nbCartesDefausseJoueurActifLabel;
    @FXML private Label nbRecompensesJoueurActifLabel;

    @FXML private HBox choixComplementairesHBox; // la zone pour les boutons de choix specifiques

    private VueJoueurActif vueJoueurActif; // la vue pour la main et le banc du joueur
    private String idPokemonActifCourant_PourGrandeCarte=null; // pour savoir quelle carte est actuellement en grand
    private boolean estEnModeAttachementEnergie_Global=false; // un drapeau pour le mode d'attachement d'energie
    private MapChangeListener<String, List<String>> energiesPokemonActifListenerJeu; // ecouteur pour les energies du pokemon actif
    private javafx.collections.ListChangeListener<String> attaquesPokemonActifListenerJeu; // ecouteur pour ses attaques
    private IPokemon pokemonActifObserveCourant=null; // le pokemon actif actuellement affiche
    private boolean modeSelectionRemplacantApresRetraiteActif=false; // si on doit choisir un remplacant
    private boolean modePaiementCoutRetraiteActif=false; // si on paie pour la retraite
    private int coutRetraiteRestant=0;
    private boolean modeSelectionBasePourEvolution=false; // si on choisit un pokemon a evoluer
    private fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution carteEvolutionSelectionnee=null;

    private ObjectProperty<IJoueur> adversaireProperty=new SimpleObjectProperty<>(null); // pour suivre qui est l'adversaire

    private int attaquesEffectueesCeTour = 0; // pour limiter les attaques a une par tour
    private boolean attaquePermise=true;

    private boolean permettreChoixRemplacantPourAdversaireParJoueurActif=false; // cas special ou le joueur clique pour l'adversaire


    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VueDuJeu.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load(); // chargement de l'interface
        } catch (IOException e) {
            e.printStackTrace(); // en cas de probleme avec le fichier fxml
        }
        this.vueJoueurActif = new VueJoueurActif(this, this.jeu, this.jeu.joueurActifProperty());
        if (scrollPanePourVueJoueur != null) {
            scrollPanePourVueJoueur.setContent(vueJoueurActif); // placement de la vue joueur
        } else {
            System.err.println("[VueDuJeu] Erreur: scrollPanePourVueJoueur est null");
            if (conteneurBas != null) { // solution de secours
                conteneurBas.getChildren().add(0, vueJoueurActif);
            }
        }
        creerBindings(); // et on met en place les mecanismes de mise a jour
    }

    private void mettreAJourGrandeCarteActive() {
        // met a jour la grande carte pokemon au centre
        if (grandeCarteActiveView == null) {
            System.err.println("[VueDuJeu] ERREUR: grandeCarteActiveView est null");
            idPokemonActifCourant_PourGrandeCarte = null; return; // si l'element n'est pas la, on ne fait rien
        }

        IJoueur joueurCourant = this.jeu.joueurActifProperty().get();
        IPokemon pokemonPrecedent = pokemonActifObserveCourant;
        IPokemon pokemonCourantPourAffichage=null;

        if (joueurCourant != null && joueurCourant.pokemonActifProperty().get() != null) {
            pokemonCourantPourAffichage = joueurCourant.pokemonActifProperty().get();
            ICarte cartePokemon = pokemonCourantPourAffichage.getCartePokemon();
            idPokemonActifCourant_PourGrandeCarte = cartePokemon.getId();
            String imagePath = "/images/cartes/" + cartePokemon.getCode() + ".png"; // chemin de l'image
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream == null) {
                // System.err.println("[VueDuJeu] pas d'image trouvee : " + imagePath);
                grandeCarteActiveView.setImage(null);
                idPokemonActifCourant_PourGrandeCarte = null;
            } else {
                grandeCarteActiveView.setImage(new Image(imageStream));
            }
        } else { // s'il n'y a pas de pokemon actif
            grandeCarteActiveView.setImage(null);
            idPokemonActifCourant_PourGrandeCarte = null;
        }

        pokemonActifObserveCourant = pokemonCourantPourAffichage;

        if (pvPokemonActifLabel != null) { // mise a jour des points de vie
            if (pokemonActifObserveCourant != null && pokemonActifObserveCourant.getCartePokemon() != null) {
                StringBinding pvBinding = Bindings.createStringBinding(() -> {
                    if (pokemonActifObserveCourant == null || pokemonActifObserveCourant.getCartePokemon() == null) return "--/-- PV";
                    CartePokemon cartePkm = (CartePokemon) pokemonActifObserveCourant.getCartePokemon();
                    return String.format("%d/%d PV", Math.max(0, pokemonActifObserveCourant.pointsDeVieProperty().get()), cartePkm.getPointsVie()); // pas de pv negatifs
                }, pokemonActifObserveCourant.pointsDeVieProperty(), pokemonActifObserveCourant.cartePokemonProperty());
                pvPokemonActifLabel.textProperty().bind(pvBinding);
                pvPokemonActifLabel.setVisible(true);
            } else {
                if (pvPokemonActifLabel.textProperty().isBound()) pvPokemonActifLabel.textProperty().unbind();
                pvPokemonActifLabel.setText("--/-- PV");
                pvPokemonActifLabel.setVisible(false);
            }
        }

        if (boutonUtiliserTalent != null) { // etat du bouton pour utiliser un talent
            if (pokemonActifObserveCourant != null && jeu.joueurActifProperty().get() != null && !jeu.finDePartieProperty().get()) {
                Pokemon pokemonMecanique = (Pokemon) pokemonActifObserveCourant;
                boutonUtiliserTalent.setDisable(!pokemonMecanique.peutUtiliserTalent());
            } else {
                boutonUtiliserTalent.setDisable(true);
            }
        }

        appliquerStyleGrandeCarteActive(); // pour les effets visuels (ko, evolution...)

        if (pokemonPrecedent != pokemonActifObserveCourant) { // si le pokemon a change, on gere les listeners d'energie et d'attaque
            if (pokemonPrecedent != null) { // on enleve les anciens listeners
                if (pokemonPrecedent.energieProperty() != null && energiesPokemonActifListenerJeu != null) pokemonPrecedent.energieProperty().removeListener(energiesPokemonActifListenerJeu);
                if (pokemonPrecedent.attaquesProperty() != null && attaquesPokemonActifListenerJeu != null) pokemonPrecedent.attaquesProperty().removeListener(attaquesPokemonActifListenerJeu);
            }

            if (pokemonActifObserveCourant != null) { // on ajoute les nouveaux listeners
                afficherEnergiesGenerique(pokemonActifObserveCourant, energiesPokemonActifHBoxJeu, true);
                afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
                if (energiesPokemonActifListenerJeu == null) {
                    energiesPokemonActifListenerJeu = change -> { // si les energies changent
                        afficherEnergiesGenerique(pokemonActifObserveCourant, energiesPokemonActifHBoxJeu, true);
                        afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true); // les attaques aussi, car leur cout peut changer
                    };
                }
                if (pokemonActifObserveCourant.energieProperty() != null) pokemonActifObserveCourant.energieProperty().addListener(energiesPokemonActifListenerJeu);
                if (attaquesPokemonActifListenerJeu == null) {
                    attaquesPokemonActifListenerJeu = change -> afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
                }
                if (pokemonActifObserveCourant.attaquesProperty() != null) pokemonActifObserveCourant.attaquesProperty().addListener(attaquesPokemonActifListenerJeu);
            } else { // pas de pokemon, on vide
                if (energiesPokemonActifHBoxJeu != null) energiesPokemonActifHBoxJeu.getChildren().clear();
                if (attaquesDisponiblesVBoxJeu != null) attaquesDisponiblesVBoxJeu.getChildren().clear();
            }
        } else if (pokemonActifObserveCourant != null) { // meme si c'est le meme pokemon, on rafraichit au cas ou
            afficherEnergiesGenerique(pokemonActifObserveCourant, energiesPokemonActifHBoxJeu, true);
            afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true);
        }
    }

    private Type getTypeFromLetter(String letter) { // petite fonction pour trouver le type d'energie a partir de sa lettre
        if (letter == null || letter.isEmpty()) return null;
        for (Type t : Type.values()) {
            if (t.asLetter().equalsIgnoreCase(letter)) return t;
        }
        return null; // si on ne trouve pas
    }

    private void afficherEnergiesGenerique(IPokemon pokemon, HBox conteneurEnergies, boolean cliquablePourDefausse) {
        // pour afficher les petites icones d'energie
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
            if (typeEnum == null) { // si le type n'est pas reconnu
                Label errorTypeLabel = new Label(entry.getKey() + "?x" + nombreEnergies);
                errorTypeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: purple;");
                conteneurEnergies.getChildren().add(errorTypeLabel); continue;
            }
            String cheminImageEnergie = "/images/energie/" + typeEnum.asLetter() + ".png";

            if (cliquablePourDefausse && this.modePaiementCoutRetraiteActif && this.coutRetraiteRestant > 0 && pokemon == this.pokemonActifObserveCourant) { // si on paie pour la retraite
                if (listeIdsEnergies != null) {
                    for (String idCarteEnergie : listeIdsEnergies) { // un bouton par energie pour la defausse
                        InputStream imgStreamIndiv = getClass().getResourceAsStream(cheminImageEnergie);
                        if (imgStreamIndiv == null) { /* ... */ continue; }
                        ImageView imgEnergieViewClic = new ImageView(new Image(imgStreamIndiv));
                        imgEnergieViewClic.setFitHeight(20); imgEnergieViewClic.setFitWidth(20);
                        Button boutonEnergieIndividuelle = new Button();
                        boutonEnergieIndividuelle.setGraphic(imgEnergieViewClic);
                        boutonEnergieIndividuelle.setStyle("-fx-padding: 1px; -fx-background-color: transparent; -fx-border-color: gold; -fx-border-width: 1px;"); // style special
                        final String idPourAction = idCarteEnergie;
                        boutonEnergieIndividuelle.setOnAction(event -> energieDuPokemonActifChoisiePourDefausse(idPourAction));
                        // effet au survol
                        boutonEnergieIndividuelle.setOnMouseEntered(e -> boutonEnergieIndividuelle.setStyle("-fx-padding: 1px; -fx-background-color: lightgoldenrodyellow; -fx-border-color: darkgoldenrod; -fx-border-width: 2px;"));
                        boutonEnergieIndividuelle.setOnMouseExited(e -> boutonEnergieIndividuelle.setStyle("-fx-padding: 1px; -fx-background-color: transparent; -fx-border-color: gold; -fx-border-width: 1px;"));
                        conteneurEnergies.getChildren().add(boutonEnergieIndividuelle);
                    }
                }
            } else { // affichage normal, on groupe les energies
                InputStream imageStream = getClass().getResourceAsStream(cheminImageEnergie);
                if (imageStream == null) { /* ... */ } else {
                    ImageView imgEnergieView = new ImageView(new Image(imageStream));
                    imgEnergieView.setFitHeight(20); imgEnergieView.setFitWidth(20);
                    Button boutonEnergieGroupe = new Button(); // un bouton pour l'apparence, meme si non cliquable
                    boutonEnergieGroupe.setGraphic(imgEnergieView);
                    boutonEnergieGroupe.setText("x" + nombreEnergies);
                    boutonEnergieGroupe.setStyle("-fx-padding: 2px;");
                    conteneurEnergies.getChildren().add(boutonEnergieGroupe);
                }
            }
        }
    }

    private void afficherAttaquesGenerique(IPokemon pokemon, VBox conteneurAttaques, boolean cliquable) {
        // pour les boutons d'attaque du pokemon
        if (conteneurAttaques == null) return;
        conteneurAttaques.getChildren().clear(); // on vide les anciens
        if (pokemon == null) return;

        ObservableList<String> nomsAttaques = pokemon.attaquesProperty();
        if (nomsAttaques == null || nomsAttaques.isEmpty()) { // si pas d'attaques
            Label pasDAttaqueLabel = new Label(cliquable ? "aucune attaque disponible" : "(aucune attaque)");
            pasDAttaqueLabel.setStyle("-fx-font-style: italic; -fx-font-size: 12px;");
            conteneurAttaques.getChildren().add(pasDAttaqueLabel); return;
        }

        for (String nomAttaque : nomsAttaques) { // pour chaque attaque
            Button boutonAttaque = new Button(nomAttaque); // on cree un bouton
            boutonAttaque.setMaxWidth(Double.MAX_VALUE); // pour qu'il prenne toute la largeur
            if (cliquable) { // si on peut cliquer (pour le joueur actif)
                boutonAttaque.setOnAction(event -> {
                    if (this.attaquePermise) { // on verifie si on a le droit d'attaquer (une fois par tour)
                        this.jeu.uneAttaqueAEteChoisie(nomAttaque); // on dit au jeu
                        this.attaquesEffectueesCeTour++; this.attaquePermise = false; // on note l'attaque
                        afficherAttaquesGenerique(pokemonActifObserveCourant, attaquesDisponiblesVBoxJeu, true); // on rafraichit pour griser les boutons
                        IJoueur adv = adversaireProperty.get();
                        if (adv != null && adv.pokemonActifProperty().get() != null) afficherAttaquesGenerique(adv.pokemonActifProperty().get(), attaquesAdversaireVBox, false);
                    } else {
                        // System.out.println("[VueDuJeu] Attaque deja effectuee ce tour.");
                    }
                });
                if (!this.attaquePermise) boutonAttaque.setDisable(true); // si on a deja attaque, on grise
            } else { // si ce n'est pas cliquable (pour l'adversaire)
                boutonAttaque.setDisable(true);
                boutonAttaque.setStyle("-fx-font-size: 11px; -fx-opacity: 0.8;");
            }
            conteneurAttaques.getChildren().add(boutonAttaque);
        }
    }


    private void appliquerStyleGrandeCarteActive() {
        // pour les effets sur la grande carte (bordure verte pour evolution, doree pour energie, grisee si KO)
        if (grandeCarteActiveView == null) return;
        String styleFinal = "";
        if (modeSelectionBasePourEvolution && carteEvolutionSelectionnee != null && idPokemonActifCourant_PourGrandeCarte != null && jeu.joueurActifProperty().get() != null) {
            fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur joueurMecanique = (fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur) jeu.joueurActifProperty().get();
            if (joueurMecanique != null && carteEvolutionSelectionnee != null) {
                List<String> ciblesValides = carteEvolutionSelectionnee.getChoixPossibles(joueurMecanique);
                if (ciblesValides.contains(idPokemonActifCourant_PourGrandeCarte)) { // bordure verte si c'est une cible d'evolution
                    styleFinal = "-fx-effect: dropshadow(gaussian, lawngreen, 20, 0.8, 0.0, 0.0); -fx-border-color: lawngreen; -fx-border-width: 4;";
                }
            }
        } else if (estEnModeAttachementEnergie_Global && idPokemonActifCourant_PourGrandeCarte != null) { // bordure doree si on attache une energie
            styleFinal = "-fx-effect: dropshadow(gaussian, gold, 15, 0.7, 0.0, 0.0); -fx-border-color: gold; -fx-border-width: 3;";
        }

        if (pokemonActifObserveCourant != null && pokemonActifObserveCourant.pointsDeVieProperty().get() <= 0) { // si le pokemon est KO, on le grise
            if (!styleFinal.isEmpty() && !styleFinal.endsWith(";") && !styleFinal.endsWith(" ")) styleFinal += ";"; // pour separer les styles
            styleFinal += " -fx-effect: grayscale(100%);";
        }
        grandeCarteActiveView.setStyle(styleFinal.trim());
    }

    public void creerBindings() { // mise en place des liaisons et des listeners importants
        if (instructionLabel != null) instructionLabel.textProperty().bind(jeu.instructionProperty()); // l'instruction en haut de l'ecran

        // listener sur le changement d'instruction, c'est un peu le coeur de l'interface
        this.jeu.instructionProperty().addListener((obs, oldInstruction, newInstruction) -> {
            // System.out.println("[VueDuJeu] Instruction: \"" + newInstruction + "\""); // pour le debug
            String instructionLower = newInstruction.toLowerCase(); // on compare en minuscules

            // on garde en memoire les modes precedents pour savoir si on en sort
            boolean previousModeEvo = modeSelectionBasePourEvolution;
            boolean previousModePaiementRetraite = modePaiementCoutRetraiteActif;
            boolean previousModeSelectionRemplacant = modeSelectionRemplacantApresRetraiteActif;
            boolean previousModeAttachementEnergie = estEnModeAttachementEnergie_Global;
            boolean previousAllowChoiceForOpponent = this.permettreChoixRemplacantPourAdversaireParJoueurActif;

            // on reinitialise les modes a chaque nouvelle instruction
            this.permettreChoixRemplacantPourAdversaireParJoueurActif = false;
            modePaiementCoutRetraiteActif = false; modeSelectionRemplacantApresRetraiteActif = false; estEnModeAttachementEnergie_Global = false;

            // on analyse l'instruction pour activer les bons modes
            if (instructionLower.startsWith("défaussez") && instructionLower.contains("énergie")) { // si on doit payer pour la retraite
                modePaiementCoutRetraiteActif = true; modeSelectionBasePourEvolution = false; carteEvolutionSelectionnee = null;
                try {
                    String[] parts = instructionLower.split(" "); // on essaie de trouver le cout
                    coutRetraiteRestant = Integer.parseInt(parts[1]);
                } catch (Exception e) { coutRetraiteRestant = 1; } // si ca marche pas, 1 par defaut
                if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);
                if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);

            } else if (instructionLower.equals("choisissez un nouveau pokémon actif.")) { // si un pokemon est KO ou se retire
                modeSelectionRemplacantApresRetraiteActif = true; modeSelectionBasePourEvolution = false; carteEvolutionSelectionnee = null; coutRetraiteRestant = 0;
                if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true); // on desactive le bouton retraite en attendant

                IJoueur joueurCourant = jeu.joueurActifProperty().get();
                IJoueur adversaire = (joueurCourant != null) ? joueurCourant.getAdversaire() : null;
                // on verifie si c'est a l'adversaire de choisir (si son pokemon est KO)
                boolean adversaireDoitChoisir = adversaire != null && adversaire.pokemonActifProperty().get() == null && joueurCourant != null && joueurCourant.pokemonActifProperty().get() != null;
                boolean joueurCourantDoitChoisir = joueurCourant != null && joueurCourant.pokemonActifProperty().get() == null;

                if (adversaireDoitChoisir) { // si c'est a l'adversaire de choisir (le joueur actif cliquera pour lui)
                    if (instructionLabel.textProperty().isBound()) instructionLabel.textProperty().unbind(); // on change l'instruction
                    instructionLabel.setText("L'adversaire doit choisir. Cliquez sur un Pokémon de son banc.");
                    this.permettreChoixRemplacantPourAdversaireParJoueurActif = true; // on active ce mode special
                    modeSelectionRemplacantApresRetraiteActif = false; // ce n'est pas le joueur actif qui choisit pour lui-meme
                    if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
                } else if (joueurCourantDoitChoisir) { // si c'est au joueur actif de choisir pour lui-meme
                    if (!instructionLabel.textProperty().isBound()) instructionLabel.textProperty().bind(jeu.instructionProperty()); // on remet l'instruction normale
                    modeSelectionRemplacantApresRetraiteActif = true;
                    if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(true);
                } else { // cas par defaut ou inattendu
                    if (!instructionLabel.textProperty().isBound()) instructionLabel.textProperty().bind(jeu.instructionProperty());
                    modeSelectionRemplacantApresRetraiteActif = true;
                    if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(true);
                }
                modeSelectionBasePourEvolution = false; carteEvolutionSelectionnee = null; coutRetraiteRestant = 0;

            } else if (instructionLower.equals("choisissez un pokémon à faire évoluer")) {
                // ce mode est surtout active par un clic sur une carte evolution, l'instruction confirme souvent cet etat
            } else if (instructionLower.contains("choisissez un pokémon") && instructionLower.contains("énergie")) { // si on doit choisir un pokemon pour y attacher une energie
                estEnModeAttachementEnergie_Global = true; modeSelectionBasePourEvolution = false; carteEvolutionSelectionnee = null;
                List<String> idsCiblesBanc = List.of(); // on identifie les cibles possibles sur le banc
                if (this.jeu.joueurActifProperty().get() != null) {
                    idsCiblesBanc = this.jeu.joueurActifProperty().get().getBanc().stream().filter(p -> p != null).map(p -> p.getCartePokemon().getId()).collect(Collectors.toList());
                }
                if (vueJoueurActif != null) vueJoueurActif.informerModeAttachementEnergie(true, idsCiblesBanc); // on dit a la vue du joueur

            } else { // si l'instruction n'active aucun mode particulier
                boolean etaitEnModeEvo = modeSelectionBasePourEvolution;
                modeSelectionBasePourEvolution = false; carteEvolutionSelectionnee = null; coutRetraiteRestant = 0; // on desactive tout
                IJoueur joueurCourant = jeu.joueurActifProperty().get();
                if (boutonEchangerPokemon != null && joueurCourant != null) boutonEchangerPokemon.setDisable(!joueurCourant.peutRetraiteProperty().get()); // on reactive le bouton retraite si possible
                else if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);

                if (vueJoueurActif != null) { // on informe la vue du joueur que les modes sont termines
                    if(previousModeSelectionRemplacant) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
                    if(previousModeAttachementEnergie) vueJoueurActif.informerModeAttachementEnergie(false, List.of());
                    if(etaitEnModeEvo) vueJoueurActif.rafraichirAffichagePourSelectionEvolution(); // si on sortait du mode evolution, on rafraichit
                }
            }

            // si le mode special pour le choix de l'adversaire a change, on met a jour sa zone
            if (previousAllowChoiceForOpponent && !this.permettreChoixRemplacantPourAdversaireParJoueurActif) mettreAJourZoneAdversaire();
            if (this.permettreChoixRemplacantPourAdversaireParJoueurActif) mettreAJourZoneAdversaire(); // pour rendre son banc cliquable

            mettreAJourChoixComplementaires(); // on met a jour les boutons de choix specifiques
            mettreAJourGrandeCarteActive(); // et on rafraichit toujours la carte active
        });

        this.jeu.joueurActifProperty().addListener((obs, oldJ, newJ) -> { // quand le joueur actif change (fin de tour)
            if (newJ != null) { // si un nouveau joueur devient actif
                this.attaquesEffectueesCeTour = 0; this.attaquePermise = true; // on reinitialise pour le nouveau tour
                adversaireProperty.set(newJ.getAdversaire()); // on met a jour l'adversaire
                vueJoueurActif.preparerListenersPourJoueur(newJ); // on prepare la vue du joueur (main, banc)
                vueJoueurActif.placerMain(); vueJoueurActif.placerBanc();
                newJ.pokemonActifProperty().addListener((obsPok, oldPok, newPok) -> mettreAJourGrandeCarteActive()); // si son pokemon actif change

                final IJoueur joueurPourBindingActif = newJ; // pour les compteurs de cartes (main, pioche, etc.)
                // code complet des bindings ici
                if (nbCartesMainJoueurActifLabel != null) { if (nbCartesMainJoueurActifLabel.textProperty().isBound()) nbCartesMainJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Main: " + (joueurPourBindingActif != null && joueurPourBindingActif.getMain() != null ? joueurPourBindingActif.getMain().size() : "-"), joueurPourBindingActif.getMain()); nbCartesMainJoueurActifLabel.textProperty().bind(b); }
                if (nbCartesPiocheJoueurActifLabel != null) { if (nbCartesPiocheJoueurActifLabel.textProperty().isBound()) nbCartesPiocheJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Pioche: " + (joueurPourBindingActif != null && joueurPourBindingActif.piocheProperty() != null ? joueurPourBindingActif.piocheProperty().size() : "--"), joueurPourBindingActif.piocheProperty()); nbCartesPiocheJoueurActifLabel.textProperty().bind(b); }
                if (nbCartesDefausseJoueurActifLabel != null) { if (nbCartesDefausseJoueurActifLabel.textProperty().isBound()) nbCartesDefausseJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Défausse: " + (joueurPourBindingActif != null && joueurPourBindingActif.defausseProperty() != null ? joueurPourBindingActif.defausseProperty().size() : "--"), joueurPourBindingActif.defausseProperty()); nbCartesDefausseJoueurActifLabel.textProperty().bind(b); }
                if (nbRecompensesJoueurActifLabel != null) { if (nbRecompensesJoueurActifLabel.textProperty().isBound()) nbRecompensesJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Récompenses: " + (joueurPourBindingActif != null && joueurPourBindingActif.recompensesProperty() != null ? joueurPourBindingActif.recompensesProperty().size() : "-"), joueurPourBindingActif.recompensesProperty()); nbRecompensesJoueurActifLabel.textProperty().bind(b); }

                if (boutonEchangerPokemon != null) { // etat du bouton retraite pour le nouveau joueur
                    newJ.peutRetraiteProperty().addListener((obsRetraite, oldVal, newVal) -> boutonEchangerPokemon.setDisable(!newVal || modeSelectionRemplacantApresRetraiteActif));
                    boutonEchangerPokemon.setDisable(!newJ.peutRetraiteProperty().get() || modeSelectionRemplacantApresRetraiteActif);
                }
            } else { // s'il n'y a plus de joueur actif
                adversaireProperty.set(null);
                if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);
                if (boutonUtiliserTalent != null) boutonUtiliserTalent.setDisable(true);
                // on vide les compteurs
                if (nbCartesMainJoueurActifLabel != null) { if(nbCartesMainJoueurActifLabel.textProperty().isBound()) nbCartesMainJoueurActifLabel.textProperty().unbind(); nbCartesMainJoueurActifLabel.setText("Main: -");}
                if (nbCartesPiocheJoueurActifLabel != null) { if(nbCartesPiocheJoueurActifLabel.textProperty().isBound()) nbCartesPiocheJoueurActifLabel.textProperty().unbind(); nbCartesPiocheJoueurActifLabel.setText("Pioche: --");}
                if (nbCartesDefausseJoueurActifLabel != null) { if(nbCartesDefausseJoueurActifLabel.textProperty().isBound()) nbCartesDefausseJoueurActifLabel.textProperty().unbind(); nbCartesDefausseJoueurActifLabel.setText("Défausse: --");}
                if (nbRecompensesJoueurActifLabel != null) { if(nbRecompensesJoueurActifLabel.textProperty().isBound()) nbRecompensesJoueurActifLabel.textProperty().unbind(); nbRecompensesJoueurActifLabel.setText("Récompenses: -");}
                if (choixComplementairesHBox != null) choixComplementairesHBox.getChildren().clear(); // on vide aussi les choix specifiques
            }
            mettreAJourGrandeCarteActive(); mettreAJourZoneAdversaire();
            if (newJ != null) mettreAJourChoixComplementaires(); // on met a jour les choix specifiques pour le nouveau joueur
        });

        IJoueur premierJoueurActif = this.jeu.joueurActifProperty().get(); // initialisation pour le premier joueur au lancement
        if (premierJoueurActif != null) {
            if (premierJoueurActif.getAdversaire() != null) adversaireProperty.set(premierJoueurActif.getAdversaire());
            final IJoueur joueurInitialPourBinding = premierJoueurActif;
            // compteurs pour le premier joueur (code complet des bindings ici aussi)
            if (nbCartesMainJoueurActifLabel != null) { if (nbCartesMainJoueurActifLabel.textProperty().isBound()) nbCartesMainJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Main: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.getMain() != null ? joueurInitialPourBinding.getMain().size() : "-"), joueurInitialPourBinding.getMain()); nbCartesMainJoueurActifLabel.textProperty().bind(b); }
            if (nbCartesPiocheJoueurActifLabel != null) { if (nbCartesPiocheJoueurActifLabel.textProperty().isBound()) nbCartesPiocheJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Pioche: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.piocheProperty() != null ? joueurInitialPourBinding.piocheProperty().size() : "--"), joueurInitialPourBinding.piocheProperty()); nbCartesPiocheJoueurActifLabel.textProperty().bind(b); }
            if (nbCartesDefausseJoueurActifLabel != null) { if (nbCartesDefausseJoueurActifLabel.textProperty().isBound()) nbCartesDefausseJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Défausse: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.defausseProperty() != null ? joueurInitialPourBinding.defausseProperty().size() : "--"), joueurInitialPourBinding.defausseProperty()); nbCartesDefausseJoueurActifLabel.textProperty().bind(b); }
            if (nbRecompensesJoueurActifLabel != null) { if (nbRecompensesJoueurActifLabel.textProperty().isBound()) nbRecompensesJoueurActifLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Récompenses: " + (joueurInitialPourBinding != null && joueurInitialPourBinding.recompensesProperty() != null ? joueurInitialPourBinding.recompensesProperty().size() : "-"), joueurInitialPourBinding.recompensesProperty()); nbRecompensesJoueurActifLabel.textProperty().bind(b); }
        }

        mettreAJourGrandeCarteActive(); mettreAJourZoneAdversaire(); mettreAJourChoixComplementaires(); // affichage initial

        if (grandeCarteActiveView != null) { // gestion du clic sur la grande carte
            grandeCarteActiveView.setOnMouseClicked(event -> {
                if (isModeSelectionBasePourEvolution()) { // si on choisit une base pour une evolution
                    if (idPokemonActifCourant_PourGrandeCarte != null && carteEvolutionSelectionnee != null && jeu.joueurActifProperty().get() != null) {
                        fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur joueurMecanique = (fr.umontpellier.iut.ptcgJavaFX.mecanique.Joueur) jeu.joueurActifProperty().get();
                        if (joueurMecanique != null) {
                            List<String> ciblesValides = carteEvolutionSelectionnee.getChoixPossibles(joueurMecanique);
                            if (ciblesValides.contains(idPokemonActifCourant_PourGrandeCarte)) pokemonDeBaseChoisiPourEvolution(idPokemonActifCourant_PourGrandeCarte); // on informe le jeu
                        }
                    }
                } else if (estEnModeAttachementEnergie_Global) { // si on attache une energie
                    if (idPokemonActifCourant_PourGrandeCarte != null) {
                        this.jeu.uneCarteComplementaireAEteChoisie(idPokemonActifCourant_PourGrandeCarte);
                        // rafraichissement si besoin, mais souvent gere par le listener d'instruction
                        IJoueur joueurCourant = this.jeu.joueurActifProperty().get();
                        if (joueurCourant != null) {
                            IPokemon pokemonCible = joueurCourant.pokemonActifProperty().get();
                            if (pokemonCible != null && pokemonCible.getCartePokemon().getId().equals(idPokemonActifCourant_PourGrandeCarte)) {
                                afficherEnergiesGenerique(pokemonCible, energiesPokemonActifHBoxJeu, true);
                                afficherAttaquesGenerique(pokemonCible, attaquesDisponiblesVBoxJeu, true);
                            } else mettreAJourGrandeCarteActive();
                        } else mettreAJourGrandeCarteActive();
                    }
                }
            });
        }

        jeu.finDePartieProperty().addListener((observable, oldValue, newValue) -> { // quand la partie est finie
            if (newValue) {
                String nomGagnant = jeu.getNomDuGagnant();
                Alert alert = new Alert(AlertType.INFORMATION); // on previent le joueur
                alert.setTitle("Fin de la Partie"); alert.setHeaderText("La partie est terminée !");
                alert.setContentText("Le gagnant est : " + nomGagnant + " ! Félicitations !");
                Platform.runLater(() -> alert.showAndWait()); // important pour les UI JavaFX

                // on desactive les boutons
                if (boutonPasser != null) boutonPasser.setDisable(true);
                if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);
                if (boutonUtiliserTalent != null) boutonUtiliserTalent.setDisable(true);
                // on reinitialise les modes
                modePaiementCoutRetraiteActif=false; modeSelectionRemplacantApresRetraiteActif=false; estEnModeAttachementEnergie_Global=false;
                modeSelectionBasePourEvolution=false; carteEvolutionSelectionnee=null; coutRetraiteRestant=0;

                mettreAJourGrandeCarteActive();
                if (vueJoueurActif != null) { // on dit a la vue du joueur de se remettre a zero aussi
                    vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
                    vueJoueurActif.informerModeAttachementEnergie(false, java.util.List.of());
                    vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
                }
                jeu.instructionProperty().set("Partie terminée. Gagnant: " + nomGagnant); // derniere instruction
            }
        });
    }

    @FXML protected void actionPasser(ActionEvent event) { this.jeu.passerAEteChoisi(); } // le bouton Passer

    // anciennes methodes, maintenant remplacees par les versions "Generique"
    // private void afficherAttaquesDisponibles() { ... }
    // private void afficherEnergiesPourPokemon(IPokemon pokemon, HBox conteneurEnergies) { ... }


    @FXML private void echangerPokemon() { // bouton Echanger Pokemon (pour la retraite)
        IJoueur joueurCourant = jeu.joueurActifProperty().get();
        if (joueurCourant == null || !joueurCourant.peutRetraiteProperty().get()) return; // si on ne peut pas

        if (modePaiementCoutRetraiteActif || modeSelectionRemplacantApresRetraiteActif) { // si on est deja en train de faire une retraite, ca annule
            modePaiementCoutRetraiteActif=false; modeSelectionRemplacantApresRetraiteActif=false; coutRetraiteRestant=0;
            jeu.instructionProperty().set("retraite annulee."); // on previent le joueur
            if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(!joueurCourant.peutRetraiteProperty().get()); // on reactive le bouton si possible
            if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false);
            mettreAJourGrandeCarteActive(); // pour enlever les styles
        } else { // sinon, on demarre la procedure de retraite
            this.jeu.retraiteAEteChoisie();
        }
    }

    @FXML private void actionUtiliserTalent() { // bouton Utiliser Talent
        if (pokemonActifObserveCourant != null && jeu.joueurActifProperty().get() != null) {
            Pokemon pokemonMecanique = (Pokemon) pokemonActifObserveCourant;
            if (pokemonMecanique.peutUtiliserTalent()) { // si le talent est utilisable
                // la maniere d'activer un talent peut dependre du jeu, ici on fait une supposition
                this.jeu.uneCarteDeLaMainAEteChoisie(pokemonMecanique.getCartePokemon().getId());
            }
        }
    }

    public void pokemonDuBancChoisiPourRemplacer(String idPokemonBanc) { // appele par VueJoueurActif quand on choisit un remplacant sur le banc
        if (!this.modeSelectionRemplacantApresRetraiteActif) return; // si on n'est pas dans ce mode, on ignore
        this.jeu.uneCarteComplementaireAEteChoisie(idPokemonBanc); // on dit au jeu quel pokemon a ete choisi
        this.modeSelectionRemplacantApresRetraiteActif=false; // on sort du mode
        // on met a jour l'etat du bouton retraite
        if (boutonEchangerPokemon != null && jeu.joueurActifProperty().get() != null) boutonEchangerPokemon.setDisable(!jeu.joueurActifProperty().get().peutRetraiteProperty().get());
        else if (boutonEchangerPokemon != null) boutonEchangerPokemon.setDisable(true);
        if (vueJoueurActif != null) vueJoueurActif.setModeSelectionPourRemplacantApresRetraite(false); // on previent la vue du joueur
    }

    public boolean isModeSelectionRemplacantApresRetraiteActif() { return modeSelectionRemplacantApresRetraiteActif; } // pour que d'autres puissent verifier ce mode

    public void energieDuPokemonActifChoisiePourDefausse(String idCarteEnergie) { // quand on clique sur une energie pour payer le cout de retraite
        if (modePaiementCoutRetraiteActif && coutRetraiteRestant > 0) { // si on est bien en train de payer
            this.jeu.uneCarteEnergieAEteChoisie(idCarteEnergie); // on informe le jeu
        }
    }

    public void activerModeSelectionBasePourEvolution(fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution carteEvo) { // appele par VueJoueurActif quand on clique sur une carte evolution
        modePaiementCoutRetraiteActif=false; modeSelectionRemplacantApresRetraiteActif=false; estEnModeAttachementEnergie_Global=false; // on desactive les autres modes
        this.carteEvolutionSelectionnee=carteEvo; this.modeSelectionBasePourEvolution=true; // on active le mode evolution
        this.jeu.uneCarteDeLaMainAEteChoisie(carteEvo.getId()); // on informe le jeu, qui va changer l'instruction
        mettreAJourGrandeCarteActive(); // on rafraichit pour montrer les cibles
        if (vueJoueurActif != null) vueJoueurActif.rafraichirAffichagePourSelectionEvolution(); // le banc aussi
    }

    public void pokemonDeBaseChoisiPourEvolution(String idPokemonBase) { // quand on clique sur un pokemon pour le faire evoluer
        if (!this.modeSelectionBasePourEvolution || this.carteEvolutionSelectionnee == null) { // si on n'est plus dans ce mode
            modeSelectionBasePourEvolution=false; this.carteEvolutionSelectionnee=null; // on reinitialise
            mettreAJourGrandeCarteActive(); if (vueJoueurActif != null) vueJoueurActif.rafraichirAffichagePourSelectionEvolution();
            return;
        }
        this.jeu.uneCarteComplementaireAEteChoisie(idPokemonBase); // on dit au jeu quel pokemon a ete choisi
        modeSelectionBasePourEvolution=false; this.carteEvolutionSelectionnee=null; // on sort du mode
        mettreAJourGrandeCarteActive(); // on rafraichit
        if (vueJoueurActif != null) vueJoueurActif.rafraichirAffichagePourSelectionEvolution(); // pour enlever les styles
    }

    public boolean isModeSelectionBasePourEvolution() { return modeSelectionBasePourEvolution; } // pour verifier ce mode
    public fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemonEvolution getCarteEvolutionSelectionnee() { return carteEvolutionSelectionnee; } // pour connaitre la carte evolution

    private void mettreAJourZoneAdversaire() { // pour la zone de l'adversaire en haut de l'ecran
        IJoueur adversaire = adversaireProperty.get();
        if (adversaire == null) { // si pas d'adversaire
            if (nomAdversaireLabel != null) nomAdversaireLabel.setText("Adversaire: ---");
            if (pokemonActifAdversaireView != null) pokemonActifAdversaireView.setImage(null);
            if (pvPokemonActifAdversaireLabel != null) pvPokemonActifAdversaireLabel.setText("PV: --/--");
            if (bancAdversaireHBox != null) bancAdversaireHBox.getChildren().clear();
            if (energiesPokemonActifAdversaireHBox != null) energiesPokemonActifAdversaireHBox.getChildren().clear();
            if (attaquesAdversaireVBox != null) attaquesAdversaireVBox.getChildren().clear();
            // vider les compteurs de l'adversaire
            if (nbCartesMainAdversaireLabel != null) { if(nbCartesMainAdversaireLabel.textProperty().isBound()) nbCartesMainAdversaireLabel.textProperty().unbind(); nbCartesMainAdversaireLabel.setText("Main: -");}
            if (nbCartesPiocheAdversaireLabel != null) { if(nbCartesPiocheAdversaireLabel.textProperty().isBound()) nbCartesPiocheAdversaireLabel.textProperty().unbind(); nbCartesPiocheAdversaireLabel.setText("Pioche: --");}
            if (nbCartesDefausseAdversaireLabel != null) { if(nbCartesDefausseAdversaireLabel.textProperty().isBound()) nbCartesDefausseAdversaireLabel.textProperty().unbind(); nbCartesDefausseAdversaireLabel.setText("Défausse: --");}
            if (nbRecompensesAdversaireLabel != null) { if(nbRecompensesAdversaireLabel.textProperty().isBound()) nbRecompensesAdversaireLabel.textProperty().unbind(); nbRecompensesAdversaireLabel.setText("Récompenses: -");}
            return;
        }

        if (nomAdversaireLabel != null) nomAdversaireLabel.setText("Adversaire: " + adversaire.getNom()); // son nom
        IPokemon pokemonActifAdv = adversaire.pokemonActifProperty().get(); // son pokemon actif

        if (pokemonActifAdversaireView != null && pvPokemonActifAdversaireLabel != null) {
            if (pokemonActifAdv != null && pokemonActifAdv.getCartePokemon() != null) { // s'il a un pokemon actif
                String imagePath = "/images/cartes/" + pokemonActifAdv.getCartePokemon().getCode() + ".png"; // son image
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                pokemonActifAdversaireView.setImage(imageStream == null ? null : new Image(imageStream));
                CartePokemon cartePkmAdv = (CartePokemon) pokemonActifAdv.getCartePokemon();
                if (pvPokemonActifAdversaireLabel.textProperty().isBound()) pvPokemonActifAdversaireLabel.textProperty().unbind(); // ses pv
                pvPokemonActifAdversaireLabel.setText(String.format("%d/%d PV", Math.max(0, pokemonActifAdv.pointsDeVieProperty().get()), cartePkmAdv.getPointsVie()));
                pvPokemonActifAdversaireLabel.setVisible(true); pokemonActifAdversaireView.setVisible(true);
                if (pokemonActifAdv.pointsDeVieProperty().get() <= 0) pokemonActifAdversaireView.setStyle("-fx-effect: grayscale(100%);"); // style KO
                else pokemonActifAdversaireView.setStyle("");

                if (energiesPokemonActifAdversaireHBox != null) afficherEnergiesGenerique(pokemonActifAdv, energiesPokemonActifAdversaireHBox, false); // ses energies
                if (attaquesAdversaireVBox != null) afficherAttaquesGenerique(pokemonActifAdv, attaquesAdversaireVBox, false); // ses attaques
            } else { // s'il n'a pas de pokemon actif
                pokemonActifAdversaireView.setImage(null); pokemonActifAdversaireView.setVisible(false); pokemonActifAdversaireView.setStyle("");
                if (pvPokemonActifAdversaireLabel.textProperty().isBound()) pvPokemonActifAdversaireLabel.textProperty().unbind();
                pvPokemonActifAdversaireLabel.setText("--/-- PV"); pvPokemonActifAdversaireLabel.setVisible(false);
                if (energiesPokemonActifAdversaireHBox != null) energiesPokemonActifAdversaireHBox.getChildren().clear();
                if (attaquesAdversaireVBox != null) attaquesAdversaireVBox.getChildren().clear();
            }
        }

        if (bancAdversaireHBox != null) { // affichage de son banc
            bancAdversaireHBox.getChildren().clear();
            for (IPokemon pokemonBancAdv : adversaire.getBanc()) { // pour chaque pokemon sur son banc
                if (pokemonBancAdv != null && pokemonBancAdv.getCartePokemon() != null) {
                    ImageView imgBancAdv = new ImageView();
                    String pathImgBanc = "/images/cartes/" + pokemonBancAdv.getCartePokemon().getCode() + ".png";
                    InputStream streamImgBanc = getClass().getResourceAsStream(pathImgBanc);
                    if (streamImgBanc != null) {
                        imgBancAdv.setImage(new Image(streamImgBanc)); imgBancAdv.setFitHeight(60); imgBancAdv.setPreserveRatio(true);
                        String currentStyle = (pokemonBancAdv.pointsDeVieProperty().get() <= 0) ? "-fx-effect: grayscale(100%);" : ""; // style KO
                        if (this.permettreChoixRemplacantPourAdversaireParJoueurActif && pokemonBancAdv.pointsDeVieProperty().get() > 0) { // si le joueur doit cliquer pour l'adversaire
                            final String idPokemonChoisi = pokemonBancAdv.getCartePokemon().getId();
                            imgBancAdv.setOnMouseClicked(event -> { // on rend le pokemon cliquable
                                this.jeu.uneCarteComplementaireAEteChoisie(idPokemonChoisi);
                                this.permettreChoixRemplacantPourAdversaireParJoueurActif = false; // on desactive le mode
                                if (!instructionLabel.textProperty().isBound()) instructionLabel.textProperty().bind(jeu.instructionProperty()); // on remet l'instruction normale
                                mettreAJourZoneAdversaire(); mettreAJourGrandeCarteActive(); // on rafraichit
                            });
                            if (!currentStyle.isEmpty() && !currentStyle.endsWith(";")) currentStyle += ";";
                            currentStyle += " -fx-border-color: blue; -fx-border-width: 2;"; // style pour indiquer que c'est cliquable
                            imgBancAdv.setCursor(javafx.scene.Cursor.HAND);
                        } else { // sinon, pas cliquable
                            imgBancAdv.setOnMouseClicked(null); imgBancAdv.setCursor(javafx.scene.Cursor.DEFAULT);
                        }
                        imgBancAdv.setStyle(currentStyle.trim());
                        bancAdversaireHBox.getChildren().add(imgBancAdv);
                    } else { Label errLabel = new Label("Err"); if (pokemonBancAdv != null && pokemonBancAdv.pointsDeVieProperty().get() <= 0) errLabel.setStyle("-fx-effect: grayscale(100%);"); bancAdversaireHBox.getChildren().add(errLabel); }
                }
            }
        }

        final IJoueur adversairePourBinding = adversaire; // pour les compteurs de cartes de l'adversaire
        // code complet des bindings ici
        if (nbCartesMainAdversaireLabel != null) { if (nbCartesMainAdversaireLabel.textProperty().isBound()) nbCartesMainAdversaireLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Main: " + (adversairePourBinding != null && adversairePourBinding.getMain() != null ? adversairePourBinding.getMain().size() : "-"), adversairePourBinding.getMain()); nbCartesMainAdversaireLabel.textProperty().bind(b); }
        if (nbCartesPiocheAdversaireLabel != null) { if (nbCartesPiocheAdversaireLabel.textProperty().isBound()) nbCartesPiocheAdversaireLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Pioche: " + (adversairePourBinding != null && adversairePourBinding.piocheProperty() != null ? adversairePourBinding.piocheProperty().size() : "--"), adversairePourBinding.piocheProperty()); nbCartesPiocheAdversaireLabel.textProperty().bind(b); }
        if (nbCartesDefausseAdversaireLabel != null) { if (nbCartesDefausseAdversaireLabel.textProperty().isBound()) nbCartesDefausseAdversaireLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Défausse: " + (adversairePourBinding != null && adversairePourBinding.defausseProperty() != null ? adversairePourBinding.defausseProperty().size() : "--"), adversairePourBinding.defausseProperty()); nbCartesDefausseAdversaireLabel.textProperty().bind(b); }
        if (nbRecompensesAdversaireLabel != null) { if (nbRecompensesAdversaireLabel.textProperty().isBound()) nbRecompensesAdversaireLabel.textProperty().unbind(); StringBinding b = Bindings.createStringBinding(() -> "Récompenses: " + (adversairePourBinding != null && adversairePourBinding.recompensesProperty() != null ? adversairePourBinding.recompensesProperty().size() : "-"), adversairePourBinding.recompensesProperty()); nbRecompensesAdversaireLabel.textProperty().bind(b); }
    }

    private void mettreAJourChoixComplementaires() {
        // cette methode s'occupe d'afficher les boutons pour les choix specifiques (par exemple, choisir une carte apres un effet)
        if (choixComplementairesHBox == null) {
            // System.err.println("[VueDuJeu] choixComplementairesHBox est null."); // si l'element fxml n'est pas la
            return;
        }

        choixComplementairesHBox.getChildren().clear(); // on vide les anciens choix
        IJoueur joueurCourant = this.jeu.joueurActifProperty().get();

        if (joueurCourant != null) {
            ObservableList<? extends ICarte> choix = joueurCourant.getChoixComplementaires(); // on recupere les choix possibles
            if (choix != null && !choix.isEmpty()) { // s'il y a des choix
                for (ICarte carteChoix : choix) { // pour chaque choix
                    Button boutonChoix = new Button(carteChoix.getNom()); // on cree un bouton
                    boutonChoix.setOnAction(event -> {
                        this.jeu.uneCarteComplementaireAEteChoisie(carteChoix.getId()); // on dit au jeu quel choix a ete fait
                        // apres un choix, l'etat du jeu peut changer, donc on rafraichit les choix
                        // cela pourrait aussi etre gere par le listener d'instruction si l'instruction change apres un choix
                        mettreAJourChoixComplementaires();
                    });
                    choixComplementairesHBox.getChildren().add(boutonChoix); // on ajoute le bouton a la zone des choix
                }
            }
        }
        // on pourrait gerer la visibilite de la HBox ici, si on veut qu'elle disparaisse quand il n'y a pas de choix
        // choixComplementairesHBox.setVisible(!choixComplementairesHBox.getChildren().isEmpty());
        // choixComplementairesHBox.setManaged(!choixComplementairesHBox.getChildren().isEmpty());
    }
}