package net.arcation.arcadion.util;

public interface Receiver<T>
{
    void receive(T t);
}
