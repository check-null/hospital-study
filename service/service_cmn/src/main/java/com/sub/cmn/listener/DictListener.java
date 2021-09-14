package com.sub.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.sub.cmn.mapper.DictMapper;
import com.sub.model.cmn.Dict;
import com.sub.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

/**
 * @author Europa
 */
public class DictListener extends AnalysisEventListener<DictEeVo> {

    private final DictMapper DICT_MAPPER;

    public DictListener(DictMapper dictMapper) {
        this.DICT_MAPPER = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        DICT_MAPPER.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
