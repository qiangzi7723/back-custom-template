package com.custom.dao.common;

import com.custom.entity.common.IllegalRequestEntity;
import org.springframework.stereotype.Component;

@Component
public interface IllegalRequestDao {
    void add(IllegalRequestEntity illegalRequestEntity);
}
