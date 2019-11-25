package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.resources.Union;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.util.List;

@AntTaskDef(name = "unzip", attributes = {"source", "dest", "overwrite"})
public class UnzipAdapter extends AntTaskAdapterSupport<Expand> {
    private String dest;
    private String source;
    private boolean overwrite;

    private Mapper mapperElement;
    private List<PatternSet> patternsets;
    private Union resources;
    private boolean resourcesSpecified;
    private boolean failOnEmptyArchive;
    private boolean stripAbsolutePathSpec;
    private boolean scanForUnicodeExtraFields;
    private Boolean allowFilesToEscapeDest;
    private String encoding;

    public UnzipAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Expand task, TaskSession session) {
        task.setDest(getDir(dest));
        task.setSrc(getFile(source));
        task.setOverwrite(overwrite);
    }
}
