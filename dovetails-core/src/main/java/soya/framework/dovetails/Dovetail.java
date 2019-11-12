package soya.framework.dovetails;

import soya.framework.Resource;

public interface Dovetail {
    String getName();

    String[] flows();

    TaskSession run();

    TaskSession run(String flow);

    TaskSession run(Resource resource);

}
