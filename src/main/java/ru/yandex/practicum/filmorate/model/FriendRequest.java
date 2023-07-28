package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class FriendRequest {
    @NotNull(message = "Id не может быть null")
    private long userId;
    @NotNull(message = "Id не может быть null")
    private long friendId;
    private boolean isApproved;
}
