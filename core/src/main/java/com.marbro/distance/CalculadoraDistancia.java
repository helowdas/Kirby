package com.marbro.distance;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static com.marbro.constants.Constantes.*;

public class CalculadoraDistancia {
    /**
     * Encuentra el cuerpo más cercano dentro de un cierto rango de un cuerpo específico,
     * excluyendo ciertos tipos de cuerpos según las categorías e incluyendo bloques.
     *
     * @param targetBody El cuerpo objetivo.
     * @param mundo El mundo de Box2D que contiene todos los cuerpos.
     * @param rango La distancia máxima permitida.
     * @param excludedCategories Las categorías de los cuerpos a excluir.
     * @param includedCategories La categoría de los bloques a incluir.
     * @return El cuerpo más cercano, o null si no hay cuerpos dentro del rango.
     */
    public static Body encontrarCuerpoMasCercano(Body targetBody, World mundo, float rango, short excludedCategories, short includedCategories) {
        Body cuerpoMasCercano = null;
        Vector2 targetPos = targetBody.getPosition();
        float distanciaMinima = rango;

        Array<Body> cuerpos = new Array<>();
        mundo.getBodies(cuerpos);

        for (Body body : cuerpos) {
            Filter filter = body.getFixtureList().first().getFilterData();

            if (!body.equals(targetBody)) { // Evitar incluir el cuerpo objetivo en sí mismo
                boolean excluir = (filter.categoryBits & excludedCategories) != 0;
                boolean incluir = (filter.categoryBits & includedCategories) != 0 || (filter.categoryBits & CATEGORY_ENEMY) != 0; // Incluir enemigos también

                if (!excluir && incluir) { // Incluir solo bloques y enemigos, y excluir categorías no deseadas
                    Vector2 bodyPos = body.getPosition();
                    float distancia = targetPos.dst(bodyPos);
                    if (distancia <= distanciaMinima) {
                        distanciaMinima = distancia;
                        cuerpoMasCercano = body;
                    }
                }
            }
        }

        return cuerpoMasCercano;
    }
}
