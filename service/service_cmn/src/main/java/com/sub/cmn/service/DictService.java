package com.sub.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.cmn.Dict;

import java.util.List;

public interface DictService extends IService<Dict> {

    List<Dict> findChild(Long id);
}
