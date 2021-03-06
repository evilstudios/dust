package com.billylindeman.dust.layer;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.billylindeman.dust.util.Color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class TexturedRect {

    private float vertices[]={
            -1.0f, 1.0f, 0.0f,  //top left
            -1.0f,-1.0f,0.0f,   //bottom left
            1.0f,-1.0f,0.0f,    //bottom right
            1.0f,1.0f,0.0f      //top right
    };

    private float texcoords[]={
            0.0f, 0.0f,  //top left
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            1.0f, 0.0f,  //top right
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
    };

    final int[] textureHandle = new int[1];
    boolean textureIsRegistered = false;

    private short[] indices = {0,1,2,0,2,3};

    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer colorBuffer;

    public TexturedRect(){
        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        texCoordBuffer = makeFloatBuffer(texcoords);

        ByteBuffer colorbb = ByteBuffer.allocateDirect(16*4);
        colorbb.order(ByteOrder.nativeOrder());
        colorBuffer = colorbb.asFloatBuffer();
    }

    public void registerTextureHandle(GL10 gl, final Bitmap bitmap) {
        /** Load bitmap into GL texture */
        gl.glGenTextures(1, textureHandle, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureHandle[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        textureIsRegistered = true;
    }

    public void freeTextureHandle(GL10 gl) {
        gl.glDeleteTextures(1, textureHandle, 0);
        textureIsRegistered = false;
    }

    public boolean hasTexture() {
        return textureIsRegistered;
    }

    public void draw(GL10 gl,float size,  Color c){
        float cb[] = {
                c.r,c.g,c.b,c.a,
                c.r,c.g,c.b,c.a,
                c.r,c.g,c.b,c.a,
                c.r,c.g,c.b,c.a,
        };
        colorBuffer.put(cb);
        colorBuffer.position(0);

        size *= .5f;
        float vertices[]={
                -size, size, 0.0f,  //top left
                -size,-size,0.0f,   //bottom left
                size,-size,0.0f,    //bottom right
                size,size,0.0f      //top right
        };

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureHandle[0]);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);


        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2,GL10.GL_FLOAT, 0, texCoordBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    public static FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }
}