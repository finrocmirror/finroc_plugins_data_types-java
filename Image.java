//
// You received this file as part of Finroc
// A framework for intelligent robot control
//
// Copyright (C) Finroc GbR (finroc.org)
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
//----------------------------------------------------------------------
package org.finroc.plugins.data_types;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.finroc.plugins.data_types.Blittable;
import org.finroc.plugins.data_types.HasBlittable;
import org.finroc.plugins.data_types.PaintablePortData;
import org.finroc.plugins.data_types.util.FastBufferedImage;
import org.rrlib.logging.Log;
import org.rrlib.logging.LogLevel;
import org.rrlib.serialization.ArrayBuffer;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.MemoryBuffer;
import org.rrlib.serialization.PortDataListImpl;
import org.rrlib.serialization.compression.Compressible;
import org.rrlib.serialization.compression.DataCompressionAlgorithm;
import org.rrlib.serialization.rtti.Copyable;
import org.rrlib.serialization.rtti.DataType;
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * Image-Blackboard
 */
public class Image implements HasBlittable, PaintablePortData, Compressible, Copyable<Image>, ArrayBuffer {

    public static class ImageList extends PortDataListImpl<Image> implements HasBlittable, PaintablePortData {

        public ImageList() {
            super(Image.TYPE);
        }

        @Override
        public Blittable getBlittable(int index) {
            return size() > index ? get(index).getBlittable(0) : null;
        }

        @Override
        public Rectangle2D getBounds() {
            return size() > 0 ? get(0).getBounds() : null;
        }

        @Override
        public void paint(Graphics2D g, FastBufferedImage imageBuffer) {
            if (size() > 0) {
                get(0).paint(g, imageBuffer);
            }
        }

        @Override
        public int getNumberOfBlittables() {
            return size();
        }

        @Override
        public boolean isYAxisPointingDownwards() {
            return true;
        }
    }

    public final static DataType<Image> TYPE = new DataType<Image>(Image.class, ImageList.class, "Image");
    public final static DataTypeBase BB_TYPE = BlackboardPlugin.registerBlackboardType(TYPE);;
    public final static DataType<Format> FORMAT_TYPE = new DataType<Format>(Format.class, "ImageFormat");

    static {
        DataCompressionAlgorithm.register(Image.class, "jpeg", false);
        DataCompressionAlgorithm.register(Image.class, "png", false);
    }

    enum Format {
        MONO8,
        MONO16,
        MONO32_FLOAT,
        RGB565,
        RGB24,
        BGR24,
        RGB32,
        BGR32,
        YUV420P,
        YUV411,
        YUV422,
        UYVY422,
        YUV444,
        BAYER_RGGB,
        BAYER_GBRG,
        BAYER_GRBG,
        BAYER_BGGR,
        HSV,
        HLS,
        HI240,
        NV21
    };

