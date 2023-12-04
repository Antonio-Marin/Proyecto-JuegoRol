package DungeonQuest.src;

public class arranca_agente {
    static Ajr nuevo_agente; // Sera el objeto agente (Ajr) asociado a este proyecto

    public static void main(String[] args) {

        //Codigo ejecución agente
        nuevo_agente = new Ajr(args[0], args[1], args[2], args[3], args[4]);

    }
}
