package soya.framework.action.actions.jgit;

import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.lib.BatchingProgressMonitor;
import soya.framework.action.*;

import java.util.ArrayList;
import java.util.List;

@CommandGroup(group = "git", title = "Git Tasks", description = "Toolkit for git commands, based on eclipse jgit project.")
public abstract class GitAction<T extends GitCommand> implements ActionCallable {

    @CommandOption(option = "h", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.git.home")
    protected String localHome;

    //@CommandOption(option = "s", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.git.ssh_dir")
    protected String sshDir;

    @CommandOption(option = "u")
    protected String url;

    @Override
    public ActionResult call() throws Exception {
        return Action.succeeded(this, "TODO");
    }


    /*protected void sshSetUp() {

        File sshDir = new File(FS.DETECTED.userHome(), sshDir);
        SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey,keyboard-interactive,password")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(sshDir).build(new JGitKeyCache());
        SshSessionFactory.setInstance(sshdSessionFactory);
    }*/


    static class ProgressLogger extends BatchingProgressMonitor {
        private List<String> messages = new ArrayList<>();

        protected void onUpdate(String taskName, int workCurr) {
            StringBuilder s = new StringBuilder();
            this.format(s, taskName, workCurr);
            this.send(s);
        }

        protected void onEndTask(String taskName, int workCurr) {
            StringBuilder s = new StringBuilder();
            this.format(s, taskName, workCurr);
            s.append("\n");
            this.send(s);
        }

        private void format(StringBuilder s, String taskName, int workCurr) {
            s.append("\r");
            s.append(taskName);
            s.append(": ");

            while (s.length() < 25) {
                s.append(' ');
            }

            s.append(workCurr);
        }

        protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
            StringBuilder s = new StringBuilder();
            this.format(s, taskName, cmp, totalWork, pcnt);
            this.send(s);
        }

        protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt) {
            StringBuilder s = new StringBuilder();
            this.format(s, taskName, cmp, totalWork, pcnt);
            s.append("\n");
            this.send(s);
        }

        private void format(StringBuilder s, String taskName, int cmp, int totalWork, int pcnt) {
            s.append("\r");
            s.append(taskName);
            s.append(": ");

            while (s.length() < 25) {
                s.append(' ');
            }

            String endStr = String.valueOf(totalWork);

            String curStr;
            for (curStr = String.valueOf(cmp); curStr.length() < endStr.length(); curStr = " " + curStr) {
            }

            if (pcnt < 100) {
                s.append(' ');
            }

            if (pcnt < 10) {
                s.append(' ');
            }

            s.append(pcnt);
            s.append("% (");
            s.append(curStr);
            s.append("/");
            s.append(endStr);
            s.append(")");
        }

        private void send(StringBuilder s) {
            messages.add(s.toString());
        }
    }
}