    public static final Channel[][] FORMAT_CHANNELS = new Channel[][] {
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 1, "Lightness"), // MONO8,
        Channel.create(AttributeType.UNSIGNED_SHORT, 0, 2, "Lightness"), // MONO16,
        Channel.create(AttributeType.FLOAT, 0, 4, "Lightness"), // MONO32_FLOAT,
        Channel.create(AttributeType.UNSIGNED_SHORT, 0, 2, "RGB565"), // RGB565,
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 3, "R", "G", "B"), // RGB24,
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 3, "B", "G", "R"), // BGR24,
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 4, "R", "G", "B"), // RGB32,
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 4, "B", "G", "R"), // BGR32,
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 1, "Y"), // YUV420P, (less U and V values than Y values)
        new Channel[0], // YUV411, (unsupported)
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 2, "Y"), // YUV422, (less U and V values than Y values)
        new Channel[0], // UYVY422, (unsupported)
        new Channel[0], // YUV444, (unsupported)
        new Channel[0], // BAYER_RGGB, (unsupported)
        new Channel[0], // BAYER_GBRG, (unsupported)
        new Channel[0], // BAYER_GRBG, (unsupported)
        new Channel[0], // BAYER_BGGR, (unsupported)
        new Channel[0], // HSV, (unsupported)
        new Channel[0], // HLS, (unsupported)
        new Channel[0], // HI240, (unsupported)
        Channel.create(AttributeType.UNSIGNED_BYTE, 0, 1, "Y") // NV21 (less U and V values than Y values)
    };

    /** Current object for blitting */
    private BlackboardBlitter blitter;

    /** Type of blittable object */
    private Format lastType = null;

    /** relevant variable regarding image data */
    private int width;
    private int height;
    private final int[] dimensions = new int[2];
    private int widthStep;
    private Format format = Format.RGB24;

    /** Image Buffer */
    private MemoryBuffer imageData = new MemoryBuffer(false);

    /** Variables to handle compressed images */
    private boolean compressed = false;
    private byte[] compressedData = new byte[0];
    private BufferedImage uncompressedImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
    private ImageBlitter compressedBlitter = new ImageBlitter();
    private static final byte PADDING_BYTES[] = new byte[3];

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void serialize(BinaryOutputStream os) {
        if (compressed) {
            throw new RuntimeException("Compressed image cannot be serialized (not yet implemented)");
        }

        os.writeInt(width);
        os.writeInt(height);
        os.writeEnum(format);
        os.writeInt(imageData.getSize());
        os.writeInt(0); // extra data size

        // region of interest
        os.writeBoolean(false);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);

        // Write image data
        int originalWidthStep = calculateWidthStep(width, format, 4);
        int padding = originalWidthStep - widthStep;
        if (padding == 0) {
            os.write(imageData.getBuffer(), 0, imageData.getSize());
        } else {
            for (int y = 0; y < height; y++) {
                os.write(imageData.getBuffer(), y * widthStep, widthStep);
                os.write(PADDING_BYTES, 0, padding);
            }
        }
    }

    @Override
    public void deserialize(BinaryInputStream is) {
        compressed = false;
        width = is.readInt();
        height = is.readInt();
        format = is.readEnum(Format.class);
        int imageSize = is.readInt();
        int extraData = is.readInt();

        // region of interest
        is.readBoolean();
        is.readInt();
        is.readInt();
        is.readInt();
        is.readInt();

        // Read image data
        imageData.clear();
        int originalWidthStep = calculateWidthStep(width, format, 4);
        widthStep = calculateWidthStep(width, format, 1);
        int padding = originalWidthStep - widthStep;
        if (padding == 0) {
            imageData.deserialize(is, imageSize);
        } else {
            imageData.setSize(widthStep * height);
            for (int y = 0; y < height; y++) {
                is.readFully(imageData.getBuffer(), y * widthStep, widthStep);
                is.skip(padding);
            }
        }
        is.skip(extraData);

        // calculate internal variables
        blitter = createBlittable();
    }

    /**
     * (see equivalent function in tImage.h)
     */
    private int calculateWidthStep(int w, Format f, int alignment) {
        if (f == Format.YUV420P || f == Format.NV21) {
            return width;
        }

        int bpp = -1;
        switch (format) {
        case RGB32:
        case BGR32:
        case MONO32_FLOAT:
            bpp = 4;
            break;
        case RGB24:
        case BGR24:
        case YUV444:
            bpp = 3;
            break;
        case RGB565:
        case MONO16:
        case YUV422:
            bpp = 2;
            break;
        case MONO8:
            bpp = 1;
            break;
        default:
            Log.log(LogLevel.DEBUG_VERBOSE_1, this, "warning (ImageBlackboard): Image format " + format + " not supported yet");
            bpp = 1;
            //return Blittable.Empty.instance;
        }

        int temp = bpp * width;
        while ((temp % alignment) != 0) {
            temp++;
        }
        return temp;
    }

    /**
     * @return Blackboard blitter
     */
    private BlackboardBlitter createBlittable() {
        if (format == lastType) {
            blitter.reinit();
            return blitter;
        }

        // init blitter object
        switch (format) {
        case RGB32:
            blitter = new RGB32();
            break;
        case RGB24:
            blitter = new RGB24();
            break;
        case BGR32:
            blitter = new BGR32();
            break;
        case BGR24:
            blitter = new BGR24();
            break;
        case MONO8:
            blitter = new Mono8();
            break;
        case MONO16:
            blitter = new Mono16();
            break;
        case MONO32_FLOAT:
            blitter = new Mono32Float();
            break;
        case RGB565:
            blitter = new RGB565();
            break;
        case YUV444:
            blitter = new YUV444();
            break;
        case YUV422:
            blitter = new YUV422();
            break;
        case YUV420P:
            blitter = new YUV420P();
            break;
        case NV21:
            blitter = new NV21();
            break;
        default:
            Log.log(LogLevel.WARNING, this, "Image format " + format + " not supported yet");
            blitter = new NullBlitter();
            //return Blittable.Empty.instance;
        }

        lastType = format;
        blitter.reinit();
        return blitter;
    }

    @Override
    public Blittable getBlittable(int index) {
        return compressed ? compressedBlitter : (blitter == null ? Blittable.Empty.instance : blitter);
    }

    @Override
    public int getNumberOfBlittables() {
        return getBlittable(0) == null ? 0 : 1;
    }

    public abstract class BlackboardBlitter extends Blittable {

        /** Width height and widthStep */
        protected int srcY;
        public transient ByteBuffer imageData;

        public void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int srcY, int srcOffset, int width) {

            int offset = widthStep * srcY;
            this.srcY = srcY;
            blitLineToRGB(destBuffer, destOffset, srcX, offset, width);
        }

        protected abstract void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width);

        /**
         * (re-)initialize
         */
        public void reinit() {
            imageData = Image.this.imageData.getBuffer().getBuffer();
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

    public class ImageBlitter extends Blittable {

        @Override
        public void blitTo(Destination destination, Point dest, Rectangle sourceArea) {
            destination.getBufferedImage().createGraphics().drawImage(uncompressedImage, dest.x, dest.y, sourceArea.width, sourceArea.height, Color.black, null);
        }

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int srcY, int srcOffset, int width) {
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
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

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 4);
            for (int x = 0; x < width; x++) {
                destBuffer[destOffset] = toInt(imageData.get(), imageData.get(), imageData.get());
                destOffset++;
                imageData.get();
            }
        }
    }

    public class RGB24 extends BlackboardBlitter {

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

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 4);
            IntBuffer ib = imageData.asIntBuffer();
            ib.get(destBuffer, destOffset, width);
        }
    }

    public class Mono8 extends BlackboardBlitter {

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

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 2);
            for (int x = 0; x < width; x++) {
                imageData.get(); // skip
                byte b = imageData.get();
                destBuffer[destOffset] = toInt(b, b, b);
                destOffset++;
            }
        }
    }


    public class Mono32Float extends BlackboardBlitter {

        float maximum = 1;

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            imageData.position(lineOffset + srcX * 4);
            for (int x = 0; x < width; x++) {
                maximum = Math.max(maximum, imageData.getFloat());
            }
            imageData.position(lineOffset + srcX * 4);
            float divisor = maximum / 255;
            for (int x = 0; x < width; x++) {
                byte value = (byte)(Math.max(0, imageData.getFloat()) / divisor);
                destBuffer[destOffset] = toInt(value, value, value);
                destOffset++;
            }
        }
    }

    public class RGB565 extends BlackboardBlitter {

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
                destBuffer[destOffset + 1] = yuvToRGB(y2, u, v);
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

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {

            int yArraySize = getHeight() * getWidth();
            int uvArraySize = yArraySize / 4;
            int yArrayOffset = 0;
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

    public class NV21 extends BlackboardBlitter {

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {

            int yArraySize = getHeight() * getWidth();
            int uvArraySize = yArraySize / 4;
            int yArrayOffset = 0;
            int uvArrayOffset = yArrayOffset + yArraySize;

            int x = srcX;
            int ypos = srcY * getWidth() + x + yArrayOffset;
            int uvOffset = ((srcY / 2) * (getWidth() / 2));

            for (int i = 0; i < width; i++) {
                byte y = imageData.get(ypos);
                int uvIndex = (uvOffset + x / 2) * 2;
                byte u = imageData.get(uvIndex + uvArrayOffset + 1);
                byte v = imageData.get(uvIndex + uvArrayOffset);
                destBuffer[destOffset] = yuvToRGB(y, u, v);
                ypos++;
                x++;
                destOffset++;
            }
        }
    }

    public class NullBlitter extends BlackboardBlitter {

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int lineOffset, int width) {
            for (int x = 0; x < width; x++) {
                destBuffer[destOffset] = 0;
                destOffset++;
            }
        }
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(0, 0, width, height);
    }

    @Override
    public void paint(Graphics2D g, FastBufferedImage imageBuffer) {
        getBlittable(0).standardPaintImplementation(g);
    }

    /**
     * Sets image data from raw RGB32 buffer
     * (as can be obtained from fingui's BufferedImageRGB)
     *
     * @param width Width of image
     * @param height Height of image
     * @param data Image Data (in RGB32 without any padding)
     */
    public void setImageDataRGB32(int width, int height, int[] data) {
        this.width = width;
        this.height = height;
        format = Format.BGR32;
        imageData.clear();
        BinaryOutputStream os = new BinaryOutputStream(imageData);
        for (int i = 0, n = width * height; i < n; i++) {
            os.writeInt(data[i]);
        }
        os.close();
        widthStep = calculateWidthStep(width, format, 1);
    }

    @Override
    public boolean isYAxisPointingDownwards() {
        return true;
    }

    @Override
    public void compressNext(BinaryOutputStream stream, String compressionType) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void decompressNext(BinaryInputStream stream, String compressionType, int maxBytesToRead) throws Exception {
        if (maxBytesToRead > compressedData.length) {
            compressedData = new byte[(int)(maxBytesToRead * 1.2)];
        }
        stream.readFully(compressedData, 0, maxBytesToRead);
        uncompressedImage = ImageIO.read(new ByteArrayInputStream(compressedData));
        width = uncompressedImage.getWidth();
        height = uncompressedImage.getHeight();
        compressed = true;
    }

    @Override
    public void copyFrom(Image source) {
        compressed = source.compressed;
        width = source.width;
        height = source.height;
        format = source.format;

        // Read image data
        if (compressed) {
            if (compressedData.length != source.compressedData.length) {
                compressedData = new byte[source.compressedData.length];
            }
            System.arraycopy(source.compressedData, 0, compressedData, 0, source.compressedData.length);
            ColorModel colorModel = source.uncompressedImage.getColorModel();
            uncompressedImage = new BufferedImage(colorModel, source.uncompressedImage.copyData(null), colorModel.isAlphaPremultiplied(), null);
        } else {
            imageData.copyFrom(source.imageData);
        }

        // calculate internal variables
        widthStep = calculateWidthStep(width, format, 1);
        lastType = null;
        blitter = createBlittable();
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return imageData.getBuffer().getBuffer();
    }

    @Override
    public Channel[] getChannels() {
        return FORMAT_CHANNELS[format.ordinal()];
    }

    @Override
    public int[] getArrayDimensions() {
        dimensions[0] = width;
        dimensions[1] = height;
        return dimensions;
    }
}

