package com.neu.webserver.protocol.user.response;

import com.neu.webserver.entity.media.MediaShort;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteUpdateResponse {

    // TODO: create a new response type for favorites,
    //  search by uuids through search search service,
    //  return resource list, an entity should include
    //  uuid, thumbnail, title, author
    private List<String> favorites;
    private int currentIndex;
    private int limit;
}
