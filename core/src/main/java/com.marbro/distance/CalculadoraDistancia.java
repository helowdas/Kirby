package com.marbro.distance;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class CalculadoraDistancia {
    /**
     * Encuentra el cuerpo más cercano dentro de un cierto rango de un cuerpo específico,
     * excluyendo el propio cuerpo objetivo.
     *
     * @param targetBody El cuerpo objetivo.
     * @param mundo El mundo de Box2D que contiene todos los cuerpos.
     * @param rango La distancia máxima permitida.
     * @return El cuerpo más cercano, o null si no hay cuerpos dentro del rango.
     */
    public static Body encontrarCuerpoMasCercano(Body targetBody, World mundo, float rango) {
        Body cuerpoMasCercano = null;
        Vector2 targetPos = targetBody.getPosition();
        float distanciaMinima = rango;

        Array<Body> cuerpos = new Array<>();
        mundo.getBodies(cuerpos);

        for (Body body : cuerpos) {
            if (!body.equals(targetBody)) { // Evitar incluir el cuerpo objetivo en sí mismo
                Vector2 bodyPos = body.getPosition();
                float distancia = targetPos.dst(bodyPos);
                if (distancia <= distanciaMinima) {
                    distanciaMinima = distancia;
                    cuerpoMasCercano = body; // Obtener el cuerpo más cercano
                }
            }
        }

        return cuerpoMasCercano;
    }
}
