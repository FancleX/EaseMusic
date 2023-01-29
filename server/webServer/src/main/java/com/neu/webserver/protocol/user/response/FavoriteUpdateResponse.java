package com.neu.webserver.protocol.user.response;

import com.neu.webserver.entity.media.MediaShort;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteUpdateResponse {

    private List<MediaShort> favorites;
}
