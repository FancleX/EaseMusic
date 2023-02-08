package com.neu.webserver.service.searchChain.cache.updater;


import com.neu.webserver.protocol.media.MediaPreview;

import java.util.List;

public interface CacheUpdater {

    /**
     * Tag all instances with given topic, adaptively update instances if an instance exits in database,
     * then update its topic, otherwise create a new instance with its topic.
     *
     * @param topic     a topic relates to those instances
     * @param mediaList instance list
     */
    void saveInstances(String topic, List<MediaPreview> mediaList);
}
