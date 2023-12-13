package DungeonQuest.src;

public class Aventurero {
    String ID_propio;
    String IP_propio;
    int Puerto_Propio_UDP;
    String clase;
    int nivel;
    int monstruosDerrotados;
    public Aventurero(String ID_propio, String IP_propio, int Puerto_Propio_UDP, String clase , int nivel, int monstruosDerrotados){
        this.ID_propio=ID_propio;
        this.IP_propio = IP_propio;
        this.Puerto_Propio_UDP=Puerto_Propio_UDP;
        this.clase=clase;
        this.nivel=nivel;
        this.monstruosDerrotados=monstruosDerrotados;
    }

    public String getIP_propio() {
        return IP_propio;
    }

    public String getID_propio() {
        return ID_propio;
    }

    public int getPuerto_Propio_UDP() {
        return Puerto_Propio_UDP;
    }

    public String getClase() {
        return clase;
    }

    public int getNivel() {
        return nivel;
    }

    public int getMonstruosDerrotados() {
        return monstruosDerrotados;
    }
}
