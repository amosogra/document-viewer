/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.emdev.ui.gl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public class RawTexture extends BasicTexture {

    private final static int[] sTextureId = new int[1];
    private final static float[] sCropRect = new float[4];

    private final boolean mOpaque;

    public RawTexture(final int width, final int height, final boolean opaque) {
        mOpaque = opaque;
        setSize(width, height);
    }

    @Override
    public boolean isOpaque() {
        return mOpaque;
    }

    protected void prepare(final GLCanvas canvas) {
        final GL11 gl = canvas.getGLInstance();

        // Define a vertically flipped crop rectangle for
        // OES_draw_texture.
        // The four values in sCropRect are: left, bottom, width, and
        // height. Negative value of width or height means flip.
        sCropRect[0] = 0;
        sCropRect[1] = mHeight;
        sCropRect[2] = mWidth;
        sCropRect[3] = -mHeight;

        // Upload the bitmap to a new texture.
        GLId.glGenTextures(1, sTextureId, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, sTextureId[0]);
        gl.glTexParameterfv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, sCropRect, 0);
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, getTextureWidth(), getTextureHeight(), 0, GL10.GL_RGBA,
                GL10.GL_UNSIGNED_BYTE, null);

        mId = sTextureId[0];
        mState = STATE_LOADED;
        setAssociatedCanvas(canvas);
    }

    @Override
    protected boolean onBind(final GLCanvas canvas) {
        return isLoaded();
    }

    @Override
    public void yield() {
        // we cannot free the texture because we have no backup.
    }

    @Override
    protected int getTarget() {
        return GL10.GL_TEXTURE_2D;
    }
}