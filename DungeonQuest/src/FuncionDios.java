import java.util.Random;

public class FuncionDios implements Runnable {

    // Para pruebas
    protected int num_men_recibidos_monitor; // Para identificar los mensajes recibidos por este agente y poder identificarlos de forma unívoca
    protected Acc agente; // Para poder acceder a los datos generales de este agente

    FuncionMonitor(Acc este_agente) {
        num_men_recibidos_monitor = 0;
        this.agente = este_agente;

        // arrancamos el hilo
        new Thread(this, "FuncionMonitor").start();
    }

    @Override
    public void run() {
        System.out.println( "\n ==> El MONITOR : "+ this.agente.ID_propio+
                " - desde la ip : "+ this.agente.Ip_Propia+
                " Arranca el hilo  : FuncionMonitor");

        while(true){
            // //////////////////////////////////////
            // Obtenemos los mensajes recibidos
                // Miramos si hay algun mensaje recibido y si lo hay lo recogemos
            //System.out.println("no hay mensaje");
            if(this.agente.num_elem_lita_recibidos() > 0) {
                System.out.println("He llegado aqui");
            recogeMensajeRecibido();
            }
        } // Fin de while(true){
    } // Fin de - public void run() {

    void recogeMensajeRecibido() {
        System.out.println(" => DESDE EL MONITOR. Vamos  a recogeMensajeRecibido. Con : " + agente.num_elem_lita_recibidos() + "mensajes en cola");
        // Obtenemos el mensaje
        Mensaje mensajeRecibido = agente.saca_de_lita_recibidos();
        mensajeRecibido.crearXML();

        num_men_recibidos_monitor++;
        String momento_actual_str = String.valueOf(System.currentTimeMillis());
        String puerto_origen_str = String.valueOf(mensajeRecibido.originPortUDP);

        System.out.println(" => DESDE EL MONITOR. Con num rec : " + num_men_recibidos_monitor +
                                    " - en T : " +momento_actual_str +
                                    " - El agente : "+ mensajeRecibido.originId +
                                    " - desde la ip : "+ mensajeRecibido.originIp +
                                    " - puerto : "+ puerto_origen_str +
                                    " - protocolo : "+ mensajeRecibido.comunicationProtocol +
                                    " - envio el mensaje \n  * Mensaje recibido : "+ mensajeRecibido.bodyInfo);
    } // Fin de - void recogeMensajeRecibido() {


} // FIn de - public class FuncionDeAgente implements Runnable {
