package com.application.strms.domain.model;

import de.huxhorn.sulky.ulid.ULID;

public final class Ulid {
    private static final ULID ULID_INSTANCE = new ULID();

    private final String value;

    public Ulid() {
        this.value = ULID_INSTANCE.nextULID();
    }

    public Ulid(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid ULID: " + value);
        }

        this.value = value;
    }

    public static Ulid fromString(String value) {
        return new Ulid(value);
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank())
            return false;

        try {
            ULID.parseULID(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ulid ulid))
            return false;

        return value.equals(ulid.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}