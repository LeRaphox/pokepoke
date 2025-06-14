package fr.umontpellier.iut.ptcgJavaFX.mecanique;

import fr.umontpellier.iut.ptcgJavaFX.ICarte;
import fr.umontpellier.iut.ptcgJavaFX.IJoueur;
import fr.umontpellier.iut.ptcgJavaFX.IPokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.Attaque;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.deplacement.DeplacementCarte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.EtatJoueur;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.attaque.EnAttaque;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.initialisation.InitialisationPokemonActifInitial;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.DefausseEnergie;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.etatsJoueur.tournormal.FinPartie;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.IntStream;

public class Joueur implements IJoueur {
    /**
     * Nom du joueur
     */
    private final String nom;
    /**
     * Les cartes dans la pile de pioche (deck)
     * Remarque : On considère que la fin de la liste correspond au sommet de la
     * pioche (la prochaine carte piochée est la dernière de la liste)
     */
    private final ObservableList<Carte> pioche;
    /**
     * Les cartes dans la pile de défausse
     */
    private final ObservableList<Carte> defausse;
    /**
     * Les cartes dans la main du joueur
     */
    private final ObservableList<Carte> main;
    /**
     * Les cartes récompenses restantes
     */
    private final ObservableList<Carte> recompenses;
    /**
     * Le pokémon actif
     */
    private final ObjectProperty<Pokemon> pokemonActif;
    /**
     * Liste des pokémon de banc. Cette liste a toujours 5 éléments qui
     * correspondent aux 5 emplacements de banc. La liste contient {@code null} si
     * l'emplacement est vide.
     */
    private final ObservableList<Pokemon> banc;
    /**
     * Liste de cartes proposées au joueur lorsqu'une carte est en jeu
     * et qu'il doit terminer une action, par exemple choisir une carte à défausser...
     */
    ObservableList<Carte> choixComplementaires;
    /**
     * Indique si le joueur peut jouer une énergie pendant ce tour.
     * (initialement {@code true} en début de tour, passe à {@code false} après
     * avoir joué une énergie)
     */
    private boolean peutJouerEnergie;
    /**
     * Indique si le joueur peut jouer une carte supporter pendant ce tour.
     * (initialement {@code true} en début de tour, passe à {@code false} après
     * avoir joué une carte supporter)
     */
    private boolean peutJouerSupporter;
    /**
     * Indique si le joueur a le droit de battre en retraite pendant ce tour.
     * (initialement {@code true} en début de tour, passe à {@code false} après
     * avoir battu en retraite une fois)
     */
    public BooleanProperty retraitePasEncoreUtilisee;
    /**
     * Devient vrai quand le joueur a toutes les conditions requises pour battre en retraite pendant ce tour.
     */
    private final BooleanProperty peutRetraite;
    /**
     * Devient vrai quand le joueur a la possibilité de mélanger des cartes.
     */
    private final BooleanProperty peutMelanger;
    /**
     * Devient vrai quand le joueur a la possibilité d'ajouter des cartes.
     */
    private final BooleanProperty peutAjouter;
    /**
     * Devient vrai quand le joueur a la possibilité de défausser toute l'énergie de son pokemon actif (Lanturn - FrapEclair).
     */
    private final BooleanProperty peutDefausserEnergie;
    /**
     * Indique si le joueur a perdu la partie parce que sa pioche était vide en
     * début de tour.
     */
    private boolean perduParPiocheVide;
    /**
     * Carte qui est en train d'être jouée (utilisée pour représenter dans
     * l'interface graphique une carte qui a été retirée de la main du joueur, mais
     * qui n'est pas encore posée quelque part)
     */
    private final ObjectProperty<Carte> carteEnJeu;
    /**
     * Référence vers le jeu auquel le joueur appartient (initialement {@code null},
     * c'est le jeu qui doit appeler la méthode {@code setJeu} pour initialiser
     * cette référence)
     */
    private Jeu jeu;
    /**
     * Quantité de dégâts supplémentaires infligés par les attaques au pokémon actif
     * de l'adversaire (remis à 0 à la fin du tour du joueur)
     */
    private int bonusDegats;

