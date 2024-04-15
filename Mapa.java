import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa {
    private static List<String> mapa;
    private Map<Character, ElementoMapa> elementos;
    private int x = 1; // Posição inicial X do personagem
    private int y = 1; // Posição inicial Y do personagem
    private final int TAMANHO_CELULA = 10; // Tamanho de cada célula do mapa
    private boolean[][] areaRevelada; // Rastreia quais partes do mapa foram reveladas
    private final Color brickColor = new Color(153, 76, 0); // Cor marrom para tijolos
    private final Color vegetationColor = new Color(34, 139, 34); // Cor verde para vegetação
    private final Color corVermelha = new Color(255, 0, 0); 
    private final Color corVerde = new Color(0, 255, 0);
    private final Color corMarrom = new Color(139, 69, 19);
    private final Color corDourada = new Color(255, 215, 0);

    private final int RAIO_VISAO = 5; // Raio de visão do personagem
    private List<MapObject> listaBaus = new ArrayList<>();
    private List<MapObject> listaVilao = new ArrayList<>();

    public Mapa(String arquivoMapa) {
        mapa = new ArrayList<>();
        elementos = new HashMap<>();
        registraElementos();
        carregaMapa(arquivoMapa);
        armazenaElementos();
        areaRevelada = new boolean[mapa.size()+1000][mapa.get(0).length()+1000];
        atualizaCelulasReveladas();
    }
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getTamanhoCelula() {
        return TAMANHO_CELULA;
    }
    
    public int getNumLinhas() {
        return mapa.size();
    }
    
    public int getNumColunas() {
        return mapa.get(0).length();
    }
    
    public ElementoMapa getElemento(int x, int y) {
        Character id = mapa.get(y).charAt(x);
        return elementos.get(id);
    }
    
    public boolean estaRevelado(int x, int y) {
        return areaRevelada[y][x];
    }
    
    // Move conforme enum Direcao
    public boolean move(Direcao direcao) {
        int dx = 0, dy = 0;
        System.out.println(x);
        System.out.println(y);
        switch (direcao) {
            case CIMA:
            dy = -TAMANHO_CELULA;
            break;
            case BAIXO:
            dy = TAMANHO_CELULA;
            break;
            case ESQUERDA:
            dx = -TAMANHO_CELULA;
            break;
            case DIREITA:
            dx = TAMANHO_CELULA;
            break;
            default:
            return false;
        }
        
        if (!podeMover(x + dx, y + dy)) {
            System.out.println("Não pode mover");
            return false;
        }
        
        x += dx;
        y += dy;
        
        // Atualiza as células reveladas
        atualizaCelulasReveladas();
        return true;
    }
    
    public void armazenaElementos() {
        for (int i = 0; i < mapa.size(); i++) {
            String linha = mapa.get(i);
            for (int j = 0; j < linha.length(); j++) {
                char caractere = linha.charAt(j);                
                if (caractere == 'B') {
                    listaBaus.add(new MapObject('B', j, i));
                }
                if (caractere == 'M') {
                    listaVilao.add(new MapObject('M', j, i));
                }
            }
        }
    }

    // Verifica se o personagem pode se mover para a próxima posição
    private boolean podeMover(int nextX, int nextY) {
        int mapX = nextX / TAMANHO_CELULA;
        int mapY = nextY / TAMANHO_CELULA - 1;
        
        if (mapa == null)
        return false;
        
        if (mapX >= 0 && mapX < mapa.get(0).length() && mapY >= 1 && mapY <= mapa.size()) {
            char id;
            
            try {
                id = mapa.get(mapY).charAt(mapX);
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }
            
            if (id == ' ')
            return true;
            
            ElementoMapa elemento = elementos.get(id);
            if (elemento != null) {
                //System.out.println("Elemento: " + elemento.getSimbolo() + " " + elemento.getCor());
                return elemento.podeSerAtravessado();
            }
        }

        return false;
    }


    public String interage() {
        if(achaBau()){
            elementos.put('B', new Tesouro('B', corDourada));
            return "Parabens achou o bau"; 
        }
        return("não achou algo para interagir");

    }
    public boolean achavilao(){
        for(int i = 0; i < listaVilao.size(); i++){
            MapObject objetoVilao = listaVilao.get(i);
            System.out.println(objetoVilao);
            System.out.println(objetoVilao.getX());
            System.out.println(objetoVilao.getY());
            System.out.println(getX() + " " + getY());
            if(calculaDistancia(getX(), objetoVilao.getX() * 10, getY(), objetoVilao.getY() * 10) <= 20.0){
                return true;
            }
            System.out.println(calculaDistancia(getX(), getY(), objetoVilao.getX() * 10, objetoVilao.getY() * 10));
        }
        return false;
    }
    public boolean achaBau(){
        for(int i = 0; i < listaBaus.size(); i++){
            MapObject objetoBau = listaBaus.get(i);
            if(calculaDistancia(getX(), objetoBau.getX() * 10, getY(), objetoBau.getY() * 10) <= 20.0){
                return true;
            }
            // if(getX() == objetoBau.getX() * 10 && getY() == objetoBau.getY() * 10){
            //     System.out.println("teste");
            //     return true;
            // }
        }
        return false;
    }
    public double calculaDistancia(int x1, int x2, int y1, int y2){
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public String ataca() {
        if(achavilao()){
            elementos.put('M', new Vilao('X', corVerde));
            return "Parabens voce matou o vilão";
        }
        return("não tem nenhum vilão por perto");

    }

    private void carregaMapa(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                mapa.add(line);
                // Se character 'P' está contido na linha atual, então define a posição inicial do personagem
                if (line.contains("P")) {
                    x = line.indexOf('P') * TAMANHO_CELULA;
                    y = mapa.size() * TAMANHO_CELULA;
                    // Remove o personagem da linha para evitar que seja desenhado
                    mapa.set(mapa.size() - 1, line.replace('P', ' '));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar as células reveladas
    private void atualizaCelulasReveladas() {
        if (mapa == null)
            return;
        for (int i = Math.max(0, y / TAMANHO_CELULA - RAIO_VISAO); i < Math.min(mapa.size(), y / TAMANHO_CELULA + RAIO_VISAO + 1); i++) {
            for (int j = Math.max(0, x / TAMANHO_CELULA - RAIO_VISAO); j < Math.min(mapa.get(i).length(), x / TAMANHO_CELULA + RAIO_VISAO + 1); j++) {
                areaRevelada[i][j] = true;
            }
        }
    }

    // Registra os elementos do mapa
    public void registraElementos() {
        // Parede
        elementos.put('#', new Parede('▣', brickColor));
        elementos.put('V', new Vegetacao('♣', vegetationColor));
        elementos.put('M', new Vilao('⊄', corVermelha));
        elementos.put('Y', new Vilao(' ', corVermelha));
        elementos.put('T', new Portal('#', corVermelha));
        elementos.put('B', new Tesouro('B', corMarrom));        
    }
    public void alternarViloes() {
        for (int i = 0; i < mapa.size(); i++) {
            String linha = mapa.get(i);
            StringBuilder novaLinha = new StringBuilder();
            for (int j = 0; j < linha.length(); j++) {
                char caractere = linha.charAt(j);
                if (caractere == 'M') {
                    novaLinha.append('Y');
                } else if (caractere == 'Y') {
                    novaLinha.append('M');
                } else {
                    novaLinha.append(caractere);
                }
            }
            mapa.set(i, novaLinha.toString());
        }
    }

    
}
