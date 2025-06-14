package fr.umontpellier.iut.ptcgJavaFX.mecanique;

import fr.umontpellier.iut.ptcgJavaFX.IPokemon;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.Carte;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.Attaque;
import fr.umontpellier.iut.ptcgJavaFX.mecanique.cartes.pokemon.CartePokemon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe représentant un Pokémon en jeu.
 * Un pokémon est caractérisé par :
 * <ul>
 * <li>une Carte Pokémon qui représente l'espèce du pokémon
 * <li>la liste des cartes qui lui sont associées : cartes pokémon (incluant les
 * niveaux précédents s'il s'agit d'une évolution), cartes énergie et
 * éventuellement des outils)
 * <li>les variables supplémentaires décrivant son état (dégâts, statuts, etc.)
 * </ul>
 */
public class Pokemon implements IPokemon {
    /**
     * La carte Pokémon qui représente l'espèce du pokémon
     */
    private ObjectProperty<CartePokemon> cartePokemon;
    /**
     * La liste des cartes qui sont attachées à ce pokémon (incluant la carte
     * Pokémon principale)
     */
    private final ObservableList<Carte> cartes;
    /**
     * Quantité de dégâts subis par le pokémon
     */
    private IntegerProperty degats;
    /**
     * Points de vie restants du pokémon
     */
    private IntegerProperty pointsDeVie;
    /**
     * Attribut qui indique si le pokémon peut évoluer, c'est-à-dire s'il a été joué
     * avant le tour actuel du joueur
     */
    private boolean peutEvoluer;
    /**
     * Indique si le pokémon est actuellement dans l'état "brûlé"
     */
    private BooleanProperty estBrule;
    /**
     * Indique si le pokémon bénéficie d'une protection contre les effets et dégâts
     * des attaques adverses
     */
    private BooleanProperty estProtegeEffetsAttaques;
    /**
     * La liste des noms des attaques qui deviennent possibles
     * quand les dégats changent
     */
    private ObservableList<String> attaques;
    /**
     * Le dictionnaire des énergies par type d'énergie
     */
    private ObservableMap<String, List<String>> energie;

    public Pokemon(CartePokemon cartePokemon) {
        this.cartePokemon = new SimpleObjectProperty<>(cartePokemon);
        cartes = FXCollections.observableArrayList();
        cartes.add(cartePokemon);
        peutEvoluer = false;
        estBrule = new SimpleBooleanProperty(false);
        estProtegeEffetsAttaques = new SimpleBooleanProperty(false);
        degats = new SimpleIntegerProperty(0);
        pointsDeVie = new SimpleIntegerProperty(0);
        pointsDeVie.bind(Bindings.createIntegerBinding(() -> this.cartePokemon.getValue().getPointsVie() - degats.getValue(), this.cartePokemon, degats));
        attaques = FXCollections.observableArrayList();
        energie = FXCollections.observableHashMap();
        miseAJourAttaquesEtEnergie();
    }

    public CartePokemon getCartePokemon() {
        return cartePokemon.getValue();
    }

    public int getCoutRetraite() {
        return getCartePokemon().getCoutRetraite(this);
    }

    public List<Carte> getCartes() {
        return new ArrayList<>(cartes);
    }

    public boolean getPeutEvoluer() {
        return peutEvoluer;
    }

    public void setEstBrule() {
        estBrule.setValue(true);
    }

    public boolean getEstProtegeEffetsAttaques() {
        return estProtegeEffetsAttaques.getValue();
    }

    public void setEstProtegeEffetsAttaques() {
        estProtegeEffetsAttaques.setValue(true);
    }

    public void ajouterCarte(Carte carte) {
        cartes.add(carte);
    }

    public boolean retirerCarte(Carte carte) {
        return cartes.remove(carte);
    }

    /**
     * Ajoute des dégâts au pokémon.
     * 
     * @param degats les dégâts à ajouter
     */
    public void ajouterDegats(int degats) {
        this.degats.setValue(this.degats.getValue() + degats);
    }

