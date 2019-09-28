package soya.framework;

public interface Callback<T extends Session> {
    void onSuccess(T session);

    void onFailure(T session, Throwable t);
}
