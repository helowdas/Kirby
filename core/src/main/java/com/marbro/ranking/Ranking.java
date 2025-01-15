package com.marbro.ranking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.marbro.MainGame.puntuacion_archivos;
import static com.marbro.MainGame.usuario;


public class Ranking {

    public static Ranking ranking;
    public boolean nuevo = false;

    private Ranking() {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");
    }

    public static Ranking getInstance() {
        if (ranking == null) {
            ranking = new Ranking();
        }
        return ranking;
    }

    // Método para verificar si un jugador existe en el archivo
    public boolean jugadorRegistrado(String nombreJugador) {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");
        String[] lineas = archivo.readString().split("\n");

        for (String linea : lineas) {
            if (linea.startsWith(nombreJugador + ",")) {
                return true;
            }
        }
        return false;
    }

    // Función para guardar las puntuaciones en el archivo
    public void guardarPuntuaciones(String nombreJugador, int puntuacion) {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");

        if (!jugadorRegistrado(nombreJugador)) {
            archivo.writeString(nombreJugador + "," + puntuacion + "\n", true);
            nuevo = true;
        } else {
            nuevo = false;
        }
    }

    // Función para obtener y ordenar el ranking
    public List<String> obtenerRanking() {
        List<String> listaPuntuaciones = new ArrayList<>();
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");

        String[] lineas = archivo.readString().split("\n");
        for (String linea : lineas) {
            listaPuntuaciones.add(linea);
        }

        Collections.sort(listaPuntuaciones, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int puntuacion1 = Integer.parseInt(o1.split(",")[1]);
                int puntuacion2 = Integer.parseInt(o2.split(",")[1]);
                return Integer.compare(puntuacion2, puntuacion1);
            }
        });

        return listaPuntuaciones;
    }

    // Función para modificar la puntuación de un jugador existente
    public void modificarPuntuacion(String nombreJugador, int nuevaPuntuacion) {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");
        String[] lineas = archivo.readString().split("\n");
        StringBuilder contenidoModificado = new StringBuilder();
        int puntuacionAnterior = -1;

        for (String linea : lineas) {
            if (linea.startsWith(nombreJugador + ",")) {
                String[] partes = linea.split(",");
                puntuacionAnterior = Integer.parseInt(partes[1]);
                linea = nombreJugador + "," + (nuevaPuntuacion + puntuacionAnterior);
            }
            contenidoModificado.append(linea).append("\n");
        }

        archivo.writeString(contenidoModificado.toString(), false);

        // borrar esto, es para debuguear
        if (puntuacionAnterior != -1) {
            System.out.println("La puntuación anterior de " + nombreJugador + " era: " + puntuacionAnterior);
        } else {
            System.out.println("No ha iniciado sesion");
        }
    }


    // Método para registrar un jugador usando JOptionPane
    public String registrarJugador() {
        String nombreJugador = JOptionPane.showInputDialog("Ingrese el nombre del jugador:");
        if (!validarEntrada(nombreJugador)) {
            JOptionPane.showMessageDialog(null, "El nuevo nombre solo puede contener letras y números. Por favor, intente de nuevo.");
            return null;
        }
        if (nombreJugador != null && !nombreJugador.isEmpty()) {
            guardarPuntuaciones(nombreJugador, 0);
            if (!nuevo) {
                JOptionPane.showMessageDialog(null, "Bienvenido de nuevo! " + nombreJugador);
            }else{
                JOptionPane.showMessageDialog(null, "Bienvenido nuevo usuario! " + nombreJugador);
            }
        } else {
            JOptionPane.showMessageDialog(null, "El nombre no puede estar vacio");
        }
        return nombreJugador;
    }

    // Método para eliminar una línea que contenga al jugador pasado por parámetros
    public void eliminarJugador() {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");
        String nombreJugador = JOptionPane.showInputDialog("Ingrese el nombre del jugador que desea eliminar:)");
        String[] lineas = archivo.readString().split("\n");
        StringBuilder contenidoModificado = new StringBuilder();
        boolean borrado = false;
        for (String linea : lineas) {
            if (!linea.startsWith(nombreJugador + ",")) {
                contenidoModificado.append(linea).append("\n");
            } else {
                borrado = true;
            }
        }
        if (borrado) {
            archivo.writeString(contenidoModificado.toString(), false);
            JOptionPane.showMessageDialog(null, "El jugador " + nombreJugador + " ha sido eliminado del archivo.");
        } else {
            JOptionPane.showMessageDialog(null, "El jugador " + nombreJugador + " no existe.");
        }
    }

    // Método para obtener la puntuación de un jugador
    public static int obtenerPuntuacion() {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");
        String[] lineas = archivo.readString().split("\n");

        for (String linea : lineas) {
            if (linea.startsWith(usuario + ",")) {
                String[] partes = linea.split(",");
                return Integer.parseInt(partes[1]);
            }
        }
        return 0;
    }

    // Método para cambiar el nombre de un jugador existente con validación de entrada
    public void cambiarNombreJugador(String nombreActual) {
        FileHandle archivo = Gdx.files.local("game_data/score/score.txt");
        String[] lineas = archivo.readString().split("\n");
        StringBuilder contenidoModificado = new StringBuilder();
        boolean nombreActualEncontrado = false;
        boolean nuevoNombreExistente = false;

        // Pedir el nuevo nombre al usuario
        String nuevoNombre = JOptionPane.showInputDialog("Ingrese el nuevo nombre del jugador:");

        // Validar la entrada del nuevo nombre
        if (!validarEntrada(nuevoNombre)) {
            JOptionPane.showMessageDialog(null, "El nuevo nombre solo puede contener letras y números. Por favor, intente de nuevo.");
            return;
        }

        // Verificar si el nuevo nombre ya está en uso
        for (String linea : lineas) {
            if (linea.startsWith(nuevoNombre + ",")) {
                nuevoNombreExistente = true;
                break;
            }
        }

        if (nuevoNombreExistente) {
            JOptionPane.showMessageDialog(null, "El nombre del jugador " + nuevoNombre + " ya está en uso. Por favor, elige otro nombre.");
            return;
        }

        // Modificar el nombre del jugador si el nombre actual existe y el nuevo nombre no está en uso
        for (String linea : lineas) {
            if (linea.startsWith(nombreActual + ",")) {
                String[] partes = linea.split(",");
                linea = nuevoNombre + "," + partes[1];
                nombreActualEncontrado = true;
            }
            contenidoModificado.append(linea).append("\n");
        }

        if (nombreActualEncontrado) {
            archivo.writeString(contenidoModificado.toString(), false);
            JOptionPane.showMessageDialog(null, "El nombre del jugador ha sido cambiado de " + nombreActual + " a " + nuevoNombre + ".");
        } else {
            JOptionPane.showMessageDialog(null, "El nombre del jugador " + nombreActual + " no existe.");
        }
    }


    public void cerrarSesion(){
        if (usuario != null) {}
        usuario = null;
        puntuacion_archivos = 0;
    }

    // Método para validar que la entrada solo contenga letras, números y un espacio entre caracteres, y que comience con un carácter alfabético
    public boolean validarEntrada(String entrada) {
        return entrada.matches("^[a-zA-Z](?:[a-zA-Z0-9]*(?: [a-zA-Z0-9]+)*)$");
    }

}
