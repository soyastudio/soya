package soya.application.albertsons.commands;

import soya.framework.core.CommandOption;
import soya.framework.core.Task;

import java.io.File;
import java.io.IOException;

public abstract class AbsTaskBase extends Task<String> {

    public static final String CMM_DIR = "CMM";
    public static final String TEMPLATES_DIR = "Templates";

    @CommandOption(option = "h", required = true,
            paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "workspace.home")
    protected String home;

    protected File homeDir;
    protected File cmmDir;
    protected File templateDir;

    protected void init() throws Exception {
        this.homeDir = new File(home);
        if (!homeDir.exists()) {
            throw new IllegalArgumentException("Directory does not exist: " + homeDir);
        }

        cmmDir = new File(home, CMM_DIR);
        if (!cmmDir.exists()) {
            cmmDir.mkdir();
        }

        templateDir = new File(home, TEMPLATES_DIR);
        if (!templateDir.exists()) {
            templateDir.exists();
        }

    }

}
