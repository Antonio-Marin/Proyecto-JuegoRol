package DungeonQuest.src;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class RecibeUdp extends Thread {

    protected Ajr agente;  // Para tener acceso a los datos de este agente
    DatagramSocket servidor_UDP;

    /**
     * Constructor de la clase
     * @author MAFG y Varios alumnos 2022-2023
     * @fechaDeCreacion: 2022-xx-xx
     * @fechaDeUltimaModificacion: 2023-10-04
     * @version: 2023-2024-01
     * @param este_agente : Es el objeto agente, donde este objeto puede encontrar toda la informacion y recursos que necesita
     * @observaciones:
     *      - Inicializa datos
     *      - Arranca el hilo encargado de recibir mediante UDP
     */
    RecibeUdp(Ajr este_agente) {
        // Inicializamos
        super();
        this.agente = este_agente;
        servidor_UDP = agente.servidor_UDP;


        // Arrancamos el hilo
        new Thread(this, "RecibeUdp").start();
    }

    public void run() {
        System.out.println( "\n ==> El agente : "+ this.agente.ID_propio+
                " - desde la ip : "+ this.agente.Ip_Propia+
                " Arranca el hilo  : RecibeUdp");

        try {
            // El socket de servicio UDP, ya se genero al buscar el nido "Acc => buscaNido()"
            byte[] bufer = new byte[1024];

            while(true) {
                // El servidor espera a que el cliente se conecte y devuelve un socket nuevo
                // Obtiene el flujo de entrada y lee el objeto del stream
                DatagramPacket datos_recibido_UDP = new DatagramPacket(new byte[1024], 1024);
                DatagramPacket paquete_recibido_UDP = new DatagramPacket(bufer, bufer.length);

                System.out.println("\n ==> ********************************** Desde  RecibeUdp ESPERANDO paquete UDP en el agente con id  : " + agente.ID_propio +
                        " - con ip : " + agente.Ip_Propia +
                        " - y Puerto_Propio_UDP : " + agente.Puerto_Propio_UDP);

                // Recibimos el DatagramPacket
                servidor_UDP.receive(paquete_recibido_UDP);

                String fileName = new String(paquete_recibido_UDP.getData(), 0, paquete_recibido_UDP.getLength());
                System.out.println("Recibiendo archivo: " + fileName);

                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                while (true) {
                    servidor_UDP.receive(paquete_recibido_UDP);
                    if (new String(paquete_recibido_UDP.getData(), 0, paquete_recibido_UDP.getLength()).equals("EOF")) {
                        break;
                    }
                    fileOutputStream.write(paquete_recibido_UDP.getData(), 0, paquete_recibido_UDP.getLength());
                }

                System.out.println("Archivo XML recibido con éxito");

                fileOutputStream.close();

                TratarXML test = new TratarXML();
                String archivo_xml = fileName;
                String archivo_xsd = "ESQUEMA_XML_PROTOCOLO_COMUNICACION.xsd";
                if (test.validarXMLConEsquema(archivo_xml, archivo_xsd)) {
                    System.out.println("El archivo se ha sometido a verificacion y es correcto");

                    String paquete_recibido = new String(paquete_recibido_UDP.getData());

                    // Convertimos el envío recibido en objeto de la clase "Mensaje"
                    String IP_or = String.valueOf(paquete_recibido_UDP.getAddress());
                    int puerto_or = paquete_recibido_UDP.getPort();
                    String id_or = "id_or por determinar";
                    String IP_dest = agente.Ip_Propia;
                    int puerto_dest = agente.Puerto_Propio_UDP;
                    String id_dest = agente.ID_propio;
                    String protocolo = "UDP";
                    String momento_actual = String.valueOf(System.currentTimeMillis());
                    String cuerpo_mens = "";

                    File xmlFile = new File("C:/Users/pablo/IdeaProjects/Proyecto-JuegoRol" + fileName);
                    /*
                    Ruta para:
                    Pablo = C:/Users/pablo/IdeaProjects/Proyecto-JuegoRol
                    Antonio = C:/Users/marti/IdeaProjects/SMA_23-24/base
                     */

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                    Document document = dBuilder.parse(xmlFile);

                    document.getDocumentElement().normalize();

                    String tagName = "info";

                    NodeList nodeList = document.getElementsByTagName(tagName);

                    if (nodeList.getLength() > 0) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Node node = nodeList.item(i);

                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;
                                cuerpo_mens = element.getTextContent();
                            }
                        }
                    }else{
                        System.out.println("No se encontro la etiqueta: " + tagName);
                    }

                        Mensaje mensaje_recibido_UDP = new Mensaje(
                                "El ID_mensaje viene en el cuerpo del mensaje", "Mensaje recibido por UDP", "Información recibida del agente",
                                id_or, IP_or, Integer.toString(puerto_or), momento_actual,
                                id_dest, IP_dest, Integer.toString(puerto_dest), momento_actual);
                        mensaje_recibido_UDP.setInfo(cuerpo_mens);


                        String num_men_por_recibidos_str = String.valueOf(agente.num_elem_lita_recibidos());
                        System.out.println("\n ==> Mensaje UDP RECIBIDO desde el agente: " + mensaje_recibido_UDP.info +
                                "\n- mensaje en cola de envio : " + num_men_por_recibidos_str +
                                " - total mensajes enviados : " + agente.num_elem_lita_enviar() +
                                "\n Destinatario id_destino : " + mensaje_recibido_UDP.destinationId +
                                " - en la ip : " + mensaje_recibido_UDP.destinationIp +
                                " - puerto destino : " + mensaje_recibido_UDP.destinationPortUDP +
                                " - protocolo : UDP"
                        );

                        // Llevamos el mensaje al contenedor de recibidos
                        if (mensaje_recibido_UDP.reto.equals("reto")||mensaje_recibido_UDP.reto.equals("true")){
                            agente.pon_en_lita_recibidos_pvp(mensaje_recibido_UDP);
                        }else{
                            agente.pon_en_lita_recibidos(mensaje_recibido_UDP);
                        }

                        System.out.println("\n ==> Desde RecibeUdp, hemos recibido un mensaje almacenado en " + fileName +
                                " - en contenedor tenemos : " + String.valueOf(agente.num_elem_lita_recibidos()) +
                                " - total recibidos : " + agente.num_elem_lita_recibidos());
                    } else {
                        System.out.println("El archivo se ha sometido a verificación y no es correcto");
                    }// Fin de if(Validar...
            } // Fin de - while (true) {
        }
        catch (Exception e) {
                //Si llegamos a un error, imprimimos la exception correspondiente
                System.out.println("\n ==> ERROR: RecibeUdp. Con Exception : " + e.getMessage());
                e.printStackTrace();
        }
    } // Fin de - public void run() {
} // FIn de - public class RecibeUdp extends Thread {

