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

    protected String comuncId;
    protected String msgId; // IP del agente que envía el mensaje
    protected String typeProtocol; //Indica el protcolo del mensaje (intercambio, etc.)
    protected String protocolStep; //paso del protocolo
    protected String comunicationProtocol;  // Se admiten protocolos TCP y UDP
    protected String originId; // Identificador único del agente que envía el mensaje
    protected String originIp; // IP del agente que envía el mensaje

    protected String originPortUDP; // Puerto del agente que envía el mensaje
    protected String originPortTCP; // Puerto del agente que envía el mensaje
    protected String originTime;

    protected String destinationId; // Identificador único del agente destino de este mensaje
    protected String destinationIp; // IP del agente destino de este mensaje
    protected String destinationPortUDP;
    protected String destinationPortTCP;
    protected String destinationTime;
    protected String bodyInfo;  //Contenido del mensaje a enviar. s el contenido de este campo, lo que viajara al destino
    protected String deathReason;
    protected ArrayList<String> ownedCardType;
    protected ArrayList<String> ownedCardQuantity;
    protected ArrayList<String> ownedCardCost;
    protected ArrayList<String> wantedCardType;
    protected String ownedMoney;
    protected String createdChilds;
    protected String deathTime;
    protected String pastTradeWantedCard;
    protected String pastTradeGivenCard;
    protected String tradeWantedCard;
    protected String tradeGivenCard;
    protected String tradeMoney;

    protected ArrayList<String> offeredCardType;
    protected ArrayList<String> offeredCardQuantity;
    protected ArrayList<String> offeredCardCost;
    protected ArrayList<String> wishedCardType;
    protected LinkedList<AccLocalizado> agentsDirectory;
    protected LinkedList<AccLocalizado> deadAgents;

//CONSTRUCTOR DE MENSAJE


    public Mensaje(String comuncId, String msgId, String typeProtocol, String protocolStep, String comunicationProtocol,
                   String originId, String originIp, String originPortUDP, String originPortTCP, String originTime,
                   String destinationId, String destinationIp, String destinationPortUDP, String destinationPortTCP, String destinationTime) {
        this.comuncId = comuncId;
        this.msgId = msgId;
        this.typeProtocol = typeProtocol;
        this.protocolStep = protocolStep;
        this.comunicationProtocol = comunicationProtocol;
        this.originId = originId;
        this.originIp = originIp;
        this.originPortUDP = originPortUDP;
        this.originPortTCP = originPortTCP;
        this.originTime = originTime;
        this.destinationId = destinationId;
        this.destinationIp = destinationIp;
        this.destinationPortUDP = destinationPortUDP;
        this.destinationPortTCP = destinationPortTCP;
        this.destinationTime = destinationTime;
    }

    //GETTERS
    public String getComuncId() {
        return comuncId;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getTypeProtocol() {
        return typeProtocol;
    }

    public String getProtocolStep() {
        return protocolStep;
    }

    public String getComunicationProtocol() {
        return comunicationProtocol;
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

    public String getOriginPortTCP() {
        return originPortTCP;
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

    public String getDestinationPortTCP() {
        return destinationPortTCP;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public String getBodyInfo() {
        return bodyInfo;
    }

    public String getDeathReason() {
        return deathReason;
    }

    public ArrayList<String> getOwnedCardType() {
        return ownedCardType;
    }

    public ArrayList<String> getOwnedCardQuantity() {
        return ownedCardQuantity;
    }

    public ArrayList<String> getOwnedCardCost() {
        return ownedCardCost;
    }

    public ArrayList<String> getWantedCardType() {
        return wantedCardType;
    }

    public String getOwnedMoney() {
        return ownedMoney;
    }

    public String getCreatedChilds() {
        return createdChilds;
    }

    public String getDeathTime() {
        return deathTime;
    }

    public String getPastTradeWantedCard() {
        return pastTradeWantedCard;
    }

    public String getPastTradeGivenCard() {
        return pastTradeGivenCard;
    }

    public String getTradeWantedCard() {
        return tradeWantedCard;
    }

    public String getTradeGivenCard() {
        return tradeGivenCard;
    }

    public String getTradeMoney() {
        return tradeMoney;
    }

    public ArrayList<String> getOfferedCardType() {
        return offeredCardType;
    }

    public ArrayList<String> getOfferedCardQuantity() {
        return offeredCardQuantity;
    }

    public ArrayList<String> getOfferedCardCost() {
        return offeredCardCost;
    }

    public ArrayList<String> getWishedCardType() {
        return wishedCardType;
    }

    public LinkedList<AccLocalizado> getAgentsDirectory() {
        return agentsDirectory;
    }

    public LinkedList<AccLocalizado> getDeadAgents() {
        return deadAgents;
    }

    //SETTERS
    public void setBodyInfo(String bodyInfo) {
        this.bodyInfo = bodyInfo;
    }

    public void setDeathReason(String deathReason) {
        this.deathReason = deathReason;
    }

    public void setOwnedCardType(ArrayList<String> ownedCardType) {
        this.ownedCardType = ownedCardType;
    }

    public void setOwnedCardQuantity(ArrayList<String> ownedCardQuantity) {
        this.ownedCardQuantity = ownedCardQuantity;
    }

    public void setOwnedCardCost(ArrayList<String> ownedCardCost) {
        this.ownedCardCost = ownedCardCost;
    }

    public void setWantedCardType(ArrayList<String> wantedCardType) {
        this.wantedCardType = wantedCardType;
    }

    public void setOwnedMoney(String ownedMoney) {
        this.ownedMoney = ownedMoney;
    }

    public void setCreatedChilds(String createdChilds) {
        this.createdChilds = createdChilds;
    }

    public void setDeathTime(String deathTime) {
        this.deathTime = deathTime;
    }

    public void setPastTradeWantedCard(String pastTradeWantedCard) {
        this.pastTradeWantedCard = pastTradeWantedCard;
    }

    public void setPastTradeGivenCard(String pastTradeGivenCard) {
        this.pastTradeGivenCard = pastTradeGivenCard;
    }

    public void setTradeWantedCard(String tradeWantedCard) {
        this.tradeWantedCard = tradeWantedCard;
    }

    public void setTradeGivenCard(String tradeGivenCard) {
        this.tradeGivenCard = tradeGivenCard;
    }

    public void setTradeMoney(String tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public void setOfferedCardType(ArrayList<String> offeredCardType) {
        this.offeredCardType = offeredCardType;
    }

    public void setOfferedCardQuantity(ArrayList<String> offeredCardQuantity) {
        this.offeredCardQuantity = offeredCardQuantity;
    }

    public void setOfferedCardCost(ArrayList<String> offeredCardCost) {
        this.offeredCardCost = offeredCardCost;
    }

    public void setWishedCardType(ArrayList<String> wishedCardType) {
        this.wishedCardType = wishedCardType;
    }

    public void setAgentsDirectory(LinkedList<AccLocalizado> agentsDirectory) {
        this.agentsDirectory = agentsDirectory;
    }

    public void setDeadAgents(LinkedList<AccLocalizado> deadAgents) {
        this.deadAgents = deadAgents;
    }

    /**
     * CREAR XML
     * Método que sirve para crear XML con los
     * atributos de la clase mensaje
     */
    public void crearXML() {
    }
}