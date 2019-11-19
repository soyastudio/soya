package soya.framework.dovetails;

import soya.framework.Resource;

import java.util.Properties;

public interface Dovetail {
    String getName();

    String[] flows();

    TaskSession run();

    TaskSession run(String flowName);

    TaskSession run(String flowName, Properties properties);

    TaskSession run(Resource resource);

}
