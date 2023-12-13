package DungeonQuest.src;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.*;

public class Ajr {

    // //////////////////////////////////////
    // //////////////////////////////////////
    // DATOS GLOBALES

    // //////////////////////////////////////
    // Datos del agente
    protected String ID_propio; // Identificador unico de este agente
    protected String Ip_Propia;  // Ip donde reside este agente
    protected int Puerto_Propio_UDP;  // Es el puerto de servidor UDP del agente (es el siguiente a "Puerto_Propio" osea - Puerto_Propio_UDP = Puerto_Propio+1)
    protected long Tiempo_de_nacimiento;  // La hora del sistema de esta maquina en la que se genera el agente
    protected tipos_de_agentes tipo_agente;  // Para indicar si es aventurero o Dios
    protected enum tipos_de_agentes
    {
        AVENTURERO, DIOS
    }
    Aventurero aventurero;

    // //////////////////////////////////////
    // Datos del entorno de ejecución
    protected String Ip_Dios;  // Es la IP donde reside el Dios (es la misma para todos los agentes del SMA)
    protected int Puerto_Dios;  // Es el puerto donde reside el Dios (es la misma para todos los agentes del SMA)
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
    protected int nivelAventurero;
    protected int monstruosDerrotados;
    protected int salidaVoluntaria;

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

    protected DatagramSocket servidor_UDP;  // Puerto para el servicio por UDP

    private LinkedList<AjrLocalizado> directorio_de_agentes = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes para enviar por un agente
    private  int num_tot_ajr_loc;  // Numero total de agentes localizados
    private LinkedList<Mensaje> contenedor_de_mensajes_a_enviar = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes para enviar por un agente
    private  int num_tot_men_env;  // Numero total de mensajes enviados por el agente
    private LinkedList<Mensaje> contenedor_de_mensajes_recibidos = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes recibidos por un agente
    private LinkedList<Mensaje> contenedor_de_mensajes_recibidos_pvp = new LinkedList<>(); // Contenedor para almacenar cada uno de los mensajes recibidos por un agente
    private  int num_tot_men_rec;  // Numero total de mensajes recibidos por el agente
    private  int num_id_local_men;  // Este numero, junto con el identificador del agente, generan un codigo unico de mensaje

    //private boolean salidaVoluntaria;

    private LinkedList<Monstruo> mazmorra_principiantes = new LinkedList<>();
    private LinkedList<Monstruo> mazmorra_intermedia = new LinkedList<>();
    private LinkedList<Monstruo> mazmorra_avanzado = new LinkedList<>();

    //TODO: Mirar ACC y añadir más código

