package com.neu.webserver.protocol.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteGetRequest {

    private int currentIndex;

    private int limit;
}
