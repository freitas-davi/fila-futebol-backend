package pilantras.domain.enums;

public enum Categoria {

    A, B, C, D;

    public int peso() {
        return switch (this) {
            case A -> 4;
            case B -> 3;
            case C -> 2;
            case D -> 1;
        };
    }
}
