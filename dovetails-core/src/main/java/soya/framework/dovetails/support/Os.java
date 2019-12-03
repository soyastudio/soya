package soya.framework.dovetails.support;

import java.io.File;
import java.util.Locale;

public class Os {
    private static final String OS_NAME;
    private static final String OS_ARCH;
    private static final String OS_VERSION;
    private static final String PATH_SEP;
    public static final String FAMILY_WINDOWS = "windows";
    public static final String FAMILY_9X = "win9x";
    public static final String FAMILY_NT = "winnt";
    public static final String FAMILY_OS2 = "os/2";
    public static final String FAMILY_NETWARE = "netware";
    public static final String FAMILY_DOS = "dos";
    public static final String FAMILY_MAC = "mac";
    public static final String FAMILY_TANDEM = "tandem";
    public static final String FAMILY_UNIX = "unix";
    public static final String FAMILY_VMS = "openvms";
    public static final String FAMILY_ZOS = "z/os";
    public static final String FAMILY_OS400 = "os/400";
    private static final String DARWIN = "darwin";
    private String family;
    private String name;
    private String version;
    private String arch;

    public Os() {
    }

    public Os(String family) {
        this.setFamily(family);
    }

    public void setFamily(String f) {
        this.family = f.toLowerCase(Locale.ENGLISH);
    }

    public void setName(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);
    }

    public void setArch(String arch) {
        this.arch = arch.toLowerCase(Locale.ENGLISH);
    }

    public void setVersion(String version) {
        this.version = version.toLowerCase(Locale.ENGLISH);
    }

    public boolean eval() {
        return isOs(this.family, this.name, this.arch, this.version);
    }

    public static boolean isFamily(String family) {
        return isOs(family, (String) null, (String) null, (String) null);
    }

    public static boolean isName(String name) {
        return isOs((String) null, name, (String) null, (String) null);
    }

    public static boolean isArch(String arch) {
        return isOs((String) null, (String) null, arch, (String) null);
    }

    public static boolean isVersion(String version) {
        return isOs((String) null, (String) null, (String) null, version);
    }

    public static boolean isOs(String family, String name, String arch, String version) {
        boolean retValue = false;
        if (family != null || name != null || arch != null || version != null) {
            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;
            if (family != null) {
                boolean isWindows = OS_NAME.contains("windows");
                boolean is9x = false;
                boolean isNT = false;
                if (isWindows) {
                    is9x = OS_NAME.contains("95") || OS_NAME.contains("98") || OS_NAME.contains("me") || OS_NAME.contains("ce");
                    isNT = !is9x;
                }

                byte var13 = -1;
                switch (family.hashCode()) {
                    case -1263172078:
                        if (family.equals("openvms")) {
                            var13 = 11;
                        }
                        break;
                    case -1009474935:
                        if (family.equals("os/400")) {
                            var13 = 10;
                        }
                        break;
                    case -881027893:
                        if (family.equals("tandem")) {
                            var13 = 7;
                        }
                        break;
                    case 99656:
                        if (family.equals("dos")) {
                            var13 = 5;
                        }
                        break;
                    case 107855:
                        if (family.equals("mac")) {
                            var13 = 6;
                        }
                        break;
                    case 3418823:
                        if (family.equals("os/2")) {
                            var13 = 3;
                        }
                        break;
                    case 3594632:
                        if (family.equals("unix")) {
                            var13 = 8;
                        }
                        break;
                    case 3683225:
                        if (family.equals("z/os")) {
                            var13 = 9;
                        }
                        break;
                    case 113134651:
                        if (family.equals("win9x")) {
                            var13 = 1;
                        }
                        break;
                    case 113136290:
                        if (family.equals("winnt")) {
                            var13 = 2;
                        }
                        break;
                    case 1349493379:
                        if (family.equals("windows")) {
                            var13 = 0;
                        }
                        break;
                    case 1843471770:
                        if (family.equals("netware")) {
                            var13 = 4;
                        }
                }

                switch (var13) {
                    case 0:
                        isFamily = isWindows;
                        break;
                    case 1:
                        isFamily = isWindows && is9x;
                        break;
                    case 2:
                        isFamily = isWindows && isNT;
                        break;
                    case 3:
                        isFamily = OS_NAME.contains("os/2");
                        break;
                    case 4:
                        isFamily = OS_NAME.contains("netware");
                        break;
                    case 5:
                        isFamily = PATH_SEP.equals(";") && !isFamily("netware");
                        break;
                    case 6:
                        isFamily = OS_NAME.contains("mac") || OS_NAME.contains("darwin");
                        break;
                    case 7:
                        isFamily = OS_NAME.contains("nonstop_kernel");
                        break;
                    case 8:
                        isFamily = PATH_SEP.equals(":") && !isFamily("openvms") && (!isFamily("mac") || OS_NAME.endsWith("x") || OS_NAME.contains("darwin"));
                        break;
                    case 9:
                        isFamily = OS_NAME.contains("z/os") || OS_NAME.contains("os/390");
                        break;
                    case 10:
                        isFamily = OS_NAME.contains("os/400");
                        break;
                    case 11:
                        isFamily = OS_NAME.contains("openvms");
                        break;
                    default:
                        throw new RuntimeException("Don't know how to detect os family \"" + family + "\"");
                }
            }

            if (name != null) {
                isName = name.equals(OS_NAME);
            }

            if (arch != null) {
                isArch = arch.equals(OS_ARCH);
            }

            if (version != null) {
                isVersion = version.equals(OS_VERSION);
            }

            retValue = isFamily && isName && isArch && isVersion;
        }

        return retValue;
    }

    static {
        OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
        OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.ENGLISH);
        PATH_SEP = File.pathSeparator;
    }
}
