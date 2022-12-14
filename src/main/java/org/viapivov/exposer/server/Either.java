package org.viapivov.exposer.server;

public class Either<L, R> {
    private final L l;
    private final R r;

    public Either(L a, R b) {
        this.l = a;
        this.r = b;
    }

    public boolean isLeft() {
        return l != null;
    }

    public boolean isRight() {
        return r != null;
    }

    public L getLeft() {
        return l;
    }

    public R getRight() {
        return r;
    }

    public static <L, R> Either<L, R> left(L l) {
        return new Either<>(l, null);
    }

    public static <L, R> Either<L, R> right(R r) {
        return new Either<>(null, r);
    }

}
