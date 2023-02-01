package com.neu.webserver.protocol.search.chain;

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
    private String queryString;
    private List<?> queryResult;
}
