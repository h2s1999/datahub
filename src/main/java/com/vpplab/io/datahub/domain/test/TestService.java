package com.vpplab.io.datahub.domain.test;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    //private final TestDao testDao;


    /*public Map<String, Object> getDashboard(HashMap<String,Object> paramMap, HttpServletRequest request) {
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> multiMap = new HashMap<>();

        paramMap.put("중개사업자ID","2");
      //  List<HashMap> getIssueList  =  testDao.getIssueList(paramMap);

        return multiMap;
    }*/

    /*public HashMap dataGrouping(HashMap inputData){
        if(inputData == null || inputData.size() == 0) return null;

        HashMap result = new HashMap();
        HashMap group01Data = new HashMap();
        HashMap group02Data = new HashMap();

        inputData.forEach((key, value) -> {
            String[] flag = key.toString().split("_");
            if("상태".equals(flag[0])) {
                group01Data.put(flag[1], value);

            } else if("합계".equals(flag[0])) {
                group02Data.put(flag[1], value);

            }
        });

        result.put("상태", group01Data);
        result.put("합계", group02Data);


        return result;
    }*/
}
