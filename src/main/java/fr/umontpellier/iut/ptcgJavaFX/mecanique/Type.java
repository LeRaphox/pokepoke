package fr.umontpellier.iut.ptcgJavaFX.mecanique;

public enum Type {
    PLANTE("Plante"),
    FEU("Feu"),
    EAU("Eau"), 
    ELECTRIQUE("Électrique"),
    PSY("Psy"),
    COMBAT("Combat"),
    OBSCURITE("Obscurité"),
    METAL("Métal"),
    FEE("Fée"),
    DRAGON("Dragon"),
    INCOLORE("Incolore");

    private final String nom;

    Type(String nom) {
        this.nom = nom;
    }

    public String asLetter() {
        return switch (this) {
            case PLANTE -> "G";
            case FEU -> "R";
            case EAU -> "W";
            case ELECTRIQUE -> "L";
            case PSY -> "P";
            case COMBAT -> "F";
            case OBSCURITE -> "D";
            case METAL -> "M";
            case FEE -> "Y";
            case DRAGON -> "N";
            case INCOLORE -> "C";
        };
    }

}
