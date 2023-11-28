import java.net.DatagramSocket;
import java.net.ServerSocket;
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
        AEVENTURERO, DIOS
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
	/**
    protected int Num_generacion; // Un agente que se arranca en una maquina genera procesos hijos y estos generan procesos nietos, este numero
                                    // indica a que generación correspondeeste agente como descendiente del agente inicial
    protected int Num_max_de_generaciones; // Los agentes de este nivel de generaciones, no generaran agente hijos
    protected int Num_hijos_generados; // Define el numero de descendientes que este agente ha generado (en primera generación)
    protected int Num_max_hijos_generados; // Define el numero maximo de descendientes de primera generación. que este agente ùede generar
    protected double Frecuencia_partos;  // Para manejar la velocidad en la que el agente se reproduce
	**/
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
}