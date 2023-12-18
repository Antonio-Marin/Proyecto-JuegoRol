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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
                //DatagramPacket datos_recibido_UDP = new DatagramPacket(new byte[1024], 1024);
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
                String archivo_xsd = "ESQUEMA_XML_PROTOCOLO_COMUNICACION_ROL.xsd";
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
                    String tipo_protocolo = "";
                    String paso_protocolo = "";
                    String mazmorra = "";
                    String nivel_aventurero = "";
                    String nombre_monstruo = "";
                    String nivel_monstruo = "";
                    String resultado_final = "";
                    String nivel_aventurero_final = "";
                    String reto = "";

                    File xmlFile = new File("C:/Users/marti/IdeaProjects/Proyecto/" + fileName);
                    /*
                    Ruta para:
                    Pablo = C:/Users/pablo/IdeaProjects/Proyecto-JuegoRol/
                    Antonio = C:/Users/marti/IdeaProjects/Proyecto/
                     */

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                    Document document = dBuilder.parse(xmlFile);

                    document.getDocumentElement().normalize();

                    /**
                     * info: obtener la info y mostrarlo por pantalla ya que son los datos de quien
                     * manda el mensaje (todo esto va en cuerpo_mens)
                     * tipo_protocolo: obtener el tipo de protocolo y guardarlo para despues
                     * procesarlo al final (todo esto va en tipo_protocolo)
                     * paso_protocolo: parecido a la anterior pero con el paso del protocolo
                     * (todo esto va en paso_protocolo)
                     */

                    List<String> nombresEtiquetas = new ArrayList<>();
                    nombresEtiquetas.add("info");
                    nombresEtiquetas.add("tipo_protocolo");
                    nombresEtiquetas.add("paso_protocolo");
                    nombresEtiquetas.add("mazmorra");
                    nombresEtiquetas.add("nivel_aventurero");
                    nombresEtiquetas.add("nombre_monstruo");
                    nombresEtiquetas.add("nivel_mosntruo");
                    nombresEtiquetas.add("resultado_final");
                    nombresEtiquetas.add("nivel_aventurero_final");
                    nombresEtiquetas.add("reto");
                    //TODO: añadir las que hacen falta
                    for (String nombreEtiqueta : nombresEtiquetas) {
                        NodeList listaNodos = document.getElementsByTagName(nombreEtiqueta);

                        // Recorrer la lista de nodos y procesar cada elemento
                        for (int i = 0; i < listaNodos.getLength(); i++) {
                            Node nodo = listaNodos.item(i);

                            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                                Element elemento = (Element) nodo;

                                // Obtener y procesar los datos del elemento según la etiqueta
                                switch (nombreEtiqueta) {
                                    case "info":
                                        cuerpo_mens = elemento.getTextContent();
                                        break;
                                    case "tipo_protocolo":
                                        tipo_protocolo = elemento.getTextContent();
                                        break;
                                    case "paso_protocolo":
                                        paso_protocolo = elemento.getTextContent();
                                        break;
                                    case "mazmorra":
                                        mazmorra = elemento.getTextContent();
                                        break;
                                    case "nivel_aventurero":
                                        nivel_aventurero = elemento.getTextContent();
                                        break;
                                    case "nombre_monstruo":
                                        nombre_monstruo = elemento.getTextContent();
                                        break;
                                    case "nivel_monstruo":
                                        nivel_monstruo = elemento.getTextContent();
                                        break;
                                    case "resultado_final":
                                        resultado_final = elemento.getTextContent();
                                        break;
                                    case "nivel_aventurero_final":
                                        nivel_aventurero_final = elemento.getTextContent();
                                        break;
                                    case "reto":
                                        nivel_aventurero_final = elemento.getTextContent();
                                        break;
                                }
                            }
                        }
                    }


                        Mensaje mensaje_recibido_UDP = new Mensaje(
                                "El ID_mensaje viene en el cuerpo del mensaje", "Mensaje recibido por UDP", "Información recibida del agente",
                                id_or, IP_or, Integer.toString(puerto_or), momento_actual,
                                id_dest, IP_dest, Integer.toString(puerto_dest), momento_actual);
                        mensaje_recibido_UDP.setInfo(cuerpo_mens);
                        mensaje_recibido_UDP.setReto(reto);


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

                        //TODO: terminar de hacerlo
                        if (tipo_protocolo.equals("2")){
                            if (paso_protocolo.equals("1")){
                                //El Dios al recibir este mensaje, envía un mensaje mandándole un monstruo
                                mensaje_recibido_UDP.setMazmorra(mazmorra);
                                mensaje_recibido_UDP.setNivelAventurero(nivel_aventurero);
                                System.out.println("DIOS TRATA MENSAJE RECIBIDO");
                                agente.mazmorraDios(mensaje_recibido_UDP);
                            } else if (paso_protocolo == "2") {
                                //El avnturero recibe el nombre y el nivel del monstruo al que debe derrotar
                                mensaje_recibido_UDP.setMazmorra(mazmorra);
                                mensaje_recibido_UDP.setNivelAventurero(nivel_aventurero);
                                mensaje_recibido_UDP.setNombreMonstruo(nombre_monstruo);
                                mensaje_recibido_UDP.setNivelMonstruo(nivel_monstruo);
                                agente.mazmorraResultado(mensaje_recibido_UDP);
                            }else if (paso_protocolo=="3"){
                                //TODO: No llama a ninguna función?
                                //Llamar a la funcion en relación con el paso protocolo 2.3
                                mensaje_recibido_UDP.setMazmorra(mazmorra);
                                mensaje_recibido_UDP.setNivelAventurero(nivel_aventurero);
                                mensaje_recibido_UDP.setNombreMonstruo(nombre_monstruo);
                                mensaje_recibido_UDP.setNivelMonstruo(nivel_monstruo);
                                mensaje_recibido_UDP.setResultadoFinal(resultado_final);
                                mensaje_recibido_UDP.setNivelAventureroFinal(nivel_aventurero_final);
                            }else{
                                System.out.println("=============================");
                                System.out.println("Paso de protocolo INCORRECTO.");
                                System.out.println("=============================");
                            }

                        }else if (tipo_protocolo == "3"){
                            if (paso_protocolo == "1"){
                                //Llamar a la funcion en relación con el paso protocolo 3.1
                            } else if (paso_protocolo == "2") {
                                //Llamar a la funcion en relación con el paso protocolo 3.2
                            }else if (paso_protocolo=="3"){
                                //Llamar a la funcion en relación con el paso protocolo 3.3
                            }else if (paso_protocolo=="4"){
                                //Llamar a la funcion en relación con el paso protocolo 3.4
                            }else{
                                System.out.println("=============================");
                                System.out.println("Paso de protocolo INCORRECTO.");
                                System.out.println("=============================");
                            }
                        }// Fin comprobacion del tipo_protocolo

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

