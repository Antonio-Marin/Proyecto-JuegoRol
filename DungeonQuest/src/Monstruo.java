package DungeonQuest.src;

public class Monstruo {
    protected String nombre;
    protected int nivel;

    public Monstruo(String nombre, int nivel) {
        this.nombre = nombre;
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return "Monstruo{" + "nombre='" + nombre + '\'' + ", nivel=" + nivel + '}';
    }
}
