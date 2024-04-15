import java.util.ArrayList;
import java.util.List;

// Classe para representar um objeto no mapa
class MapObject {
    private char symbol;
    private int x;
    private int y;

    public MapObject(char symbol, int x, int y) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
    }

    // Getters e Setters
    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}