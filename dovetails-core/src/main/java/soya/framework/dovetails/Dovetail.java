package soya.framework.dovetails;

public interface Dovetail {
    String getName();

    String[] flows();

    void run();

    void run(String flow);

}
