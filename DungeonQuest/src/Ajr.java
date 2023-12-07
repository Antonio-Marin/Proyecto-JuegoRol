package DungeonQuest.src;

import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Ajr {

    // //////////////////////////////////////
    // //////////////////////////////////////
    // DATOS GLOBALES

    // //////////////////////////////////////
    // Datos del agente
    protected String ID_propio; // Identificador unico de este agente
    protected String Ip_Propia;  // Ip donde reside este agente
    protected int Puerto_Propio_TCP;  // Es el puerto de servidor TCP del agente (coincide con el puerto asociado al agente)
    protected int Puerto_Propio_UDP;  // Es el puerto de servidor UDP del agente (es el siguiente a "Puerto_Propio" osea - Puerto_Propio_UDP = Puerto_Propio+1)
    protected long Tiempo_de_nacimiento;  // La hora del sistema de esta maquina en la que se genera el agente
    protected tipos_de_agentes tipo_agente;  // Para indicar si es cambiacromos o Dios
    protected enum tipos_de_agentes
    {
        AVENTURERO, DIOS
    }

    // //////////////////////////////////////
    // Datos del entorno de ejecución
    protected String Ip_Dios;  // Es la IP donde reside el Dios (es la misma para todos los agentes del SMA)
    protected int Puerto_Dios;  // Es el puerto donde reside el Dios (es la misma para todos los agentes del SMA)
    protected int Puerto_Dios_TCP;  // Es el puerto de servidor TCP del agente Dios (es el mismo que "Puerto_Dios" y es la misma para todos los agentes del SMA)
    protected int Puerto_Dios_UDP;  // Es el puerto de servidor UDP del agente Dios (es el mismo que "Puerto_Dios+1" y es la misma para todos los agentes del SMA)
    protected String Inicio_rango_IPs; // Para indicar el inicio del rango de IPs donde este agente podrá buscar otros agentes
    protected int Rango_IPs; // Se suma a "Inicio_rango_IPs" para definir la ultima IP del rango donde este agente podrá buscar otros agentes
    protected int Puerto_Inicio; // Para indicar el inicio del rango de puertos donde este agente podrá buscar otros agentes, o buscar donde anidar
    protected int Rango_Puertos; // Se suma a "Puerto_Inicio" para definir el ultimo puerto del rango donde este agente podrá buscar otros agentes, o buscar donde anidar
    protected String localizacion_codigo;
    protected long tiempo_espera_fin_env; // Es el tiempo maximo que esperaremos para enviar los mensajen pendientes en la cola de envios, antes de finalizar el agente
                                        // si transcurrido esta cantidad de milisegundos sigue habiendo mensajes por enviar en la cola de envios, el agente se cierra y estos
                                        // quedaran sin enviar
    protected FuncionDios funcionDios;  // Sera un hilo de ejecución

    // //////////////////////////////////////
    // Datos para ingeniería social
    protected ComportamientoBase comportamientoBase ;  // Sera un hilo de ejecución
    protected enum Estado_del_AJR
    {
        VIVO, MUERTO
    }
    protected Estado_del_AJR Estado_Actual;
    protected long Tiempo_de_vida; // Definimos aqui en milisegundos el tiempo que el proceso del agente estara activo antes de terminarse

    protected int Num_generacion; // Un agente que se arranca en una maquina genera procesos hijos y estos generan procesos nietos, este numero
                                    // indica a que generación correspondeeste agente como descendiente del agente inicial
    protected int Num_max_de_generaciones; // Los agentes de este nivel de generaciones, no generaran agente hijos
    protected int Num_hijos_generados; // Define el numero de descendientes que este agente ha generado (en primera generación)
    protected int Num_max_hijos_generados; // Define el numero maximo de descendientes de primera generación. que este agente ùede generar
    protected double Frecuencia_partos;  // Para manejar la velocidad en la que el agente se reproduce

    protected double Frecuencia_rastreo_puertos; // Para manejar la velocidad en la que el agente busca otros agentes

    // //////////////////////////////////////
    // //////////////////////////////////////
    // Datos de la función del agente
    protected FuncionDeAventurero funcionAventurero;  // Sera un hilo de ejecución

    // //////////////////////////////////////
    // //////////////////////////////////////
    // Datos del sistema de comunicaciones
    protected Enviar enviar;  // Sera un hlo de ejecución
    protected RecibeUdp recibeUdp;  // Sera un hilo de ejecución

    protected ServerSocket servidor_TCP;  // Puerto para el servicio por TCP
    protected DatagramSocket servidor_UDP;  // Puerto para el servicio por UDP

    private LinkedList<AjrLocalizado> directorio_de_agentes = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes para enviar por un agente
    private  int num_tot_ajr_loc;  // Numero total de agentes localizados
    private LinkedList<Mensaje> contenedor_de_mensajes_a_enviar = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes para enviar por un agente
    private  int num_tot_men_env;  // Numero total de mensajes enviados por el agente
    private LinkedList<Mensaje> contenedor_de_mensajes_recibidos = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes recibidos por un agente
    private  int num_tot_men_rec;  // Numero total de mensajes recibidos por el agente
    private  int num_id_local_men;  // Este numero, junto con el identificador del agente, generan un codigo unico de mensaje

    //TODO: Mirar ACC y añadir más código

    public Ajr (String id_propio, String este_num_generacion_str, String este_tipo_agente, String este_Ip_Dios, String este_Puerto_Monitor){
        long pid = obtenerPID();
        System.out.println("\n ========================================================================================" +
                "\n> =========================== INICIO AGENTE ==============================================" +
                "\n> ========================================================================================" +
                " \n ID_propio : "+ ID_propio+
                " Num generacion : "+ este_num_generacion_str+
                " Tipo agente : "+ este_tipo_agente+
                " Ip Dios : "+ este_Ip_Dios +
                " Puerto Monitor : "+ este_Puerto_Monitor+
                "\n> ========================= PID proceso : "+pid+" ======================================" +
                "\n> ============ Para matar el proceso : taskkill /PID "+pid+" /F  ========================" +
                "\n> ========================================================================================");

        generaConfiguracionInicial(ID_propio, este_num_generacion_str, este_tipo_agente, este_Ip_Dios, este_Puerto_Monitor);
        if(this.tipo_agente == tipos_de_agentes.AVENTURERO)
        {
            // Para el agente CAMBIACROMOS tenemos que buscar los puertos donde albergar el agente
            buscaNido();
        }
        else if (this.tipo_agente == tipos_de_agentes.DIOS) {
            // Para el agente DIOS los puertos vienen fijados al generar el agente
            //this.Puerto_Propio_TCP = this.Puerto_Dios;
            this.Puerto_Propio_UDP = this.Puerto_Propio_TCP + 1;

            // Generamos los sockets de TCP y UDP en el monitor, ya que este sabe cuales son sus puerton y no usa "buscaNido()" para localizarse
            try {
                //servidor_TCP = new ServerSocket(Puerto_Propio_TCP);
                servidor_UDP = new DatagramSocket(Puerto_Propio_UDP);
            } catch (Exception e) {
                System.out.println("\n ==> ERROR. Desde Ajr al abrir los puertos de comunicaciones con Puerto_Propio : " + Puerto_Propio_TCP +
                       // " - con Puerto_Propio_TCP : " + Puerto_Propio_TCP +
                        " - con Puerto_Propio_UDP : " + Puerto_Propio_UDP +
                        " - en el MONITOR");
            }
        }
        else
        {
            System.out.println("Desde public Ajr. ERROR al procesar el tipo de agente al buscar nido");
        }
        this.recibeUdp = new RecibeUdp(this);
        this.enviar = new Enviar(this);
        this.comportamientoBase = new ComportamientoBase(this);

        if(this.tipo_agente == tipos_de_agentes.AVENTURERO)
        {
            // Para el agente CAMBIACROMOS arrancamos su funcion del agente y notificamos al monitor su nacimiento
            this.funcionAventurero = new FuncionDeAventurero(this);
            notificaNacimiento();
            menuInicial();
        }
        else if (this.tipo_agente == tipos_de_agentes.DIOS)
        {
            // Para el agente MONITOR tan solo arrancamos su funcion del agente monitor
            this.funcionDios = new FuncionDios(this);
            // EL monitor no se notifica su propio nacimiento
        }
        else
        {
            System.out.println("Desde public Acc. ERROR al procesar el tipo de agente al ir a notificar el nacimiento");
        }

        this.Estado_Actual = Estado_del_AJR.VIVO;
    }


    protected void generaConfiguracionInicial(String ID_propio, String este_Num_generacion_str, String este_tipo_agente, String este_Ip_Monitor, String este_Puerto_Monitor) {

        // //////////////////////////////////////
        // Definiendo datos del agente
        this.ID_propio = ID_propio;
        try {
            this.Ip_Propia = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Desde public Ajr. => generaConfiguracionInicial. ERROR al adquirir la Ip_Propia, da un valor : " + Ip_Propia);
            throw new RuntimeException(e);
        }

        if (este_tipo_agente.equals("DIOS")) {
            this.tipo_agente = tipos_de_agentes.DIOS;
        } else if (este_tipo_agente.equals("AVENTURERO")) {
            this.tipo_agente = tipos_de_agentes.AVENTURERO;
        } else {
            System.out.println("Desde public Ajr. ERROR definiendo tipo_agente : " + este_tipo_agente + " - no es un tipo de agente conocido");
        }

        // //////////////////////////////////////
        // Definiendo Datos del entorno de ejecución
        if (este_tipo_agente.equals("DIOS")) {
            this.Ip_Dios = this.Ip_Propia; // Si el agente es el monitor su ip y la del monitor son la misma evidentemente
        } else if (este_tipo_agente.equals("AVENTURERO")) {
            this.Ip_Propia = este_Ip_Monitor;
        } else {
            System.out.println("Desde public Ajr. ERROR Definiendo DATOS DEL ENTORNO : " + este_tipo_agente + " - no es un tipo de agente conocido");
        }
        this.Puerto_Dios = Integer.parseInt(este_Puerto_Monitor);  // El puerto de monitor se define como parametro de llamada al proceso
        //this.Puerto_Monitor_TCP = this.Puerto_Monitor;  // Es el puerto de servidor TCP del agente monitor (es el mismo que "Puerto_Monitor" y es la misma para todos los agentes del SMA)
        this.Puerto_Dios_UDP = this.Puerto_Dios + 1;  // Es el puerto de servidor UDP del agente monitor (es el mismo que "Puerto_Monitor+1" y es la misma para todos los agentes del SMA)

        this.Inicio_rango_IPs = Ip_Propia;  // Solo para pruebas
        this.Rango_IPs = 0;
        this.Puerto_Inicio = 50000;
        this.Rango_Puertos = 10000;
        this.localizacion_codigo = "C:/Users/pablo/IdeaProjects/SMA_23-24/base/out/production/base"; //cambia segun quien lo ejecute
        /*
        Localización código:
        Pablo: C:/Users/pablo/IdeaProjects/SMA_23-24/base/out/production/base
        Antonio: C:/Users/marti/IdeaProjects/SMA_23-24/base/out/production/base
         */
        this.tiempo_espera_fin_env = 1000 * 1; // Es el tiempo (milisegundos) que esperaremos para enviar los mensajen pendientes en la cola de envios, antes de finalizar el agente

        // //////////////////////////////////////
        // Definiendo  Datos para ingeniería social
        this.Estado_Actual = Estado_del_AJR.VIVO;
        this.Tiempo_de_nacimiento = System.currentTimeMillis();

        //ToDo Pensar si quitar el tiempo de vida

        if (este_tipo_agente.equals("DIOS")) {
            this.Tiempo_de_vida = 1000 * 100;  // Lo es en milisegundos
        } else if (este_tipo_agente.equals("AVENTURERO")) {
            this.Tiempo_de_vida = 1000 * 50;  // Lo es en milisegundos
        } else {
            System.out.println("Desde public Acc. ERROR definiendo Tiempo_de_vida : " + este_tipo_agente + " - no es un tipo de agente conocido");
        }
        int este_Num_generacion = Integer.parseInt(este_Num_generacion_str);
        this.Num_generacion = este_Num_generacion;
        this.Num_max_de_generaciones = 0; //CAMBIADO a 0
        this.Num_hijos_generados = 0; // Por ahora el agente no ha generado ningún descendiente
        this.Num_max_hijos_generados = 0; // CAMBIADO a 0 | el agente no debe superar este numero de descendientes en primera generacion (en principio arbitrario)
        this.Frecuencia_partos = 0.00; //CAMBIADO a 0.00 | anteriormente valia 0.01

        this.Frecuencia_rastreo_puertos = 0.00001f;

        // //////////////////////////////////////
        // Definimos datos del sistema de comunicaciones
        this.num_tot_ajr_loc = 0;  // Numero total de agentes localizados
        this.num_tot_men_env = 0;  // Numero total de mensajes enviados por el agente
        this.num_tot_men_rec = 0;  // Numero total de mensajes recibidos por el agente
        this.num_id_local_men = 0;  // Cada vez que se solicita, se incrementa en uno, para generar un codigo local unico para los mensajes (ver dame_codigo_id_local_men())
    }

    /**
     * función notificaNacimiento()
     * Notificamos el inicio de la partida al Dios. Para ello envía un mensaje al Dios.
     */

    //ToDo Revisar tooooda la clase --> Antes hay que hacer la clase Mensaje bien y definir el esquema
    protected void notificaNacimiento() {

        // Por ahora solo es una función prototipo
        // Construimos el mensaje

        String ID_mensaje = dame_codigo_id_local_men();
        String momento_actual = String.valueOf(System.currentTimeMillis());
        String Puerto_Propio_str = String.valueOf(Puerto_Propio_TCP);
        String Puerto_Monitor_UDP_str = String.valueOf(Puerto_Monitor_UDP);
        String cuerpo_mens = "Esto es el MENSAJE HE NACIDO  - que el agente con ID_propio : " + ID_propio +
                " - con ip : " + Ip_Propia +
                " - con Puerto_Propio : " + Puerto_Propio_str +
                " - con ID_mensaje : " + ID_mensaje +
                " - envia al monitor con Ip_Monitor : "+Ip_Monitor+
                " - con Puerto_Monitor : "+Puerto_Monitor_UDP_str+
                " :  - en T : " + momento_actual;

        Mensaje mensaje_he_nacido = new Mensaje("1",
                "1", "1", "0", "UDP",
                ID_propio, Ip_Propia, Integer.toString(Puerto_Propio_TCP+1), Puerto_Propio_str, momento_actual,
                "ID_Monitor", Ip_Monitor, Puerto_Monitor_UDP_str, Integer.toString(Puerto_Monitor_TCP), momento_actual);
        mensaje_he_nacido.setBodyInfo(cuerpo_mens);
        mensaje_he_nacido.setDeathReason("0");
        ArrayList<String> e = new ArrayList();
        e.add("0");
        mensaje_he_nacido.setOwnedCardCost(e);
        mensaje_he_nacido.setOwnedCardQuantity(e);
        mensaje_he_nacido.setOwnedCardType(e);
        mensaje_he_nacido.setWantedCardType(e);
        mensaje_he_nacido.setOwnedMoney("0");
        mensaje_he_nacido.setCreatedChilds(String.valueOf(this.Num_hijos_generados));
        mensaje_he_nacido.setDeathTime("0");
        mensaje_he_nacido.setPastTradeWantedCard("-");
        mensaje_he_nacido.setPastTradeGivenCard("-");
        mensaje_he_nacido.setTradeWantedCard("-");
        mensaje_he_nacido.setTradeGivenCard("-");
        mensaje_he_nacido.setOfferedCardType(e);
        mensaje_he_nacido.setOfferedCardCost(e);
        mensaje_he_nacido.setOfferedCardQuantity(e);
        mensaje_he_nacido.setWishedCardType(e);
        mensaje_he_nacido.setTradeMoney("0");
        ArrayList<AccTest> h = new ArrayList<>();
        AccLocalizado ej = new AccLocalizado("id", "ip", 10000000,15550005 );
        directorio_de_agentes.add(ej);
        mensaje_he_nacido.setAgentsDirectory(this.directorio_de_agentes);
        mensaje_he_nacido.setDeadAgents(this.directorio_de_agentes);


        // Insertamos el mensaje
        pon_en_lita_enviar(mensaje_he_nacido);

        String Num_generacion_str = String.valueOf(this.Num_generacion);
        String Tiempo_de_nacimiento_str = String.valueOf(this.Tiempo_de_nacimiento);
        System.out.println("\n ==> Ha nacido un agente en la IP = "+Ip_Propia+
                " - con ID_propio :" + this.ID_propio +
                " - en el puerto :" + this.Puerto_Propio_TCP +
                " - Su generación es :" + Num_generacion_str +
                " - t de generación :" + Tiempo_de_nacimiento_str);
    }

    /**
     * Función finalizaAgente()
     * Notificamos el fin de la partida al Dios. Para ello envía un mensaje al Dios.
     */

    //ToDo hacer como en nacimiento

    protected void finalizaAgente() {
        Estado_Actual = Estado_del_AJR.MUERTO;

/////////////////////////////////////////////////////////
        // Notificamos al monitor que este agente ha finalizadO
        if (tipo_agente == tipos_de_agentes.AVENTURERO) {
            String ID_mensaje = dame_codigo_id_local_men();
            long momento_actual = System.currentTimeMillis();
            String momento_actual_str = String.valueOf(System.currentTimeMillis());
            String tiempo_vivido = String.valueOf(System.currentTimeMillis() - Tiempo_de_nacimiento);
            String Puerto_Propio_str = String.valueOf(Puerto_Propio_TCP);
            String Puerto_Monitor_TCP_str = String.valueOf(Puerto_Monitor_TCP);
            String cuerpo_mens_fin_agente = "Esto es el MENSAJE FIN DE AGENTE  - que el agente con ID_propio : " + ID_propio +
                    " - con ip : " + Ip_Propia +
                    " - con Puerto_Propio : " + Puerto_Propio_str +
                    " - con ID_mensaje : " + ID_mensaje +
                    " - envia al monitor con Ip_Monitor : " + Ip_Monitor +
                    " - con Puerto_Monitor : " + Puerto_Monitor_TCP_str +
                    " - en T : " + momento_actual_str +
                    " - con T de vida : " + Tiempo_de_vida +
                    " - con T vivido : " + tiempo_vivido;

            //TODO: mensaje muerte revisar
            Mensaje mensaje_fin_agente = new Mensaje("2",
                    "2", "2", "0", "UDP",
                    ID_propio, Ip_Propia, Integer.toString(Puerto_Propio_TCP + 1), Puerto_Propio_str, momento_actual_str,
                    "ID_Monitor", Ip_Monitor, Integer.toString(Puerto_Monitor_TCP + 1), Integer.toString(Puerto_Monitor_TCP), momento_actual_str);
            mensaje_fin_agente.setBodyInfo(cuerpo_mens_fin_agente);
            mensaje_fin_agente.setDeathReason("2");
            ArrayList<String> e = new ArrayList();
            e.add("0");
            mensaje_fin_agente.setOwnedCardCost(e);
            mensaje_fin_agente.setOwnedCardQuantity(e);
            mensaje_fin_agente.setOwnedCardType(e);
            mensaje_fin_agente.setWantedCardType(e);
            mensaje_fin_agente.setOwnedMoney("0");
            mensaje_fin_agente.setCreatedChilds(String.valueOf(this.Num_hijos_generados));
            mensaje_fin_agente.setDeathTime(String.valueOf(this.Tiempo_de_vida));
            mensaje_fin_agente.setPastTradeWantedCard("-");
            mensaje_fin_agente.setPastTradeGivenCard("-");
            mensaje_fin_agente.setTradeWantedCard("-");
            mensaje_fin_agente.setTradeGivenCard("-");
            mensaje_fin_agente.setOfferedCardType(e);
            mensaje_fin_agente.setOfferedCardCost(e);
            mensaje_fin_agente.setOfferedCardQuantity(e);
            mensaje_fin_agente.setWishedCardType(e);
            mensaje_fin_agente.setTradeMoney("0");
            ArrayList<AccTest> h = new ArrayList<>();
            //AccLocalizado ej = new AccLocalizado("id", "ip", 10000000,15550005 );
            //directorio_de_agentes.add(ej);
            mensaje_fin_agente.setAgentsDirectory(this.directorio_de_agentes);
            mensaje_fin_agente.setDeadAgents(this.directorio_de_agentes);

            // Insertamos el mensaje
            pon_en_lita_enviar(mensaje_fin_agente);

            System.out.println("\n ==> NOTIFICACION LOCAL de FIN DE AGENTE - \n " + cuerpo_mens_fin_agente);
        } else {
            System.out.println("\n ==> NOTIFICACION LOCAL de FIN DE AGENTE: " + ID_propio);
        }
        // ///////////////////////////////////////////////////////
        // Nos vamos
        //  - Antes de cerrar los sockets, esperamos a que todos los mensajes esten enviados. Los
        //       recibidos, como estamos muertos no nos importan

        boolean espera_fin_envios = true;
        while (espera_fin_envios) {
            //System.out.println(num_elem_lita_enviar());
            if ((num_elem_lita_enviar() <= 0)) {
                espera_fin_envios = false;
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        // Dejamos la casa como estaba
        cerrarSockets();

        System.out.println("\n ==> Finalizamos el agente con : \n - men pendientes de envio : " + num_elem_lita_enviar() + " - Total enviados : " + num_tot_men_env +
                "\n - men en cola recibidos : " + num_elem_lita_recibidos() + " - Total recibidos : " + num_tot_men_rec);
        System.out.println("\n ========================================================================================" +
                "\n> ========================================================================================" +
                "\n> ==============================   FIN DE AGENTE  ========================================" +
                "\n                              ID_propio : " + ID_propio +
                "\n> ========================================================================================" +
                "\n> ========================================================================================");

        // Terminamos el proceso
        System.exit(0);     // Parar el agente
    }

    /**
     * Función cerrarSockets()
     * Cierra los sockets antes de terminar
     * @return
     */

    void cerrarSockets() {

        // Cerramos el socket UDP
        try {
            recibeUdp.servidor_UDP.close();
        } catch (Exception e) {
            System.out.println("Desde Acc - cerrarSockets : Problemas al cerrar el socket");
        }
    }

    /**
     * Función obtenerPid()
     * Obtenemos el PID
     * @return
     */

    public static long obtenerPID() {
        String nombreGestion = ManagementFactory.getRuntimeMXBean().getName();
        // El nombre de gestión tiene el formato "pid@hostname"
        String[] partes = nombreGestion.split("@");

        if (partes.length > 0) {
            try {
                return Long.parseLong(partes[0]);
            } catch (NumberFormatException e) {
                // Manejar la excepción si no se puede convertir a un número
                e.printStackTrace();
            }
        }

        // Si no se pudo obtener el PID, devolver un valor predeterminado
        return -1;
    }

    /**
     * Función menuInicial()
     * Será el menú inicial del Aventurero.
     */


    /**
     * Funciones para sincronizar información
     */

    protected void pon_en_lita_enviar(Mensaje este_mensaje) {contenedor_de_mensajes_a_enviar.add(este_mensaje); num_tot_men_env++; }
    protected void pon_en_lita_recibidos(Mensaje este_mensaje) {contenedor_de_mensajes_recibidos.add(este_mensaje);num_tot_men_rec++; }
    protected void pon_en_directorio_de_agentes(AjrLocalizado este_accLocalizado) {directorio_de_agentes.add(este_accLocalizado); num_tot_ajr_loc++; }

    protected int num_elem_lita_enviar() {int num_elem1 = contenedor_de_mensajes_a_enviar.size(); return num_elem1;}
    protected int num_elem_lita_recibidos() {int num_elem2 = contenedor_de_mensajes_recibidos.size(); return num_elem2;}
    protected int num_elem_directorio_de_agentes() {int num_elem3 = directorio_de_agentes.size(); return num_elem3;}

    protected int dime_num_tot_men_env() {return num_tot_men_env;}
    protected int dime_num_tot_men_rec() {return num_tot_men_rec;}
    protected int dime_num_tot_acc_loc() {return num_tot_ajr_loc;}

    protected String dame_codigo_id_local_men(){
        String codigo_id_local_men = ID_propio + "_men_" + num_id_local_men;
        num_id_local_men++;
        return codigo_id_local_men;
    }

    protected Mensaje saca_de_lita_enviar() {Mensaje este_mensaje = contenedor_de_mensajes_a_enviar.pop(); return este_mensaje; }
    protected Mensaje saca_de_lita_recibidos() {Mensaje este_mensaje = contenedor_de_mensajes_recibidos.pop(); return este_mensaje; }
    protected AjrLocalizado saca_de_directorio_de_agentes() {AjrLocalizado este_accLocalizado = directorio_de_agentes.pop(); return este_accLocalizado; }
}