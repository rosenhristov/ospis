package com.obectron.primitives;

public interface OspisObject {
    default boolean isAtom() { return this instanceof Atom; }
    default boolean isCons() { return this instanceof Cons; }
}
