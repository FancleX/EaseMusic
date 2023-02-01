package com.neu.webserver.protocol.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaPreview {
    private String uuid;
    private String thumbnail;
    private String title;
    private String author;
    private String description;
}
