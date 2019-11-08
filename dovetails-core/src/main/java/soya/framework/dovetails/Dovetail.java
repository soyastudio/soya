package soya.framework.dovetails;

public interface Dovetail {
    String getName();

    String[] flows();

    TaskSession run();

    TaskSession run(String flow);

}