    public Ajr (String ID_propio, String este_num_generacion_str, String este_tipo_agente, String este_Ip_Dios, String este_Puerto_Monitor){
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
            this.Puerto_Propio_UDP = this.Puerto_Dios;

            // Generamos los sockets de TCP y UDP en el monitor, ya que este sabe cuales son sus puerton y no usa "buscaNido()" para localizarse
            try {
                //servidor_TCP = new ServerSocket(Puerto_Propio_TCP);
                servidor_UDP = new DatagramSocket(Puerto_Propio_UDP);
            } catch (Exception e) {
                System.out.println("\n ==> ERROR. Desde Ajr al abrir los puertos de comunicaciones con Puerto_Propio_UDP : " + Puerto_Propio_UDP +
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
            randomizedDungeons(); //Crea las mazmorras con los monstruos aleatorios
            menuInicial();
            finalizaAgente();
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
        this.Puerto_Dios_UDP = this.Puerto_Dios;  // Es el puerto de servidor UDP del agente monitor (es el mismo que "Puerto_Monitor+1" y es la misma para todos los agentes del SMA)

        this.Inicio_rango_IPs = Ip_Propia;  // Solo para pruebas
        this.Rango_IPs = 0;
        this.Puerto_Inicio = 50000;
        this.Rango_Puertos = 10000;
        this.localizacion_codigo = "C:/Users/pablo/IdeaProjects/Proyecto-JuegoRol/out/production/Proyecto-JuegoRol/DungeonQuest"; //cambia segun quien lo ejecute
        /*
        Localización código:
        Pablo: C:/Users/pablo/IdeaProjects/Proyecto-JuegoRol/out/production/Proyecto-JuegoRol/DungeonQuest
        Antonio: C:/Users/marti/IdeaProjects/Proyecto/out\production/Proyecto-JuegoRol/DungeonQuest
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
        this.nivelAventurero = 0;
        this.monstruosDerrotados = 0;
        this.salidaVoluntaria = 0;

        this.Frecuencia_rastreo_puertos = 0.00001f;
        //this.salidaVoluntaria = false;

        // //////////////////////////////////////
        // Definimos datos del sistema de comunicaciones
        this.num_tot_ajr_loc = 0;  // Numero total de agentes localizados
        this.num_tot_men_env = 0;  // Numero total de mensajes enviados por el agente
        this.num_tot_men_rec = 0;  // Numero total de mensajes recibidos por el agente
        this.num_id_local_men = 0;  // Cada vez que se solicita, se incrementa en uno, para generar un codigo local unico para los mensajes (ver dame_codigo_id_local_men())
    }

    /**
     * buscaNido() : Busca en la maquina dosde se esta ejecutando un puerto libre para alojar al agente
     */
    protected void buscaNido() {

        Random rand = new Random();
        int puerto_ini_busqueda = Puerto_Inicio + rand.nextInt(Rango_Puertos + 1);
        puerto_ini_busqueda = (puerto_ini_busqueda / 2) * 2; // Nos aseguramos que el numero sea par
        int puerto_busqueda = puerto_ini_busqueda;
        boolean sigue_buscando = true;
        // Para evitar que el proceso se eternice
        int num_intentos = 0; // Para llevar una cuenta del numero de puertos en los que hemos intentado anidar
        int max_num_intentos = 5000; // Para llevar una cuenta del numero de puertos en los que hemos intentado anidar
        long T_ini_busqueda =  System.currentTimeMillis();  // La hora del sistema de esta maquina en la que se inicia la busqueda de nido
        long T_max_busqueda = 1000 * 10;  // El periodo maximo de tiempo que permitimos que el agente este buscando su nido (en milisegundos)
        long T_limite_busqueda = T_ini_busqueda + T_max_busqueda;  // El momento en el que el agente debe parar de buscar su nido (en milisegundos)

        while (sigue_buscando) {
            try {
                servidor_UDP = new DatagramSocket(puerto_busqueda + 1);

                // Si hemos podido ocupar los dos puertos, ya son nuestros y por tanto anotamos nuestra localizacion
                this.Puerto_Propio_UDP = puerto_busqueda;

                // Si los dos puertos han funcionado, ya tenemos nido y podemos para de buscar
                sigue_buscando = false;

                long T_actual = System.currentTimeMillis();
                long T_buscando = System.currentTimeMillis() - T_ini_busqueda;
                System.out.println("\n ==> Desde Acc => buscaNido ANIDADO CORRECTAMENTE con num_intentos : "+num_intentos+
                        " - con max_num_intentos : "+ max_num_intentos +
                        " - con T_ini_busqueda : "+ T_ini_busqueda +
                        " - con T_actual : "+ T_actual +
                        " - con T_limite_busqueda : "+ T_limite_busqueda +
                        " - tiempo invertido (milisegundos) : "+ T_buscando+
                        "\n - anidado en Puerto_Propio : "+ this.Puerto_Propio_UDP +
                        " - Puerto_Propio_UDP : " + this.Puerto_Propio_UDP);

            } catch (Exception e) {
                // Si NO hemos podido ocupar los dos puertos, debemos seguir buscando mas adelante
                puerto_busqueda++;
                num_intentos++;
                if (puerto_busqueda > (Puerto_Inicio + Rango_Puertos)) {
                    puerto_busqueda = Puerto_Inicio;
                } // SI nos salimos del rango, volvemos al principio

                // COntrolamos si debemos detener la busqueda de nido
                long T_actual = System.currentTimeMillis();
                if((num_intentos > max_num_intentos) || (T_actual > T_limite_busqueda))
                {
                    sigue_buscando = false;
                    long T_buscando = System.currentTimeMillis() - T_ini_busqueda;
                    System.out.println("\n ==> Desde Acc => buscaNido.Detenemos la busqueda por exceso de intentos o tiempo con num_intentos : "+num_intentos+
                            " - con max_num_intentos : "+ max_num_intentos +
                            " - con T_ini_busqueda : "+ T_ini_busqueda +
                            " - con T_actual : "+ T_actual +
                            " - con T_limite_busqueda : "+ T_limite_busqueda +
                            " - tiempo invertido (milisegundos) : "+ T_buscando);
                }
            } // Fin de - try catch
        }  // FIn de - while (sigue_buscando)
    } // FIn de - protected void buscaNido()

    /**
     * función notificaNacimiento()
     * Notificamos el inicio de la partida al Dios. Para ello envía un mensaje al Dios.
     */

    //ToDo Revisar tooooda la clase --> Antes hay que hacer la clase Mensaje bien y definir el esquema
    protected void notificaNacimiento() {

        // Por ahora solo es una función prototipo
        // Construimos el mensaje

        String ID_mensaje = dame_codigo_id_local_men();
        String msgId = String.valueOf(ID_mensaje.charAt(ID_mensaje.length()-1));
        String momento_actual = String.valueOf(System.currentTimeMillis());
        String Puerto_Propio_str = String.valueOf(Puerto_Propio_UDP);
        String Puerto_Dios_str = String.valueOf(Puerto_Dios_UDP);
        String cuerpo_mens = "Esto es el MENSAJE HE NACIDO  - que el agente con ID_propio : " + ID_propio +
                " - con ip : " + Ip_Propia +
                " - con Puerto_Propio : " + Puerto_Propio_str +
                " - con ID_mensaje : " + ID_mensaje +
                " - envia al monitor con Ip_Monitor : "+Ip_Dios+
                " - con Puerto_Dios : "+Puerto_Dios_str+
                " :  - en T : " + momento_actual;

        Mensaje mensaje_he_nacido = new Mensaje(
                msgId, "1", "0",
                ID_propio, Ip_Propia, Puerto_Propio_str, momento_actual,
                "ID_Dios", Ip_Dios, Puerto_Dios_str, momento_actual);
        mensaje_he_nacido.setInfo(cuerpo_mens);
        //Rellenamos el resto del XML como "vacío" para que no de probelmas al validar

        mensaje_he_nacido.setMotivoMuerte("0");
        mensaje_he_nacido.setAgenteFinalizadoNivel("0");
        mensaje_he_nacido.setMonstruosDerrotados("0");
        mensaje_he_nacido.setDeathTime("0");

        mensaje_he_nacido.setMazmorra("-");
        mensaje_he_nacido.setNivelAventurero("0");
        mensaje_he_nacido.setNombreMonstruo("-");
        mensaje_he_nacido.setNivelMonstruo("0");
        mensaje_he_nacido.setResultadoFinal("-");
        mensaje_he_nacido.setNivelAventureroFinal("0");

        mensaje_he_nacido.setId1("-");
        mensaje_he_nacido.setIp1("-");
        mensaje_he_nacido.setNivel1("0");
        mensaje_he_nacido.setId2("-");
        mensaje_he_nacido.setIp2("-");
        mensaje_he_nacido.setNivel2("0");
        mensaje_he_nacido.setReto("false");
        mensaje_he_nacido.setResultado("-");
        mensaje_he_nacido.setNivelFinal1("0");
        mensaje_he_nacido.setNivelFinal2("0");

        AjrLocalizado ej = new AjrLocalizado("id", "ip", 10000000,15550005 );
        directorio_de_agentes.add(ej);
        mensaje_he_nacido.setAgentsDirectory(this.directorio_de_agentes);
        mensaje_he_nacido.setDeadAgents(this.directorio_de_agentes);


        // Insertamos el mensaje
        pon_en_lita_enviar(mensaje_he_nacido);

        String Num_generacion_str = String.valueOf(this.Num_generacion);
        String Tiempo_de_nacimiento_str = String.valueOf(this.Tiempo_de_nacimiento);
        System.out.println("\n ==> Ha nacido un agente en la IP = "+Ip_Propia+
                " - con ID_propio :" + this.ID_propio +
                " - en el puerto :" + this.Puerto_Propio_UDP +
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
            String msgId = String.valueOf(ID_mensaje.charAt(ID_mensaje.length()-1));
            long momento_actual = System.currentTimeMillis();
            String momento_actual_str = String.valueOf(System.currentTimeMillis());
            String tiempo_vivido = String.valueOf(System.currentTimeMillis() - Tiempo_de_nacimiento);
            String Puerto_Propio_str = String.valueOf(Puerto_Propio_UDP);
            String Puerto_Dios_UDP_str = String.valueOf(Puerto_Dios_UDP);
            String cuerpo_mens_fin_agente = "Esto es el MENSAJE FIN DE AGENTE  - que el agente con ID_propio : " + ID_propio +
                    " - con ip : " + Ip_Propia +
                    " - con Puerto_Propio : " + Puerto_Propio_str +
                    " - con ID_mensaje : " + ID_mensaje +
                    " - envia al monitor con Ip_Monitor : " + Ip_Dios +
                    " - con Puerto_Monitor : " + Puerto_Dios_UDP_str +
                    " - en T : " + momento_actual_str +
                    " - con T de vida : " + Tiempo_de_vida +
                    " - con T vivido : " + tiempo_vivido;

            //TODO: mensaje muerte revisar
            Mensaje mensaje_fin_agente = new Mensaje(
                    msgId, "4", "0",
                    ID_propio, Ip_Propia, Puerto_Propio_str, momento_actual_str,
                    "ID_Monitor", Ip_Dios, Integer.toString(Puerto_Dios_UDP), momento_actual_str);
            mensaje_fin_agente.setInfo(cuerpo_mens_fin_agente);

            // 0 --> Derrotado
            // 1 --> Nivel Máximo
            // 2 --> Salida Voluntaria
            if (this.salidaVoluntaria != 0){
                mensaje_fin_agente.setMotivoMuerte("2");
            } else if(this.nivelAventurero == 99){
                mensaje_fin_agente.setMotivoMuerte("1");
            }
            else{mensaje_fin_agente.setMotivoMuerte("0");}


            mensaje_fin_agente.setAgenteFinalizadoNivel(Integer.toString(this.nivelAventurero));
            mensaje_fin_agente.setMonstruosDerrotados(Integer.toString(this.monstruosDerrotados));
            mensaje_fin_agente.setDeathTime(momento_actual_str);

            //Rellenamos el resto del XML como "vacío" para que no de probelmas al validar

            mensaje_fin_agente.setMazmorra("-");
            mensaje_fin_agente.setNivelAventurero("0");
            mensaje_fin_agente.setNombreMonstruo("-");
            mensaje_fin_agente.setNivelMonstruo("0");
            mensaje_fin_agente.setResultadoFinal("-");
            mensaje_fin_agente.setNivelAventureroFinal("0");

            mensaje_fin_agente.setId1("-");
            mensaje_fin_agente.setIp1("-");
            mensaje_fin_agente.setNivel1("0");
            mensaje_fin_agente.setId2("-");
            mensaje_fin_agente.setIp2("-");
            mensaje_fin_agente.setNivel2("0");
            mensaje_fin_agente.setReto("false");
            mensaje_fin_agente.setResultado("-");
            mensaje_fin_agente.setNivelFinal1("0");
            mensaje_fin_agente.setNivelFinal2("0");

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
     * Función randomizedDungeons()
     * Antes de pasar al menu y despues de haber notificado
     * el nacimiento se crearan las mazmorras aleatoriamente
     */
    protected void randomizedDungeons(){
        this.mazmorra_principiantes.clear();
        this.mazmorra_intermedia.clear();
        this.mazmorra_avanzado.clear();

        Random rand = new Random();
        List<String> nombres_principiante = Arrays.asList("Slime", "Hilichurl", "Megaflora", "Hilichurl ballestero", "Samachurl", "Duende", "Bokoblin", "Esqueleto");
        List<String> nombres_intermedio = Arrays.asList("Skulltula", "Lawachurl", "Ogro", "Recaudador Fatui", "Fantasma", "Zombie", "Caballero errante", "Jauría de lobos");
        List<String> nombres_avanzado = Arrays.asList("Dragón", "Regisvid", "Oceánida", "Protrodragarto", "Oni Espadachín", "Serpiente de las Ruinas", "Kraken", "Cíclope");
        for(int i =0 ; i<10; i++){
            Monstruo monstruo_principiante = new Monstruo(nombres_principiante.get(rand.nextInt(8)), rand.nextInt(1,6));
            Monstruo monstruo_intermedio = new Monstruo(nombres_intermedio.get(rand.nextInt(8)), rand.nextInt(6, 16));
            Monstruo monstruo_avanzado = new Monstruo(nombres_avanzado.get(rand.nextInt(8)), rand.nextInt(16, 30));
            this.mazmorra_principiantes.add(monstruo_principiante);
            this.mazmorra_intermedia.add(monstruo_intermedio);
            this.mazmorra_avanzado.add(monstruo_avanzado);
        }

    }
    /**
     * Función menuInicial()
     * Será el menú inicial del Aventurero.
     */
protected void menuInicial(){
    //TODO: hacer más bonito el menú inicial
    Scanner s = new Scanner(System.in);
    boolean var = true;
    while(var) {
        while ((funcionAventurero.mensajesPVP.isEmpty())){
            Mensaje m = funcionAventurero.mensajesPVP.pop();
            pvpAdversario(m);
        }
        int op;
        System.out.println("Introduce una opción: \n 1. Ir a una mazmorra.\n 2. PVP\n 3. Salir");
        op = s.nextInt();
        switch (op){
            case 1:
                var = mazmorra();
            case 2:
                pvp();
                //wait();
                //pvpResultado();


            case 3:
                var = false;
                this.salidaVoluntaria = 1;
                break;
            default:
                System.out.println("Opcion incorrecta");
                break;
        }
    }
}

/**        PRUEBA PARA MOSTRAR LOS ENEMIGOS DE LAS MAZMORRAS (FUNCIONA)
 *
 *         System.out.println("======== ===================== ========");
 *         System.out.println("======== MAZMORRA PRINCIPIANTE ========");
 *         System.out.println("======== ===================== ========");
 *         for (Monstruo elemento : this.mazmorra_principiantes) {
 *             System.out.println(elemento.toString());
 *         }
 *         System.out.println("======== ===================== ========");
 *         System.out.println("======== MAZMORRA INTERMEDIA ========");
 *         System.out.println("======== ===================== ========");
 *         for (Monstruo elemento : this.mazmorra_intermedia) {
 *             System.out.println(elemento.toString());
 *         }
 *         System.out.println("======== ===================== ========");
 *         System.out.println("======== MAZMORRA AVANZADA ========");
 *         System.out.println("======== ===================== ========");
 *         for (Monstruo elemento : this.mazmorra_avanzado) {
 *             System.out.println(elemento.toString());
 *         }
 */

    /**
     * Función mazmorra()
     * Te da distintas opciones de mazmorra a la que ir y allí encontrarás un monstruo. Si lo derrotas conseguirás experiencia y subirás de nivel.
     */
    public static boolean mazmorra(){
        Scanner s = new Scanner(System.in);
        System.out.println("Hola valiente aventurero \n Has elegido entrar en una mazmorra \n Decide en cual: ");
        String resp = s.next();

        if(true) {// if resp es una mazmorra válida
            // Crea mensaje petición monstruo al Dios (2.1)


            //Espera mensaje del Dios dando un monstruo (2.2)


            //Una vez recibe el mensaje  saca los datos necesarios y calcula el resultado del combate


            System.out.println("Te has encontrado en la mazmorra [Mazmorra], al monstruo [Monstruo] de nivel [Nivel]");


            System.out.println("Resultado: [Victoria/Derrota] \n Experiencia ganada: [Exp] \n Nivel actual: [Nivel]");

            if(true){ //Si nivel del aventurero es >= 99
                // nivel = 99
                return false; // Ya que ha terminado la partida.
            }else if(true){ // Si ha sido derrotado
                return false; // Ya que ha terminado la partida.
            } else {return true;}

            //Crea mensaje info para el Dios (2.3)
        }else {
            System.out.println("Nombre de mazmorra no valido, volvemos al menu inicial");
            return true;
        }
    }


    /**
     * Función pvp()
     * Te da distintas opciones de mazmorra a la que ir y allí encontrarás un monstruo. Si lo derrotas conseguirás experiencia y subirás de nivel.
     */
     public void pvp() {
        Scanner s = new Scanner(System.in);
        int opcion, yo = 0;
        AjrLocalizado adversario;

        System.out.println("Con que el aventurero desea luchar con otro de su condición... \n Buscando un aventurero");
        // Buscar un aventurero disponible.

        //Mira si solo esta el
        if (directorio_de_agentes.size() == 1) {
            System.out.println("No hay aventureros disponibles.");
        }
        else {

            //Imprime lista de todos los aventureros vivos
            //menos el mismo
            System.out.println("Aventureros disponibles:");
            for (int i = 0; i < directorio_de_agentes.size(); i++) {
                if (!directorio_de_agentes.get(i).ID.equals(ID_propio)) {
                    System.out.println((i + 1) + " -> " + directorio_de_agentes.get(i).ID);
                } else {
                    yo = i + 1;
                }
            }

            do {
                System.out.print("Numero del contrincante: ");
                opcion = s.nextInt();
                s.nextLine();

                if (opcion == yo) { //Si me elijo a mi mismo a pesar de no mostrarme
                    System.out.println("Deja de apuntarte con tu arma y elige");
                }
                if (opcion > directorio_de_agentes.size()) { //si elijo un numero que no esta disponible
                    System.out.println("Ese aventurero no esta aqui ¿esperas a alguien mas?");
                    //ToDo puede dar error?
                }
            } while (opcion == yo || opcion > directorio_de_agentes.size());

            if (opcion != 0) {
                adversario = directorio_de_agentes.get(opcion - 1);


                String ID_mensaje = dame_codigo_id_local_men();
                String msgId = String.valueOf(ID_mensaje.charAt(ID_mensaje.length() - 1));

                //String aventureroID=this.getID_propio();
                //String aventureroIP=aventurero.getIP_propio();
                //String aventureroPuerto=String.valueOf(aventurero.getPuerto_Propio_UDP());
                String Puerto_Propio_str = String.valueOf(Puerto_Propio_UDP);
                String momento_actual = String.valueOf(System.currentTimeMillis());
                String adversarioID = adversario.ID;
                String adversarioIP = adversario.IP;
                String adversarioPuerto = String.valueOf(adversario.puerto);

                Mensaje mensajePVP = new Mensaje(msgId, "3", "1", ID_propio, Ip_Propia,
                        Puerto_Propio_str, momento_actual, adversarioID, adversarioIP, adversarioPuerto, momento_actual);

                String cuerpo_mens_pvp = "Esto es el MENSAJE PVP  - que el agente con ID_propio : " + ID_propio +
                        " - con ip : " + Ip_Propia +
                        " - con Puerto_Propio : " + Puerto_Propio_str +
                        " - con ID_mensaje : " + ID_mensaje +
                        " - envia al adversario con Id : " + adversarioID +
                        " - con Ip : " + adversarioIP +
                        " - con Puerto_Propio : " + adversarioPuerto +
                        " - en T : " + momento_actual +
                        " - con Nivel : " + nivelAventurero +
                        " - con Monstruos Eliminados : " + aventurero.getMonstruosDerrotados();

                //Unica información relevante del mensaje 3.1
                mensajePVP.setInfo(cuerpo_mens_pvp);
                mensajePVP.setReto("true");

                //Rellenamos el resto del XML como "vacío" para que no de probelmas al validar

                mensajePVP.setMotivoMuerte("0");
                mensajePVP.setAgenteFinalizadoNivel("0");
                mensajePVP.setMonstruosDerrotados("0");
                mensajePVP.setDeathTime("0");

                mensajePVP.setMazmorra("-");
                mensajePVP.setNivelAventurero("0");
                mensajePVP.setNombreMonstruo("-");
                mensajePVP.setNivelMonstruo("0");
                mensajePVP.setResultadoFinal("-");
                mensajePVP.setNivelAventureroFinal("0");

                mensajePVP.setId1(ID_propio);
                mensajePVP.setIp1(Ip_Propia);
                mensajePVP.setNivel1("0");
                mensajePVP.setId2(adversarioID);
                mensajePVP.setIp2(adversarioIP);
                mensajePVP.setNivel2("0");
                mensajePVP.setResultado("-");
                mensajePVP.setNivelFinal1("0");
                mensajePVP.setNivelFinal2("0");

                mensajePVP.setAgentsDirectory(this.directorio_de_agentes);
                mensajePVP.setDeadAgents(this.directorio_de_agentes);


                // Insertamos el mensaje
                pon_en_lita_enviar(mensajePVP);


                System.out.println("\n ==> Se ha lanzado un reto PvP en la IP = " + Ip_Propia +
                        " - con ID_propio :" + ID_propio +
                        " - con Nivel :" + nivelAventurero +
                        " - en el puerto :" + Puerto_Propio_str);

                //Esperar confirmación (3.2)-----------------------------------------------------------------------------------
                //wait();
            }
        }

        // Si esa confirmación es true le manda el nivel (3.3) y el otro aventurero procesa el resultado


        //Recibes el resultado (3.4) y te actualizas
        //Mensaje mensajeResultado = funcionAventurero.mensajesPVP.pop();
        //aventurero.nivel=Integer.parseInt(mensajeResultado.nivelFinal2);
        /*adversario.nivel=Integer.parseInt(mensajeResultado.nivelFinal1);

        }
        if(nivelAventurero>=100 || nivelAventurero<=0){ //Si nivel del aventurero es >= 99
            // nivel superior a 99 o muerto con nivel cero
            return false; // Ya que ha terminado la partida
        }else{
            return true;
        }

         */
    }

    public void pvpAdversario(Mensaje msj){
        Scanner s = new Scanner(System.in);
        int opcion, nivel1=0, nivel2=0;
        String win=null;

        if (msj.getReto().equals("true")){
            System.out.print("El Aventurero "+msj.originId+" te ha desafiado.\n 1. Aceptas \n 2. Rechazas");

            do{
                opcion = s.nextInt(); s.nextLine();
                switch (opcion){
                    case 1:
                        System.out.println("Has aceptado el desafio, preparate para el combate");
                        /*int resultado = Integer.parseInt(msj.nivel1) - Integer.parseInt(msj.nivel2);
                        if (resultado>0){
                            System.out.println("Has perdido el combate, te has quedado sin "+resultado+" niveles");
                            nivel1=Integer.parseInt(msj.nivel1)+resultado;
                            nivel2=Integer.parseInt(msj.nivel1)-resultado;
                            win=msj.id1;
                        } else if (resultado<0) {
                            System.out.println("¡¡Has ganado!! todo un campeon, disfruta de tus "+(resultado*(-1))+" niveles extra");
                            nivel1=Integer.parseInt(msj.nivel1)+resultado;
                            nivel2=Integer.parseInt(msj.nivel1)-resultado;
                            win=msj.id2;
                        }else{
                            System.out.println("Un buen choque de espadas, pero no ha servido de nada");
                        }

                         */

                        String ID_mensaje = dame_codigo_id_local_men();
                        String msgId = String.valueOf(ID_mensaje.charAt(ID_mensaje.length()-1));

                        String aventureroID=msj.destinationId;
                        String aventureroIP=msj.destinationIp;
                        String aventureroPuerto=msj.destinationPortUDP;
                        String momento_actual = String.valueOf(System.currentTimeMillis());
                        String adversarioID = msj.originId;
                        String adversarioIP = msj.originIp;
                        String adversarioPuerto = msj.originPortUDP;

                        Mensaje mensajePVP = new Mensaje(msgId, "3", "2",aventureroID,aventureroIP,
                                aventureroPuerto, momento_actual,adversarioID,adversarioIP,adversarioPuerto, momento_actual);

                        String cuerpo_mens_pvp = "Esto es el MENSAJE CONFIRMACION  - que el agente con ID_propio : " + aventureroID +
                                " - con ip : " + aventureroIP +
                                " - con Puerto_Propio : " + aventureroPuerto +
                                " - con ID_mensaje : " + ID_mensaje +
                                " - envia al adversario con Ip : " + adversarioIP +
                                " - con Puerto_Propio : " + adversarioPuerto +
                                " - en T : " + momento_actual;

                        //Mandar mensaje (3.1)
                        mensajePVP.setInfo(cuerpo_mens_pvp);
                        mensajePVP.setReto("true");
                        mensajePVP.setId1(msj.originId);
                        mensajePVP.setIp1(msj.originIp);
                        mensajePVP.setNivel1("0");
                        mensajePVP.setId2(msj.destinationId);
                        mensajePVP.setIp2(msj.destinationIp);
                        mensajePVP.setNivel2(Integer.toString(nivelAventurero));

                        //Rellenamos el resto del XML como "vacío" para que no de probelmas al validar

                        mensajePVP.setMotivoMuerte("0");
                        mensajePVP.setAgenteFinalizadoNivel("0");
                        mensajePVP.setMonstruosDerrotados("0");
                        mensajePVP.setDeathTime("0");

                        mensajePVP.setMazmorra("-");
                        mensajePVP.setNivelAventurero("0");
                        mensajePVP.setNombreMonstruo("-");
                        mensajePVP.setNivelMonstruo("0");
                        mensajePVP.setResultadoFinal("-");
                        mensajePVP.setNivelAventureroFinal("0");



                        mensajePVP.setResultado("-");
                        mensajePVP.setNivelFinal1("0");
                        mensajePVP.setNivelFinal2("0");

                        mensajePVP.setAgentsDirectory(this.directorio_de_agentes);
                        mensajePVP.setDeadAgents(this.directorio_de_agentes);


                        // Insertamos el mensaje
                        pon_en_lita_enviar(mensajePVP);

                        System.out.println("\n ==> Se ha aceptado un combate PvP ");
                        break;
                    case 2:
                        System.out.println("Has rechazado el desafio, sigue con tu aventura");

                        String ID_mensaje_rech = dame_codigo_id_local_men();
                        String msgId_rech = String.valueOf(ID_mensaje_rech.charAt(ID_mensaje_rech.length()-1));

                        String aventureroID_rech=msj.destinationId;
                        String aventureroIP_rech=msj.destinationIp;
                        String aventureroPuerto_rech=msj.destinationPortUDP;
                        String momento_actual_rech = String.valueOf(System.currentTimeMillis());
                        String adversarioID_rech = msj.originId;
                        String adversarioIP_rech = msj.originIp;
                        String adversarioPuerto_rech = msj.originPortUDP;

                        Mensaje mensajePVP_rech = new Mensaje(msgId_rech, "3", "2",aventureroID_rech,aventureroIP_rech,
                                aventureroPuerto_rech, momento_actual_rech,adversarioID_rech,adversarioIP_rech,adversarioPuerto_rech, momento_actual_rech);

                        String cuerpo_mens_pvp_rech = "Esto es el MENSAJE CONFIRMACION  - que el agente con ID_propio : " + aventureroID_rech +
                                " - con ip : " + aventureroIP_rech +
                                " - con Puerto_Propio : " + aventureroPuerto_rech +
                                " - con ID_mensaje : " + ID_mensaje_rech +
                                " - envia al adversario con Ip : " + adversarioIP_rech +
                                " - con Puerto_Propio : " + adversarioPuerto_rech +
                                " - en T : " + momento_actual_rech;

                        //Mandar mensaje (3.1)
                        mensajePVP_rech.setInfo(cuerpo_mens_pvp_rech);
                        mensajePVP_rech.setReto("false");
                        mensajePVP_rech.setId1(msj.originId);
                        mensajePVP_rech.setIp1(msj.originIp);
                        mensajePVP_rech.setNivel1("0");
                        mensajePVP_rech.setId2(msj.destinationId);
                        mensajePVP_rech.setIp2(msj.destinationIp);
                        mensajePVP_rech.setNivel2("0");

                        //Rellenamos el resto del XML como "vacío" para que no de probelmas al validar

                        mensajePVP_rech.setMotivoMuerte("0");
                        mensajePVP_rech.setAgenteFinalizadoNivel("0");
                        mensajePVP_rech.setMonstruosDerrotados("0");
                        mensajePVP_rech.setDeathTime("0");

                        mensajePVP_rech.setMazmorra("-");
                        mensajePVP_rech.setNivelAventurero("0");
                        mensajePVP_rech.setNombreMonstruo("-");
                        mensajePVP_rech.setNivelMonstruo("0");
                        mensajePVP_rech.setResultadoFinal("-");
                        mensajePVP_rech.setNivelAventureroFinal("0");



                        mensajePVP_rech.setResultado("-");
                        mensajePVP_rech.setNivelFinal1("0");
                        mensajePVP_rech.setNivelFinal2("0");

                        mensajePVP_rech.setAgentsDirectory(this.directorio_de_agentes);
                        mensajePVP_rech.setDeadAgents(this.directorio_de_agentes);


                        // Insertamos el mensaje
                        pon_en_lita_enviar(mensajePVP_rech);

                        break;
                }

                //ToDo Puede dar error?
            }while(!(opcion == 1 || opcion == 2));
        }
    }

    /**
     * Funcion pvpResultado();
     */

    public void pvpResultado(Mensaje msg_recibido){
        //ToDo hacer clase que resuelve el conflicto y notifica

        if(msg_recibido.reto.equals("true")){

        } else{
            System.out.println("Seguimos con nuestra vida");
        }
    }

    /**
     * Funciones para sincronizar información
     */

    protected void pon_en_lita_enviar(Mensaje este_mensaje) {contenedor_de_mensajes_a_enviar.add(este_mensaje); num_tot_men_env++; }
    protected void pon_en_lita_recibidos(Mensaje este_mensaje) {contenedor_de_mensajes_recibidos.add(este_mensaje);num_tot_men_rec++; }
    protected void pon_en_lita_recibidos_pvp(Mensaje este_mensaje) {contenedor_de_mensajes_recibidos_pvp.add(este_mensaje);num_tot_men_rec++; }
    protected void pon_en_directorio_de_agentes(AjrLocalizado este_accLocalizado) {directorio_de_agentes.add(este_accLocalizado); num_tot_ajr_loc++; }

    protected int num_elem_lita_enviar() {int num_elem1 = contenedor_de_mensajes_a_enviar.size(); return num_elem1;}
    protected int num_elem_lita_recibidos() {int num_elem2 = contenedor_de_mensajes_recibidos.size(); return num_elem2;}
    protected int num_elem_lita_recibidos_pvp() {int num_elem2 = contenedor_de_mensajes_recibidos_pvp.size(); return num_elem2;}
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
    protected Mensaje saca_de_lita_recibidos_pvp() {Mensaje este_mensaje = contenedor_de_mensajes_recibidos_pvp.pop(); return este_mensaje; }
    protected AjrLocalizado saca_de_directorio_de_agentes() {AjrLocalizado este_accLocalizado = directorio_de_agentes.pop(); return este_accLocalizado; }
}