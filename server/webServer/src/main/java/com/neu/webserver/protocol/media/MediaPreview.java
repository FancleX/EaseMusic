package com.neu.webserver.protocol.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 *
 * TODO: create a new response type for favorites,
 *  search by uuids through search search service,
 *  return resource list, an entity should include
 *  uuid, thumbnail, title, author
 */
public class MediaPreview {
    private String uuid;
    private String thumbnail;
    private String title;
    private String author;
    private String description;
}
