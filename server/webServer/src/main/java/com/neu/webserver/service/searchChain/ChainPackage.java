package com.neu.webserver.service.searchChain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChainPackage {

    private List<?> queryResult;

    private String queryString;
    private Status nextStage;
    private int offset;

    public enum Status {
        CACHE_EVALUATE,
        SEARCH,
        CACHE_UPDATE,
        COMPLETED
    }

}
