package com.neu.webserver.protocol.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteUpdateRequest {

    private String uuid;

    private int currentIndex;

    private int limit;

}
