package com.example.gccoffee.model;

import org.springframework.util.Assert;

import java.util.Objects;

public class Email {
    private final String address;

    public Email(String address) {
        Assert.notNull(address, "address should not be null.");
        Assert.isTrue(address.length() >= 4 && checkAddress(address),
                "address is invalid.");
        this.address = address;
    }

    private static boolean checkAddress(String address){ // https://regexr.com
        return address.matches("^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
        // return Pattern.matches("^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", address);
    }

    public String getAddress() {
        return address;
    }

    // VO를 만들 때는 equals와 hashcode를 직접 구현하는 것이 좋다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Email{");
        sb.append("address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
