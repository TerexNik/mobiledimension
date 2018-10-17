package ru.mobiledimension.test.exception;

import lombok.Getter;

public class AlreadyHaveThisFriend extends RuntimeException {
    @Getter
    private Integer id;

    public AlreadyHaveThisFriend(Integer id) {
        super(String.format("Person with id [%d] is already your friend", id));
    }
}