    public Joueur(String nom, List<Carte> deck) {
        this.nom = nom;
        // toutes les cartes du deck sont initialement placées dans la pioche
        pioche = FXCollections.observableArrayList(deck);
        defausse = FXCollections.observableArrayList();
        main = FXCollections.observableArrayList();
        recompenses = FXCollections.observableArrayList();
        choixComplementaires = FXCollections.observableArrayList();
        retraitePasEncoreUtilisee = new SimpleBooleanProperty(false);
        peutRetraite = new SimpleBooleanProperty(false);
        peutMelanger = new SimpleBooleanProperty(false);
        peutAjouter = new SimpleBooleanProperty(false);
        peutDefausserEnergie = new SimpleBooleanProperty(false);
        perduParPiocheVide = false;
        bonusDegats = 0;
        carteEnJeu = new SimpleObjectProperty<>();
        pokemonActif = new SimpleObjectProperty<>();
        // préparation des emplacements de pokémon (tous initialement vides)
        banc = FXCollections.observableArrayList(
                pokemon -> new Observable[]{pokemon.cartePokemonProperty(), pokemon.cartesProperty()});

        // lie la propriété peutRetraite à toutes ses conditions
        peutRetraite.bind(new BooleanBinding() {
            {
                super.bind(pokemonActif, retraitePasEncoreUtilisee, banc);
                pokemonActif.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        super.bind(newValue.cartesProperty());
                        super.bind(newValue.cartePokemonProperty());
                    }
                });
            }
            @Override
            protected boolean computeValue() {
                return peutRetraite();
            }
        });

        for (int i = 0; i < 5; i++) {
            banc.add(null);
        }

        // mélange toutes les cartes dans la pioche et pioche 7 cartes en main. Cette
        // boucle est répétée tant que le joueur n'a pas pioché de pokémon de base en
        // main.
        do {
            pioche.addAll(main);
            main.clear();
            melangerPioche();
            piocherEnMain(7);
        } while (main.stream().noneMatch(c -> c.peutJouerInit(this)));

        // prépare les cartes récompense
        for (int i = 0; i < 6; i++) {
            recompenses.add(piocher());
        }

    }

    // TODO: retirer cette méthode (uniquement pour débugger)
    /**
     * Constructeur qui permet de placer des cartes spécifiques initialement dans la
     * main du joueur (pour débugger)
     * 
     * @param nom    nom du joueur
     * @param deck   cartes du joueur
     * @param cartes cartes à ajouter dans la main du joueur
     */
    public Joueur(String nom, List<Carte> deck, Carte... cartes) {
        this(nom, deck);
        main.addAll(Arrays.asList(cartes));
    }

    public Jeu getJeu() {
        return jeu;
    }

    public void setJeu(Jeu jeu) {
        this.jeu = jeu;
    }

    public String getNom() {
        return nom;
    }

    public int getBonusDegats() {
        return bonusDegats;
    }

    public void incrementerBonusDegats(int bonus) {
        bonusDegats += bonus;
    }

    public void setCarteEnJeu(Carte carte) {
        carteEnJeu.setValue(carte);
    }

    public List<Carte> getCartesMain() {
        return new ArrayList<>(main);
    }

    public void ajouterCarteMain(Carte carte) {
        main.add(carte);
    }

    public void retirerCarteMain(Carte carte) {
        main.remove(carte);
    }

    public int getNombreDeCartesEnMain() {
        return main.size();
    }

    public List<Carte> getCartesPioche() {
        return new ArrayList<>(pioche);
    }

    public Pokemon getPokemonActif() {
        return pokemonActif.getValue();
    }

    public void setPokemonActif(Pokemon pokemon) {
        pokemonActif.setValue(pokemon);
    }

    public void setPeutMelanger(boolean peutMelanger) {
        this.peutMelanger.set(peutMelanger);
    }

    public void setPeutAjouter(boolean peutAjouter) {
        this.peutAjouter.set(peutAjouter);
    }

    public void setPeutDefausserEnergie(boolean peutDefausserEnergie) {
        this.peutDefausserEnergie.set(peutDefausserEnergie);
    }

    public List<Carte> getCartesDefausse() {
        return new ArrayList<>(defausse);
    }

    public void ajouterCarteDefausse(Carte carte) {
        defausse.add(carte);
    }

    public void retirerCarteDefausse(Carte carte) {
        defausse.remove(carte);
    }

    public void ajouterCartePioche(Carte carte) {
        pioche.add(carte);
    }

    public void retirerCartePioche(Carte carte) {
        pioche.remove(carte);
    }

    public void ajouterCarteSousLaPioche(Carte carte) {
        pioche.addFirst(carte);
    }

    public void retirerCarteChoixComplementaires(Carte carte) {
        getChoixComplementaires().remove(carte);
    }

    /**
     * Mélange les cartes de la pioche
     */
    public void melangerPioche() {
        Collections.shuffle(pioche);
    }

    /**
     * Pioche une carte dans la pioche du joueur
     * 
     * @return la carte piochée ou {@code null} si la pioche est vide
     */
    public Carte piocher() {
        if (!pioche.isEmpty()) {
            return pioche.removeLast();
        }
        return null;
    }

    /**
     * Pioche un nombre donné de cartes dans la pioche du joueur
     * 
     * @param n nombre de cartes à piocher
     * @return une liste contenant les cartes piochées (peut contenir moins de n
     *         cartes si la pioche contenait moins de n cartes).
     */
    public List<Carte> piocher(int n) {
        ArrayList<Carte> cartes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Carte c = piocher();
            if (c == null) {
                break;
            }
            cartes.add(c);
        }
        return cartes;
    }

    /**
     * Pioche une carte dans la pioche du joueur et la place dans sa main. Si la
     * pioche est vide, aucune carte n'est placée dans la main du joueur.
     * 
     * @return la carte piochée ou {@code null} si la pioche est vide
     */
    public Carte piocherEnMain() {
        Carte carte = piocher();
        if (carte != null) {
            main.add(carte);
        }
        return carte;
    }

    /**
     * Pioche un nombre donné de cartes dans la pioche du joueur et les place dans
     * la main du joueur.
     * 
     * @param n nombre de cartes à piocher
     * @return la liste des cartes qui ont été piochées et mises dans la main du
     *         joueur (peut contenir moins de n cartes si la pioche contenait moins
     *         de n cartes).
     */
    public List<Carte> piocherEnMain(int n) {
        List<Carte> cartes = piocher(n);
        main.addAll(cartes);
        return cartes;
    }

    /**
     * @return l'adversaire du joueur dans le jeu
     */
    @Override
    public Joueur getAdversaire() {
        return jeu.getAdversaire(this);
    }

    /**
     * @return le numéro du tour de jeu actuel (1 pour le premier tour du premier
     *         joueur, puis incrémenté de 1 à chaque changement de joueur)
     */
    public int getCompteurTour() {
        return jeu.getCompteurTour();
    }

    /**
     * @return le nombre d'emplacements de banc vides
     */
    public long getNbEmplacementsLibres() {
        return banc.stream().filter(Objects::isNull).count();
    }

    /**
     * @return une liste d'entiers correspondant aux indices des emplacements vides
     *         du banc
     */
    public List<String> getIndicesDeBancVides() {
        List<String> indices = new ArrayList<>();
        for (int i = 0; i < banc.size(); i++) {
            if (banc.get(i) == null) {
                indices.add(String.valueOf(i));
            }
        }
        return indices;
    }

    /**
     * @return {@code true} si le joueur a le droit de jouer une carte énergie de sa
     *         main
     */
    public boolean peutJouerEnergie() {
        return peutJouerEnergie;
    }

    /**
     * Cette méthode est appelée lorsque le joueur joue une carte énergie de sa
     * main, pour indiquer qu'il ne peut plus jouer d'énergie pendant ce tour.
     */
    public void setAJoueEnergie() {
        this.peutJouerEnergie = false;
    }

    /**
     * @return {@code true} si le joueur a le droit de jouer une carte supporter de
     *         sa main
     */
    public boolean getPeutJouerSupporter() {
        return peutJouerSupporter;
    }

    /**
     * Passe la variable {@code peutJouerSupporter} à {@code false} pour indiquer
     * que le joueur a joué une carte supporter ce tour-ci
     */
    public void setAJoueSupporter() {
        peutJouerSupporter = false;
    }

    /**
     * Met un pokémon dans un emplacement de banc
     * 
     * @param pokemon le pokémon à mettre dans l'emplacement
     * @param index   indice de l'emplacement de banc
     */
    public void setPokemonBanc(Pokemon pokemon, int index) {
        banc.set(index, pokemon);
    }

    /**
     * @return une liste des pokémon dans les emplacements non vides du banc du
     *         joueur
     */
    public List<Pokemon> getListePokemonDeBanc() {
        return banc.stream().filter(Objects::nonNull).toList();
    }

    /**
     * Renvoie la liste de tous les pokémon en jeu du joueur (le pokémon actif et
     * les pokémon de banc)
     * 
     * @return une liste des pokémon dans les emplacements non vide du banc et
     *         l'emplacement actif du joueur
     */
    public List<Pokemon> getListePokemonEnJeu() {
        List<Pokemon> listePokemon = new ArrayList<>();
        if (pokemonActif.getValue() != null) {
            listePokemon.add(pokemonActif.getValue());
        }
        listePokemon.addAll(getListePokemonDeBanc());
        return listePokemon;
    }

    public void terminerJouerCarteEnJeu(String numCarte) {
        carteEnJeu.getValue().jouerQuandEnJeu(this, numCarte);
    }

    /**
     * Renvoie le pokémon parmi les pokémon en jeu du joueur dont la carte pokémon
     * active est passée en argument ou {@code null} si aucun pokémon n'a cette
     * carte.
     * 
     * @param carte carte pokémon recherchée
     * @return le pokémon correspondant à la carte ou {@code null} si aucun pokémon
     *         du joueur ne correspond
     */
    public Pokemon getPokemon(Carte carte) {
        if (pokemonActif.getValue() != null && pokemonActif.getValue().getCartePokemon() == carte) {
            return pokemonActif.getValue();
        }
        for (Pokemon pokemon : banc) {
            if (pokemon != null && pokemon.getCartePokemon() == carte) {
                return pokemon;
            }
        }
        return null;
    }

    /**
     * Joue une carte (exécute l'action {@code jouer} de la carte)
     * 
     * @param carte la carte à jouer
     */
    public void jouerCarte(Carte carte) {
        carte.jouer(this);
    }

    public void initialiserPokemons() {
        etatCourant = new InitialisationPokemonActifInitial(this);
        etatCourant.choisirPokemon();
    }

    public void jouerCarteEnMain(String numCarte) {
        Carte carte = Carte.get(numCarte);
        main.remove(carte);
        jouerCarte(carte);
    }

    public void deplacer(String numCarte, DeplacementCarte deplacement) {
        deplacement.deplacer(Carte.get(numCarte), this);
    }

    public void deplacerCarteComplementaire(String numCarte, DeplacementCarte deplacement) {
        Carte carte = Carte.get(numCarte);
        retirerCarteChoixComplementaires(carte);
        deplacer(numCarte, deplacement);
    }

    /**
     * Exécute un tirage à pile ou face
     * <p>
     * Remarque : Cette méthode appelle directement l'implémentation de
     * {@code lancerPiece()} dans {@code Jeu}
     * 
     * @return {@code true} si le tirage est face, {@code false} sinon
     */
    public boolean lancerPiece() {
        return jeu.lancerPiece();
    }

    public void jouerTour() {
        onDebutTour();
        Carte cartePiochee = piocherEnMain();
        if (cartePiochee == null) {
            // si le joueur n'a plus de carte à piocher en début de tour, il perd
            // immédiatement la partie
            perduParPiocheVide = true;
            setEtatCourant(new FinPartie(this));
            // remarque : si le joueur perd, car sa pioche est vide en début de tour, il perd
            // la partie avant d'exécuter le contrôle de pokémon
        }
    }

    public List<String> getCartesEnMainJouables() {
        ArrayList<String> choixPossibles = new ArrayList<>();
        for (Carte carte : main) {
            // cartes que le joueur peut jouer de sa main
            if (carte.peutJouer(this)) {
                choixPossibles.add(carte.getId());
            }
        }
        for (Attaque attaque : getAttaquesPossibles()) {
            // attaques que le joueur peut utiliser sur son pokémon actif
            choixPossibles.add(attaque.getNom());
        }
        for (Pokemon pokemon : getListePokemonEnJeu()) {
            // talents que le joueur peut utiliser sur ses pokémon en jeu (activés en
            // sélectionnant la CartePokemon du pokémon)
            if (pokemon.peutUtiliserTalent()) {
                choixPossibles.add(pokemon.getCartePokemon().getId());
            }
        }
        return choixPossibles;
    }


    /**
     * Fonction exécutée en début de tour pour préparer l'état du joueur
     */
    public void onDebutTour() {
        peutJouerEnergie = true;
        // le premier joueur ne peut pas jouer de supporter pendant son premier tour
        peutJouerSupporter = jeu.getCompteurTour() != 1;
        retraitePasEncoreUtilisee.setValue(true);
        peutMelanger.setValue(false);
        peutAjouter.setValue(false);

        // initialisation du tour pour les pokémon en jeu
        for (Pokemon p : getListePokemonEnJeu()) {
            p.onDebutTour(this);
        }
    }

    /**
     * Fonction exécutée en fin de tour pour ajuster l'état du joueur
     */
    public void onFinTour() {
        for (Pokemon p : getListePokemonEnJeu()) {
            p.onFinTour(this);
        }
        bonusDegats = 0;
    }

    /**
     * Indique si le joueur peut battre en retraite. Pour pouvoir battre en
     * retraite, if faut
     * <p>
     * - que le joueur ait un pokémon actif
     * — que le joueur ait au moins un pokémon sur son banc
     * — que le pokémon actif soit capable de battre en retraite (énergie
     * suffisante, et éventuels états spéciaux)
     * — que le joueur n'ait pas déjà battu en retraite pendant ce tour
     * 
     * @return {@code true} si le joueur peut battre en retraite, {@code false}
     *         sinon
     */
    public boolean peutRetraite() {
        return pokemonActif.getValue() != null
                && !getListePokemonDeBanc().isEmpty()
                && retraitePasEncoreUtilisee.getValue()
                && pokemonActif.getValue().peutRetraite();
    }

    /**
     * Renvoie la liste des attaques que le pokémon actif du joueur peut exécuter
     * pendant ce tour.
     * <p>
     * Cette fonction ne liste que les attaques dont le coût en énergie est couvert
     * par l'énergie du pokémon actif (obtenues en appelant
     * {@code pokemonActif.getAttaquesPossibles()}).
     * <p>
     * Remarque : Si c'est le premier tour du premier joueur, la fonction renvoie
     * toujours une liste vide, car le premier joueur n'a pas le droit d'exécuter une
     * attaque pendant son premier tour.
     * <p>
     * Prérequis : On suppose que la fonction n'est appelée que pendant un des tours
     * du joueur
     * 
     * @return la liste des attaques que le pokémon actif du joueur peut exécuter
     */
    public List<Attaque> getAttaquesPossibles() {
        // le joueur ne peut pas attaquer si c'est le premier tour de jeu (uniquement le
        // premier joueur) ou s'il n'a pas de pokémon actif
        if (jeu.getCompteurTour() != 1 && pokemonActif.getValue() != null) {
            return pokemonActif.getValue().getAttaquesPossibles();
        }
        return new ArrayList<>();
    }

    /**
     * Fonction exécutée lorsque le joueur fait battre en retraite son pokémon
     * actif.
     * <p>
     * Cette fonction demande au joueur de sélectionner les éventuelles énergies à
     * défausser pour battre en retraite, puis de choisir un nouveau pokémon actif
     * parmi les pokémon de banc.
     * <p>
     * Prérequis : le joueur doit pouvoir battre en retraite (vérifié par
     * {@code peutRetraite()})
     */
    public void retraite() {
        setEtatCourant(new DefausseEnergie(this, getPokemonActif().getCoutRetraite()));
    }

    /**
     * Fonction exécutée lorsque le joueur fait avancer un pokemon de banc
     * en pokemon actif.
     */
    public void avancerPokemon(String numCarte) {
        Carte cartePokemon = Carte.get(numCarte);
        Pokemon pokemon = getPokemon(cartePokemon);
        avancerPokemonDeBanc(pokemon);
        retraitePasEncoreUtilisee.setValue(false);
    }

    /**
     * Vérifie pour chacun des pokémon en jeu du joueur s'il a été mis KO (dégâts >=
     * points de vie), en commençant par les pokémon de banc puis le pokémon actif.
     * Pour chaque pokémon KO, les cartes qui lui sont attachées sont défaussées et
     * le joueur adverse prend une carte récompense.
     */
    public void defausserPokemonsKO(Joueur joueur) {
        // retirer tous les pokémon de banc qui sont KO
        for (int i = 0; i < joueur.banc.size(); i++) {
            Pokemon pokemon = joueur.banc.get(i);
            if (pokemon != null && pokemon.estKO()) {
                joueur.onPokemonKO(pokemon);
                for (Carte c : pokemon.getCartes()) {
                    joueur.ajouterCarteDefausse(c);
                }
                joueur.banc.set(i, null);
                joueur.getAdversaire().prendreRecompense();
            }
        }
        // tester si le pokemon actif est KO
        if (joueur.pokemonActif.getValue() != null && joueur.pokemonActif.getValue().estKO()) {
            joueur.onPokemonKO(joueur.pokemonActif.getValue());
            for (Carte c : joueur.pokemonActif.getValue().getCartes()) {
                joueur.ajouterCarteDefausse(c);
            }
            joueur.pokemonActif.setValue(null);
            joueur.getAdversaire().prendreRecompense();
        }
    }

    /**
     * Cette méthode est appelée lorsqu'un pokémon du joueur est mis KO (pour
     * exécuter les effets spécifiques qui se déclenchent dans cette situation).
     * 
     * @param pokemon le pokémon qui est mis KO
     */
    public void onPokemonKO(Pokemon pokemon) {
        for (Pokemon p : getListePokemonEnJeu()) {
            if (!p.estKO()) {
                p.onPokemonKO(pokemon, this);
            }
        }
    }

    /**
     * Avance le pokémon passé en argument pour le mettre en tant que pokémon actif.
     * Le pokémon actuellement actif est placé sur le banc en remplacement.
     * <p>
     * Prérequis : le pokémon passé en argument doit être sur le banc.
     * 
     * @param pokemon le pokémon de banc à mettre dans le rôle actif
     */
    public void avancerPokemonDeBanc(Pokemon pokemon) {
        int indice = IntStream.range(0, banc.size())
                .filter(i -> banc.get(i) == pokemon)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Le pokémon passé en argument n'est pas sur le banc"));
        Pokemon p = pokemonActif.getValue();
        pokemonActif.setValue(pokemon);
        banc.set(indice, p);
        if (p != null) {
            p.retirerEffets();
        }
    }

    /**
     * Pioche une carte récompense en main (par simplicité, on prend toujours la
     * première carte récompense)
     */
    public void prendreRecompense() {
        if (!recompenses.isEmpty()) {
            main.add(recompenses.removeFirst());
        }
    }

    /**
     * Teste si le joueur a gagné la partie parce qu'il a pris toutes les cartes
     * récompenses
     * 
     * @return {@code true} si le joueur a gagné parce qu'il a pris toutes ses
     *         cartes récompense, {@code false} sinon
     */
    public boolean aGagne() {
        return recompenses.isEmpty();
    }

    /**
     * Teste si le joueur a perdu la partie parce qu'il n'a plus de pokémon actif ou
     * parce qu'il n'avait plus de carte à piocher en début de tour
     * 
     * @return {@code true} si le joueur a perdu la partie, {@code false} sinon
     */
    public boolean aPerdu() {
        return pokemonActif.getValue() == null || perduParPiocheVide;
    }

    /**
     * Exécute la phase de contrôle pokémon sur tous les pokémon en jeu du joueur
     */
    public void controlePokemon() {
        if (pokemonActif.getValue() != null) {
            pokemonActif.getValue().controlePokemon(this);
        }
        for (Pokemon pokemon : banc) {
            if (pokemon != null) {
                pokemon.controlePokemon(this);
            }
        }
    }

    /**
     * Fonction qui retourne la liste des pokemons en main
     * qu'on peut choisir pendant la phase d'initialisation
     */
    public List<String> getPokemonsDeBaseEnMain() {
          return main.stream()
                .filter(c -> c.peutJouerInit(this))
                .map(Carte::getId)
                .toList();
    }

    /**
     * Fonction qui permet de connaitre les cartes qui peuvent être jouées
     * lorsqu'une carte est en jeu
     * par exemple, la liste des cartes pokemon en jeu dont la carte
     * en jeu est l'évolution
     */
    public List<String> getCartesJouablesEnSuite() {
        return carteEnJeu.getValue().getChoixPossibles(this);
    }

    public void attaquer(String attaque) {
        setEtatCourant(new EnAttaque(this));
        getPokemonActif().utiliserAttaque(attaque, this);
    }

    public void finaliserAttaque(String numCarte) {
        Carte carte = Carte.get(numCarte);
        getPokemonActif().retirerCarte(carte);
        ajouterCarteDefausse(carte);
    }

    /**
     * Fonction exécutée lorsque certaines cartes doivent être exclues
     * de la liste des cartes parmi lesquelles choisir
     * lorsqu'une carte est en jeu
     */
    public void removeCartesComplementaires(Type typeEnergie) {
        List<Carte> cartesEnEnlever = getChoixComplementaires().stream()
                .filter(c -> c.getTypeEnergie() == typeEnergie).toList();
        cartesEnEnlever.stream().forEach(c -> getChoixComplementaires().remove(c));
    }

    /**
     * Fonction exécutée lorsqu'une carte a été choisie
     * et qu'elle doit être ajoutée à un des pokemons en jeu
     */
    public void ajouterCarteEnJeuAuPokemon(String numCartePokemon) {
        Pokemon pokemon = getPokemon(Carte.get(numCartePokemon));
        retirerCarteMain(carteEnJeu.getValue());
        pokemon.ajouterCarte(carteEnJeu.getValue());
        setCarteEnJeu(null);
    }

    /**
     * Liste de cartes parmi lesquelles un choix sera fait
     * quand une première carte est en jeu
     */
    public void setListChoixComplementaires(List<? extends Carte> list) {
        choixComplementaires.addAll(list);
    }

    public void viderListChoixComplementaires() {
        choixComplementaires.clear();
    }

    /**
     * Gestion des états du joueur courant
     */
    private EtatJoueur etatCourant;

    public void setEtatCourant(EtatJoueur etatCourant) {
        this.etatCourant = etatCourant;
    }

    public EtatJoueur getEtatCourant() {
        return etatCourant;
    }

    /**
     * Propriétés exportées à utiliser dans l'IHM
     */
    @Override
    public ObjectProperty<Pokemon> pokemonActifProperty() {
        return pokemonActif;
    }

    @Override
    public ObservableList<? extends ICarte> getMain() {
        return main;
    }

    @Override
    public ObservableList<? extends IPokemon> getBanc() {
        return banc;
    }

    @Override
    public ObservableList<? extends ICarte> piocheProperty() {
        return pioche;
    }

    @Override
    public ObservableList<? extends ICarte> defausseProperty() {
        return defausse;
    }

    @Override
    public ObservableList<? extends ICarte> recompensesProperty() {
        return recompenses;
    }

    @Override
    public ObservableList<Carte> getChoixComplementaires() {
        return choixComplementaires;
    }

    @Override
    public ObjectProperty<Carte> carteEnJeuProperty() {
        return carteEnJeu;
    }

    @Override
    public BooleanProperty peutRetraiteProperty() {
        return peutRetraite;
    }

    @Override
    public BooleanProperty peutMelangerProperty() {
        return peutMelanger;
    }

    @Override
    public BooleanProperty peutAjouterProperty() {
        return peutAjouter;
    }

    @Override
    public BooleanProperty peutDefausserEnergieProperty() {
        return peutDefausserEnergie;
    }

}