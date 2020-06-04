package com.niuren.dsapi.util;

import com.niuren.dsapi.exception.TemplateCheckException;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@Slf4j
public class TemplateCheckUtil {

    private static boolean isIdentifier(String word) {
        try {
            if (StringUtils.isEmpty(word) || word.length() == 0) {
                return false;
            }
            if (word.charAt(0) == ':' || word.charAt(0) == '{') {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isIdentifierAfterFrom(String content) {
        try {
            String[] wordsMap = content.split("\\s+");
            for (int i = 0; i < wordsMap.length - 1; ++i) {
                if (wordsMap[i].toLowerCase().equals("from")) {
                    if (isIdentifier(wordsMap[i + 1])) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean containsSelectAndFrom(String source) {
        if (StringUtils.isEmpty(source)) {
            return false;
        }
        String[] words = source.toLowerCase().split("\\s+");
        Set<String> set = new HashSet<>(Arrays.asList(words));
        return set.contains("select") && set.contains("from");
    }

    private static boolean containsSelectAndFrom(Map<String, Object> map) {
        try {
            if (map == null) {
                return false;
            }

            for (Object value : map.values()) {
                if (value instanceof String) {
                    String value1 = (String) value;
                    if (containsSelectAndFrom(value1)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void checkSqlTemplateContent(String content) throws TemplateCheckException {
        if (content == null) {
            return;
        }
        if (!containsSelectAndFrom(content)) {
            throw new TemplateCheckException("模板不能没有select和from");
        }
        if (isIdentifierAfterFrom(content)) {
            throw new TemplateCheckException("表名不能当作参数注入");
        }
    }

    public static void checkTempleRequestDto(SqlTempleRequestDto dto) throws TemplateCheckException {
        if (dto == null) {
            return;
        }
        Map<String, Object> paramMap = dto.getParamsMap();
        if (containsSelectAndFrom(paramMap)) {
            throw new TemplateCheckException("map 参数中不能包含select 和 from 关键字");
        }
    }

    /*
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        /*
        while (scanner.hasNext()) {
            String source = scanner.nextLine();
            try {
                checkSqlTemplateContent(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("select", "select");
            map.put("from", "from");
            map.put("ddd", "select from");
            SqlTempleRequestDto dto = new SqlTempleRequestDto();
            dto.setParamsMap(map);
            checkTempleRequestDto(dto);
            containsSelectAndFrom(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
