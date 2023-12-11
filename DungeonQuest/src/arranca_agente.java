package DungeonQuest.src;

public class arranca_agente {
    static Ajr nuevo_agente; // Sera el objeto agente (Ajr) asociado a este proyecto

    public static void main(String[] args) {

        //Ej.: para arrancar el proceso :

        //Para un AJR monitor
        // PARÁMETROS: "nombre_agente_monitor" 0 "DIOS" "ip monitor" "puerto monitor"

        //Para un AJR AVENTURERO
        // PARÁMETROS: "nombre_agente" 0 "AVENTURERO" "ip monitor" "puerto monitor"

        //Codigo ejecución agente
        nuevo_agente = new Ajr(args[0], args[1], args[2], args[3], args[4]);

    }
}
