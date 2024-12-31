package com.marbro.TileMapHelpers;

import com.badlogic.gdx.physics.box2d.*;

import static com.marbro.constants.Constantes.*;


public class BodyHelperService
{
    //atributos
    private FixtureDef fixtureDef;

    public Body createBody(float x, float y, float width, float height, boolean isStatic, boolean isKinetic, World world)
    {
        //bodydef
        BodyDef bodyDef = new BodyDef();
        if(isKinetic)
        {
            bodyDef.type = BodyDef.BodyType.KinematicBody;
        }
        else
        {
            bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        }
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = true;

        //creacion body
        Body body = world.createBody(bodyDef);

        //creacion fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);

        return body;
    }

    public FixtureDef getFixtureDef()
    {
        return fixtureDef;
    }
}