    /**
     * Ajoute des dégâts au pokémon suite à une attaque d'un pokémon adverse.
     * <p>
     * Cette méthode tient compte des faiblesses et résistances du pokémon, et n'est
     * appelée que si le pokémon est le pokémon actif du joueur.
     * 
     * @param degats les dégâts infligés par l'attaque
     * @param type   le type du pokémon attaquant
     */
    public void ajouterDegats(int degats, Type type) {
        if (getCartePokemon().getFaiblesse() == type) {
            ajouterDegats(2 * degats);
        } else if (getCartePokemon().getResistance() == type) {
            ajouterDegats(Math.max(0, degats - 20));
        } else {
            this.degats.setValue(this.degats.getValue() + degats);
        }
    }

    /**
     * Retire les effets de statut du pokémon qui ne sont applicables qu'au pokémon
     * actif.
     */
    public void retirerEffets() {
        estBrule.setValue(false);
        estProtegeEffetsAttaques.setValue(false);
    }

    /**
     * Cette méthode est appelée lorsqu'un pokémon du joueur est mis KO.
     * 
     * @param pokemon le pokémon qui est mis KO
     * @param joueur  le joueur possédant le pokémon
     */
    public void onPokemonKO(Pokemon pokemon, Joueur joueur) {
        getCartePokemon().onPokemonKO(pokemon, joueur);
    }

    /**
     * @return un dictionnaire qui associe à chaque type d'énergie le nombre de
     *         cartes énergie correspondantes attachées au pokémon
     */
    public Map<Type, Integer> getEnergie() {
        Map<Type, Integer> energie = new HashMap<>();
        for (Type type : Type.values()) {
            energie.put(type, 0);
        }

        for (Carte carte : cartes) {
            Type t = carte.getTypeEnergie();
            if (t != null) {
                energie.put(t, energie.get(t) + 1);
            }
        }
        return energie;
    }

    /**
     * Teste si un pokémon a assez d'énergie attachée pour payer un coût donné.
     * <p>
     * Le coût est passé sous la forme d'un dictionnaire. Les types d'énergie autres
     * que {@code INCOLORE} sont comparées directement au nombre de carte de ce type
     * attachées au pokémon. Le coût en énergie {@code INCOLORE} est testé en
     * comptant le nombre total de cartes énergie attachées.
     * 
     * @param coutEnergie le coût à payer, sous la forme d'un dictionnaire associant
     *                    à un type d'énergie le nombre d'énergie de ce type à payer
     * @return {@code true} si le pokémon peut payer le coût, {@code false} sinon
     */
    public boolean peutPayerCout(Map<Type, Integer> coutEnergie) {
        Map<Type, Integer> energieTotale = getEnergie();
        for (Type type : coutEnergie.keySet()) {
            if (type != Type.INCOLORE && energieTotale.get(type) < coutEnergie.get(type)) {
                return false;
            }
        }

        int sommeCoutEnergie = coutEnergie.values().stream().mapToInt(Integer::intValue).sum();
        int sommeEnergieTotale = energieTotale.values().stream().mapToInt(Integer::intValue).sum();
        return sommeEnergieTotale >= sommeCoutEnergie;
    }

    /**
     * @return {@code true} si le pokémon a assez d'énergie attachée pour battre en
     *         retraite
     */
    public boolean peutRetraite() {
        Map<Type, Integer> cout = new HashMap<>();
        cout.put(Type.INCOLORE, getCartePokemon().getCoutRetraite(this));
        return peutPayerCout(cout);
    }

    /**
     * @return la liste des attaques que le pokémon peut utiliser avec les cartes
     *         énergie qui lui sont attachées
     */
    public List<Attaque> getAttaquesPossibles() {
        return getCartePokemon().getAttaques().stream().filter(attaque -> peutPayerCout(attaque.getCoutEnergie()))
                .toList();
    }

    /**
     * @return {@code true} si le pokémon est KO, c'est-à-dire s'il a subi des
     *         dégâts au moins égaux à ses points de vie.
     */
    public boolean estKO() {
        return degats.getValue() >= getCartePokemon().getPointsVie();
    }

