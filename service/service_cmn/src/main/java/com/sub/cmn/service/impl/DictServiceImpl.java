package com.sub.cmn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sub.cmn.mapper.DictMapper;
import com.sub.cmn.service.DictService;
import com.sub.model.cmn.Dict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService{

    @Override
    public List<Dict> findChild(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);

        List<Dict> list = baseMapper.selectList(wrapper);
        list.forEach(dict -> {
            boolean hasChildren = hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });
        return list;
    }

    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        return baseMapper.selectCount(wrapper) > 0;
    }
}
