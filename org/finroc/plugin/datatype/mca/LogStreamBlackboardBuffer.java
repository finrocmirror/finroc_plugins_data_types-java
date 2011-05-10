package org.finroc.plugin.datatype.mca;

import java.io.EOFException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.finroc.log.LogLevel;
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.datatype.ContainsStrings;
import org.finroc.plugin.datatype.DataTypePlugin;
import org.finroc.serialization.DataTypeBase;
import org.finroc.serialization.FixedBuffer;
import org.finroc.serialization.InputStreamBuffer;

/**
 * @author max
 *
 * MCA2 Log Stream Blackboard Buffer
 */
public class LogStreamBlackboardBuffer extends MCABlackboardBuffer implements ContainsStrings {

    public static class Elem extends BlackboardBuffer {}
    public final static DataTypeBase TYPE = getMcaBlackboardType(LogStreamBlackboardBuffer.class, Elem.class, "Log Stream");

    public ArrayList<String> contents = new ArrayList<String>();

    //public static final DateFormat format = DateFormat.getTimeInstance();
    public static final DateFormat format = DateFormat.getTimeInstance();
    public final Date date = new Date();

    // Helper variables for deserializing
    public final StringBuilder sbuf = new StringBuilder();
    public int lastPos, curPos, wrapPos;
    public final FixedBuffer hdr = new FixedBuffer(12);

    public LogStreamBlackboardBuffer() {
        super(TYPE);
    }

    @Override
    public CharSequence getString(int index) {
        return contents.get(index);
    }

    @Override
    public void setString(int index, CharSequence newString) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public int stringCount() {
        return contents.size();
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        super.deserialize(is);

        // deserialize
        contents.clear();
        try {
            wrapPos = getBuffer().getElementSize();
            lastPos = getBuffer().getBuffer().getInt(0) + wrapPos;
            curPos = lastPos;

            while (true) {
                if (readNextByte() != 0) { // end of string
                    break;
                }
                sbuf.setLength(0);
                byte c = 0;
                while ((c = readNextByte()) != 0) {
                    sbuf.append((char)c);
                }
                sbuf.reverse();
                if (sbuf.length() <= 0) {
                    break; // ok, we're done
                }

                // copy header
                hdr.putByte(11, c);
                for (int i = 10; i >= 0; i--) {
                    hdr.putByte(i, readNextByte());
                }

                // read tTime
                long sec = MCA.tTime._tv_sec.getRel(hdr, 0);
                long usec = MCA.tTime._tv_usec.getRel(hdr, 0);
                long time = (sec * 1000L) + (usec / 1000L);
                date.setTime(time);
                contents.add(format.format(time) + ": " + sbuf.toString().trim());
            }

        } catch (EOFException e) {
            // normal when buffer was wrapped around
        } catch (Exception e) {
            DataTypePlugin.logDomain.log(LogLevel.LL_ERROR, "LogStreamBlackboard", e);
        }
    }

    public byte readNextByte() throws EOFException {
        curPos--;
        if (curPos < wrapPos) {
            curPos = getBuffer().getSize() - 1;
        }
        if (curPos == lastPos) {
            throw new EOFException();
        }
        return getBuffer().getBuffer().getByte(curPos);
    }

    @Override
    public void setSize(int newSize) {
        throw new RuntimeException("Unsupported");
    }
}
