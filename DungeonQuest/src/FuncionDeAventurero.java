package DungeonQuest.src;

import java.util.LinkedList;
import java.util.Random;

public class FuncionDeAventurero implements Runnable {

    // Para pruebas
    protected Random random; // Lo utilizaremos para diversos eventos aleatorios
    protected float Frecuencia_envio;  // este parametro nos permite definir la latencia de envio de mensajes
    // Fin de - Para pruebas

    protected int num_men_enviados_fa; // Para identificar los mensajes enviados por este agente y poder identificarlos de forma unívoca
    protected int num_men_recibidos_fa; // Para identificar los mensajes recibidos por este agente y poder identificarlos de forma unívoca
    protected Mensaje mensajeRecibido;
    protected LinkedList<Mensaje> mensajesPVP=new LinkedList<>();


    protected Ajr agente; // Para poder acceder a los datos generales de este agente

    FuncionDeAventurero(Ajr este_agente) {
        // Para pruebas
        random = new Random();
        Frecuencia_envio = 0f;  // Entre 0 y 1. Cuanto mas pequeña menor frecuencia de envios
        // Fin de - Para pruebas

        num_men_enviados_fa = 0;
        num_men_recibidos_fa = 0;
        this.agente = este_agente;

        // arrancamos el hilo
        new Thread(this, "FuncionDeAgente").start();
    }

    @Override
    public void run() {
        System.out.println( "\n ==> El agente : "+ this.agente.ID_propio+
                " - desde la ip : "+ this.agente.Ip_Propia+
                " Arranca el hilo  : FuncionDeAgente");


        while(true){

            // //////////////////////////////////////
            // Obtenemos los mensajes recibidos
                // Miramos si hay algun mensaje recibido y si lo hay lo recogemos
            if(agente.num_elem_lita_recibidos() > 0) {
                recogeMensajeRecibido(1);
            }
            if(agente.num_elem_lita_recibidos_pvp() > 0) {
                recogeMensajeRecibido(2);
            }

            // //////////////////////////////////////
            // enviamos un mensaje de vez en cuando
            if (Frecuencia_envio > this.random.nextFloat())
            {
                // Miramos en el directorio y seleccionamos un agente para enviar
                // PARA PRUEBAS, tomamos el primer agente del directorio y le enviamos el mensaje
                if ((agente.num_elem_directorio_de_agentes() > 0) & (agente.Estado_Actual == Ajr.Estado_del_AJR.VIVO )) {
                    // seleccionamos el agente
                    AjrLocalizado otro_agente = agente.saca_de_directorio_de_agentes();

                    // Construimos el mensaje
                    num_men_enviados_fa = num_men_enviados_fa + 1;
                    String IP_or = agente.Ip_Propia;
                    //int puertoTCP_or = agente.Puerto_Propio_TCP;
                    int puertoUDP_or = agente.Puerto_Propio_UDP;
                    String id_or = agente.ID_propio;
                    String IP_dest = otro_agente.IP;
                    int puertoTCP_dest = otro_agente.puerto;
                    int puertoUDP_dest = otro_agente.puerto+1;
                    String id_dest = otro_agente.ID;
                    String protocolo = "UDP";

                    String ID_mensaje = agente.dame_codigo_id_local_men();
                    String momento_actual = String.valueOf(System.currentTimeMillis());
                    String puerto_dest_str = String.valueOf(puertoTCP_dest);
                    String cuerpo_mens = "Esto es el MENSAJE num = " + num_men_enviados_fa +
                            " - que el agente : " + agente.ID_propio +
                            " - con ip " + agente.Ip_Propia +
                            " - con ID_mensaje " + ID_mensaje +
                            " - envia al agente con id_dest = "+id_dest+
                            " - con IP_dest = "+IP_dest+
                            " - con puerto_dest = "+puerto_dest_str+
                            " :  - en T = " + momento_actual;


                    Mensaje nuevo_mensaje = new Mensaje(ID_mensaje, "Nacimiento", "Envio información al monitor",
                            id_or, IP_or, Integer.toString(puertoUDP_or),momento_actual,
                            id_dest, IP_dest, Integer.toString(puertoUDP_dest), momento_actual);
                    // Enviamos el mensaje a la cola de envíos
                    enviaMensaje(nuevo_mensaje);
                }
            }
        } // Fin de while(true){
    } // Fin de - public void run() {

    void recogeMensajeRecibido(int n) {
        // Obtenemos el mensaje
        if(n==1) {
            mensajeRecibido = agente.saca_de_lita_recibidos();
        }else{
            mensajeRecibido = agente.saca_de_lita_recibidos_pvp();
            mensajesPVP.add(mensajeRecibido);
            mensajesPVP.notifyAll();
        }
        mensajeRecibido.crearXML();
        TratarXML test = new TratarXML();
        String archivo_xml = "xml_"+ mensajeRecibido.msgId +".xml";
        String archivo_xsd = "ESQUEMA_XML_PROTOCOLO_COMUNICACION.xsd";
        if(test.validarXMLConEsquema(archivo_xml, archivo_xsd)) {
            num_men_recibidos_fa = num_men_recibidos_fa + 1;

            // Lo notificamos (para PRUEBAS)
            String momento_actual = String.valueOf(System.currentTimeMillis());
            System.out.println("Desde procesaMensajeRecibido. El agenteagente : " + agente.ID_propio +
                    " - con ip " + agente.Ip_Propia +
                    " - ha recibido el mensaje  : " + mensajeRecibido.info +
                    " - ordinal = " + num_men_recibidos_fa +
                    " - en T = " + momento_actual);
        }else{
            System.out.println("Mensaje erróneo");
        }
    } // Fin de - void recogeMensajeRecibido() {

    void enviaMensaje(Mensaje nuevo_mensaje) {
        // Insertamos el mensaje
        agente.pon_en_lita_enviar(nuevo_mensaje);

        // Lo notificamos (para PRUEBAS)
        String momento_actual = String.valueOf(System.currentTimeMillis());
        System.out.println("Desde generaMensajeAEnviar. El agenteagente : "+agente.ID_propio+
                                    " - con ip "+agente.Ip_Propia+
                                    " - envia el mensaje  : "+ nuevo_mensaje.info+
                                    " - ordinal = "+num_men_enviados_fa+
                                    " - en T = "+momento_actual);
    } // Fin de - void enviaMensaje(Mensaje nuevo_mensaje) {

} // FIn de - public class FuncionDeAgente implements Runnable {
