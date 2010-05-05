package org.finroc.plugin.datatype.mca;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.finroc.core.buffer.CoreInput;
import org.finroc.core.portdatabase.DataType;
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.blackboard.BlackboardPlugin;
import org.finroc.plugin.datatype.ContainsStrings;

/**
 * @author max
 *
 * MCA2 Log Stream Blackboard Buffer
 */
public class LogStreamBlackboardBuffer extends BlackboardBuffer implements ContainsStrings {

    public static DataType TYPE = BlackboardPlugin.registerBlackboardType(LogStreamBlackboardBuffer.class, "Log Stream");
    public static DataType MTYPE = TYPE.getRelatedType();

    public ArrayList<String> contents = new ArrayList<String>();

    //public static final DateFormat format = DateFormat.getTimeInstance();
    public static final DateFormat format = DateFormat.getTimeInstance();
    public final Date date = new Date();

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
    public void deserialize(CoreInput is) {
        super.deserialize(is);

        // deserialize
        contents.clear();
        int pos = getElementSize();
        while (pos + 12 < getSize() && getBuffer().getByte(pos + 12) != 0) {
            long sec = MCA.tTime._tv_sec.getRel(getBuffer(), pos);
            long usec = MCA.tTime._tv_usec.getRel(getBuffer(), pos);
            pos += 12;
            String s = getBuffer().getString(pos);
            pos += s.length() + 1;

            long time = (sec * 1000L) + (usec / 1000L);
            date.setTime(time);
            contents.add(0, format.format(time) + ": " + s.trim());
        }
    }
}