    /**
     * Exécute l'attaque passée en paramètre.
     * <p>
     * Prérequis : le pokémon doit avoir assez d'énergie attachée pour payer le coût
     * de l'attaque.
     * 
     * @param nomAttaque nom de l'attaque à exécuter
     * @param joueur     le joueur qui exécute l'attaque
     */
    public void utiliserAttaque(String nomAttaque, Joueur joueur) {
        Attaque attaque = getCartePokemon().getAttaques().stream().filter(a -> a.getNom().equals(nomAttaque)).findFirst()
                .orElseThrow();
        attaque.attaquer(joueur);
    }

    /**
     * Évolue le pokémon en un nouveau pokémon.
     * <p>
     * Prérequis : la carte pokémon passé en argument doit être une évolution de la
     * carte pokémon actuelle.
     * 
     * @param cartePokemon le nouveau pokémon qui doit remplacer la carte Pokémon
     *                     actuelle
     */
    public void evoluer(CartePokemon cartePokemon) {
        this.cartePokemon.setValue(cartePokemon);
        cartes.add(cartePokemon);
        peutEvoluer = false;
        retirerEffets();
    }

    /**
     * Cette méthode est appelée au début du tour du joueur.
     * 
     * @param joueur joueur qui possède le pokémon
     */
    public void onDebutTour(Joueur joueur) {
        estProtegeEffetsAttaques.setValue(false);
    }

    /**
     * Cette méthode est appelée à la fin du tour du joueur.
     * 
     * @param joueur joueur qui possède le pokémon
     */
    public void onFinTour(Joueur joueur) {
        peutEvoluer = true;
        getCartePokemon().onFinTour(joueur);
    }

    /**
     * @return {@code true} si le pokémon peut utiliser le talent de sa carte
     *         pokémon
     */
    public boolean peutUtiliserTalent() {
        return getCartePokemon().peutUtiliserTalent();
    }

    /**
     * Exécute l'action du talent de la carte pokémon.
     * 
     * @param joueur joueur qui possède le pokémon
     */
    public void utiliserTalent(Joueur joueur) {
        getCartePokemon().utiliserTalent(joueur);
    }

    /**
     * Exécute la phase de contrôle sur un pokémon
     */
    public void controlePokemon(Joueur joueur) {
        if (estBrule.getValue()) {
            ajouterDegats(20);
            if (joueur.lancerPiece()) {
                estBrule.setValue(false);
            }
        }
    }

    /**
     * Mise à jour des propriétés attaques et énergie du pokemon
     * lorsqu'on lui ajoute de nouvelles cartes
     */
    private void miseAJourAttaquesEtEnergie() {
        cartes.addListener((ListChangeListener<Carte>) nouvelleCarte -> {
            List<String> nomsAttaquesPossibles = getAttaquesPossibles().stream()
                    .map(Attaque::getNom)
                    .toList();
            attaques = FXCollections.observableArrayList(nomsAttaquesPossibles);
            Map<String, List<String>> cartesEnergie = cartes.stream()
                    .filter(c -> c.getTypeEnergie() != null)
                    .collect(Collectors.groupingBy(
                            c -> c.getTypeEnergie().asLetter(),
                            Collectors.mapping(Carte::getId, Collectors.toList())
                    ));
            energie = FXCollections.observableMap(cartesEnergie);
        });
    }

    /**
     * Propriétés exportées à utiliser dans l'IHM
     */
    @Override
    public ObjectProperty<CartePokemon> cartePokemonProperty() {
        return cartePokemon;
    }

    @Override
    public ObservableList<Carte> cartesProperty() {
        return cartes;
    }

    @Override
    public IntegerProperty pointsDeVieProperty() {
        return pointsDeVie;
    }

    @Override
    public ObservableList<String> attaquesProperty() {
        return attaques;
    }

    @Override
    public ObservableMap<String, List<String>> energieProperty() {
        return energie;
    }

    @Override
    public BooleanProperty estBruleProperty() {
        return estBrule;
    }

    @Override
    public BooleanProperty estProtegeEffetsAttaquesProperty() {
        return estProtegeEffetsAttaques;
    }
}
