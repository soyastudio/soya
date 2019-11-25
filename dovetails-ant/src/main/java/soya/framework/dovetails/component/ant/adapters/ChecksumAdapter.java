package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Checksum;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@AntTaskDef(name = "checksum", attributes = {"file", "dir", "executable"})
public class ChecksumAdapter extends AntTaskAdapterSupport<Checksum> {
    private static final int NIBBLE = 4;
    private static final int WORD = 16;
    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int BYTE_MASK = 0xFF;

    private String file = null;
    private String todir;
    private String algorithm = "MD5";
    private String provider = null;
    private String fileext;
    /**
     * Holds generated checksum and gets set as a Project Property.
     */
    private String property;
    /**
     * Holds checksums for all files (both calculated and cached on disk).
     * Key:   java.util.File (source file)
     * Value: java.lang.String (digest)
     */
    private Map<File, byte[]> allDigests = new HashMap<>();
    /**
     * Holds relative file names for all files (always with a forward slash).
     * This is used to calculate the total hash.
     * Key:   java.util.File (source file)
     * Value: java.lang.String (relative file name)
     */
    private Map<File, String> relativeFilePaths = new HashMap<>();
    /**
     * Property where totalChecksum gets set.
     */
    private String totalproperty;
    /**
     * Whether or not to create a new file.
     * Defaults to <code>false</code>.
     */
    private boolean forceOverwrite;
    /**
     * Contains the result of a checksum verification. ("true" or "false")
     */
    private String verifyProperty;
    /**
     * Resource Collection.
     */
    // private FileUnion resources = null;
    /**
     * Stores SourceFile, DestFile pairs and SourceFile, Property String pairs.
     */
    private Hashtable<File, Object> includeFileMap = new Hashtable<>();
    /**
     * Message Digest instance
     */
    private MessageDigest messageDigest;
    /**
     * is this task being used as a nested condition element?
     */
    private boolean isCondition;
    /**
     * Size of the read buffer to use.
     */
    private int readBufferSize = BUFFER_SIZE;

    /**
     * Formatter for the checksum file.
     */
    private MessageFormat format = Checksum.FormatElement.getDefault().getFormat();

    public ChecksumAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Checksum task, TaskSession session) {
        task.setFile(getFile(file));
    }
}
