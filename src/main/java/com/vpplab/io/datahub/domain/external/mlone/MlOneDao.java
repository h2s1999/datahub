package com.vpplab.io.datahub.domain.external.mlone;


import java.util.List;
import java.util.Map;

public interface MlOneDao {
    /**
     * ML1 연동 데이터 DB insert
     * @param insertParam
     * @return
     */
    int setMlone(List<Map<String, Object>> insertParam);
}
