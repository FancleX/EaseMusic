package com.neu.webserver.protocol.user.response;

import com.neu.webserver.protocol.media.MediaPreview;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteUpdateResponse {

    private List<MediaPreview> favorites;
    private int currentIndex;
    private int limit;
}
