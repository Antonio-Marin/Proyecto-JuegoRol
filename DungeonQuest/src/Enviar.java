package DungeonQuest.src;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Enviar extends Thread {

    protected Ajr agente;
    protected int latenciaDeAtencionDeEnvio;  // Es el tiempo en milisegundos que queremos que transcurra desde que
                                            // no encontramos mensajes a enviar hasta que volvemos a buscar de nuevo en la lista
    Enviar(Ajr este_agente){
        super();
        this.agente = este_agente;
        this.latenciaDeAtencionDeEnvio = 10;
        new Thread(this, "envia-mensaje").start();
    }

    @Override
    public void run()
    {
        System.out.println( "\n ==> El agente : "+ this.agente.ID_propio+
                " - desde la ip : "+ this.agente.Ip_Propia+
                " Arranca el hilo  : Enviar");

        while (true) {
            // Si el agente no tiene mensajes para enviar, se para 1s antes de mirar otra vez
            try {
                //System.out.println("Entrando en envía mensaje");

                // Si el agente no tiene mensajes para enviar, se para 1s antes de mirar otra vez
                if (agente.num_elem_lita_enviar() >0) {
                    Mensaje mensajeAEnviar = agente.saca_de_lita_enviar();

                        mensajeAEnviar.crearXML();
                        TratarXML test = new TratarXML();
                        String archivo_xml = "xml_"+ mensajeAEnviar.msgId +".xml";
                        String archivo_xsd = "ESQUEMA_XML_PROTOCOLO_COMUNICACION_ROL.xsd";
                        if(test.validarXMLConEsquema(archivo_xml,archivo_xsd)){
                            EnviaUdp(mensajeAEnviar);
                            System.out.println("Mensaje valido. Mensaje enviado");
                        } else{
                            System.out.println("Mensaje no valido");
                        }
                }
                else{
                    sleep(latenciaDeAtencionDeEnvio); // Para controlar la velocidad de envio
                }
            } catch (Exception e){
                System.out.println(e);
                System.out.println("\n ==> No se ha podido enviar el mensaje");
            }
        } // Fin de - while (true) {
    } // Fin de - public void run()

    /*public void EnviaTcp(Mensaje mensajeAEnviar) throws ParserConfigurationException, IOException, SAXException {

        int puerto_destino_TCP = Integer.parseInt(mensajeAEnviar.destinationPortTCP); // EL TCP es el puerto destino y el UDP es el mismo incrementado en uno

        try {

            // Creación socket para comunicarse con el servidor con el host y puerto asociados al servidor
            Socket skCliente = new Socket(mensajeAEnviar.destinationIp, puerto_destino_TCP);
            // Creación flujo de salida
            DataOutputStream obj = new DataOutputStream(skCliente.getOutputStream());
            // Envía objeto al servidor
            obj.writeUTF(mensajeAEnviar.bodyInfo);
            // Cierra flujo de salida
            obj.close();
            // Cierra socket
            skCliente.close();
            // Ok

//            String num_men_por_enviar_str = String.valueOf(agente.num_elem_lita_enviar());
            System.out.println("\n \n ==> Mensaje TCP enviado desde el agente con id  : "+agente.ID_propio +
                                    " - en la ip "+agente.Ip_Propia+
                    " - en la ip : "+agente.Ip_Propia+
                    " - en Puerto_Propio : "+agente.Puerto_Propio_TCP+
//                    " - mensaje en cola de envio : "+num_men_por_enviar_str+
//                    " - total mensajes enviados : "+agente.num_total_lita_enviar()+
                    "\n Destinatario id_destino : "+mensajeAEnviar.destinationId+
                    " - en la ip : "+mensajeAEnviar.destinationIp+
                    " - puerto destino : "+mensajeAEnviar.destinationPortTCP+
                    " - protocolo : "+mensajeAEnviar.comunicationProtocol+
                    "\n - mensaje : "+mensajeAEnviar.bodyInfo);

            // ////////////////////////////////////////////////////////
            // Ahora, si el mensaje no va destinado al Monitor, enviamos al monitor una copia de este mensaje
            // (hemos decidido qe monitorizaremos el SMA enviando copia de todos los mensajes al monitor)
            if ((!mensajeAEnviar.destinationIp.equals(agente.Ip_Monitor)) & (puerto_destino_TCP != agente.Puerto_Monitor_TCP))
            {
                // Creación socket para comunicarse con el servidor con el host y puerto asociados al servidor
                Socket skCliente_Monitor = new Socket(agente.Ip_Monitor, agente.Puerto_Monitor_TCP);
                // Creación flujo de salida
                DataOutputStream obj_Monitor = new DataOutputStream(skCliente_Monitor.getOutputStream());
                // Envía objeto al servidor
                obj_Monitor.writeUTF(mensajeAEnviar.bodyInfo);
                // Cierra flujo de salida
                obj_Monitor.close();
                // Cierra socket
                skCliente_Monitor.close();
            }

       }
        catch (Exception e) {
            // Failure
            String num_men_por_enviar_str = String.valueOf(agente.num_elem_lita_enviar());
            System.out.println("\n ==> Error: fallo al enviar mensaje  TCP : " + e + "\n" +
                            " Desde : agente con id  : "+agente.ID_propio +
                            " - en la ip "+agente.Ip_Propia+
                            " - en Puerto_Propio : "+agente.Puerto_Propio_TCP+
                            " - mensaje en cola de envio : "+num_men_por_enviar_str+
                            " - total mensajes enviados : "+agente.dime_num_tot_men_env()+
                            "\n Destinatario id_destino : "+mensajeAEnviar.destinationId+
                            " - en la ip : "+mensajeAEnviar.destinationIp+
                            " - puerto destino : "+mensajeAEnviar.destinationPortTCP+
                            " - protocolo : "+mensajeAEnviar.comunicationProtocol+
                            "\n - mensaje : "+mensajeAEnviar.bodyInfo);
        }
    } // Fin de - public void EnviaTcp(Mensaje mensajeAEnviar) throws ParserConfigurationException, IOException, SAXException {
     */

    public void EnviaUdp(Mensaje mensajeAEnviar)
    {
        int puerto_destino_UDP = Integer.parseInt(mensajeAEnviar.destinationPortUDP); // EL TCP es el puerto destino y el UDP es el mismo incrementado en uno

        try {
            //Creamos el socket de UDP
            String filePath ="C:/Users/luisp/IdeaProjects/Proyecto-JuegoRol/" +"xml_"+mensajeAEnviar.msgId+".xml";
            /*
            Ruta para:
            Pablo = C:\Users\pablo\IdeaProjects\Proyecto-JuegoRol\out\production\Proyecto-JuegoRol\DungeonQuest
            Antonio = C:/Users/marti/IdeaProjects/SMA_23-24/Proyecto/
            Luis = C:\Users\luisp\IdeaProjects\Proyecto-JuegoRol
             */
            String fileName = new File(filePath).getName();
            DatagramSocket socketUDP = new DatagramSocket();
            //Convertimos el mensaje a bytes
            byte[] mensaje_UDP = fileName.getBytes();
            //Creamos un datagrama
            DatagramPacket paquete_UDP_nombre = new DatagramPacket(mensaje_UDP, mensaje_UDP.length, InetAddress.getByName(mensajeAEnviar.destinationIp), puerto_destino_UDP);
            //Lo enviamos con send
            socketUDP.send(paquete_UDP_nombre);

            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int byteRead;

            while((byteRead = fileInputStream.read(buffer)) != -1){
                DatagramPacket dataPacket = new DatagramPacket(buffer, byteRead, InetAddress.getByName(mensajeAEnviar.destinationIp), puerto_destino_UDP);
                socketUDP.send(dataPacket);
            }

            byte[] eofData = "EOF".getBytes();
            DatagramPacket eofPacket = new DatagramPacket(eofData, eofData.length, InetAddress.getByName(mensajeAEnviar.destinationIp), puerto_destino_UDP);
            socketUDP.send(eofPacket);

            System.out.println("Archivo XML enviado con exito");
            //Cerramos el socket
            fileInputStream.close();
            socketUDP.close();

            String num_men_por_enviar_str = String.valueOf(agente.num_elem_lita_enviar());
            System.out.println("\n \n ==> Mensaje UDP enviado desde el agente con id  : "+agente.ID_propio +
                    " - con ip "+agente.Ip_Propia+
                    " - y Puerto_Propio : "+agente.Puerto_Propio_UDP+
                    " - mensaje en cola de envio : "+num_men_por_enviar_str+
                    " - total mensajes enviados : "+agente.dime_num_tot_men_env()+
                    "\n => Destinatario id_destino : "+mensajeAEnviar.destinationId+
                    " - con ip : "+mensajeAEnviar.destinationIp+
                    " - puerto destino : "+mensajeAEnviar.destinationPortUDP+
                    " - y protocolo : UDP \n => mensaje : "+mensajeAEnviar.info);

            // ////////////////////////////////////////////////////////
            // Ahora, si el mensaje no va destinado al Monitor, enviamos al monitor una copia de este mensaje
            // (hemos decidido qe monitorizaremos el SMA enviando copia de todos los mensajes al monitor)
            if ((!mensajeAEnviar.destinationIp.equals(agente.Ip_Dios)) & (puerto_destino_UDP != agente.Puerto_Dios_UDP))
            {
                //Creamos el socket de UDP
                DatagramSocket socketUDP_Monitor = new DatagramSocket();
                //Convertimos el mensaje a bytes
                byte[] mensaje_UDP_Monitor = mensajeAEnviar.info.getBytes();
                //Creamos un datagrama
                DatagramPacket paquete_UDP_Monitor = new DatagramPacket(mensaje_UDP_Monitor, mensaje_UDP_Monitor.length, InetAddress.getByName(agente.Ip_Dios), agente.Puerto_Dios);
                //Lo enviamos con send
                socketUDP.send(paquete_UDP_Monitor);
                //Cerramos el socket
                socketUDP_Monitor.close();
            }

        }
//        catch (Exception ex)ConnectException
        catch (IOException ex)
        {
            String num_men_por_enviar_str = String.valueOf(agente.num_elem_lita_enviar());
            System.out.println("\n ==> Error: fallo al enviar mensaje UDP : " + ex + "\n"+
                    " Desde : agente con id  : "+agente.ID_propio +
                    " - en la ip "+agente.Ip_Propia+
                    " - en Puerto_Propio : "+agente.Puerto_Propio_UDP+
                    " - mensaje en cola de envio : "+num_men_por_enviar_str+
                    " - total mensajes enviados : "+agente.dime_num_tot_men_env()+
                    "\n Destinatario id_destino : "+mensajeAEnviar.destinationId+
                    " - en la ip : "+mensajeAEnviar.destinationIp+
                    " - puerto destino : "+mensajeAEnviar.destinationPortUDP+
                    " - protocolo : UDP \n - mensaje : "+mensajeAEnviar.info);
        }
    } // FIn de - public void EnviaUdp(Mensaje mensajeAEnviar)
} // Fin de - public class Enviar extends Thread {
