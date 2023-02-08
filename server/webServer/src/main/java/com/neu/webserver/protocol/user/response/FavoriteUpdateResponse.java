package com.neu.webserver.protocol.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteUpdateResponse {

    private List<?> favorites;
    private int currentIndex;
    private int limit;
}
