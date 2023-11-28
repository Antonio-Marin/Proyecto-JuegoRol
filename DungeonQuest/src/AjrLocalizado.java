import java.util.Date;

public class AjrLocalizado {
    // Los siguientes son los datos del agente localizado
    protected String ID; // Identificador único
    protected String IP;
    protected int puerto;
    protected long fecha_encontrado;

    AccLocalizado(String ID, String IP, int puerto, long fecha_encontrado){
        this.ID = ID;
        this.IP = IP;
        this.puerto = puerto;
        this.fecha_encontrado = fecha_encontrado;
    }

    @Override
    public String toString() {
        String puerto_str = String.valueOf(this.puerto);
        String fecha_encontrado_str = String.valueOf(this.fecha_encontrado);
        return "Datos Agente: \t ID: " + this.ID + "\tIP: " + this.IP + "\tPuerto:" + puerto_str + "\t Fecha localización: " +fecha_encontrado_str + "\n" ;
        //return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AccLocalizado))
            return false;

        return this.ID.equals(((AccLocalizado) obj).ID) && this.IP.equals(((AccLocalizado) obj).IP) && this.puerto == (((AccLocalizado) obj).puerto);
    }
}
