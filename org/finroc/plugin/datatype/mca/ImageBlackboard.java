/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2007-2010 Max Reichardt,
 *   Robotics Research Lab, University of Kaiserslautern
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.finroc.plugin.datatype.mca;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.finroc.core.portdatabase.DataType;
import org.finroc.log.LogLevel;
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.blackboard.BlackboardPlugin;
import org.finroc.plugin.datatype.Blittable;
import org.finroc.plugin.datatype.DataTypePlugin;
import org.finroc.plugin.datatype.HasBlittable;

/**
 * @author max
 *
 * Image-Blackboard
 */
public class ImageBlackboard extends BlackboardBuffer implements HasBlittable {

    public static DataType TYPE = BlackboardPlugin.registerBlackboardType(ImageBlackboard.class, "2D Image");
    public static DataType MTYPE = TYPE.getRelatedType();

    /** Structure to access image information */
    private final MCA.tImageInfo info = new MCA.tImageInfo();

    /** Current object for blitting */
    private BlackboardBlitter blitter;

    /** Type of blittable object */
    private int lastType = -1;

    /** The size of tImageInfo plus alignment padding (see tImage.h) */
    private static final int cImageInfoSizeWithPadding = MCA.cImageInfoSizeWithPadding;

    @Override
    public Blittable getBlittable() {
        synchronized (info) {
            info.setBuffer(getBuffer());
            int type = info.format.get();
            if (type == lastType) {
                blitter.reinit();
                return blitter;
            }

            // init blitter object
            switch (type) {
            case MCA.eIMAGE_FORMAT_RGB32:
                blitter = new RGB32();
                break;
            case MCA.eIMAGE_FORMAT_RGB24:
                blitter = new RGB24();
                break;
            case MCA.eIMAGE_FORMAT_BGR24:
                blitter = new BGR24();
                break;
            case MCA.eIMAGE_FORMAT_MONO8:
                blitter = new Mono8();
                break;
            case MCA.eIMAGE_FORMAT_MONO16:
                blitter = new Mono16();
                break;
            case MCA.eIMAGE_FORMAT_RGB565:
                blitter = new RGB565();
                break;
            case MCA.eIMAGE_FORMAT_YUV444:
                blitter = new YUV444();
                break;
            case MCA.eIMAGE_FORMAT_YUV422:
                blitter = new YUV422();
                break;
            case MCA.eIMAGE_FORMAT_YUV420P:
                blitter = new YUV420P();
                break;
            default:
                DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "GeometryBlackboard", "warning (ImageBlackboard): Image format " + type + " not supported yet");
                return Blittable.Empty.instance;
            }

            lastType = type;
            return blitter;
        }
    }

    public abstract class BlackboardBlitter extends Blittable {

        /** UID */
        private static final long serialVersionUID = -6221787372563681396L;

        /** Width height and widthStep */
        protected int width, height, widthStep, srcY;
        public transient ByteBuffer imageData;

        public void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int srcY, int srcOffset, int width) {

            int offset = cImageInfoSizeWithPadding + widthStep * srcY;
            this.srcY = srcY;
            blitLineToRGB(destBuffer, destOffset, srcX, offset, width);
        }

        protected abstract void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width);

        /**
         * (re-)initialize
         */
        public void reinit() {
            width = (int)info.width.get();
            height = (int)info.height.get();
            widthStep = (int)info.width_step.get();
            imageData = getBuffer().getBuffer();
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        protected int toInt(byte r, byte g, byte b) {
            return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
        }

        protected int yuvToRGB(byte y, byte u, byte v) {
            int _y = y & 0xFF;
            int _u = (u & 0xFF) - 128;
            int _v = (v & 0xFF) - 128;
            int _r = _y + ((_v * 1436) >> 10);
            int _g = _y - ((_u * 352 + _v * 731) >> 10);
            int _b = _y + ((_u * 1814) >> 10);
            int r = _r < 0 ? 0 : (_r > 255 ? 255 : _r);
            int g = _g < 0 ? 0 : (_g > 255 ? 255 : _g);
            int b = _b < 0 ? 0 : (_b > 255 ? 255 : _b);
            return toInt((byte)r, (byte)g, (byte)b);
        }
    }

    /* void tImage::GetPixelRGB(unsigned int x, unsigned int y, unsigned char& col_r, unsigned char& col_g, unsigned char& col_b)
    {
      if (x < 0 | x >= _image_info->width | y < 0 | y >= _image_info->height)
      {
        fprintf(stderr, "tImage::GetPixelRGB>> Pixel position (%d,%d) exceeds image dimensions!\n", x, y);
        return ;
      }

      switch (_image_info->format)
      {
      case eIMAGE_FORMAT_MONO8:
      {
        char grey = *((char*) GetPixelAddress(x, y));
        col_r = col_g = col_b = grey;
      }
      break;

      case eIMAGE_FORMAT_RGB565:   // assuming unsigned short is 16 bits long
      {
        unsigned short data = *((unsigned short*) GetPixelAddress(x, y));
        col_r = (data & 0x001F);
        col_g = (data & 0x07E0) >> 6;
        col_b = (data & 0xF800) >> 11;
      }
      break;

      case eIMAGE_FORMAT_RGB24:
      case eIMAGE_FORMAT_RGB32:
      {
        unsigned char* data = (unsigned char*) GetPixelAddress(x, y);
        //cerr << "Got Pixel address: " << (void*) data << " at " << x << ", " << y << endl;
        col_r = data[ 0 ];
        col_g = data[ 1 ];
        col_b = data[ 2 ];
      }
      break;
      case eIMAGE_FORMAT_BGR24:
      case eIMAGE_FORMAT_BGR32:
      {
        unsigned char* data = (unsigned char*) GetPixelAddress(x, y);
        col_r = data[ 2 ];
        col_g = data[ 1 ];
        col_b = data[ 0 ];
      }
      break;
      case eIMAGE_FORMAT_YUV422:
      {
        unsigned int data = *((unsigned int*) GetPixelAddress(x & ~(0x1), y));         // need even address and 32 bit to read out packed format
        unsigned char y = ((x & 0x1) == 1) ? (data & 0x00FF0000) >> 16 : (data & 0x0000FF);
        sImageConverter::ConvertPixelYUVToRGB(y, (data & 0x0000FF00) >> 8, (data & 0xFF000000) >> 24, &col_r, &col_g, &col_b);
      }
      break;
      case eIMAGE_FORMAT_YUV444:
      {
        unsigned char* data = (unsigned char*) GetPixelAddress(x, y);
        sImageConverter::ConvertPixelYUVToRGB(data[ 0 ], data[ 1 ], data[ 2 ], &col_r, &col_g, &col_b);
      }
      break;

      case eIMAGE_FORMAT_YUV420P:
      default:
        fprintf(stderr, "tImage::GetPixelRGB>> Format not implemented yet.\n");
        break;
      }

    }*/

    public class RGB32 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = 3347494921353574228L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset - 1 + srcX * 4);
            IntBuffer ib = imageData.asIntBuffer();
            ib.get(destBuffer, destOffset, width);
        }
    }

    public class RGB24 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = 862575747828052539L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 3);
            for (int x = 0; x < width; x++) {
                destBuffer[destOffset] = toInt(imageData.get(), imageData.get(), imageData.get());
                destOffset++;
            }
        }
    }

    public class BGR24 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = 8580032150270926134L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 3);
            for (int x = 0; x < width; x++) {
                byte b = imageData.get();
                byte g = imageData.get();
                destBuffer[destOffset] = toInt(imageData.get(), g, b);
                destOffset++;
            }
        }
    }

    public class BGR32 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = -4429188067092456195L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 4);
            for (int x = 0; x < width; x++) {
                byte b = imageData.get();
                byte g = imageData.get();
                destBuffer[destOffset] = toInt(imageData.get(), g, b);
                imageData.get(); // skip
                destOffset++;
            }
        }
    }

    public class Mono8 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = 5857468128941036873L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX);
            for (int x = 0; x < width; x++) {
                byte b = imageData.get();
                destBuffer[destOffset] = toInt(b, b, b);
                destOffset++;
            }
        }
    }

    public class Mono16 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = -3377683723648179410L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 2);
            for (int x = 0; x < width; x++) {
                byte b = imageData.get();
                destBuffer[destOffset] = toInt(b, b, b);
                imageData.get(); // skip
                destOffset++;
            }
        }
    }

    public class RGB565 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = -4786403807992138925L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            byte zero = 0;
            imageData.position(lineOffset + srcX * 2);
            for (int x = 0; x < width; x++) {
                byte b1 = imageData.get();
                byte b2 = imageData.get();
                int sh = toInt(zero, b1, b2);
                int r = (sh & 0x001F);
                int g = (sh & 0x07E0) >> 6;
                int b = (sh & 0xF800) >> 11;
                destBuffer[destOffset] = toInt((byte)r, (byte)g, (byte)b);
                imageData.get(); // skip
                destOffset++;
            }
        }
    }

    public class YUV444 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = 4529284403628031942L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 3);
            for (int x = 0; x < width; x++) {
                destBuffer[destOffset] = yuvToRGB(imageData.get(), imageData.get(), imageData.get());
                destOffset++;
            }
        }
    }

    public class YUV422 extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = 1619579721348846824L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {

            byte y1, y2, u, v;

            // ungerade Zahl am Anfang?
            imageData.position(lineOffset + (srcX / 2) * 4);
            if (srcX % 2 != 0) {
                y1  = imageData.get();
                u = imageData.get();
                y2  = imageData.get();
                v = imageData.get();
                destBuffer[destOffset] = yuvToRGB(y2, u, v);
                destOffset++;
                width--;
            }

            // main block
            boolean singlePixelEnd = (width % 2 != 0);
            width = (width / 2) * 2;
            for (int x = 0; x < width; x += 2) {
                y1  = imageData.get();
                u = imageData.get();
                y2  = imageData.get();
                v = imageData.get();
                destBuffer[destOffset] = yuvToRGB(y1, u, v);
                destBuffer[destOffset+1] = yuvToRGB(y2, u, v);
                destOffset += 2;
            }

            // ungerade Zahl am Ende?
            if (singlePixelEnd) {
                y1  = imageData.get();
                u = imageData.get();
                y2  = imageData.get();
                v = imageData.get();
                destBuffer[destOffset] = yuvToRGB(y1, u, v);
            }
        }
    }

    public class YUV420P extends BlackboardBlitter {

        /** UID */
        private static final long serialVersionUID = -4256927785125538010L;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {

            int yArraySize = getHeight() * getWidth();
            int uvArraySize = yArraySize / 4;
            int yArrayOffset = cImageInfoSizeWithPadding;
            int uArrayOffset = yArrayOffset + yArraySize;
            int vArrayOffset = uArrayOffset + uvArraySize;

            int x = srcX;
            int ypos = srcY * getWidth() + x + yArrayOffset;
            int uvOffset = (srcY / 2) * (getWidth() / 2);

            for (int i = 0; i < width; i++) {
                byte y = imageData.get(ypos);
                int uvIndex = uvOffset + x / 2;
                byte u = imageData.get(uvIndex + uArrayOffset);
                byte v = imageData.get(uvIndex + vArrayOffset);
                destBuffer[destOffset] = yuvToRGB(y, u, v);
                ypos++;
                x++;
                destOffset++;
            }
        }
    }
}

