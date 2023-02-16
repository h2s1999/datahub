package com.vpplab.io.datahub.domain.external.mlone;

import com.vpplab.io.datahub.global.utils.DatetimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MlOneController {

    private final MlOneService mlOneService;

    /**
     * ML1 연동 데이터 가져오기
     */
    @RequestMapping("/getAllForecasts")
    public Map<String, Object> getAllForecasts() throws Exception {
        log.info("ML1 데이터 컨트롤러 시작 ");
        return mlOneService.procMl1forecastIn();
    }

}
