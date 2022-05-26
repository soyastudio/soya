package soya.framework.tasks.jgit;

import org.eclipse.jgit.lib.BatchingProgressMonitor;
import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.TaskCallable;

@CommandGroup(group = "git", title = "Git Service", description = "Toolkit for git commands.")
public abstract class GitTask implements TaskCallable {

    @CommandOption(option = "h", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.git.home")
    protected String localHome;


    //@CommandOption(option = "s", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.git.ssh_dir")
    protected String sshDir;

    @CommandOption(option = "u")
    protected String url;


    /*protected void sshSetUp() {

        File sshDir = new File(FS.DETECTED.userHome(), sshDir);
        SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey,keyboard-interactive,password")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(sshDir).build(new JGitKeyCache());
        SshSessionFactory.setInstance(sshdSessionFactory);
    }*/


    static class ProgressLogger extends BatchingProgressMonitor {

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
            System.out.println(s.toString());

        }
    }
}
