package DungeonQuest.src;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Mensaje {

    /** tipoProtocolo y pasoProtocolo
     *  1: Nacimiento
     *  2: Luchar contra un monstruo
     *      2.1: El aventurero informa a Dios de que quiere ir a una mazmorra
     *      2.2: Dios manda un monstruo al aventurero (en el aventurero se procesa la información)
     *      2.3: Aventurero manda mensaje del resultado a Dios (con nivel actual)
     *  3: PVP
     *      3.1: Aventurero_1 reta a Aventurero_2
     *      3.2: Aventurero_2 confirma o niega ese reto del Aventurero_1
     *      3.3: Si acepta intrcambian niveles y comienza la lucha (suma o resta de niveles)
     *      3.4 : Una vez terminada la lucha cada Aventurero manda un mensaje a Dios con el resultado (guien gana y nuevo nivel)
     *  4: Muerte
     */
    /**
     * DATOS PARA COMUNICID 1: Naciminto
     */
    protected String msgId; // Id del mensaje
    protected String tipoProtocolo; //Indica el protcolo del mensaje (Nacimiento, lucha, etc.)
    protected String pasoProtocolo; //información del paso en el que esta el protocolo

    protected String originId; // Identificador único del agente que envía el mensaje
    protected String originIp; // IP del agente que envía el mensaje
    protected String originPortUDP; // Puerto del agente que envía el mensaje
    protected String originTime; //hora del agente origen

    protected String destinationId; // Identificador único del agente destino de este mensaje
    protected String destinationIp; // IP del agente destino de este mensaje
    protected String destinationPortUDP; //Puerto del agente que recibe el mensaje
    protected String destinationTime; ////hora del agente destino
    protected String info;  //Contenido del mensaje a enviar
    /**
     * DATOS PARA COMUNICID 4: Muerte
     * 0. consigue maximo nivel
     * 1. Es derrotado
     * 2. Sale voluntariamente
     */
    protected String motivoMuerte;
    protected String agenteFinalizadoNivel; // Nivel del agente muerto
    protected String monstruosDerrotados; // Monstruos que derreotó
    protected String deathTime; // Hora muerte
    /**
     * DATOS PARA COMUNICID 2: Luchar contra un monstruo
     */
    protected String mazmorra;
    protected String nivelAventurero;
    protected String nombreMonstruo;
    protected String nivelMonstruo;
    protected String resultadoFinal;
    protected String nivelAventureroFinal;

    /**
     * DATOS PARA COMUNICID 3: PVP
     */
    protected String id1;
    protected String ip1;
    protected String nivel1;
    protected String id2;
    protected String ip2;
    protected String nivel2;
    protected String reto;
    protected String resultado;
    protected String nivelFinal1;
    protected String nivelFinal2;

    protected LinkedList<AjrLocalizado> agentsDirectory;
    protected LinkedList<AjrLocalizado> deadAgents;

//CONSTRUCTOR DE MENSAJE


    public Mensaje(String msgId, String tipoProtocolo, String pasoProtocolo,
                   String originId, String originIp, String originPortUDP, String originTime,
                   String destinationId, String destinationIp, String destinationPortUDP, String destinationTime) {
        this.msgId = msgId;
        this.tipoProtocolo = tipoProtocolo;
        this.pasoProtocolo = pasoProtocolo;
        this.originId = originId;
        this.originIp = originIp;
        this.originPortUDP = originPortUDP;
        this.originTime = originTime;
        this.destinationId = destinationId;
        this.destinationIp = destinationIp;
        this.destinationPortUDP = destinationPortUDP;
        this.destinationTime = destinationTime;
    }

    //GETTERS

    public String getMsgId() {
        return msgId;
    }

    public String getTipoProtocolo() {
        return tipoProtocolo;
    }

    public String getPasoProtocolo() {
        return pasoProtocolo;
    }

    public String getOriginId() {
        return originId;
    }

    public String getOriginIp() {
        return originIp;
    }

    public String getOriginPortUDP() {
        return originPortUDP;
    }

    public String getOriginTime() {
        return originTime;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public String getDestinationPortUDP() {
        return destinationPortUDP;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public String getInfo() {
        return info;
    }

    public String getMotivoMuerte() {
        return motivoMuerte;
    }

    public String getAgenteFinalizadoNivel() {
        return agenteFinalizadoNivel;
    }

    public String getMonstruosDerrotados() {
        return monstruosDerrotados;
    }

    public String getDeathTime() {
        return deathTime;
    }

    public String getMazmorra() {
        return mazmorra;
    }

    public String getNivelAventurero() {
        return nivelAventurero;
    }

    public String getNombreMonstruo() {
        return nombreMonstruo;
    }

    public String getNivelMonstruo() {
        return nivelMonstruo;
    }

    public String getResultadoFinal() {
        return resultadoFinal;
    }

    public String getNivelAventureroFinal() {
        return nivelAventureroFinal;
    }

    public String getId1() {
        return id1;
    }

    public String getIp1() {
        return ip1;
    }

    public String getNivel1() {
        return nivel1;
    }

    public String getId2() {
        return id2;
    }

    public String getIp2() {
        return ip2;
    }

    public String getNivel2() {
        return nivel2;
    }

    public String getReto() {
        return reto;
    }

    public String getResultado() {
        return resultado;
    }

    public String getNivelFinal1() {
        return nivelFinal1;
    }

    public String getNivelFinal2() {
        return nivelFinal2;
    }

    public LinkedList<AjrLocalizado> getAgentsDirectory() {
        return agentsDirectory;
    }

    public LinkedList<AjrLocalizado> getDeadAgents() {
        return deadAgents;
    }

    //SETTERS


    public void setInfo(String info) {
        this.info = info;
    }

    public void setMotivoMuerte(String motivoMuerte) {
        this.motivoMuerte = motivoMuerte;
    }

    public void setAgenteFinalizadoNivel(String agenteFinalizadoNivel) {
        this.agenteFinalizadoNivel = agenteFinalizadoNivel;
    }

    public void setMonstruosDerrotados(String monstruosDerrotados) {
        this.monstruosDerrotados = monstruosDerrotados;
    }

    public void setDeathTime(String deathTime) {
        this.deathTime = deathTime;
    }

    public void setMazmorra(String mazmorra) {
        this.mazmorra = mazmorra;
    }

    public void setNivelAventurero(String nivelAventurero) {
        this.nivelAventurero = nivelAventurero;
    }

    public void setNombreMonstruo(String nombreMonstruo) {
        this.nombreMonstruo = nombreMonstruo;
    }

    public void setNivelMonstruo(String nivelMonstruo) {
        this.nivelMonstruo = nivelMonstruo;
    }

    public void setResultadoFinal(String resultadoFinal) {
        this.resultadoFinal = resultadoFinal;
    }

    public void setNivelAventureroFinal(String nivelAventureroFinal) {
        this.nivelAventureroFinal = nivelAventureroFinal;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public void setNivel1(String nivel1) {
        this.nivel1 = nivel1;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public void setNivel2(String nivel2) {
        this.nivel2 = nivel2;
    }

    public void setReto(String reto) {
        this.reto = reto;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public void setNivelFinal1(String nivelFinal1) {
        this.nivelFinal1 = nivelFinal1;
    }

    public void setNivelFinal2(String nivelFinal2) {
        this.nivelFinal2 = nivelFinal2;
    }

    public void setAgentsDirectory(LinkedList<AjrLocalizado> agentsDirectory) {
        this.agentsDirectory = agentsDirectory;
    }

    public void setDeadAgents(LinkedList<AjrLocalizado> deadAgents) {
        this.deadAgents = deadAgents;
    }

    /**
     * CREAR XML
     * Método que sirve para crear XML con los
     * atributos de la clase mensaje
     */
    public void crearXML() {
        try {

            // Crear un DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Crear un Document
            Document doc = builder.newDocument();

            // Crear elementos y construir la estructura
            Element mensajeElement = doc.createElement("Mensaje");
            doc.appendChild(mensajeElement);

            Element msgIdElement = doc.createElement("msg_id");
            msgIdElement.appendChild(doc.createTextNode(this.msgId));
            mensajeElement.appendChild(msgIdElement);

            // Crear el elemento header y agregarlo a Mensaje
            Element headerElement = doc.createElement("header");
            mensajeElement.appendChild(headerElement);

            // Añadir los elementos dentro de header
            Element ttipoProtocoloElement = doc.createElement("tipo_protocolo");
            ttipoProtocoloElement.appendChild(doc.createTextNode(this.tipoProtocolo));
            headerElement.appendChild(ttipoProtocoloElement);

            Element pasoProtocoloElement = doc.createElement("paso_protocolo");
            pasoProtocoloElement.appendChild(doc.createTextNode(this.pasoProtocolo));
            headerElement.appendChild(pasoProtocoloElement);

            Element originElement = doc.createElement("origin");
            headerElement.appendChild(originElement);

            Element originIdElement = doc.createElement("origin_id");
            originIdElement.appendChild(doc.createTextNode(this.originId));
            originElement.appendChild(originIdElement);

            Element originIpElement = doc.createElement("origin_ip");
            originIpElement.appendChild(doc.createTextNode(this.originIp));
            originElement.appendChild(originIpElement);

            Element originPortUDPElement = doc.createElement("origin_port_UDP");
            originPortUDPElement.appendChild(doc.createTextNode(this.originPortUDP));
            originElement.appendChild(originPortUDPElement);

            Element originTimeElement = doc.createElement("origin_time");
            originTimeElement.appendChild(doc.createTextNode(this.originTime));
            originElement.appendChild(originTimeElement);


            Element destinationElement = doc.createElement("destination");
            headerElement.appendChild(destinationElement);

            Element destinationIdElement = doc.createElement("destination_id");
            destinationIdElement.appendChild(doc.createTextNode(this.destinationId));
            destinationElement.appendChild(destinationIdElement);

            Element destinationIpElement = doc.createElement("destination_ip");
            destinationIpElement.appendChild(doc.createTextNode(this.destinationIp));
            destinationElement.appendChild(destinationIpElement);

            Element destinationPortUDPElement = doc.createElement("destination_port_UDP");
            destinationPortUDPElement.appendChild(doc.createTextNode(this.destinationPortUDP));
            destinationElement.appendChild(destinationPortUDPElement);

            Element destinationTimeElement = doc.createElement("destination_time");
            destinationTimeElement.appendChild(doc.createTextNode(this.destinationTime));
            destinationElement.appendChild(destinationTimeElement);


            // Creamos los elementos del body
            Element bodyElement = doc.createElement("body");
            mensajeElement.appendChild(bodyElement);

            Element infoElement = doc.createElement("info");
            infoElement.appendChild(doc.createTextNode(this.info));
            bodyElement.appendChild(infoElement);

            // Este es el contenido de finalizacionAgente()
            Element contenidoFinalizacionElement = doc.createElement("contenido_finalizacion");
            bodyElement.appendChild(contenidoFinalizacionElement);

            Element motivoMuerteElement = doc.createElement("motivo_muerte");
            motivoMuerteElement.appendChild(doc.createTextNode(this.motivoMuerte));
            contenidoFinalizacionElement.appendChild(motivoMuerteElement);

            Element agenteFinalizadoInfoElement = doc.createElement("agente_finalizado_info");
            contenidoFinalizacionElement.appendChild(agenteFinalizadoInfoElement);

            Element agenteFinalizadoNivelElement = doc.createElement("agente_finalizado_nivel");
            agenteFinalizadoNivelElement.appendChild(doc.createTextNode(this.agenteFinalizadoNivel));
            agenteFinalizadoInfoElement.appendChild(agenteFinalizadoNivelElement);

            Element monstruosDerrotadosElement = doc.createElement("monstruos_derrotados");
            monstruosDerrotadosElement.appendChild(doc.createTextNode(this.monstruosDerrotados));
            agenteFinalizadoInfoElement.appendChild(monstruosDerrotadosElement);

            Element deathTimeElement = doc.createElement("death_time");
            deathTimeElement.appendChild(doc.createTextNode(this.deathTime));
            agenteFinalizadoInfoElement.appendChild(deathTimeElement);


            // Este es el contenido de lucharMonstruos()
            Element contenidoMonstruoElement = doc.createElement("contenido_monstruo");
            bodyElement.appendChild(contenidoMonstruoElement);

            Element mazmorraElement = doc.createElement("mazmorra");
            mazmorraElement.appendChild(doc.createTextNode(this.mazmorra));
            contenidoMonstruoElement.appendChild(mazmorraElement);

            Element nivelAventureroElement = doc.createElement("nivel_aventurero");
            nivelAventureroElement.appendChild(doc.createTextNode(this.nivelAventurero));
            contenidoMonstruoElement.appendChild(nivelAventureroElement);

            Element nombreMonstruoElement = doc.createElement("nombre_monstruo");
            nombreMonstruoElement.appendChild(doc.createTextNode(this.nombreMonstruo));
            contenidoMonstruoElement.appendChild(nombreMonstruoElement);

            Element nivelMonstruoElement = doc.createElement("nivel_monstruo");
            nivelMonstruoElement.appendChild(doc.createTextNode(this.nivelMonstruo));
            contenidoMonstruoElement.appendChild(nivelMonstruoElement);

            Element resultadoFinalElement = doc.createElement("resultado_final");
            resultadoFinalElement.appendChild(doc.createTextNode(this.resultadoFinal));
            contenidoMonstruoElement.appendChild(resultadoFinalElement);

            Element nivelAventureroFinalElement = doc.createElement("nivel_aventurero_final");
            nivelAventureroFinalElement.appendChild(doc.createTextNode(this.nivelAventureroFinal));
            contenidoMonstruoElement.appendChild(nivelAventureroFinalElement);


            // Este es el contenido de lucharPVP()
            Element contenidoPvpElement = doc.createElement("contenido_PVP");
            bodyElement.appendChild(contenidoPvpElement);

            Element id1Element = doc.createElement("id_1");
            id1Element.appendChild(doc.createTextNode(this.id1));
            contenidoPvpElement.appendChild(id1Element);

            Element ip1Element = doc.createElement("ip_1");
            ip1Element.appendChild(doc.createTextNode(this.ip1));
            contenidoPvpElement.appendChild(ip1Element);

            Element nivel1Element = doc.createElement("nivel_1");
            nivel1Element.appendChild(doc.createTextNode(this.nivel1));
            contenidoPvpElement.appendChild(nivel1Element);

            Element id2Element = doc.createElement("id_2");
            id2Element.appendChild(doc.createTextNode(this.id2));
            contenidoPvpElement.appendChild(id2Element);

            Element ip2Element = doc.createElement("ip_2");
            ip2Element.appendChild(doc.createTextNode(this.ip2));
            contenidoPvpElement.appendChild(ip2Element);

            Element nivel2Element = doc.createElement("nivel_2");
            nivel2Element.appendChild(doc.createTextNode(this.nivel2));
            contenidoPvpElement.appendChild(nivel2Element);

            Element retoElement = doc.createElement("reto");
            retoElement.appendChild(doc.createTextNode(this.reto));
            contenidoPvpElement.appendChild(retoElement);

            Element resultadoElement = doc.createElement("resultado");
            resultadoElement.appendChild(doc.createTextNode(this.resultado));
            contenidoPvpElement.appendChild(resultadoElement);

            Element nivelFinal1Element = doc.createElement("nivel_final_1");
            nivelFinal1Element.appendChild(doc.createTextNode(this.nivelFinal1));
            contenidoPvpElement.appendChild(nivelFinal1Element);

            Element nivelFinal2Element = doc.createElement("nivel_final_2");
            nivelFinal2Element.appendChild(doc.createTextNode(this.nivelFinal2));
            contenidoPvpElement.appendChild(nivelFinal2Element);


            // Estos son los datos comunes
            Element commonContentElement = doc.createElement("common_content");
            mensajeElement.appendChild(commonContentElement);

            Element agentsDirectoryElement = doc.createElement("agents_directory");
            commonContentElement.appendChild(agentsDirectoryElement);

            if (this.agentsDirectory != null) {
                for (int i = 1; i <= this.agentsDirectory.size(); i++) {
                    String text5 = "ajr";

                    Element ajrElement = doc.createElement(text5);
                    agentsDirectoryElement.appendChild(ajrElement);

                    Element idAjrElement = doc.createElement("id");
                    idAjrElement.appendChild(doc.createTextNode(this.agentsDirectory.get(i - 1).ID));
                    ajrElement.appendChild(idAjrElement);

                    Element portUDPAccElement = doc.createElement("port_UDP");
                    portUDPAccElement.appendChild(doc.createTextNode(Integer.toString(this.agentsDirectory.get(i - 1).puerto)));
                    ajrElement.appendChild(portUDPAccElement);

                    Element ipAccElement = doc.createElement("ip");
                    ipAccElement.appendChild(doc.createTextNode(this.agentsDirectory.get(i - 1).IP));
                    ajrElement.appendChild(ipAccElement);

                }
            }

            Element deadAgentsElement = doc.createElement("dead_agents");
            commonContentElement.appendChild(deadAgentsElement);
            if (this.deadAgents != null) {
                for (int i = 1; i <= this.deadAgents.size(); i++) {
                    String text6 = "dead_ajr";

                    Element deadAjrElement = doc.createElement(text6);
                    deadAgentsElement.appendChild(deadAjrElement);

                    Element idAjrElement = doc.createElement("id");
                    idAjrElement.appendChild(doc.createTextNode(this.deadAgents.get(i - 1).ID));
                    deadAjrElement.appendChild(idAjrElement);
                }
            }

            // Escribir el DOM en un archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Configuración de la salida
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            //System.out.println("Llego hasta aquí");
            File file = new File("xml_" + msgId + ".xml");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);

            System.out.println("Archivo XML creado correctamente.");
        } catch(ParserConfigurationException | TransformerException e){
            e.printStackTrace();

        }
    }
}