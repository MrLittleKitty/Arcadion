package net.arcation.util;

public interface Receiver<T>
{
    void receive(T t);
}
